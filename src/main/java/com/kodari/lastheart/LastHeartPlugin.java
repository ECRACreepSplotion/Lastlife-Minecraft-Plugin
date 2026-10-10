package com.kodari.lastheart;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Keyed;
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
    private NamespacedKey witherBootsKey;
    private NamespacedKey witherArmorKey;
    private NamespacedKey demonicInitiatorKey;
    private NamespacedKey demonicInitiatorRecipeKey;
    private NamespacedKey empoweredDragonKey;
    private NamespacedKey dragonArmorKey;
    private NamespacedKey dragonChestplateKey;
    private NamespacedKey elderGuardianArmorKey;
    private NamespacedKey elderGuardianHelmetKey;
    private NamespacedKey soulFragmentKey;
    private NamespacedKey heartRemoverKey;
    private NamespacedKey heartRemoverRecipeKey;
    private NamespacedKey soulTableKey;
    private NamespacedKey soulTableRecipeKey;
    private NamespacedKey soulMaceKey;
    private final Map<UUID, PermissionAttachment> locatePermissionAttachments = new HashMap<>();
    private final Map<String, Supplier<ItemStack>> customItemFactories = new HashMap<>();
    private final List<String> customItemNames = new ArrayList<>();

    @Override
    public void onEnable() {
        maxHealthAttribute = XAttribute.of("max_health")
                .map(XAttribute::get)
                .orElseThrow(() -> new IllegalStateException("Could not find the max health attribute"));
        evolvedTotemKey = new NamespacedKey(this, "evolved_totem");
        evolvedTotemUsesKey = new NamespacedKey(this, "evolved_totem_uses");
        wardenArmorKey = new NamespacedKey(this, "warden_armor");
        wardenLeggingsKey = new NamespacedKey(this, "warden_leggings");
        witherBootsKey = new NamespacedKey(this, "wither_boots");
        witherArmorKey = new NamespacedKey(this, "wither_armor");
        demonicInitiatorKey = new NamespacedKey(this, "demonic_initiator");
        demonicInitiatorRecipeKey = new NamespacedKey(this, "demonic_initiator");
        empoweredDragonKey = new NamespacedKey(this, "empowered_dragon");
        dragonArmorKey = new NamespacedKey(this, "dragon_armor");
        dragonChestplateKey = new NamespacedKey(this, "dragon_chestplate");
        elderGuardianArmorKey = new NamespacedKey(this, "elder_guardian_armor");
        elderGuardianHelmetKey = new NamespacedKey(this, "elder_guardian_helmet");
        soulFragmentKey = new NamespacedKey(this, "soul_fragment");
        heartRemoverKey = new NamespacedKey(this, "heart_remover");
        heartRemoverRecipeKey = new NamespacedKey(this, "heart_remover");
        soulTableKey = new NamespacedKey(this, "soul_table");
        soulTableRecipeKey = new NamespacedKey(this, "soul_table_recipe");
        soulMaceKey = new NamespacedKey(this, "soul_mace");
        registerCustomItemFactories();

        playerDataFile = new File(getDataFolder(), "players.yml");
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            throw new IllegalStateException("Could not create the plugin data folder");
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        registerDemonicInitiatorRecipe();
        registerHeartRemoverRecipe();
        registerSoulTableRecipe();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("heartunban").setExecutor(this);
        getCommand("sethearts").setExecutor(this);
        getCommand("checkhearts").setExecutor(this);
        getCommand("givecustom").setExecutor(this);
        Bukkit.getScheduler().runTaskTimer(this,
                () -> Bukkit.getOnlinePlayers().forEach(player -> {
                    refreshWardenLeggingsEffects(player);
                    refreshWitherBootsEffects(player);
                    refreshDragonChestplateEffects(player);
                    refreshElderGuardianHelmetEffects(player);
                    refreshLocatePermission(player);
                }), 0L, 20L);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PermissionAttachment attachment = locatePermissionAttachments.remove(player.getUniqueId());
            if (attachment != null) {
                player.removeAttachment(attachment);
            }
        }
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
        refreshLocatePermission(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        int hearts = getHearts(playerId);

        if (ThreadLocalRandom.current().nextDouble() < Math.min(0.30, hearts * 0.03)) {
            event.getDrops().add(createSoulFragment());
        }

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
    public void onElderGuardianDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof ElderGuardian)
                || ThreadLocalRandom.current().nextDouble() >= 0.25) {
            return;
        }

        double pieceRoll = ThreadLocalRandom.current().nextDouble();
        if (pieceRoll < 0.50) {
            event.getDrops().add(createElderGuardianArmor("NETHERITE_BOOTS", "Boots", false));
        } else if (pieceRoll < 0.80) {
            event.getDrops().add(createElderGuardianArmor("NETHERITE_LEGGINGS", "Leggings", false));
        } else if (pieceRoll < 0.95) {
            event.getDrops().add(createElderGuardianArmor("NETHERITE_CHESTPLATE", "Chestplate", false));
        } else {
            event.getDrops().add(createElderGuardianArmor("NETHERITE_HELMET", "Helmet", true));
        }
    }

    @EventHandler
    public void onStructureLootGenerate(LootGenerateEvent event) {
        String lootTable = event.getLootTable().getKey().toString();
        double chance = getSoulFragmentDropChance(lootTable);
        if (chance > 0.0 && ThreadLocalRandom.current().nextDouble() < chance) {
            event.getLoot().add(createSoulFragment());
        }
    }

    private double getSoulFragmentDropChance(String lootTable) {
        if ("minecraft:chests/buried_treasure".equals(lootTable)) {
            return 0.85;
        }
        if (lootTable.startsWith("minecraft:chests/shipwreck_")) {
            return 0.60;
        }
        if ("minecraft:chests/jungle_temple".equals(lootTable)) {
            return 1.0;
        }
        if ("minecraft:chests/pillager_outpost".equals(lootTable)) {
            return 0.45;
        }
        if ("minecraft:chests/desert_pyramid".equals(lootTable)) {
            return 0.50;
        }
        if (lootTable.startsWith("minecraft:chests/village/")) {
            return 0.25;
        }
        return 0.0;
    }

    @EventHandler
    public void onWitherDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Wither)
                || ThreadLocalRandom.current().nextDouble() >= 0.30) {
            return;
        }

        String environment = event.getEntity().getWorld().getEnvironment().name();
        double pieceRoll = ThreadLocalRandom.current().nextDouble();
        if ("NETHER".equals(environment)) {
            if (pieceRoll < 0.10) {
                event.getDrops().add(createWitherArmor("NETHERITE_BOOTS", "Boots", true));
            } else if (pieceRoll < 0.50) {
                event.getDrops().add(createWitherArmor("NETHERITE_HELMET", "Helmet", false));
            } else if (pieceRoll < 0.60) {
                event.getDrops().add(createWitherArmor("NETHERITE_CHESTPLATE", "Chestplate", false));
            } else {
                event.getDrops().add(createWitherArmor("NETHERITE_LEGGINGS", "Leggings", false));
            }
        } else if ("THE_END".equals(environment)) {
            if (pieceRoll < 0.40) {
                event.getDrops().add(createWitherArmor("NETHERITE_HELMET", "Helmet", false));
            } else if (pieceRoll < 0.60) {
                event.getDrops().add(createWitherArmor("NETHERITE_CHESTPLATE", "Chestplate", false));
            } else {
                event.getDrops().add(createWitherArmor("NETHERITE_LEGGINGS", "Leggings", false));
            }
        } else {
            if (pieceRoll < 0.03) {
                event.getDrops().add(createWitherArmor("NETHERITE_BOOTS", "Boots", true));
            } else if (pieceRoll < 0.43) {
                event.getDrops().add(createWitherArmor("NETHERITE_HELMET", "Helmet", false));
            } else if (pieceRoll < 0.60) {
                event.getDrops().add(createWitherArmor("NETHERITE_CHESTPLATE", "Chestplate", false));
            } else {
                event.getDrops().add(createWitherArmor("NETHERITE_LEGGINGS", "Leggings", false));
            }
        }
    }

    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon dragon)
                || !isEmpoweredDragon(dragon)) {
            return;
        }

        int guaranteedChestplates = playerData.getInt("dragon.guaranteed-chestplates", 0);
        if (guaranteedChestplates > 0) {
            event.getDrops().add(createDragonArmor("NETHERITE_CHESTPLATE", "Chestplate", true));
            playerData.set("dragon.guaranteed-chestplates", guaranteedChestplates - 1);
            savePlayerData();
            return;
        }
        if (ThreadLocalRandom.current().nextDouble() >= 0.70) {
            return;
        }

        double pieceRoll = ThreadLocalRandom.current().nextDouble();
        if (pieceRoll < 0.30) {
            event.getDrops().add(createDragonArmor("NETHERITE_BOOTS", "Boots", false));
        } else if (pieceRoll < 0.60) {
            event.getDrops().add(createDragonArmor("NETHERITE_HELMET", "Helmet", false));
        } else if (pieceRoll < 0.90) {
            event.getDrops().add(createDragonArmor("NETHERITE_LEGGINGS", "Leggings", false));
        } else {
            event.getDrops().add(createDragonArmor("NETHERITE_CHESTPLATE", "Chestplate", true));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEmpoweredDragonPhaseChange(EnderDragonChangePhaseEvent event) {
        if (isEmpoweredDragon(event.getEntity())
                && event.getNewPhase() == EnderDragon.Phase.LAND_ON_PORTAL) {
            event.setNewPhase(EnderDragon.Phase.CIRCLING);
        }
    }

    @EventHandler
    public void onWitherSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Wither wither)) {
            return;
        }

        double multiplier = switch (wither.getWorld().getEnvironment().name()) {
            case "NORMAL" -> 2.0;
            case "NETHER" -> 5.0;
            default -> 1.0;
        };
        AttributeInstance healthAttribute = wither.getAttribute(maxHealthAttribute);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(healthAttribute.getBaseValue() * multiplier);
            wither.setHealth(healthAttribute.getValue());
        }
    }

    @EventHandler
    public void onElderGuardianSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof ElderGuardian elderGuardian)) {
            return;
        }

        AttributeInstance healthAttribute = elderGuardian.getAttribute(maxHealthAttribute);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(healthAttribute.getBaseValue() * 3.0);
            elderGuardian.setHealth(healthAttribute.getValue());
        }
    }

    @EventHandler
    public void onSoulTablePlace(BlockPlaceEvent event) {
        if (!isSoulTable(event.getItemInHand())) {
            return;
        }

        List<String> soulTables = playerData.getStringList("soul-tables");
        String locationKey = soulTableLocationKey(event.getBlockPlaced());
        if (!soulTables.contains(locationKey)) {
            soulTables.add(locationKey);
            playerData.set("soul-tables", soulTables);
            savePlayerData();
        }
    }

    @EventHandler
    public void onSoulTableBreak(BlockBreakEvent event) {
        List<String> soulTables = playerData.getStringList("soul-tables");
        if (!soulTables.remove(soulTableLocationKey(event.getBlock()))) {
            return;
        }

        playerData.set("soul-tables", soulTables);
        savePlayerData();
        event.setDropItems(false);
        if (event.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE) {
            Map<Integer, ItemStack> overflow = event.getPlayer().getInventory().addItem(createSoulTable());
            overflow.values().forEach(item -> event.getBlock().getWorld()
                    .dropItemNaturally(event.getBlock().getLocation(), item));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSoulMaceDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)
                || !(event.getEntity() instanceof LivingEntity)
                || !isSoulMace(player.getInventory().getItemInMainHand())) {
            return;
        }

        double healing = event.getFinalDamage() / 3.0;
        if (healing <= 0.0) {
            return;
        }
        Bukkit.getScheduler().runTask(this, () -> {
            if (player.isOnline() && !player.isDead()) {
                AttributeInstance healthAttribute = player.getAttribute(maxHealthAttribute);
                if (healthAttribute != null) {
                    player.setHealth(Math.min(healthAttribute.getValue(), player.getHealth() + healing));
                }
            }
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof SoulTableHolder holder) {
            if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
                if (event.isShiftClick()) {
                    event.setCancelled(true);
                }
                return;
            }

            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) {
                return;
            }
            if (event.getRawSlot() == 49) {
                holder.transitioning = true;
                openSoulTable(player, holder.grid, 1 - holder.page);
            } else if (event.getRawSlot() == 53) {
                craftSoulMace(player, holder);
            } else {
                int gridSlot = holder.gridSlot(event.getRawSlot());
                if (gridSlot >= 0 && (event.isLeftClick() || event.isRightClick())) {
                    moveSoulTableItem(player, holder, gridSlot, event.isRightClick());
                }
            }
            return;
        }
        if (event.getWhoClicked() instanceof Player player) {
            refreshMaxHealthNextTick(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof SoulTableHolder) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot < event.getView().getTopInventory().getSize())) {
                event.setCancelled(true);
            }
            return;
        }
        if (event.getWhoClicked() instanceof Player player) {
            refreshMaxHealthNextTick(player);
        }
    }

    @EventHandler
    public void onSoulTableClose(InventoryCloseEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof SoulTableHolder holder)
                || holder.transitioning
                || !(event.getPlayer() instanceof Player player)) {
            return;
        }

        syncSoulTablePage(holder);
        for (int slot = 0; slot < holder.grid.length; slot++) {
            ItemStack item = holder.grid[slot];
            if (isEmpty(item)) {
                continue;
            }
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);
            overflow.values().forEach(overflowItem -> player.getWorld()
                    .dropItemNaturally(player.getLocation(), overflowItem));
            holder.grid[slot] = null;
        }
    }

    @EventHandler
    public void onPrepareDemonicInitiator(PrepareItemCraftEvent event) {
        if (isDemonicInitiatorRecipe(event.getRecipe())) {
            event.getInventory().setResult(isDemonicInitiatorRecipeInput(event.getInventory().getMatrix())
                    ? createDemonicInitiator()
                    : null);
        }
    }

    @EventHandler
    public void onPrepareHeartRemover(PrepareItemCraftEvent event) {
        if (isHeartRemoverRecipe(event.getRecipe())) {
            event.getInventory().setResult(isHeartRemoverRecipeInput(event.getInventory().getMatrix())
                    ? createHeartRemover()
                    : null);
        }
    }

    @EventHandler
    public void onCraftDemonicInitiator(CraftItemEvent event) {
        if (isDemonicInitiatorRecipe(event.getRecipe())
                && !isDemonicInitiatorRecipeInput(event.getInventory().getMatrix())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraftHeartRemover(CraftItemEvent event) {
        if (isHeartRemoverRecipe(event.getRecipe())
                && !isHeartRemoverRecipeInput(event.getInventory().getMatrix())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        refreshMaxHealthNextTick(event.getPlayer());
        if (event.getHand() == EquipmentSlot.HAND
                && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null
                && isSoulTableLocation(event.getClickedBlock())) {
            event.setCancelled(true);
            openSoulTable(event.getPlayer(), new ItemStack[81], 0);
            return;
        }
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && isHeartRemover(event.getItem())) {
            event.setCancelled(true);
            consumeHeartRemover(event.getPlayer(), event.getHand());
            return;
        }
        if ((event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                || !isDemonicInitiator(event.getItem())) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!"THE_END".equals(player.getWorld().getEnvironment().name())) {
            player.sendMessage(ChatColor.RED + "The Demonic Initiator can only be used in the End.");
            return;
        }

        EnderDragon dragon = player.getWorld().getEntitiesByClass(EnderDragon.class).stream()
                .filter(candidate -> !candidate.isDead() && candidate.getHealth() > 0.0)
                .findFirst()
                .orElse(null);
        if (dragon == null) {
            player.sendMessage(ChatColor.RED + "There is no living Ender Dragon to empower.");
            return;
        }
        if (isEmpoweredDragon(dragon)) {
            player.sendMessage(ChatColor.RED + "This Ender Dragon has already been empowered.");
            return;
        }

        ItemStack initiator = event.getItem();
        if (initiator.getAmount() <= 1) {
            if (event.getHand() == EquipmentSlot.HAND) {
                player.getInventory().setItemInMainHand(null);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
        } else {
            initiator.setAmount(initiator.getAmount() - 1);
        }
        empowerDragon(dragon);
        player.sendMessage(ChatColor.DARK_PURPLE + "The Ender Dragon has been empowered!");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerElytraBoost(PlayerElytraBoostEvent event) {
        if (hasDragonChestplateInInventory(event.getPlayer())) {
            event.setShouldConsume(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PermissionAttachment attachment = locatePermissionAttachments.remove(event.getPlayer().getUniqueId());
        if (attachment != null) {
            event.getPlayer().removeAttachment(attachment);
        }
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
            case "givecustom" -> giveCustomItem(sender, args);
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

    private void giveCustomItem(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /givecustom <player> <item name> <amount>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("That player is not online.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[args.length - 1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("Amount must be a positive whole number.");
            return;
        }
        if (amount < 1) {
            sender.sendMessage("Amount must be a positive whole number.");
            return;
        }

        String itemName = String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1));
        Supplier<ItemStack> itemFactory = customItemFactories.get(normalizeCustomItemName(itemName));
        if (itemFactory == null) {
            sender.sendMessage("Unknown custom item. Use the custom item's name.");
            sender.sendMessage("Available custom item names:");
            for (String customItemName : customItemNames) {
                sender.sendMessage(ChatColor.YELLOW + "- " + customItemName);
            }
            return;
        }

        ItemStack item = itemFactory.get();
        item.setAmount(amount);
        target.getInventory().addItem(item).values().forEach(remaining ->
                target.getWorld().dropItemNaturally(target.getLocation(), remaining));
        sender.sendMessage("Gave " + amount + " " + args[1] + " to " + target.getName() + ".");
    }

    private void consumeHeartRemover(Player player, EquipmentSlot hand) {
        UUID playerId = player.getUniqueId();
        int hearts = getHearts(playerId);
        if (hearts <= 1) {
            player.sendMessage(ChatColor.RED + "You need more than one heart to use the Heart Remover.");
            return;
        }

        playerData.set(playerPath(playerId) + ".hearts", hearts - 1);
        savePlayerData();
        ItemStack item = hand == EquipmentSlot.HAND
                ? player.getInventory().getItemInMainHand()
                : player.getInventory().getItemInOffHand();
        if (item.getAmount() <= 1) {
            if (hand == EquipmentSlot.HAND) {
                player.getInventory().setItemInMainHand(null);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
        } else {
            item.setAmount(item.getAmount() - 1);
        }
        applyMaxHealth(player, false);
        player.sendMessage(ChatColor.RED + "You sacrificed one heart for an extra life. This cannot be undone.");
    }

    private void registerCustomItemFactories() {
        registerCustomItem("Demonic Initiator", this::createDemonicInitiator);
        registerCustomItem("Soul Fragments", this::createSoulFragment, "soulfragment");
        registerCustomItem("Heart Remover", this::createHeartRemover, "heartremover");
        registerCustomItem("Soul Table", this::createSoulTable, "soultable");
        registerCustomItem("Soul Mace", this::createSoulMace, "soulmace");
        registerCustomItem("V1 Evolved Totem", this::createEvolvedTotem, "evolvedtotem");
        registerCustomItem("The Warden's Netherite Boots",
                () -> createWardenArmor("NETHERITE_BOOTS", "Boots", false), "wardenboots");
        registerCustomItem("The Warden's Netherite Helmet",
                () -> createWardenArmor("NETHERITE_HELMET", "Helmet", false), "wardenhelmet");
        registerCustomItem("The Warden's Netherite Chestplate",
                () -> createWardenArmor("NETHERITE_CHESTPLATE", "Chestplate", false), "wardenchestplate");
        registerCustomItem("The Warden's Netherite Leggings",
                () -> createWardenArmor("NETHERITE_LEGGINGS", "Leggings", true), "wardenleggings");
        registerCustomItem("The Wither's Netherite Boots",
                () -> createWitherArmor("NETHERITE_BOOTS", "Boots", true), "witherboots");
        registerCustomItem("The Wither's Netherite Helmet",
                () -> createWitherArmor("NETHERITE_HELMET", "Helmet", false), "witherhelmet");
        registerCustomItem("The Wither's Netherite Chestplate",
                () -> createWitherArmor("NETHERITE_CHESTPLATE", "Chestplate", false), "witherchestplate");
        registerCustomItem("The Wither's Netherite Leggings",
                () -> createWitherArmor("NETHERITE_LEGGINGS", "Leggings", false), "witherleggings");
        registerCustomItem("The Ender Dragon's Netherite Boots",
                () -> createDragonArmor("NETHERITE_BOOTS", "Boots", false), "dragonboots");
        registerCustomItem("The Ender Dragon's Netherite Helmet",
                () -> createDragonArmor("NETHERITE_HELMET", "Helmet", false), "dragonhelmet");
        registerCustomItem("The Ender Dragon's Netherite Leggings",
                () -> createDragonArmor("NETHERITE_LEGGINGS", "Leggings", false), "dragonleggings");
        registerCustomItem("The Ender Dragon's Netherite Chestplate",
                () -> createDragonArmor("NETHERITE_CHESTPLATE", "Chestplate", true), "dragonchestplate");
        registerCustomItem("The Elder Guardian's Netherite Boots",
                () -> createElderGuardianArmor("NETHERITE_BOOTS", "Boots", false), "elderguardianboots");
        registerCustomItem("The Elder Guardian's Netherite Leggings",
                () -> createElderGuardianArmor("NETHERITE_LEGGINGS", "Leggings", false), "elderguardianleggings");
        registerCustomItem("The Elder Guardian's Netherite Chestplate",
                () -> createElderGuardianArmor("NETHERITE_CHESTPLATE", "Chestplate", false), "elderguardianchestplate");
        registerCustomItem("The Elder Guardian's Netherite Helmet",
                () -> createElderGuardianArmor("NETHERITE_HELMET", "Helmet", true),
                "elderguardianhelmet");
    }

    private void registerCustomItem(String name, Supplier<ItemStack> factory, String... aliases) {
        customItemNames.add(name);
        customItemFactories.put(normalizeCustomItemName(name), factory);
        for (String alias : aliases) {
            customItemFactories.put(normalizeCustomItemName(alias), factory);
        }
    }

    private String normalizeCustomItemName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
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
                + (isWardenLeggings(player.getInventory().getLeggings()) ? 10.0 : 0.0)
                + (isDragonChestplate(player.getInventory().getChestplate()) ? 20.0 : 0.0);
        attribute.setBaseValue(maxHealth);
        if (restoreHealth) {
            player.setHealth(maxHealth);
        } else if (player.getHealth() > maxHealth) {
            player.setHealth(maxHealth);
        }
        refreshWardenLeggingsEffects(player);
        refreshDragonChestplateEffects(player);
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

    private void refreshWitherBootsEffects(Player player) {
        if (isWitherBoots(player.getInventory().getBoots())) {
            XPotion.matchXPotion("SPEED")
                    .map(potion -> potion.buildPotionEffect(40, 2))
                    .ifPresent(effect -> player.addPotionEffect(effect, true));
        } else {
            XPotion.matchXPotion("SPEED")
                    .map(potion -> potion.buildPotionEffect(1, 2).getType())
                    .ifPresent(player::removePotionEffect);
        }
    }

    private void refreshDragonChestplateEffects(Player player) {
        if (isDragonChestplate(player.getInventory().getChestplate())) {
            XPotion.matchXPotion("RESISTANCE")
                    .map(potion -> potion.buildPotionEffect(40, 2))
                    .ifPresent(effect -> player.addPotionEffect(effect, true));
        } else {
            XPotion.matchXPotion("RESISTANCE")
                    .map(potion -> potion.buildPotionEffect(1, 2).getType())
                    .ifPresent(player::removePotionEffect);
        }
    }

    private void refreshElderGuardianHelmetEffects(Player player) {
        if (isElderGuardianHelmet(player.getInventory().getHelmet())) {
            XPotion.matchXPotion("WEAVING")
                    .map(potion -> potion.buildPotionEffect(40, 5))
                    .ifPresent(effect -> player.addPotionEffect(effect, true));
            XPotion.matchXPotion("NIGHT_VISION")
                    .map(potion -> potion.buildPotionEffect(40, 0))
                    .ifPresent(effect -> player.addPotionEffect(effect, true));
            XPotion.matchXPotion("HASTE")
                    .map(potion -> potion.buildPotionEffect(40, 3))
                    .ifPresent(effect -> player.addPotionEffect(effect, true));
        } else {
            XPotion.matchXPotion("WEAVING")
                    .map(potion -> potion.buildPotionEffect(1, 5).getType())
                    .ifPresent(player::removePotionEffect);
            XPotion.matchXPotion("NIGHT_VISION")
                    .map(potion -> potion.buildPotionEffect(1, 0).getType())
                    .ifPresent(player::removePotionEffect);
            XPotion.matchXPotion("HASTE")
                    .map(potion -> potion.buildPotionEffect(1, 1).getType())
                    .ifPresent(player::removePotionEffect);
        }
    }

    private void registerDemonicInitiatorRecipe() {
        Bukkit.removeRecipe(demonicInitiatorRecipeKey);
        ShapedRecipe recipe = new ShapedRecipe(demonicInitiatorRecipeKey, createDemonicInitiator());
        recipe.shape("DHD", "DCD", "DDD");
        recipe.setIngredient('D', XMaterial.matchXMaterial("DRAGON_HEAD")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Dragon head is unavailable")));
        recipe.setIngredient('H', XMaterial.matchXMaterial("NETHER_STAR")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Nether star is unavailable")));
        recipe.setIngredient('C', XMaterial.matchXMaterial("SCULK_CATALYST")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Sculk catalyst is unavailable")));
        Bukkit.addRecipe(recipe);
    }

    private void registerHeartRemoverRecipe() {
        Bukkit.removeRecipe(heartRemoverRecipeKey);
        ShapedRecipe recipe = new ShapedRecipe(heartRemoverRecipeKey, createHeartRemover());
        recipe.shape("SSS", "SCS", "SSS");
        recipe.setIngredient('S', new RecipeChoice.ExactChoice(createSoulFragment()));
        recipe.setIngredient('C', XMaterial.matchXMaterial("HEAVY_CORE")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Heavy core is unavailable")));
        Bukkit.addRecipe(recipe);
    }

    private void registerSoulTableRecipe() {
        Bukkit.removeRecipe(soulTableRecipeKey);
        Material trimTemplate = XMaterial.matchXMaterial("SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Silence armor trim template is unavailable"));
        Material craftingTable = XMaterial.matchXMaterial("CRAFTING_TABLE")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Crafting table is unavailable"));
        Material netheriteIngot = XMaterial.matchXMaterial("NETHERITE_INGOT")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Netherite ingot is unavailable"));
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                soulTableRecipeKey,
                createSoulTable(),
                new RecipeChoice.MaterialChoice(trimTemplate),
                new RecipeChoice.MaterialChoice(craftingTable),
                new RecipeChoice.MaterialChoice(netheriteIngot));
        Bukkit.addRecipe(recipe);
    }

    private boolean isHeartRemoverRecipe(Recipe recipe) {
        return recipe instanceof Keyed keyed && heartRemoverRecipeKey.equals(keyed.getKey());
    }

    private boolean isHeartRemoverRecipeInput(ItemStack[] matrix) {
        if (matrix.length != 9) {
            return false;
        }
        for (int slot = 0; slot < matrix.length; slot++) {
            if (slot == 4) {
                if (matrix[slot] == null || !"HEAVY_CORE".equals(matrix[slot].getType().name())) {
                    return false;
                }
            } else if (!isSoulFragment(matrix[slot])) {
                return false;
            }
        }
        return true;
    }

    private void openSoulTable(Player player, ItemStack[] grid, int page) {
        SoulTableHolder holder = new SoulTableHolder(grid, page);
        holder.inventory = Bukkit.createInventory(holder, 54,
                ChatColor.BLUE + "Soul Table - Page " + (page + 1) + "/2");
        renderSoulTable(holder);
        player.openInventory(holder.inventory);
    }

    private void renderSoulTable(SoulTableHolder holder) {
        for (int slot = 0; slot < holder.inventory.getSize(); slot++) {
            int gridSlot = holder.gridSlot(slot);
            if (gridSlot >= 0) {
                holder.inventory.setItem(slot, holder.grid[gridSlot]);
            } else if (slot >= 45) {
                holder.inventory.setItem(slot, createSoulTableButton("GRAY_STAINED_GLASS_PANE", " "));
            }
        }

        holder.inventory.setItem(49, createSoulTableButton("ARROW",
                holder.page == 0 ? ChatColor.AQUA + "View rows 6-9" : ChatColor.AQUA + "View rows 1-5"));
        if (isSoulMaceRecipe(holder.grid)) {
            holder.inventory.setItem(53, createSoulMace());
        } else {
            holder.inventory.setItem(53, createSoulTableButton("BARRIER",
                    ChatColor.RED + "Complete the 9x9 recipe"));
        }
    }

    private ItemStack createSoulTableButton(String materialName, String displayName) {
        ItemStack button = XMaterial.matchXMaterial(materialName)
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException(materialName + " is unavailable"));
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(displayName);
        button.setItemMeta(meta);
        return button;
    }

    private void moveSoulTableItem(Player player, SoulTableHolder holder, int gridSlot, boolean rightClick) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack stored = holder.grid[gridSlot];
        cursor = isEmpty(cursor) ? null : cursor.clone();
        stored = isEmpty(stored) ? null : stored.clone();

        if (cursor == null && stored != null) {
            int amount = rightClick ? (stored.getAmount() + 1) / 2 : stored.getAmount();
            ItemStack taken = stored.clone();
            taken.setAmount(amount);
            stored.setAmount(stored.getAmount() - amount);
            player.setItemOnCursor(taken);
            holder.grid[gridSlot] = isEmpty(stored) ? null : stored;
        } else if (cursor != null && stored == null) {
            if (rightClick) {
                ItemStack placed = cursor.clone();
                placed.setAmount(1);
                cursor.setAmount(cursor.getAmount() - 1);
                holder.grid[gridSlot] = placed;
                player.setItemOnCursor(isEmpty(cursor) ? null : cursor);
            } else {
                holder.grid[gridSlot] = cursor;
                player.setItemOnCursor(null);
            }
        } else if (cursor != null && stored != null && stored.isSimilar(cursor)) {
            int maxAmount = Math.min(stored.getMaxStackSize(), cursor.getMaxStackSize());
            int moved = Math.min(rightClick ? 1 : cursor.getAmount(), maxAmount - stored.getAmount());
            if (moved > 0) {
                stored.setAmount(stored.getAmount() + moved);
                cursor.setAmount(cursor.getAmount() - moved);
                holder.grid[gridSlot] = stored;
                player.setItemOnCursor(isEmpty(cursor) ? null : cursor);
            }
        } else if (!rightClick) {
            holder.grid[gridSlot] = cursor;
            player.setItemOnCursor(stored);
        }

        renderSoulTable(holder);
    }

    private void syncSoulTablePage(SoulTableHolder holder) {
        for (int slot = 0; slot < holder.inventory.getSize(); slot++) {
            int gridSlot = holder.gridSlot(slot);
            if (gridSlot >= 0) {
                holder.grid[gridSlot] = holder.inventory.getItem(slot);
            }
        }
    }

    private void craftSoulMace(Player player, SoulTableHolder holder) {
        if (!isSoulMaceRecipe(holder.grid)) {
            player.sendMessage(ChatColor.RED + "The Soul Mace recipe is incomplete.");
            return;
        }

        for (int slot = 0; slot < holder.grid.length; slot++) {
            ItemStack ingredient = holder.grid[slot];
            ingredient.setAmount(ingredient.getAmount() - 1);
            if (ingredient.getAmount() <= 0) {
                holder.grid[slot] = null;
            }
        }

        Map<Integer, ItemStack> overflow = player.getInventory().addItem(createSoulMace());
        overflow.values().forEach(item -> player.getWorld()
                .dropItemNaturally(player.getLocation(), item));
        renderSoulTable(holder);
    }

    private boolean isSoulMaceRecipe(ItemStack[] grid) {
        if (grid.length != 81) {
            return false;
        }
        for (int slot = 0; slot < grid.length; slot++) {
            int row = slot / 9;
            int column = slot % 9;
            ItemStack ingredient = grid[slot];
            if (row >= 3 && row <= 5 && column >= 3 && column <= 5) {
                if (isEmpty(ingredient)
                        || !"MACE".equals(ingredient.getType().name())
                        || isSoulMace(ingredient)) {
                    return false;
                }
            } else if (!isSoulFragment(ingredient)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpty(ItemStack item) {
        return item == null || item.getType().isAir() || item.getAmount() <= 0;
    }

    private String soulTableLocationKey(Block block) {
        Location location = block.getLocation();
        return location.getWorld().getUID() + ";" + location.getBlockX()
                + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    private boolean isSoulTableLocation(Block block) {
        return playerData.getStringList("soul-tables").contains(soulTableLocationKey(block));
    }

    private ItemStack createDemonicInitiator() {
        ItemStack initiator = XMaterial.matchXMaterial("SKELETON_SKULL")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Skeleton skull is unavailable"));
        ItemMeta meta = initiator.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Demonic Initiator");
        meta.getPersistentDataContainer().set(demonicInitiatorKey, PersistentDataType.BYTE, (byte) 1);
        XEnchantment.matchXEnchantment("UNBREAKING")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 1, true));
        initiator.setItemMeta(meta);
        return initiator;
    }

    private ItemStack createSoulFragment() {
        ItemStack fragment = XMaterial.matchXMaterial("WITHER_ROSE")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Wither rose is unavailable"));
        ItemMeta meta = fragment.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Soul Fragment");
        meta.getPersistentDataContainer().set(soulFragmentKey, PersistentDataType.BYTE, (byte) 1);
        fragment.setItemMeta(meta);
        return fragment;
    }

    private ItemStack createSoulTable() {
        ItemStack table = XMaterial.matchXMaterial("CRAFTING_TABLE")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Crafting table is unavailable"));
        ItemMeta meta = table.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Soul Table");
        meta.setItemModel(new NamespacedKey(this, "soul_table"));
        meta.getPersistentDataContainer().set(soulTableKey, PersistentDataType.BYTE, (byte) 1);
        table.setItemMeta(meta);
        return table;
    }

    private ItemStack createSoulMace() {
        ItemStack mace = XMaterial.matchXMaterial("MACE")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Mace is unavailable"));
        ItemMeta meta = mace.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Soul Mace");
        meta.setLore(List.of(ChatColor.GRAY + "Heals 1 heart per 3 hearts of damage dealt."));
        meta.getPersistentDataContainer().set(soulMaceKey, PersistentDataType.BYTE, (byte) 1);
        Attribute attackDamage = XAttribute.of("attack_damage")
                .map(XAttribute::get)
                .orElseThrow(() -> new IllegalStateException("Attack damage attribute is unavailable"));
        meta.addAttributeModifier(attackDamage, new AttributeModifier(
                new NamespacedKey(this, "soul_mace_bonus"), 4.0,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        mace.setItemMeta(meta);
        return mace;
    }

    private ItemStack createHeartRemover() {
        ItemStack heartRemover = XMaterial.matchXMaterial("WITHER_SKELETON_SKULL")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Wither skeleton skull is unavailable"));
        ItemMeta meta = heartRemover.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Heart Remover");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Sacrifices one heart for an extra life.",
                ChatColor.DARK_RED + "This action cannot be undone."));
        meta.getPersistentDataContainer().set(heartRemoverKey, PersistentDataType.BYTE, (byte) 1);
        heartRemover.setItemMeta(meta);
        return heartRemover;
    }

    private boolean isDemonicInitiatorRecipe(Recipe recipe) {
        return recipe instanceof Keyed keyed && demonicInitiatorRecipeKey.equals(keyed.getKey());
    }

    private boolean isDemonicInitiatorRecipeInput(ItemStack[] matrix) {
        if (matrix.length != 9) {
            return false;
        }
        for (int slot = 0; slot < matrix.length; slot++) {
            if (slot == 1) {
                if (matrix[slot] == null || !"NETHER_STAR".equals(matrix[slot].getType().name())) {
                    return false;
                }
            } else if (slot == 4) {
                if (matrix[slot] == null || !"SCULK_CATALYST".equals(matrix[slot].getType().name())) {
                    return false;
                }
            } else if (matrix[slot] == null || !"DRAGON_HEAD".equals(matrix[slot].getType().name())) {
                return false;
            }
        }
        return true;
    }

    private void empowerDragon(EnderDragon dragon) {
        dragon.getPersistentDataContainer().set(empoweredDragonKey, PersistentDataType.BYTE, (byte) 1);
        int empowermentCount = playerData.getInt("dragon.empowerment-count", 0) + 1;
        playerData.set("dragon.empowerment-count", empowermentCount);
        if (empowermentCount % 15 == 0) {
            playerData.set("dragon.guaranteed-chestplates",
                    playerData.getInt("dragon.guaranteed-chestplates", 0) + 1);
        }
        savePlayerData();
        AttributeInstance healthAttribute = dragon.getAttribute(maxHealthAttribute);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(healthAttribute.getBaseValue() * 10.0);
            dragon.setHealth(healthAttribute.getValue());
        }
        dragon.getBossBar().setColor(BarColor.RED);
        dragon.setPhase(EnderDragon.Phase.CIRCLING);
        regenerateEndCrystals(dragon.getWorld());
    }

    private void regenerateEndCrystals(org.bukkit.World world) {
        Set<Long> towerTops = new HashSet<>();
        for (int x = -60; x <= 60; x++) {
            for (int z = -60; z <= 60; z++) {
                int distanceSquared = x * x + z * z;
                if (distanceSquared < 625 || distanceSquared > 3600) {
                    continue;
                }
                org.bukkit.block.Block highest = world.getHighestBlockAt(x, z);
                if (highest.getY() >= 70 && "OBSIDIAN".equals(highest.getType().name())) {
                    towerTops.add(packCoordinates(x, z));
                }
            }
        }

        while (!towerTops.isEmpty()) {
            long start = towerTops.iterator().next();
            ArrayDeque<Long> pending = new ArrayDeque<>();
            pending.add(start);
            towerTops.remove(start);
            int count = 0;
            int totalX = 0;
            int totalY = 0;
            int totalZ = 0;
            while (!pending.isEmpty()) {
                long packed = pending.removeFirst();
                int x = unpackX(packed);
                int z = unpackZ(packed);
                count++;
                totalX += x;
                totalY += world.getHighestBlockAt(x, z).getY();
                totalZ += z;
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                        long neighbor = packCoordinates(x + offsetX, z + offsetZ);
                        if ((offsetX != 0 || offsetZ != 0) && towerTops.remove(neighbor)) {
                            pending.addLast(neighbor);
                        }
                    }
                }
            }
            if (count < 4 || count > 100) {
                continue;
            }

            int centerX = (int) Math.round(totalX / (double) count);
            int centerY = (int) Math.round(totalY / (double) count);
            int centerZ = (int) Math.round(totalZ / (double) count);
            Location crystalLocation = new Location(world, centerX + 0.5, centerY + 1.0, centerZ + 0.5);
            boolean crystalExists = world.getNearbyEntities(crystalLocation, 4.0, 6.0, 4.0)
                    .stream()
                    .anyMatch(EnderCrystal.class::isInstance);
            if (!crystalExists) {
                world.spawn(crystalLocation, EnderCrystal.class, crystal -> crystal.setShowingBottom(false));
            }
        }
    }

    private long packCoordinates(int x, int z) {
        return ((long) x << 32) | (z & 0xffffffffL);
    }

    private int unpackX(long packed) {
        return (int) (packed >> 32);
    }

    private int unpackZ(long packed) {
        return (int) packed;
    }

    private void refreshLocatePermission(Player player) {
        UUID playerId = player.getUniqueId();
        PermissionAttachment attachment = locatePermissionAttachments.get(playerId);
        if (hasDragonChestplateInInventory(player)) {
            if (attachment == null) {
                attachment = player.addAttachment(this);
                locatePermissionAttachments.put(playerId, attachment);
            }
            attachment.setPermission("minecraft.command.locate", true);
        } else if (attachment != null) {
            player.removeAttachment(attachment);
            locatePermissionAttachments.remove(playerId);
        }
    }

    private boolean hasDragonChestplateInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isDragonChestplate(item)) {
                return true;
            }
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (isDragonChestplate(item)) {
                return true;
            }
        }
        return isDragonChestplate(player.getInventory().getItemInOffHand());
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

    private ItemStack createWitherArmor(String materialName, String pieceName, boolean boots) {
        ItemStack armor = XMaterial.matchXMaterial(materialName)
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException(materialName + " is unavailable"));
        ItemMeta meta = armor.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "The Wither's Netherite " + pieceName);
        meta.getPersistentDataContainer().set(witherArmorKey, PersistentDataType.BYTE, (byte) 1);
        XEnchantment.matchXEnchantment("PROTECTION")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
        if (boots) {
            meta.setUnbreakable(true);
            meta.getPersistentDataContainer().set(witherBootsKey, PersistentDataType.BYTE, (byte) 1);
            XEnchantment.matchXEnchantment("DEPTH_STRIDER")
                    .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
            XEnchantment.matchXEnchantment("FEATHER_FALLING")
                    .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
            XEnchantment.matchXEnchantment("SOUL_SPEED")
                    .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
        }
        armor.setItemMeta(meta);
        return armor;
    }

    private ItemStack createDragonArmor(String materialName, String pieceName, boolean chestplate) {
        ItemStack armor = XMaterial.matchXMaterial(materialName)
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException(materialName + " is unavailable"));
        ItemMeta meta = armor.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "The Ender Dragon's Netherite " + pieceName);
        meta.getPersistentDataContainer().set(dragonArmorKey, PersistentDataType.BYTE, (byte) 1);
        if (chestplate) {
            meta.getPersistentDataContainer().set(dragonChestplateKey, PersistentDataType.BYTE, (byte) 1);
        }
        XEnchantment.matchXEnchantment("PROTECTION")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
        armor.setItemMeta(meta);
        return armor;
    }

    private ItemStack createElderGuardianArmor(String materialName, String pieceName, boolean helmet) {
        ItemStack armor = XMaterial.matchXMaterial(materialName)
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException(materialName + " is unavailable"));
        ItemMeta meta = armor.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "The Elder Guardian's Netherite " + pieceName);
        meta.getPersistentDataContainer().set(elderGuardianArmorKey, PersistentDataType.BYTE, (byte) 1);
        XEnchantment.matchXEnchantment("PROTECTION")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
        if (helmet) {
            meta.setUnbreakable(true);
            meta.getPersistentDataContainer().set(elderGuardianHelmetKey, PersistentDataType.BYTE, (byte) 1);
            XEnchantment.matchXEnchantment("THORNS")
                    .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
            XEnchantment.matchXEnchantment("RESPIRATION")
                    .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 6, true));
            XEnchantment.matchXEnchantment("AQUA_AFFINITY")
                    .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 1, true));
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

    private boolean isWitherBoots(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(witherBootsKey, PersistentDataType.BYTE);
    }

    private boolean isDragonChestplate(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(dragonChestplateKey, PersistentDataType.BYTE);
    }

    private boolean isElderGuardianHelmet(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(elderGuardianHelmetKey, PersistentDataType.BYTE);
    }

    private boolean isSoulFragment(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(soulFragmentKey, PersistentDataType.BYTE);
    }

    private boolean isSoulTable(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(soulTableKey, PersistentDataType.BYTE);
    }

    private boolean isSoulMace(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(soulMaceKey, PersistentDataType.BYTE);
    }

    private boolean isHeartRemover(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(heartRemoverKey, PersistentDataType.BYTE);
    }

    private boolean isDemonicInitiator(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(demonicInitiatorKey, PersistentDataType.BYTE);
    }

    private boolean isEmpoweredDragon(EnderDragon dragon) {
        return dragon.getPersistentDataContainer().has(empoweredDragonKey, PersistentDataType.BYTE);
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

    private final class SoulTableHolder implements InventoryHolder {
        private final ItemStack[] grid;
        private final int page;
        private Inventory inventory;
        private boolean transitioning;

        private SoulTableHolder(ItemStack[] grid, int page) {
            this.grid = grid;
            this.page = page;
        }

        private int gridSlot(int inventorySlot) {
            if (page == 0 && inventorySlot >= 0 && inventorySlot < 45) {
                return inventorySlot;
            }
            if (page == 1 && inventorySlot >= 0 && inventorySlot < 36) {
                return inventorySlot + 45;
            }
            return -1;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}