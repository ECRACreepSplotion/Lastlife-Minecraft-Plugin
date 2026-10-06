package com.kodari.lastheart;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class LastHeartPlugin extends JavaPlugin implements Listener, CommandExecutor {
    private static final int MAX_HEARTS = 10;
    private static final String BAN_REASON = "You lost all 10 hearts.";

    private File playerDataFile;
    private FileConfiguration playerData;
    private Attribute maxHealthAttribute;
    private NamespacedKey evolvedTotemKey;
    private NamespacedKey evolvedTotemUsesKey;
    private NamespacedKey wardenArmorKey;
    private NamespacedKey wardenLeggingsKey;

    @Override
    public void onEnable() {
        maxHealthAttribute = XAttribute.of("max_health")
                .map(XAttribute::get)
                .orElseThrow(() -> new IllegalStateException("Could not find the max health attribute"));
        evolvedTotemKey = new NamespacedKey(this, "evolved_totem");
        evolvedTotemUsesKey = new NamespacedKey(this, "evolved_totem_uses");
        wardenArmorKey = new NamespacedKey(this, "warden_armor");
        wardenLeggingsKey = new NamespacedKey(this, "warden_leggings");

        playerDataFile = new File(getDataFolder(), "players.yml");
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            throw new IllegalStateException("Could not create the plugin data folder");
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("heartunban").setExecutor(this);
        getCommand("sethearts").setExecutor(this);
        getCommand("checkhearts").setExecutor(this);
        Bukkit.getScheduler().runTaskTimer(this,
                () -> Bukkit.getOnlinePlayers().forEach(this::refreshWardenLeggingsEffects), 0L, 20L);
    }

    @Override
    public void onDisable() {
        if (playerData != null) {
            savePlayerData();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (isDeathBanned(event.getPlayer().getUniqueId())) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, BAN_REASON);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        applyMaxHealth(event.getPlayer(), false);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        int hearts = getHearts(playerId);

        if (hearts >= MAX_HEARTS) {
            playerData.set(playerPath(playerId) + ".death-banned", true);
            savePlayerData();
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), BAN_REASON, null, getName());
            player.kickPlayer(BAN_REASON);
            return;
        }

        playerData.set(playerPath(playerId) + ".hearts", hearts + 1);
        savePlayerData();
    }

    @EventHandler
    public void onEvokerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Evoker
                && ThreadLocalRandom.current().nextDouble() < 0.10) {
            event.getDrops().add(createEvolvedTotem());
        }
    }

    @EventHandler
    public void onWardenDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Warden)
                || ThreadLocalRandom.current().nextDouble() >= 0.15) {
            return;
        }

        double pieceRoll = ThreadLocalRandom.current().nextDouble();
        if (pieceRoll < 0.45) {
            event.getDrops().add(createWardenArmor("NETHERITE_BOOTS", "Boots", false));
        } else if (pieceRoll < 0.75) {
            event.getDrops().add(createWardenArmor("NETHERITE_HELMET", "Helmet", false));
        } else if (pieceRoll < 0.92) {
            event.getDrops().add(createWardenArmor("NETHERITE_CHESTPLATE", "Chestplate", false));
        } else {
            event.getDrops().add(createWardenArmor("NETHERITE_LEGGINGS", "Leggings", true));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            refreshMaxHealthNextTick(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            refreshMaxHealthNextTick(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        refreshMaxHealthNextTick(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        EquipmentSlot hand = isTotemOfUndying(player.getInventory().getItemInMainHand())
                ? EquipmentSlot.HAND
                : EquipmentSlot.OFF_HAND;
        ItemStack usedTotem = hand == EquipmentSlot.HAND
                ? player.getInventory().getItemInMainHand()
                : player.getInventory().getItemInOffHand();
        if (!isEvolvedTotem(usedTotem)) {
            return;
        }

        ItemMeta meta = usedTotem.getItemMeta();
        int uses = meta.getPersistentDataContainer().getOrDefault(
                evolvedTotemUsesKey, PersistentDataType.INTEGER, 1);
        if (uses <= 1) {
            return;
        }

        ItemStack remainingTotem = usedTotem.clone();
        remainingTotem.setAmount(1);
        ItemMeta remainingMeta = remainingTotem.getItemMeta();
        remainingMeta.getPersistentDataContainer().set(
                evolvedTotemUsesKey, PersistentDataType.INTEGER, uses - 1);
        remainingTotem.setItemMeta(remainingMeta);

        Bukkit.getScheduler().runTask(this, () -> {
            if (!player.isOnline()) {
                return;
            }

            PlayerInventory inventory = player.getInventory();
            ItemStack handItem = hand == EquipmentSlot.HAND
                    ? inventory.getItemInMainHand()
                    : inventory.getItemInOffHand();
            if (handItem == null || handItem.getType().isAir()) {
                if (hand == EquipmentSlot.HAND) {
                    inventory.setItemInMainHand(remainingTotem);
                } else {
                    inventory.setItemInOffHand(remainingTotem);
                }
                return;
            }

            Map<Integer, ItemStack> leftovers = inventory.addItem(remainingTotem);
            leftovers.values().forEach(item -> player.getWorld()
                    .dropItemNaturally(player.getLocation(), item));
        });
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(this, () -> {
            if (player.isOnline()) {
                applyMaxHealth(player, true);
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "heartunban" -> unbanPlayer(sender, args);
            case "sethearts" -> setPlayerHearts(sender, args);
            case "checkhearts" -> checkPlayerHearts(sender, args);
            default -> {
                return false;
            }
        }
        return true;
    }

    private void unbanPlayer(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /heartunban <player>");
            return;
        }

        OfflinePlayer target = findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("That player has not played on this server.");
            return;
        }

        UUID playerId = target.getUniqueId();
        if (!isDeathBanned(playerId)) {
            sender.sendMessage(target.getName() + " is not death-banned.");
            return;
        }

        playerData.set(playerPath(playerId) + ".death-banned", false);
        savePlayerData();
        String playerName = target.getName() == null ? args[0] : target.getName();
        Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
        sender.sendMessage("Unbanned " + playerName + " from the death ban.");
    }

    private void setPlayerHearts(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /sethearts <player> <1-10>");
            return;
        }

        OfflinePlayer target = findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("That player has not played on this server.");
            return;
        }

        int hearts;
        try {
            hearts = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("Hearts must be a whole number from 1 to 10.");
            return;
        }
        if (hearts < 1 || hearts > MAX_HEARTS) {
            sender.sendMessage("Hearts must be from 1 to 10.");
            return;
        }

        UUID playerId = target.getUniqueId();
        playerData.set(playerPath(playerId) + ".hearts", hearts);
        savePlayerData();
        Player onlinePlayer = Bukkit.getPlayer(playerId);
        if (onlinePlayer != null) {
            applyMaxHealth(onlinePlayer, false);
        }
        sender.sendMessage("Set " + target.getName() + " to " + hearts + " heart(s).");
    }

    private void checkPlayerHearts(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /checkhearts <player>");
            return;
        }

        OfflinePlayer target = findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("That player has not played on this server.");
            return;
        }

        sender.sendMessage(target.getName() + " has " + getHearts(target.getUniqueId()) + " heart(s).");
    }

    private OfflinePlayer findPlayer(String name) {
        Player onlinePlayer = Bukkit.getPlayerExact(name);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.hasPlayedBefore() ? offlinePlayer : null;
    }

    private void applyMaxHealth(Player player, boolean restoreHealth) {
        AttributeInstance attribute = player.getAttribute(maxHealthAttribute);
        if (attribute == null) {
            getLogger().severe("Could not apply max health for " + player.getName());
            return;
        }

        double maxHealth = getHearts(player.getUniqueId()) * 2.0
                + (isWardenLeggings(player.getInventory().getLeggings()) ? 10.0 : 0.0);
        attribute.setBaseValue(maxHealth);
        if (restoreHealth) {
            player.setHealth(maxHealth);
        } else if (player.getHealth() > maxHealth) {
            player.setHealth(maxHealth);
        }
        refreshWardenLeggingsEffects(player);
    }

    private void refreshWardenLeggingsEffects(Player player) {
        if (isWardenLeggings(player.getInventory().getLeggings())) {
            XPotion.matchXPotion("FIRE_RESISTANCE")
                    .map(potion -> potion.buildPotionEffect(40, 0))
                    .ifPresent(effect -> player.addPotionEffect(effect, true));
            XPotion.matchXPotion("REGENERATION")
                    .map(potion -> potion.buildPotionEffect(40, 2))
                    .ifPresent(effect -> player.addPotionEffect(effect, true));
        } else {
            XPotion.matchXPotion("FIRE_RESISTANCE")
                    .map(potion -> potion.buildPotionEffect(1, 0).getType())
                    .ifPresent(player::removePotionEffect);
            XPotion.matchXPotion("REGENERATION")
                    .map(potion -> potion.buildPotionEffect(1, 2).getType())
                    .ifPresent(player::removePotionEffect);
        }
    }

    private ItemStack createEvolvedTotem() {
        ItemStack totem = XMaterial.matchXMaterial("TOTEM_OF_UNDYING")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Totem of Undying is unavailable"));
        ItemMeta meta = totem.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "V1 Evolved Totem");
        meta.getPersistentDataContainer().set(evolvedTotemKey, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(evolvedTotemUsesKey, PersistentDataType.INTEGER, 2);
        XEnchantment.matchXEnchantment("UNBREAKING")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 1, true));
        totem.setItemMeta(meta);
        return totem;
    }

    private ItemStack createWardenArmor(String materialName, String pieceName, boolean leggings) {
        ItemStack armor = XMaterial.matchXMaterial(materialName)
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException(materialName + " is unavailable"));
        ItemMeta meta = armor.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "The Warden's Netherite " + pieceName);
        meta.getPersistentDataContainer().set(wardenArmorKey, PersistentDataType.BYTE, (byte) 1);
        XEnchantment.matchXEnchantment("PROTECTION")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
        if (leggings) {
            meta.setUnbreakable(true);
            meta.getPersistentDataContainer().set(wardenLeggingsKey, PersistentDataType.BYTE, (byte) 1);
            XEnchantment.matchXEnchantment("SWIFT_SNEAK")
                    .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
        }
        armor.setItemMeta(meta);
        return armor;
    }

    private boolean isWardenLeggings(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(wardenLeggingsKey, PersistentDataType.BYTE);
    }

    private void refreshMaxHealthNextTick(Player player) {
        Bukkit.getScheduler().runTask(this, () -> {
            if (player.isOnline()) {
                applyMaxHealth(player, false);
            }
        });
    }

    private boolean isEvolvedTotem(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(evolvedTotemKey, PersistentDataType.BYTE);
    }

    private boolean isTotemOfUndying(ItemStack item) {
        return item != null && "TOTEM_OF_UNDYING".equals(item.getType().name());
    }

    private int getHearts(UUID playerId) {
        return Math.max(1, Math.min(MAX_HEARTS,
                playerData.getInt(playerPath(playerId) + ".hearts", 1)));
    }

    private boolean isDeathBanned(UUID playerId) {
        return playerData.getBoolean(playerPath(playerId) + ".death-banned", false);
    }

    private String playerPath(UUID playerId) {
        return "players." + playerId;
    }

    private void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException exception) {
            getLogger().severe("Could not save player data: " + exception.getMessage());
        }
    }
}