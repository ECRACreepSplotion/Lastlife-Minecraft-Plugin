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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.boss.BarColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.Keyed;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public final class LastHeartPlugin extends JavaPlugin implements Listener, CommandExecutor {
    private static final int MAX_HEARTS = 10;
    private static final String BAN_REASON = "You lost all 10 hearts.";

    private File playerDataFile;
    private FileConfiguration playerData;
    private File naturalLootLocationsFile;
    private FileConfiguration naturalLootLocations;
    private File marketDataFile;
    private FileConfiguration marketData;
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
    private NamespacedKey empoweredWitherKey;
    private NamespacedKey empoweredWardenKey;
    private NamespacedKey empoweredStrengthPotionKey;
    private NamespacedKey empoweredSpeedPotionKey;
    private NamespacedKey empoweredInstantHealthPotionKey;
    private NamespacedKey empoweredBottleKey;
    private NamespacedKey empoweredSpearKey;
    private NamespacedKey dragonArmorKey;
    private NamespacedKey dragonChestplateKey;
    private NamespacedKey elderGuardianArmorKey;
    private NamespacedKey elderGuardianHelmetKey;
    private NamespacedKey soulFragmentKey;
    private NamespacedKey lastLifeRootAdvancementKey;
    private NamespacedKey soulGatheringAdvancementKey;
    private NamespacedKey riskyResetAdvancementKey;
    private NamespacedKey octopiAdvancementKey;
    private NamespacedKey backToSquareOneAdvancementKey;
    private NamespacedKey theTotemTotemAdvancementKey;
    private NamespacedKey shhhAdvancementKey;
    private NamespacedKey turningTablesAdvancementKey;
    private NamespacedKey bringMeTheirSoulsAdvancementKey;
    private NamespacedKey aHerosRewardAdvancementKey;
    private NamespacedKey wearingTheFallensFleshAdvancementKey;
    private NamespacedKey riseAdvancementKey;
    private NamespacedKey decayingAdvancementKey;
    private NamespacedKey oceanManAdvancementKey;
    private NamespacedKey hereWeGoAgainAdvancementKey;
    private NamespacedKey deathIsAtTheDoorAdvancementKey;
    private NamespacedKey summoningLuciferAdvancementKey;
    private NamespacedKey wasntSoHardAdvancementKey;
    private NamespacedKey psstHesOverThereAdvancementKey;
    private NamespacedKey feelDizzyAdvancementKey;
    private NamespacedKey iHateYouAdvancementKey;
    private NamespacedKey heartRemoverKey;
    private NamespacedKey heartRemoverRecipeKey;
    private NamespacedKey soulTableKey;
    private NamespacedKey soulTableRecipeKey;
    private NamespacedKey soulMaceKey;
    private NamespacedKey deathsHeadKey;
    private final Map<UUID, PermissionAttachment> locatePermissionAttachments = new HashMap<>();
    private final Map<UUID, PendingMarketPurchase> pendingMarketPurchases = new ConcurrentHashMap<>();
    private final Map<UUID, PendingMarketEdit> pendingMarketEdits = new ConcurrentHashMap<>();
    private final Map<UUID, String> marketEditStockSelections = new ConcurrentHashMap<>();
    private final Map<UUID, MarketType> marketEditMarketSelections = new ConcurrentHashMap<>();
    private final Set<UUID> marketEditSessions = ConcurrentHashMap.newKeySet();
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
        empoweredWitherKey = new NamespacedKey(this, "empowered_wither");
        empoweredWardenKey = new NamespacedKey(this, "empowered_warden");
        empoweredStrengthPotionKey = new NamespacedKey(this, "empowered_strength_potion");
        empoweredSpeedPotionKey = new NamespacedKey(this, "empowered_speed_potion");
        empoweredInstantHealthPotionKey = new NamespacedKey(this, "empowered_instant_health_potion");
        empoweredBottleKey = new NamespacedKey(this, "empowered_bottle");
        empoweredSpearKey = new NamespacedKey(this, "empowered_spear");
        dragonArmorKey = new NamespacedKey(this, "dragon_armor");
        dragonChestplateKey = new NamespacedKey(this, "dragon_chestplate");
        elderGuardianArmorKey = new NamespacedKey(this, "elder_guardian_armor");
        elderGuardianHelmetKey = new NamespacedKey(this, "elder_guardian_helmet");
        soulFragmentKey = new NamespacedKey(this, "soul_fragment");
        lastLifeRootAdvancementKey = new NamespacedKey(this, "lastlife_root");
        soulGatheringAdvancementKey = new NamespacedKey(this, "soul_gathering");
        riskyResetAdvancementKey = new NamespacedKey(this, "risky_reset");
        octopiAdvancementKey = new NamespacedKey(this, "octopi");
        backToSquareOneAdvancementKey = new NamespacedKey(this, "back_to_square_one");
        theTotemTotemAdvancementKey = new NamespacedKey(this, "the_totem_totem");
        shhhAdvancementKey = new NamespacedKey(this, "shhh");
        turningTablesAdvancementKey = new NamespacedKey(this, "turning_tables");
        bringMeTheirSoulsAdvancementKey = new NamespacedKey(this, "bring_me_their_souls");
        aHerosRewardAdvancementKey = new NamespacedKey(this, "a_heros_reward");
        wearingTheFallensFleshAdvancementKey = new NamespacedKey(this, "wearing_the_fallens_flesh");
        riseAdvancementKey = new NamespacedKey(this, "rise");
        decayingAdvancementKey = new NamespacedKey(this, "decaying");
        oceanManAdvancementKey = new NamespacedKey(this, "ocean_man");
        hereWeGoAgainAdvancementKey = new NamespacedKey(this, "here_we_go_again");
        deathIsAtTheDoorAdvancementKey = new NamespacedKey(this, "death_is_at_the_door");
        summoningLuciferAdvancementKey = new NamespacedKey(this, "summoning_lucifer");
        wasntSoHardAdvancementKey = new NamespacedKey(this, "wasnt_so_hard");
        psstHesOverThereAdvancementKey = new NamespacedKey(this, "psst_hes_over_there");
        feelDizzyAdvancementKey = new NamespacedKey(this, "i_feel_dizzy");
        iHateYouAdvancementKey = new NamespacedKey(this, "i_hate_you");
        heartRemoverKey = new NamespacedKey(this, "heart_remover");
        heartRemoverRecipeKey = new NamespacedKey(this, "heart_remover");
        soulTableKey = new NamespacedKey(this, "soul_table");
        soulTableRecipeKey = new NamespacedKey(this, "soul_table_recipe");
        soulMaceKey = new NamespacedKey(this, "soul_mace");
        deathsHeadKey = new NamespacedKey(this, "deaths_head");
        registerCustomItemFactories();
        registerAdvancement(lastLifeRootAdvancementKey, "advancements/lastlife_root.json");
        registerAdvancement(soulGatheringAdvancementKey, "advancements/soul_gathering.json");
        registerAdvancement(riskyResetAdvancementKey, "advancements/risky_reset.json");
        registerAdvancement(octopiAdvancementKey, "advancements/octopi.json");
        registerAdvancement(backToSquareOneAdvancementKey, "advancements/back_to_square_one.json");
        registerAdvancement(theTotemTotemAdvancementKey, "advancements/the_totem_totem.json");
        registerAdvancement(shhhAdvancementKey, "advancements/shhh.json");
        registerAdvancement(turningTablesAdvancementKey, "advancements/turning_tables.json");
        registerAdvancement(bringMeTheirSoulsAdvancementKey, "advancements/bring_me_their_souls.json");
        registerAdvancement(aHerosRewardAdvancementKey, "advancements/a_heros_reward.json");
        registerAdvancement(wearingTheFallensFleshAdvancementKey,
                "advancements/wearing_the_fallens_flesh.json");
        registerAdvancement(riseAdvancementKey, "advancements/rise.json");
        registerAdvancement(decayingAdvancementKey, "advancements/decaying.json");
        registerAdvancement(oceanManAdvancementKey, "advancements/ocean_man.json");
        registerAdvancement(hereWeGoAgainAdvancementKey, "advancements/here_we_go_again.json");
        registerAdvancement(deathIsAtTheDoorAdvancementKey,
                "advancements/death_is_at_the_door.json");
        registerAdvancement(summoningLuciferAdvancementKey,
                "advancements/summoning_lucifer.json");
        registerAdvancement(wasntSoHardAdvancementKey,
                "advancements/wasnt_so_hard.json");
        registerAdvancement(psstHesOverThereAdvancementKey,
                "advancements/psst_hes_over_there.json");
        registerAdvancement(feelDizzyAdvancementKey, "advancements/i_feel_dizzy.json");
        registerAdvancement(iHateYouAdvancementKey, "advancements/i_hate_you.json");

        playerDataFile = new File(getDataFolder(), "players.yml");
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            throw new IllegalStateException("Could not create the plugin data folder");
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        naturalLootLocationsFile = new File(getDataFolder(), "natural-loot-locations.yml");
        naturalLootLocations = YamlConfiguration.loadConfiguration(naturalLootLocationsFile);
        saveDefaultConfig();
        marketDataFile = new File(getDataFolder(), "market.yml");
        marketData = YamlConfiguration.loadConfiguration(marketDataFile);
        initializeMarketStocks();
        initializeEnchantedAppleMarketStocks();
        registerDemonicInitiatorRecipe();
        registerHeartRemoverRecipe();
        registerSoulTableRecipe();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("heartunban").setExecutor(this);
        getCommand("sethearts").setExecutor(this);
        getCommand("checkhearts").setExecutor(this);
        getCommand("givecustom").setExecutor(this);
        getCommand("locateitem").setExecutor(this);
        getCommand("market").setExecutor(this);
        Bukkit.getScheduler().runTaskTimer(this, this::simulateMarketTrades, 20L, 20L);
        Bukkit.getScheduler().runTaskTimer(this,
                () -> Bukkit.getOnlinePlayers().forEach(player -> {
                    refreshWardenLeggingsEffects(player);
                    refreshWitherBootsEffects(player);
                    refreshEmpoweredSpearEffect(player);
                    refreshDragonChestplateEffects(player);
                    refreshElderGuardianHelmetEffects(player);
                    refreshLocatePermission(player);
                    refreshSoulGatheringAdvancement(player);
                    refreshRiskyResetAdvancement(player);
                    refreshBackToSquareOneAdvancement(player);
                    refreshTheTotemTotemAdvancement(player);
                    refreshShhhAdvancement(player);
                    refreshWearingTheFallensFleshAdvancement(player);
                    refreshRiseAdvancement(player);
                    refreshDecayingAdvancement(player);
                    refreshOceanManAdvancement(player);
                    refreshIHateYouAdvancement(player);
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
        if (naturalLootLocations != null) {
            saveNaturalLootLocations();
        }
        if (marketData != null) {
            saveMarketData();
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
        List<String> marketNotifications = marketData.getStringList(
                "players." + event.getPlayer().getUniqueId() + ".market-notifications");
        if (!marketNotifications.isEmpty()) {
            marketNotifications.forEach(event.getPlayer()::sendMessage);
            marketData.set("players." + event.getPlayer().getUniqueId() + ".market-notifications", null);
            saveMarketData();
        }
    }

    @EventHandler
    public void onMarketPurchaseChat(AsyncPlayerChatEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (pendingMarketEdits.containsKey(playerId)) {
            return;
        }
        PendingMarketPurchase pending = pendingMarketPurchases.get(playerId);
        if (pending == null) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();
        Bukkit.getScheduler().runTask(this, () -> {
            if (!pendingMarketPurchases.remove(playerId, pending)) {
                return;
            }
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
                return;
            }
            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage(ChatColor.YELLOW + "Market purchase cancelled.");
                openMarketStockDetail(player, pending.marketType, pending.stockId, pending.page);
                return;
            }
            try {
                int amount = Integer.parseInt(message.trim());
                if (amount < 1 || amount > 64) {
                    throw new NumberFormatException();
                }
                buyMarketShares(player, pending.marketType, pending.stockId, amount, pending.page);
            } catch (NumberFormatException exception) {
                player.sendMessage(ChatColor.RED + "Enter a whole number from 1 to 64, or type cancel.");
                openMarketStockDetail(player, pending.marketType, pending.stockId, pending.page);
            }
        });
    }

    @EventHandler
    public void onMarketEditChat(AsyncPlayerChatEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        PendingMarketEdit pending = pendingMarketEdits.get(playerId);
        String message = event.getMessage();
        boolean cancel = message.trim().equalsIgnoreCase("cancel confirm");
        boolean crash = message.trim().equalsIgnoreCase("crash this stock");
        if (crash && marketEditSessions.contains(playerId)
                && marketEditStockSelections.containsKey(playerId)) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(this, () -> {
                Player player = Bukkit.getPlayer(playerId);
                if (player == null || !marketEditSessions.contains(playerId)) {
                    return;
                }
                if (!player.hasPermission("lastlife.market.edit")) {
                    pendingMarketEdits.remove(playerId);
                    marketEditStockSelections.remove(playerId);
                    marketEditSessions.remove(playerId);
                    player.sendMessage(ChatColor.RED + "You no longer have permission to edit the market.");
                    return;
                }
                String stockId = marketEditStockSelections.get(playerId);
                MarketType marketType = marketEditMarketSelections.get(playerId);
                if (stockId == null || marketType == null
                        || !marketData.isConfigurationSection(marketStockPath(marketType, stockId))) {
                    pendingMarketEdits.remove(playerId);
                    marketEditStockSelections.remove(playerId);
                    marketEditMarketSelections.remove(playerId);
                    player.sendMessage(ChatColor.RED + "That stock is no longer available.");
                    openMarketStocks(player, marketType == null ? MarketType.GOLD : marketType, 0, true);
                    return;
                }
                String path = marketStockPath(marketType, stockId);
                String stockName = marketData.getString(path + ".name", stockId);
                double price = marketData.getDouble(path + ".price", 1.0);
                pendingMarketEdits.remove(playerId);
                marketEditStockSelections.remove(playerId);
                marketEditMarketSelections.remove(playerId);
                declareMarketStockBankrupt(marketType, stockId, price, 0.0);
                player.sendMessage(ChatColor.RED + "You manually crashed " + stockName
                        + ". The stock and all its shares have been removed.");
                openMarketStocks(player, marketType, 0, true);
            });
            return;
        }
        if (pending == null && !(cancel && marketEditSessions.contains(playerId))) {
            return;
        }

        event.setCancelled(true);
        Bukkit.getScheduler().runTask(this, () -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !marketEditSessions.contains(playerId)) {
                return;
            }
            if (cancel) {
                pendingMarketEdits.remove(playerId);
                marketEditStockSelections.remove(playerId);
                marketEditMarketSelections.remove(playerId);
                marketEditSessions.remove(playerId);
                player.sendMessage(ChatColor.YELLOW + "Market editing cancelled.");
                openMarketSelector(player);
                return;
            }
            if (pending == null || pendingMarketEdits.get(playerId) != pending) {
                return;
            }
            if (!player.hasPermission("lastlife.market.edit")) {
                pendingMarketEdits.remove(playerId);
                marketEditSessions.remove(playerId);
                player.sendMessage(ChatColor.RED + "You no longer have permission to edit the market.");
                return;
            }
            String stockPath = marketStockPath(pending.marketType, pending.stockId);
            if (!marketData.isConfigurationSection(stockPath)) {
                pendingMarketEdits.remove(playerId);
                player.sendMessage(ChatColor.RED + "That stock is no longer available.");
                openMarketStocks(player, pending.marketType, pending.page, true);
                return;
            }

            switch (pending.step) {
                case SHARE_PRICE -> {
                    try {
                        double price = Double.parseDouble(message.trim());
                        if (!Double.isFinite(price) || price <= 0.0) {
                            throw new NumberFormatException();
                        }
                        marketData.set(stockPath + ".price", price);
                        pendingMarketEdits.remove(playerId);
                        saveMarketData();
                        player.sendMessage(ChatColor.GREEN + "Share price set to "
                                + formatGoldPrice(price) + " " + pending.marketType.currencyName + ".");
                        openMarketStockEditOptions(player, pending.marketType, pending.stockId, pending.page);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(ChatColor.RED + "Enter a positive number for the share price, or type cancel confirm.");
                    }
                }
                case BUY_TAX -> {
                    try {
                        double taxPercent = Double.parseDouble(message.trim());
                        if (!Double.isFinite(taxPercent) || taxPercent < 0.0) {
                            throw new NumberFormatException();
                        }
                        marketData.set(stockPath + ".tax_percent", taxPercent);
                        pendingMarketEdits.remove(playerId);
                        saveMarketData();
                        player.sendMessage(ChatColor.GREEN + "Buy tax set to "
                                + formatGoldPrice(taxPercent) + "%.");
                        openMarketStockEditOptions(player, pending.marketType, pending.stockId, pending.page);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(ChatColor.RED + "Enter a non-negative number for the buy tax, or type cancel confirm.");
                    }
                }
                case PLAYER_NAME -> {
                    OfflinePlayer target = findPlayer(message.trim());
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "That player has not joined this server before. Enter another username, or type cancel confirm.");
                        return;
                    }
                    String targetName = target.getName() == null ? message.trim() : target.getName();
                    pendingMarketEdits.put(playerId, new PendingMarketEdit(pending.marketType, pending.stockId,
                            pending.page, MarketEditStep.SHARE_AMOUNT, target.getUniqueId(), targetName));
                    player.sendMessage(ChatColor.YELLOW + "Enter the number of shares " + targetName
                            + " should own (0 or greater), or type cancel confirm.");
                }
                case SHARE_AMOUNT -> {
                    try {
                        int amount = Integer.parseInt(message.trim());
                        if (amount < 0 || pending.targetPlayerId == null) {
                            throw new NumberFormatException();
                        }
                        marketData.set(marketHoldingPath(pending.marketType,
                                pending.targetPlayerId, pending.stockId), amount);
                        String lotsPath = marketShareLotsPath(pending.marketType,
                                pending.targetPlayerId, pending.stockId);
                        if (amount == 0) {
                            marketData.set(lotsPath, null);
                        } else {
                            double price = marketData.getDouble(stockPath + ".price", 1.0);
                            marketData.set(lotsPath, List.of(amount + "|" + Double.toString(price)
                                    + "|" + Double.toString(price * amount)));
                        }
                        pendingMarketEdits.remove(playerId);
                        saveMarketData();
                        player.sendMessage(ChatColor.GREEN + pending.targetPlayerName + " now owns "
                                + amount + " share(s) of "
                                + marketData.getString(stockPath + ".name", pending.stockId) + ".");
                        openMarketStockEditOptions(player, pending.marketType, pending.stockId, pending.page);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(ChatColor.RED + "Enter a whole number of 0 or greater, or type cancel confirm.");
                    }
                }
            }
        });
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
        if (!(event.getEntity() instanceof Warden warden)) {
            return;
        }

        if (isEmpoweredWarden(warden) && warden.getKiller() != null) {
            awardAdvancement(warden.getKiller(), psstHesOverThereAdvancementKey);
        }
        if (ThreadLocalRandom.current().nextDouble() >= 0.15) {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBossDropAdvancement(EntityDeathEvent event) {
        LivingEntity boss = event.getEntity();
        if (!(boss instanceof Warden) && !(boss instanceof Wither)
                && !(boss instanceof EnderDragon) && !(boss instanceof ElderGuardian)) {
            return;
        }

        Player killer = boss.getKiller();
        if (killer == null) {
            return;
        }
        for (ItemStack drop : event.getDrops()) {
            if (isCustomBossArmor(drop)) {
                awardAdvancement(killer, aHerosRewardAdvancementKey);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStructureLootGenerate(LootGenerateEvent event) {
        String lootTable = event.getLootTable().getKey().getKey();
        boolean bastionChest = lootTable.startsWith("chests/bastion/")
                || lootTable.startsWith("chests/bastion_");
        double chance = bastionChest ? 1.0 : getSoulFragmentDropChance(lootTable);
        if (chance > 0.0 && ThreadLocalRandom.current().nextDouble() < chance) {
            event.getLoot().add(createSoulFragment());
        }
        if ("chests/ruined_portal".equals(lootTable)
                && ThreadLocalRandom.current().nextDouble() < 0.02) {
            event.getLoot().add(createHeartRemover());
        }
        if (bastionChest) {
            if (isBastionLootTable(lootTable, "treasure")) {
                event.getLoot().add(createDemonicInitiator());
            }
            double heartRemoverChance = isBastionLootTable(lootTable, "bridge", "other")
                    ? 0.05
                    : isBastionLootTable(lootTable, "treasure", "hoglin_stable") ? 0.15 : 0.0;
            if (ThreadLocalRandom.current().nextDouble() < heartRemoverChance) {
                event.getLoot().add(createHeartRemover());
            }
        }
        recordGeneratedLootLocations(event);
    }

    private void recordGeneratedLootLocations(LootGenerateEvent event) {
        InventoryHolder holder = event.getInventoryHolder();
        if (holder == null) {
            return;
        }

        Location location = holder instanceof BlockState blockState
                ? blockState.getLocation()
                : holder.getInventory().getLocation();
        if (location == null || location.getWorld() == null) {
            return;
        }

        String serializedLocation = location.getWorld().getUID() + ";"
                + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
        boolean updated = false;
        for (ItemStack item : event.getLoot()) {
            String itemKey = getNaturalLootItemKey(item);
            if (itemKey == null) {
                continue;
            }
            String path = "containers." + itemKey;
            List<String> locations = naturalLootLocations.getStringList(path);
            if (!locations.contains(serializedLocation)) {
                locations.add(serializedLocation);
                naturalLootLocations.set(path, locations);
                updated = true;
            }
        }
        if (updated) {
            saveNaturalLootLocations();
        }
    }

    private String getNaturalLootItemKey(ItemStack item) {
        if (isSoulFragment(item)) {
            return "soul_fragment";
        }
        if (isDemonicInitiator(item)) {
            return "demonic_initiator";
        }
        if (isHeartRemover(item)) {
            return "heart_remover";
        }
        return null;
    }

    private String getNaturalLootItemKey(String itemName) {
        return switch (normalizeCustomItemName(itemName)) {
            case "soulfragment", "soulfragments" -> "soul_fragment";
            case "demonicinitiator" -> "demonic_initiator";
            case "heartremover" -> "heart_remover";
            default -> null;
        };
    }

    private boolean isNaturalLootItem(ItemStack item, String itemKey) {
        return switch (itemKey) {
            case "soul_fragment" -> isSoulFragment(item);
            case "demonic_initiator" -> isDemonicInitiator(item);
            case "heart_remover" -> isHeartRemover(item);
            default -> false;
        };
    }

    private String getNaturalLootItemName(String itemKey) {
        return switch (itemKey) {
            case "soul_fragment" -> "Soul Fragment";
            case "demonic_initiator" -> "Demonic Initiator";
            case "heart_remover" -> "Heart Remover";
            default -> itemKey;
        };
    }

    private void saveNaturalLootLocations() {
        try {
            naturalLootLocations.save(naturalLootLocationsFile);
        } catch (IOException exception) {
            getLogger().severe("Could not save natural loot locations: " + exception.getMessage());
        }
    }

    private double getSoulFragmentDropChance(String lootTable) {
        if ("chests/ruined_portal".equals(lootTable)) {
            return 1.0;
        }
        if ("chests/buried_treasure".equals(lootTable)) {
            return 0.85;
        }
        if (lootTable.startsWith("chests/shipwreck_")) {
            return 0.60;
        }
        if ("chests/jungle_temple".equals(lootTable)) {
            return 1.0;
        }
        if ("chests/pillager_outpost".equals(lootTable)) {
            return 0.45;
        }
        if ("chests/desert_pyramid".equals(lootTable)) {
            return 0.50;
        }
        if (lootTable.startsWith("chests/village/")) {
            return 0.25;
        }
        return 0.0;
    }

    private boolean isBastionLootTable(String lootTable, String... types) {
        for (String type : types) {
            if (lootTable.equals("chests/bastion/" + type)
                    || lootTable.equals("chests/bastion_" + type)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onWitherDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Wither wither)) {
            return;
        }

        if (isEmpoweredWither(wither)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            boolean droppedAny = false;
            if (ThreadLocalRandom.current().nextDouble() < 0.20) {
                event.getDrops().add(createEmpoweredStrengthPotion());
                droppedAny = true;
            }
            if (ThreadLocalRandom.current().nextDouble() < 0.20) {
                event.getDrops().add(createEmpoweredSpeedPotion());
                droppedAny = true;
            }
            if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                event.getDrops().add(createEmpoweredInstantHealthPotion());
                droppedAny = true;
            }
            if (ThreadLocalRandom.current().nextDouble() < 0.30) {
                event.getDrops().add(createEmpoweredBottle());
                droppedAny = true;
            }
            if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                event.getDrops().add(createEmpoweredSpear());
                droppedAny = true;
            }
            if (!droppedAny) {
                int fallbackRoll = ThreadLocalRandom.current().nextInt(100);
                if (fallbackRoll < 20) {
                    event.getDrops().add(createEmpoweredStrengthPotion());
                } else if (fallbackRoll < 40) {
                    event.getDrops().add(createEmpoweredSpeedPotion());
                } else if (fallbackRoll < 65) {
                    event.getDrops().add(createEmpoweredInstantHealthPotion());
                } else if (fallbackRoll < 95) {
                    event.getDrops().add(createEmpoweredBottle());
                } else {
                    event.getDrops().add(createEmpoweredSpear());
                }
            }
            Player killer = wither.getKiller();
            if (killer != null) {
                awardAdvancement(killer, wasntSoHardAdvancementKey);
            }
            Location deathLocation = wither.getLocation();
            Block ground = wither.getWorld().getHighestBlockAt(
                    deathLocation.getBlockX(), deathLocation.getBlockZ());
            Location wardenLocation = ground.getLocation().add(0.5, 1.0, 0.5);
            if (wither.getWorld().spawnEntity(wardenLocation, EntityType.WARDEN) instanceof Warden warden) {
                warden.getPersistentDataContainer().set(empoweredWardenKey,
                        PersistentDataType.BYTE, (byte) 1);
            }
            return;
        }

        if (ThreadLocalRandom.current().nextDouble() >= 0.30) {
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

        Player killer = dragon.getKiller();
        if (killer != null) {
            awardAdvancement(killer, feelDizzyAdvancementKey);
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

    @EventHandler(ignoreCancelled = true)
    public void onEmpoweredWitherStructurePlace(BlockPlaceEvent event) {
        if (isDeathsHead(event.getItemInHand())
                && event.getBlockPlaced().getState() instanceof Skull skull) {
            skull.getPersistentDataContainer().set(deathsHeadKey, PersistentDataType.BYTE, (byte) 1);
            skull.update(true, false);
        }

        Block[] structure = findEmpoweredWitherStructure(event.getBlockPlaced());
        if (structure == null) {
            return;
        }

        Material air = XMaterial.matchXMaterial("AIR")
                .map(XMaterial::parseMaterial)
                .orElseThrow(() -> new IllegalStateException("Air is unavailable"));
        Location spawnLocation = structure[1].getLocation().add(0.5, 2.0, 0.5);
        for (Block block : structure) {
            block.setType(air, false);
        }
        if (event.getBlockPlaced().getWorld().spawnEntity(spawnLocation, EntityType.WITHER)
                instanceof Wither wither) {
            empowerWither(wither);
            awardAdvancement(event.getPlayer(), summoningLuciferAdvancementKey);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEmpoweredBossDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player
                && event.getDamager() instanceof Wither wither
                && isEmpoweredWither(wither)) {
            ignoreResistance(event);
            event.setCancelled(true);
            int[] reductions = {20, 6, 6, 8};
            ItemStack[] armor = player.getInventory().getArmorContents();
            int reduction = 0;
            for (int slot = 0; slot < armor.length; slot++) {
                if (hasEnchantment(armor[slot], "BLAST_PROTECTION", 1)) {
                    reduction += reductions[slot];
                }
            }
            player.setHealth(Math.max(0.0, player.getHealth() - Math.max(8.0, 48.0 - reduction)));
            return;
        }

        if (event.getEntity() instanceof Player player
                && event.getDamager() instanceof Warden warden
                && isEmpoweredWarden(warden)) {
            ignoreResistance(event);
            if (hasFullProtectionFour(player)) {
                event.setDamage(18.0);
            } else {
                event.setDamage(1_000_000.0);
            }
            return;
        }

        if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Wither wither
                && isEmpoweredWither(wither)) {
            double damage = event.getDamage() * 3.0;
            if (event.getEntity() instanceof Player player) {
                damage -= empoweredWitherBlastProtectionReduction(player);
                event.setDamage(Math.max(0.0, damage));
                ignoreEmpoweredWitherMitigation(event);
            } else {
                event.setDamage(damage);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEmpoweredSpearDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || (event.getCause() != EntityDamageEvent.DamageCause.FALL
                && event.getCause() != EntityDamageEvent.DamageCause.FLY_INTO_WALL)) {
            return;
        }
        if (isEmpoweredSpear(player.getInventory().getItemInMainHand())
                || isEmpoweredSpear(player.getInventory().getItemInOffHand())) {
            event.setCancelled(true);
        }
    }

    private int empoweredWitherBlastProtectionReduction(Player player) {
        int[] reductions = {20, 6, 6, 8};
        ItemStack[] armor = player.getInventory().getArmorContents();
        int reduction = 0;
        for (int slot = 0; slot < armor.length; slot++) {
            if (hasEnchantment(armor[slot], "BLAST_PROTECTION", 1)) {
                reduction += reductions[slot];
            }
        }
        return reduction;
    }

    private void ignoreEmpoweredWitherMitigation(EntityDamageByEntityEvent event) {
        for (DamageModifier modifier : new DamageModifier[]{
                DamageModifier.HARD_HAT,
                DamageModifier.BLOCKING,
                DamageModifier.ARMOR,
                DamageModifier.RESISTANCE,
                DamageModifier.MAGIC
        }) {
            if (event.isApplicable(modifier)) {
                event.setDamage(modifier, 0.0);
            }
        }
    }

    private void ignoreResistance(EntityDamageByEntityEvent event) {
        if (event.isApplicable(DamageModifier.RESISTANCE)) {
            event.setDamage(DamageModifier.RESISTANCE, 0.0);
        }
    }

    private Block[] findEmpoweredWitherStructure(Block placed) {
        for (int baseY = placed.getY() + 1; baseY >= placed.getY() - 1; baseY--) {
            for (int baseX = placed.getX() - 1; baseX <= placed.getX() + 1; baseX++) {
                for (int baseZ = placed.getZ() - 1; baseZ <= placed.getZ() + 1; baseZ++) {
                    Block center = placed.getWorld().getBlockAt(baseX, baseY, baseZ);
                    for (boolean alongX : new boolean[]{true, false}) {
                        int dx = alongX ? 1 : 0;
                        int dz = alongX ? 0 : 1;
                        Block[] structure = {
                                center.getRelative(-dx, 0, -dz),
                                center,
                                center.getRelative(dx, 0, dz),
                                center.getRelative(0, -1, 0),
                                center.getRelative(-dx, 1, -dz),
                                center.getRelative(0, 1, 0),
                                center.getRelative(dx, 1, dz)
                        };
                        boolean matches = true;
                        for (int index = 0; index < 4; index++) {
                            if (!"NETHERITE_BLOCK".equals(structure[index].getType().name())) {
                                matches = false;
                                break;
                            }
                        }
                        for (int index = 4; matches && index < structure.length; index++) {
                            if (!isDeathsHeadBlock(structure[index])) {
                                matches = false;
                            }
                        }
                        if (matches) {
                            return structure;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void empowerWither(Wither wither) {
        wither.getPersistentDataContainer().set(empoweredWitherKey, PersistentDataType.BYTE, (byte) 1);
        double environmentMultiplier = switch (wither.getWorld().getEnvironment().name()) {
            case "NORMAL" -> 2.0;
            case "NETHER" -> 5.0;
            default -> 1.0;
        };
        AttributeInstance healthAttribute = wither.getAttribute(maxHealthAttribute);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(healthAttribute.getBaseValue() * 10.0 / environmentMultiplier);
            wither.setHealth(healthAttribute.getValue());
        }
        XAttribute.of("movement_speed")
                .map(XAttribute::get)
                .map(wither::getAttribute)
                .ifPresent(speedAttribute -> speedAttribute.setBaseValue(speedAttribute.getBaseValue() * 2.0));

        int[] taskId = new int[1];
        taskId[0] = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!wither.isValid() || wither.isDead()) {
                Bukkit.getScheduler().cancelTask(taskId[0]);
                return;
            }
            Player target = null;
            double closestDistance = Double.MAX_VALUE;
            for (Player candidate : wither.getWorld().getPlayers()) {
                if (candidate.isDead() || candidate.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
                    continue;
                }
                double distance = candidate.getLocation().distanceSquared(wither.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    target = candidate;
                }
            }
            wither.setTarget(target);
            if (target != null) {
                Vector direction = target.getLocation().toVector().subtract(wither.getLocation().toVector());
                if (direction.lengthSquared() > 0.01) {
                    wither.setVelocity(direction.normalize().multiply(0.25));
                }
            }
        }, 1L, 1L).getTaskId();
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
        if (event.getView().getTopInventory().getHolder() instanceof MarketMenuHolder holder) {
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
            if (holder.editMode && !player.hasPermission("lastlife.market.edit")) {
                pendingMarketEdits.remove(player.getUniqueId());
                marketEditSessions.remove(player.getUniqueId());
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "You no longer have permission to edit the market.");
                return;
            }
            if (holder.view == MarketView.MARKET_SELECT && event.getRawSlot() == 11) {
                openMarketStocks(player, MarketType.GOLD, 0, holder.editMode);
            } else if (holder.view == MarketView.MARKET_SELECT && event.getRawSlot() == 15) {
                openMarketStocks(player, MarketType.ENCHANTED_GOLDEN_APPLE, 0, holder.editMode);
            } else if (holder.view == MarketView.STOCK_SELECT) {
                if (event.getRawSlot() == 22) {
                    openMarketSelector(player, holder.editMode);
                } else if (event.getRawSlot() == 18 && holder.page > 0) {
                    openMarketStocks(player, holder.marketType, holder.page - 1, holder.editMode);
                } else if (event.getRawSlot() == 26
                        && (holder.page + 1) * 7 < getMarketStockIds(holder.marketType).size()) {
                    openMarketStocks(player, holder.marketType, holder.page + 1, holder.editMode);
                } else {
                    String stockId = holder.stockSlots.get(event.getRawSlot());
                    if (stockId != null) {
                        if (holder.editMode) {
                            openMarketStockEditOptions(player, holder.marketType, stockId, holder.page);
                        } else {
                            openMarketStockDetail(player, holder.marketType, stockId, holder.page);
                        }
                    }
                }
            } else if (holder.view == MarketView.STOCK_EDIT_OPTIONS && holder.stockId != null) {
                if (event.getRawSlot() == 22) {
                    openMarketStocks(player, holder.marketType, holder.page, true);
                } else if (event.getRawSlot() == 11) {
                    promptMarketEdit(player, holder.marketType, holder.stockId, holder.page,
                            MarketEditStep.SHARE_PRICE,
                            "Enter the new share price in " + holder.marketType.currencyName + ".");
                } else if (event.getRawSlot() == 13) {
                    promptMarketEdit(player, holder.marketType, holder.stockId, holder.page, MarketEditStep.BUY_TAX,
                            "Enter the new buy tax percentage. Values above 100% are allowed.");
                } else if (event.getRawSlot() == 15) {
                    promptMarketEdit(player, holder.marketType, holder.stockId, holder.page,
                            MarketEditStep.PLAYER_NAME,
                            "Enter the username of a player who has joined this server before.");
                }
            } else if (holder.view == MarketView.STOCK_DETAIL && holder.stockId != null) {
                if (event.getRawSlot() == 22) {
                    openMarketStocks(player, holder.marketType, holder.page, false);
                } else {
                    switch (event.getRawSlot()) {
                        case 10 -> buyMarketShares(player, holder.marketType, holder.stockId, 1, holder.page);
                        case 11 -> buyMarketShares(player, holder.marketType, holder.stockId, 5, holder.page);
                        case 12 -> buyMarketShares(player, holder.marketType, holder.stockId, 10, holder.page);
                        case 13 -> buyMarketShares(player, holder.marketType, holder.stockId, 64, holder.page);
                        case 14 -> promptMarketShareAmount(player, holder.marketType, holder.stockId, holder.page);
                        case 15 -> sellMarketShares(player, holder.marketType, holder.stockId, 1, holder.page);
                        case 16 -> sellMarketShares(player, holder.marketType, holder.stockId, 5, holder.page);
                        case 17 -> sellMarketShares(player, holder.marketType, holder.stockId, 10, holder.page);
                        case 18 -> sellMarketShares(player, holder.marketType, holder.stockId, -1, holder.page);
                        default -> {
                        }
                    }
                }
            }
            return;
        }
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
                int deathsHeadRecipe = findDeathsHeadRecipe(holder.grid);
                if (deathsHeadRecipe >= 0) {
                    craftDeathsHead(player, holder, deathsHeadRecipe);
                } else {
                    craftSoulMace(player, holder);
                }
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
        if (event.getView().getTopInventory().getHolder() instanceof MarketMenuHolder) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot < event.getView().getTopInventory().getSize())) {
                event.setCancelled(true);
            }
            return;
        }
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
    public void onSmithSoulTable(SmithItemEvent event) {
        if (event.getWhoClicked() instanceof Player player && isSoulTable(event.getCurrentItem())) {
            awardAdvancement(player, turningTablesAdvancementKey);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        refreshMaxHealthNextTick(event.getPlayer());
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && isEmpoweredInstantHealthPotion(event.getItem())) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            consumeHeldItem(player, event.getHand());
            AttributeInstance maxHealth = player.getAttribute(maxHealthAttribute);
            if (maxHealth != null) {
                player.setHealth(maxHealth.getValue());
            }
            return;
        }
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && isEmpoweredBottle(event.getItem())) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            consumeHeldItem(player, event.getHand());
            boolean repairedItem = false;
            for (ItemStack item : player.getInventory().getContents()) {
                ItemMeta meta = item == null || item.getType().isAir() ? null : item.getItemMeta();
                if (!hasEnchantment(item, "MENDING", 1)
                        || !(meta instanceof Damageable damageable)
                        || damageable.getDamage() <= 0) {
                    continue;
                }
                damageable.setDamage(0);
                item.setItemMeta(meta);
                repairedItem = true;
            }
            if (!repairedItem) {
                player.setLevel(player.getLevel() + 50);
            }
            return;
        }
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
        awardAdvancement(player, hereWeGoAgainAdvancementKey);
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
        UUID playerId = event.getPlayer().getUniqueId();
        pendingMarketEdits.remove(playerId);
        marketEditStockSelections.remove(playerId);
        marketEditMarketSelections.remove(playerId);
        marketEditSessions.remove(playerId);
        PermissionAttachment attachment = locatePermissionAttachments.remove(playerId);
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
            case "market" -> {
                if (args.length == 0) {
                    openMarket(sender);
                } else if (args.length == 1 && args[0].equalsIgnoreCase("edit")) {
                    openMarketEdit(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /market [edit]");
                }
            }
            case "heartunban" -> unbanPlayer(sender, args);
            case "sethearts" -> setPlayerHearts(sender, args);
            case "checkhearts" -> checkPlayerHearts(sender, args);
            case "givecustom" -> giveCustomItem(sender, args);
            case "locateitem" -> locateItem(sender, args);
            default -> {
                return false;
            }
        }
        return true;
    }

    private void openMarket(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can open the market.");
            return;
        }

        marketEditSessions.remove(player.getUniqueId());
        pendingMarketEdits.remove(player.getUniqueId());

        if (!player.hasPermission("lastlife.market")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use the market.");
            return;
        }
        String permission = getConfig().getString("market.permission", "");
        if (permission != null && !permission.isBlank() && !player.hasPermission(permission)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use the market.");
            return;
        }
        openMarketSelector(player);
    }

    private void openMarketEdit(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can edit the market.");
            return;
        }
        if (!player.hasPermission("lastlife.market.edit")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to edit the market.");
            return;
        }
        pendingMarketPurchases.remove(player.getUniqueId());
        pendingMarketEdits.remove(player.getUniqueId());
        marketEditSessions.add(player.getUniqueId());
        openMarketSelector(player, true);
    }

    private void initializeMarketStocks() {
        ConfigurationSection stocks = marketData.getConfigurationSection("stocks");
        if (stocks != null && !stocks.getKeys(false).isEmpty()) {
            for (String stockId : stocks.getKeys(false)) {
                String taxPath = "stocks." + stockId + ".tax_percent";
                if (!marketData.contains(taxPath)) {
                    marketData.set(taxPath, generateGoldStockTaxPercent());
                }
            }
            saveMarketData();
            return;
        }
        for (int index = 0; index < 9; index++) {
            createRandomGoldStock();
        }
        saveMarketData();
    }

    private void initializeEnchantedAppleMarketStocks() {
        String stocksPath = MarketType.ENCHANTED_GOLDEN_APPLE.stockRoot;
        ConfigurationSection stocks = marketData.getConfigurationSection(stocksPath);
        if (stocks != null && !stocks.getKeys(false).isEmpty()) {
            for (String stockId : stocks.getKeys(false)) {
                String taxPath = marketStockPath(MarketType.ENCHANTED_GOLDEN_APPLE, stockId) + ".tax_percent";
                if (!marketData.contains(taxPath)) {
                    marketData.set(taxPath, generateEnchantedAppleStockTaxPercent());
                }
            }
            saveMarketData();
            return;
        }
        for (int index = 0; index < 9; index++) {
            createRandomEnchantedAppleStock();
        }
        saveMarketData();
    }

    private void createRandomGoldStock() {
        String[] prefixes = {"Auric", "Gilded", "Golden", "Crown", "Bullion", "Sunlit", "Midas", "Ore"};
        String[] suffixes = {"Holdings", "Exchange", "Mining", "Capital", "Reserve", "Industries", "Trading", "Works"};
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int index = marketData.getInt("next_stock_id", 1);
        String stockId;
        do {
            stockId = String.format(Locale.ROOT, "gold_%02d", index++);
        } while (marketData.isConfigurationSection("stocks." + stockId));
        marketData.set("next_stock_id", index);

        Set<String> names = new HashSet<>();
        for (String existingId : getGoldStockIds()) {
            names.add(marketData.getString("stocks." + existingId + ".name", existingId));
        }
        String baseName = prefixes[random.nextInt(prefixes.length)] + " "
                + suffixes[random.nextInt(suffixes.length)];
        String name = baseName;
        for (int suffix = 2; names.contains(name); suffix++) {
            name = baseName + " " + suffix;
        }

        marketData.set("stocks." + stockId + ".name", name);
        marketData.set("stocks." + stockId + ".price", random.nextInt(100, 1501) / 100.0);
        marketData.set("stocks." + stockId + ".gold_reserve", 0);
        int changeIntervalSeconds = random.nextInt(5, 121);
        marketData.set("stocks." + stockId + ".change_interval_seconds", changeIntervalSeconds);
        marketData.set("stocks." + stockId + ".next_change_at",
                System.currentTimeMillis() + changeIntervalSeconds * 1000L);
        marketData.set("stocks." + stockId + ".natural_change_scale", random.nextDouble(0.5, 1.51));
        marketData.set("stocks." + stockId + ".tax_percent", generateGoldStockTaxPercent());
    }

    private double generateGoldStockTaxPercent() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int roll = random.nextInt(1000);
        if (roll == 0) {
            return 100.0;
        }
        if (roll < 10) {
            return random.nextInt(501, 1000) / 10.0;
        }
        if (roll < 20) {
            return random.nextInt(201, 501) / 10.0;
        }
        if (roll < 40) {
            return random.nextInt(101, 201) / 10.0;
        }
        if (roll < 100) {
            return random.nextInt(51, 101) / 10.0;
        }
        return random.nextInt(1, 51) / 10.0;
    }

    private void createRandomEnchantedAppleStock() {
        String[] prefixes = {"Enchanted", "Golden", "Glistening", "Mystic", "Crimson", "Orchard"};
        String[] suffixes = {"Orchard", "Harvest", "Exchange", "Farms", "Foods", "Holdings"};
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int index = marketData.getInt("next_apple_stock_id", 1);
        String stockId;
        do {
            stockId = String.format(Locale.ROOT, "apple_%02d", index++);
        } while (marketData.isConfigurationSection(marketStockPath(
                MarketType.ENCHANTED_GOLDEN_APPLE, stockId)));
        marketData.set("next_apple_stock_id", index);

        Set<String> names = new HashSet<>();
        for (String existingId : getMarketStockIds(MarketType.ENCHANTED_GOLDEN_APPLE)) {
            names.add(marketData.getString(marketStockPath(
                    MarketType.ENCHANTED_GOLDEN_APPLE, existingId) + ".name", existingId));
        }
        String baseName = prefixes[random.nextInt(prefixes.length)] + " "
                + suffixes[random.nextInt(suffixes.length)];
        String name = baseName;
        for (int suffix = 2; names.contains(name); suffix++) {
            name = baseName + " " + suffix;
        }

        String path = marketStockPath(MarketType.ENCHANTED_GOLDEN_APPLE, stockId);
        marketData.set(path + ".name", name);
        marketData.set(path + ".price", random.nextInt(100, 1501) / 100.0);
        marketData.set(path + ".apple_reserve", 0);
        int changeIntervalSeconds = random.nextInt(5, 121);
        marketData.set(path + ".change_interval_seconds", changeIntervalSeconds);
        marketData.set(path + ".next_change_at", System.currentTimeMillis() + changeIntervalSeconds * 1000L);
        marketData.set(path + ".natural_change_scale", random.nextDouble(0.5, 1.51));
        marketData.set(path + ".tax_percent", generateEnchantedAppleStockTaxPercent());
    }

    private double generateEnchantedAppleStockTaxPercent() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int roll = random.nextInt(1000);
        if (roll == 0) {
            return random.nextInt(1, 50) / 10.0;
        }
        if (roll < 100) {
            return random.nextInt(51, 100) / 10.0;
        }
        if (roll < 900) {
            return random.nextInt(101, 200) / 10.0;
        }
        if (roll < 960) {
            return random.nextInt(201, 500) / 10.0;
        }
        if (roll < 980) {
            return random.nextInt(501, 750) / 10.0;
        }
        if (roll < 990) {
            return random.nextInt(751, 850) / 10.0;
        }
        if (roll < 999) {
            return random.nextInt(851, 1000) / 10.0;
        }
        return 100.0;
    }

    private void simulateMarketTrades() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long now = System.currentTimeMillis();
        for (MarketType marketType : MarketType.values()) {
            for (String stockId : getMarketStockIds(marketType)) {
                String path = marketStockPath(marketType, stockId);
                int changeIntervalSeconds = marketData.getInt(path + ".change_interval_seconds", 0);
                if (changeIntervalSeconds < 5) {
                    changeIntervalSeconds = random.nextInt(5, 121);
                    marketData.set(path + ".change_interval_seconds", changeIntervalSeconds);
                }
                long nextChangeAt = marketData.getLong(path + ".next_change_at", 0L);
                if (nextChangeAt <= 0L) {
                    marketData.set(path + ".next_change_at", now + changeIntervalSeconds * 1000L);
                    continue;
                }
                if (now < nextChangeAt) {
                    continue;
                }
                marketData.set(path + ".next_change_at", now + changeIntervalSeconds * 1000L);
                double currentPrice = marketData.getDouble(path + ".price", 1.0);
                double naturalChangeScale = marketData.getDouble(path + ".natural_change_scale", 0.0);
                if (naturalChangeScale <= 0.0) {
                    naturalChangeScale = random.nextDouble(0.5, 1.51);
                    marketData.set(path + ".natural_change_scale", naturalChangeScale);
                }
                double valueScale = Math.max(0.25, Math.min(2.0, currentPrice / 10.0));
                double changeScale = valueScale * naturalChangeScale;
                boolean fakePlayerBuys = random.nextBoolean();
                int fakeShares = random.nextInt(1, 51);
                double tradeImpact = Math.min(0.05, fakeShares * 0.001 * changeScale);
                double randomMarketMovement = random.nextDouble(-0.02, 0.02) * changeScale;
                double direction = fakePlayerBuys ? tradeImpact : -tradeImpact;
                updateMarketStockPrice(marketType,
                        stockId, currentPrice * (1.0 + direction + randomMarketMovement),
                        "fake:" + UUID.randomUUID());
            }
            if (random.nextInt(600) == 0) {
                if (marketType == MarketType.GOLD) {
                    createRandomGoldStock();
                } else {
                    createRandomEnchantedAppleStock();
                }
            }
        }
        saveMarketData();
    }

    private void updateMarketStockPrice(MarketType marketType, String stockId, double price, String traderId) {
        String path = marketStockPath(marketType, stockId);
        if (!marketData.isConfigurationSection(path)) {
            return;
        }
        double previousPrice = marketData.getDouble(path + ".price", 1.0);
        double boundedPrice = Math.max(0.25, Math.min(1_000_000.0, price));
        double updatedPrice = Math.round(boundedPrice * 100.0) / 100.0;
        marketData.set(path + ".price", updatedPrice);
        List<Double> recentTradePrices = marketData.getDoubleList(path + ".recent_trade_prices");
        recentTradePrices.add(updatedPrice);
        while (recentTradePrices.size() > 15) {
            recentTradePrices.remove(0);
        }
        marketData.set(path + ".recent_trade_prices", recentTradePrices);

        if (updatedPrice < previousPrice) {
            String baselinePath = path + ".crash_start_price";
            if (!marketData.contains(baselinePath)) {
                marketData.set(baselinePath, previousPrice);
            }
            List<String> crashActors = new ArrayList<>(marketData.getStringList(path + ".crash_actors"));
            if (traderId != null && !crashActors.contains(traderId)) {
                crashActors.add(traderId);
                marketData.set(path + ".crash_actors", crashActors);
            }
            double crashStartPrice = marketData.getDouble(baselinePath, previousPrice);
            if (crashActors.size() >= 3 && updatedPrice < crashStartPrice * 0.25) {
                declareMarketStockBankrupt(marketType, stockId, crashStartPrice, updatedPrice);
            }
        } else if (updatedPrice > previousPrice) {
            marketData.set(path + ".crash_start_price", null);
            marketData.set(path + ".crash_actors", null);
        }
    }

    private List<String> getGoldStockIds() {
        return getMarketStockIds(MarketType.GOLD);
    }

    private List<String> getMarketStockIds(MarketType marketType) {
        ConfigurationSection stocks = marketData.getConfigurationSection(marketType.stockRoot);
        if (stocks == null) {
            return List.of();
        }
        List<String> stockIds = new ArrayList<>(stocks.getKeys(false));
        stockIds.sort(String::compareTo);
        return stockIds;
    }

    private void declareMarketStockBankrupt(
            MarketType marketType, String stockId, double crashStartPrice, double finalPrice) {
        String path = marketStockPath(marketType, stockId);
        String stockName = marketData.getString(path + ".name", stockId);
        ConfigurationSection players = marketData.getConfigurationSection("players");
        if (players != null) {
            for (String playerId : new ArrayList<>(players.getKeys(false))) {
                String holdingPath = "players." + playerId + "." + marketType.holdingsKey + "." + stockId;
                int shares = marketData.getInt(holdingPath, 0);
                if (shares <= 0) {
                    continue;
                }
                marketData.set(holdingPath, null);
                marketData.set("players." + playerId + "." + marketType.shareLotsKey + "." + stockId, null);
                String notification = ChatColor.RED + "The " + marketType.displayName + " stock " + stockName
                        + " went bankrupt. Your " + shares
                        + " share(s) were lost, along with all " + marketType.currencyName
                        + " remaining in the stock.";
                try {
                    Player onlinePlayer = Bukkit.getPlayer(UUID.fromString(playerId));
                    if (onlinePlayer != null) {
                        onlinePlayer.sendMessage(notification);
                    } else {
                        String notificationsPath = "players." + playerId + ".market-notifications";
                        List<String> notifications = new ArrayList<>(marketData.getStringList(notificationsPath));
                        notifications.add(notification);
                        marketData.set(notificationsPath, notifications);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        getLogger().info(marketType.displayName + " stock " + stockName
                + " declared bankrupt after falling from " + formatGoldPrice(crashStartPrice)
                + " to " + formatGoldPrice(finalPrice) + " " + marketType.currencyName + ".");
        marketData.set(path, null);
        saveMarketData();
    }

    private void openMarketSelector(Player player) {
        openMarketSelector(player, false);
    }

    private void openMarketSelector(Player player, boolean editMode) {
        marketEditStockSelections.remove(player.getUniqueId());
        marketEditMarketSelections.remove(player.getUniqueId());
        MarketMenuHolder holder = new MarketMenuHolder(
                MarketView.MARKET_SELECT, null, 0, MarketType.GOLD, editMode);
        holder.inventory = Bukkit.createInventory(holder, 27, ChatColor.DARK_GREEN + "Markets");
        holder.inventory.setItem(11, createMarketItem("GOLD_INGOT", ChatColor.GOLD + "Gold Market",
                ChatColor.GRAY + "Trade shares priced in gold ingots.",
                ChatColor.YELLOW + "Click to browse stocks"));
        holder.inventory.setItem(15, createMarketItem("ENCHANTED_GOLDEN_APPLE",
                ChatColor.GOLD + "Enchanted Golden Apple Market",
                ChatColor.GRAY + "Trade shares priced in enchanted golden apples.",
                ChatColor.YELLOW + "Click to browse stocks"));
        player.openInventory(holder.inventory);
    }

    private void openGoldStocks(Player player) {
        openGoldStocks(player, 0);
    }

    private void openGoldStocks(Player player, int requestedPage) {
        openGoldStocks(player, requestedPage, false);
    }

    private void openGoldStocks(Player player, int requestedPage, boolean editMode) {
        openMarketStocks(player, MarketType.GOLD, requestedPage, editMode);
    }

    private void openMarketStocks(Player player, MarketType marketType, int requestedPage, boolean editMode) {
        marketEditStockSelections.remove(player.getUniqueId());
        marketEditMarketSelections.remove(player.getUniqueId());
        List<String> stockIds = getMarketStockIds(marketType);
        stockIds.sort((left, right) -> {
            boolean leftOwned = getMarketShares(player, marketType, left) > 0;
            boolean rightOwned = getMarketShares(player, marketType, right) > 0;
            if (leftOwned != rightOwned) {
                return leftOwned ? -1 : 1;
            }
            return left.compareTo(right);
        });
        int maxPage = Math.max(0, (stockIds.size() - 1) / 7);
        int page = Math.max(0, Math.min(requestedPage, maxPage));
        MarketMenuHolder holder = new MarketMenuHolder(
                MarketView.STOCK_SELECT, null, page, marketType, editMode);
        holder.inventory = Bukkit.createInventory(holder, 27,
                ChatColor.DARK_GREEN + marketType.displayName + " " + (page + 1) + "/" + (maxPage + 1));
        int[] slots = {10, 11, 12, 13, 14, 15, 16};
        int start = page * slots.length;
        for (int index = start; index < stockIds.size() && index < start + slots.length; index++) {
            String stockId = stockIds.get(index);
            String path = marketStockPath(marketType, stockId);
            int slot = slots[index - start];
            holder.stockSlots.put(slot, stockId);
            List<String> stockLore = new ArrayList<>();
            stockLore.add(ChatColor.GRAY + "Share price: "
                    + formatGoldPrice(marketData.getDouble(path + ".price", 1.0)) + " "
                    + marketType.currencyUnit);
            stockLore.add(ChatColor.GRAY + "Buy tax: "
                    + formatGoldPrice(marketData.getDouble(path + ".tax_percent", 0.1)) + "%");
            stockLore.add(ChatColor.GRAY + "Shares owned: " + getMarketShares(player, marketType, stockId));
            stockLore.addAll(getMarketStockPerformanceLore(marketType, stockId));
            stockLore.add(ChatColor.YELLOW + "Click to view this stock");
            holder.inventory.setItem(slot, createMarketItem(marketType.currencyItem,
                    ChatColor.GOLD + marketData.getString(path + ".name", stockId),
                    stockLore.toArray(new String[0])));
        }
        if (page > 0) {
            holder.inventory.setItem(18, createMarketItem("ARROW", ChatColor.YELLOW + "Previous page"));
        }
        if (page < maxPage) {
            holder.inventory.setItem(26, createMarketItem("ARROW", ChatColor.YELLOW + "Next page"));
        }
        holder.inventory.setItem(22, createMarketItem("BARRIER", ChatColor.YELLOW + "Back to markets"));
        player.openInventory(holder.inventory);
    }

    private void openGoldStockEditOptions(Player player, String stockId, int page) {
        openMarketStockEditOptions(player, MarketType.GOLD, stockId, page);
    }

    private void openMarketStockEditOptions(Player player, MarketType marketType, String stockId, int page) {
        String path = marketStockPath(marketType, stockId);
        if (!marketData.isConfigurationSection(path)) {
            openMarketStocks(player, marketType, page, true);
            return;
        }
        marketEditStockSelections.put(player.getUniqueId(), stockId);
        marketEditMarketSelections.put(player.getUniqueId(), marketType);
        String stockName = marketData.getString(path + ".name", stockId);
        MarketMenuHolder holder = new MarketMenuHolder(
                MarketView.STOCK_EDIT_OPTIONS, stockId, page, marketType, true);
        holder.inventory = Bukkit.createInventory(holder, 27, ChatColor.DARK_GREEN + "Edit " + stockName);
        holder.inventory.setItem(11, createMarketItem("EMERALD", ChatColor.GREEN + "Share Price",
                ChatColor.GRAY + "Set this stock's share price in chat."));
        holder.inventory.setItem(13, createMarketItem("GOLD_INGOT", ChatColor.GOLD + "Buy Tax",
                ChatColor.GRAY + "Set any non-negative tax percentage."));
        holder.inventory.setItem(15, createMarketItem("PAPER", ChatColor.YELLOW + "Shares Owned",
                ChatColor.GRAY + "Set a player's total shares in this stock."));
        holder.inventory.setItem(22, createMarketItem("ARROW",
                ChatColor.YELLOW + "Back to " + marketType.displayName + " stocks"));
        player.openInventory(holder.inventory);
    }

    private void promptMarketEdit(Player player, MarketType marketType, String stockId,
            int page, MarketEditStep step, String prompt) {
        pendingMarketEdits.put(player.getUniqueId(),
                new PendingMarketEdit(marketType, stockId, page, step, null, null));
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + prompt + " Type cancel confirm at any time to stop editing.");
    }

    private List<String> getGoldStockPerformanceLore(String stockId) {
        return getMarketStockPerformanceLore(MarketType.GOLD, stockId);
    }

    private List<String> getMarketStockPerformanceLore(MarketType marketType, String stockId) {
        String path = marketStockPath(marketType, stockId);
        List<Double> recentTradePrices = marketData.getDoubleList(path + ".recent_trade_prices");
        List<String> lore = new ArrayList<>();
        if (recentTradePrices.size() >= 2) {
            double previousPrice = recentTradePrices.get(recentTradePrices.size() - 2);
            double latestPrice = recentTradePrices.get(recentTradePrices.size() - 1);
            double changePercent = (latestPrice - previousPrice) / previousPrice * 100.0;
            ChatColor color = changePercent > 1.0 ? ChatColor.GREEN
                    : changePercent < -1.0 ? ChatColor.RED : ChatColor.YELLOW;
            String sign = changePercent > 0 ? "+" : "";
            lore.add(color + "Recent performance: " + sign
                    + String.format(Locale.US, "%.2f%%", changePercent));
        } else {
            lore.add(ChatColor.YELLOW + "Recent performance: not enough trades");
        }
        if (recentTradePrices.isEmpty()) {
            lore.add(ChatColor.GOLD + "15-trade average: unavailable");
        } else {
            double averagePrice = recentTradePrices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            lore.add(ChatColor.GOLD + "15-trade average: " + formatGoldPrice(averagePrice)
                    + " " + marketType.currencyName);
        }
        return lore;
    }

    private void openGoldStockDetail(Player player, String stockId) {
        openGoldStockDetail(player, stockId, 0);
    }

    private void openGoldStockDetail(Player player, String stockId, int page) {
        openMarketStockDetail(player, MarketType.GOLD, stockId, page);
    }

    private void openMarketStockDetail(Player player, MarketType marketType, String stockId, int page) {
        String path = marketStockPath(marketType, stockId);
        if (!marketData.isConfigurationSection(path)) {
            openMarketStocks(player, marketType, page, false);
            return;
        }

        double price = marketData.getDouble(path + ".price", 1.0);
        int ownedShares = getMarketShares(player, marketType, stockId);
        String stockName = marketData.getString(path + ".name", stockId);
        MarketMenuHolder holder = new MarketMenuHolder(
                MarketView.STOCK_DETAIL, stockId, page, marketType, false);
        holder.inventory = Bukkit.createInventory(holder, 27, ChatColor.DARK_GREEN + stockName);
        List<String> stockLore = new ArrayList<>();
        stockLore.add(ChatColor.GRAY + "Current price: " + formatGoldPrice(price) + " "
                + marketType.currencyUnit + " per share");
        double taxPercent = marketData.getDouble(path + ".tax_percent", 0.1);
        stockLore.add(ChatColor.GRAY + "Buy tax: " + formatGoldPrice(taxPercent) + "%");
        stockLore.add(ChatColor.GRAY + "You own: " + ownedShares + " share(s)");
        for (GoldShareLot lot : getMarketShareLots(player, marketType, stockId)) {
            stockLore.add(ChatColor.YELLOW + String.valueOf(lot.shares) + " share(s) bought at "
                    + formatGoldPrice(lot.price) + " " + marketType.currencyName + " per share");
            stockLore.add(ChatColor.GRAY + "Invested: " + formatGoldPrice(lot.cost) + " "
                    + marketType.currencyUnit);
        }
        holder.inventory.setItem(4, createMarketItem(marketType.currencyItem, ChatColor.GOLD + stockName,
                stockLore.toArray(new String[0])));

        int[] buySlots = {10, 11, 12, 13};
        int[] buyAmounts = {1, 5, 10, 64};
        for (int index = 0; index < buySlots.length; index++) {
            int amount = buyAmounts[index];
            double subtotal = price * amount;
            int cost = getGoldPurchaseCost(price, amount, taxPercent);
            holder.inventory.setItem(buySlots[index], createMarketItem("EMERALD",
                    ChatColor.GREEN + "Buy " + amount + " share(s)",
                    ChatColor.GRAY + "Subtotal: " + formatGoldPrice(subtotal) + " gold",
                    ChatColor.GRAY + "Tax rate: " + formatGoldPrice(taxPercent) + "%",
                    ChatColor.GREEN + "Total: " + cost + " " + marketType.currencyUnit));
        }
        holder.inventory.setItem(14, createMarketItem("PAPER", ChatColor.GREEN + "Buy a custom amount",
                ChatColor.GRAY + "Type a quantity from 1 to 64 in chat."));

        int[] sellSlots = {15, 16, 17};
        int[] sellAmounts = {1, 5, 10};
        for (int index = 0; index < sellSlots.length; index++) {
            int amount = sellAmounts[index];
            holder.inventory.setItem(sellSlots[index], createMarketItem(marketType.currencyItem,
                    ChatColor.YELLOW + "Sell " + amount + " share(s)",
                    getMarketSellLore(player, marketType, stockId, amount, price)));
        }
        holder.inventory.setItem(18, createMarketItem(marketType.currencyItem, ChatColor.YELLOW + "Sell all shares",
                getMarketSellLore(player, marketType, stockId, -1, price)));
        holder.inventory.setItem(22, createMarketItem("ARROW",
                ChatColor.YELLOW + "Back to " + marketType.displayName + " stocks"));
        player.openInventory(holder.inventory);
    }

    private ItemStack createMarketItem(String materialName, String displayName, String... lore) {
        ItemStack item = XMaterial.matchXMaterial(materialName)
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException(materialName + " is unavailable"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private String formatGoldPrice(double price) {
        return String.format(Locale.US, "%.2f", price);
    }

    private String marketStockPath(MarketType marketType, String stockId) {
        return marketType.stockRoot + "." + stockId;
    }

    private String marketHoldingPath(MarketType marketType, UUID playerId, String stockId) {
        return "players." + playerId + "." + marketType.holdingsKey + "." + stockId;
    }

    private String marketShareLotsPath(MarketType marketType, UUID playerId, String stockId) {
        return "players." + playerId + "." + marketType.shareLotsKey + "." + stockId;
    }

    private int getMarketShares(Player player, MarketType marketType, String stockId) {
        return Math.max(0, marketData.getInt(
                marketHoldingPath(marketType, player.getUniqueId(), stockId), 0));
    }

    private int getGoldShares(Player player, String stockId) {
        return getMarketShares(player, MarketType.GOLD, stockId);
    }

    private static final class GoldShareLot {
        private final int shares;
        private final double price;
        private final double cost;

        private GoldShareLot(int shares, double price, double cost) {
            this.shares = shares;
            this.price = price;
            this.cost = cost;
        }
    }

    private List<GoldShareLot> getGoldShareLots(Player player, String stockId) {
        return getMarketShareLots(player, MarketType.GOLD, stockId);
    }

    private List<GoldShareLot> getMarketShareLots(Player player, MarketType marketType, String stockId) {
        String lotsPath = marketShareLotsPath(marketType, player.getUniqueId(), stockId);
        List<GoldShareLot> lots = new ArrayList<>();
        for (String encodedLot : marketData.getStringList(lotsPath)) {
            String[] values = encodedLot.split("\\|", -1);
            if (values.length != 3) {
                continue;
            }
            try {
                int shares = Integer.parseInt(values[0]);
                double price = Double.parseDouble(values[1]);
                double cost = Double.parseDouble(values[2]);
                if (shares > 0) {
                    lots.add(new GoldShareLot(shares, price, cost));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        if (lots.isEmpty()) {
            int legacyShares = getMarketShares(player, marketType, stockId);
            if (legacyShares > 0) {
                double currentPrice = marketData.getDouble(marketStockPath(marketType, stockId) + ".price", 1.0);
                lots.add(new GoldShareLot(legacyShares, currentPrice, currentPrice * legacyShares));
                saveMarketShareLots(player, marketType, stockId, lots);
            }
        }
        return lots;
    }

    private void saveGoldShareLots(Player player, String stockId, List<GoldShareLot> lots) {
        saveMarketShareLots(player, MarketType.GOLD, stockId, lots);
    }

    private void saveMarketShareLots(
            Player player, MarketType marketType, String stockId, List<GoldShareLot> lots) {
        List<String> encodedLots = new ArrayList<>();
        for (GoldShareLot lot : lots) {
            encodedLots.add(lot.shares + "|" + Double.toString(lot.price) + "|" + Double.toString(lot.cost));
        }
        marketData.set(marketShareLotsPath(marketType, player.getUniqueId(), stockId), encodedLots);
        saveMarketData();
    }

    private void addGoldShareLot(List<GoldShareLot> lots, int shares, double price, double cost) {
        for (int index = 0; index < lots.size(); index++) {
            GoldShareLot lot = lots.get(index);
            if (Double.compare(lot.price, price) == 0) {
                lots.set(index, new GoldShareLot(lot.shares + shares, price, lot.cost + cost));
                return;
            }
        }
        lots.add(new GoldShareLot(shares, price, cost));
    }

    private double getGoldShareCostBasis(Player player, String stockId, int sharesToSell) {
        return getMarketShareCostBasis(player, MarketType.GOLD, stockId, sharesToSell);
    }

    private double getMarketShareCostBasis(
            Player player, MarketType marketType, String stockId, int sharesToSell) {
        int remaining = sharesToSell;
        double costBasis = 0.0;
        for (GoldShareLot lot : getMarketShareLots(player, marketType, stockId)) {
            int sold = Math.min(remaining, lot.shares);
            if (sold > 0) {
                costBasis += sold == lot.shares ? lot.cost : lot.cost * sold / lot.shares;
                remaining -= sold;
            }
            if (remaining == 0) {
                break;
            }
        }
        return costBasis;
    }

    private void removeGoldShareLots(Player player, String stockId, int sharesToSell) {
        removeMarketShareLots(player, MarketType.GOLD, stockId, sharesToSell);
    }

    private void removeMarketShareLots(Player player, MarketType marketType, String stockId, int sharesToSell) {
        int remaining = sharesToSell;
        List<GoldShareLot> remainingLots = new ArrayList<>();
        List<GoldShareLot> lots = getMarketShareLots(player, marketType, stockId);
        for (int index = 0; index < lots.size(); index++) {
            GoldShareLot lot = lots.get(index);
            int sold = Math.min(remaining, lot.shares);
            if (sold < lot.shares) {
                double soldCost = lot.cost * sold / lot.shares;
                remainingLots.add(new GoldShareLot(lot.shares - sold, lot.price, lot.cost - soldCost));
            }
            remaining -= sold;
            if (remaining == 0) {
                for (int laterIndex = index + 1; laterIndex < lots.size(); laterIndex++) {
                    remainingLots.add(lots.get(laterIndex));
                }
                break;
            }
        }
        saveMarketShareLots(player, marketType, stockId, remainingLots);
    }

    private String[] getMarketSellLore(
            Player player, MarketType marketType, String stockId, int requestedAmount, double price) {
        int ownedShares = getMarketShares(player, marketType, stockId);
        int sharesToSell = requestedAmount < 0 ? ownedShares : Math.min(requestedAmount, ownedShares);
        if (sharesToSell <= 0) {
            return new String[]{ChatColor.GRAY + "Shares owned: " + ownedShares,
                    ChatColor.GRAY + "No shares available to sell."};
        }
        int payout = (int) Math.max(1, Math.floor(price * sharesToSell));
        double profit = payout - getMarketShareCostBasis(player, marketType, stockId, sharesToSell);
        String result = profit > 0.005
                ? ChatColor.GREEN + "Expected gain: " + formatGoldPrice(profit) + " " + marketType.currencyName
                : profit < -0.005
                        ? ChatColor.RED + "Expected loss: " + formatGoldPrice(-profit) + " " + marketType.currencyName
                        : ChatColor.YELLOW + "Expected break-even";
        return new String[]{ChatColor.GRAY + "Shares to sell: " + sharesToSell,
                ChatColor.GRAY + "Estimated return: " + payout + " " + marketType.currencyUnit, result};
    }

    private String[] getGoldSellLore(Player player, String stockId, int requestedAmount, double price) {
        int ownedShares = getGoldShares(player, stockId);
        int sharesToSell = requestedAmount < 0 ? ownedShares : Math.min(requestedAmount, ownedShares);
        if (sharesToSell <= 0) {
            return new String[]{ChatColor.GRAY + "Shares owned: " + ownedShares,
                    ChatColor.GRAY + "No shares available to sell."};
        }
        int payout = (int) Math.max(1, Math.floor(price * sharesToSell));
        double profit = payout - getGoldShareCostBasis(player, stockId, sharesToSell);
        String result = profit > 0.005
                ? ChatColor.GREEN + "Expected gain: " + formatGoldPrice(profit) + " gold"
                : profit < -0.005
                        ? ChatColor.RED + "Expected loss: " + formatGoldPrice(-profit) + " gold"
                        : ChatColor.YELLOW + "Expected break-even";
        return new String[]{ChatColor.GRAY + "Shares to sell: " + sharesToSell,
                ChatColor.GRAY + "Estimated return: " + payout + " gold ingot(s)", result};
    }

    private int countGoldIngots(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null && "GOLD_INGOT".equals(item.getType().name())) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private boolean removeGoldIngots(Player player, int amount) {
        int remaining = amount;
        int storageSize = player.getInventory().getStorageContents().length;
        for (int slot = 0; slot < storageSize && remaining > 0; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null || !"GOLD_INGOT".equals(item.getType().name())) {
                continue;
            }
            int removed = Math.min(remaining, item.getAmount());
            remaining -= removed;
            if (removed == item.getAmount()) {
                player.getInventory().setItem(slot, null);
            } else {
                item.setAmount(item.getAmount() - removed);
            }
        }
        return remaining == 0;
    }

    private void giveGoldIngots(Player player, int amount) {
        ItemStack gold = XMaterial.matchXMaterial("GOLD_INGOT")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Gold ingot is unavailable"));
        gold.setAmount(amount);
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(gold);
        overflow.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }

    private int getGoldPurchaseCost(double price, int amount, double taxPercent) {
        return (int) Math.max(1, Math.ceil(price * amount * (1.0 + taxPercent / 100.0)));
    }

    private void buyMarketShares(Player player, MarketType marketType, String stockId, int amount, int page) {
        if (marketType == MarketType.GOLD) {
            buyGoldShares(player, stockId, amount, page);
        } else {
            buyEnchantedAppleShares(player, stockId, amount, page);
        }
    }

    private void sellMarketShares(Player player, MarketType marketType, String stockId, int amount, int page) {
        if (marketType == MarketType.GOLD) {
            sellGoldShares(player, stockId, amount, page);
        } else {
            sellEnchantedAppleShares(player, stockId, amount, page);
        }
    }

    private void promptMarketShareAmount(Player player, MarketType marketType, String stockId, int page) {
        pendingMarketPurchases.put(player.getUniqueId(), new PendingMarketPurchase(marketType, stockId, page));
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Type how many shares to buy (1-64), or type cancel.");
    }

    private int countEnchantedGoldenApples(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null && "ENCHANTED_GOLDEN_APPLE".equals(item.getType().name())) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private boolean removeEnchantedGoldenApples(Player player, int amount) {
        int remaining = amount;
        int storageSize = player.getInventory().getStorageContents().length;
        for (int slot = 0; slot < storageSize && remaining > 0; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null || !"ENCHANTED_GOLDEN_APPLE".equals(item.getType().name())) {
                continue;
            }
            int removed = Math.min(remaining, item.getAmount());
            remaining -= removed;
            if (removed == item.getAmount()) {
                player.getInventory().setItem(slot, null);
            } else {
                item.setAmount(item.getAmount() - removed);
            }
        }
        return remaining == 0;
    }

    private void giveEnchantedGoldenApples(Player player, int amount) {
        ItemStack apples = XMaterial.matchXMaterial("ENCHANTED_GOLDEN_APPLE")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Enchanted golden apple is unavailable"));
        apples.setAmount(amount);
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(apples);
        overflow.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }

    private void buyGoldShares(Player player, String stockId, int amount) {
        buyGoldShares(player, stockId, amount, 0);
    }

    private void buyGoldShares(Player player, String stockId, int amount, int page) {
        String path = "stocks." + stockId;
        if (!marketData.isConfigurationSection(path)) {
            player.sendMessage(ChatColor.RED + "That stock is no longer available.");
            openGoldStocks(player, page);
            return;
        }
        if (amount < 1 || amount > 64) {
            player.sendMessage(ChatColor.RED + "You can buy between 1 and 64 shares at a time.");
            openGoldStockDetail(player, stockId, page);
            return;
        }
        double price = marketData.getDouble(path + ".price", 1.0);
        double taxPercent = marketData.getDouble(path + ".tax_percent", 0.1);
        double subtotal = price * amount;
        int cost = getGoldPurchaseCost(price, amount, taxPercent);
        if (countGoldIngots(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " gold ingot(s) to buy those shares.");
            return;
        }
        if (!removeGoldIngots(player, cost)) {
            return;
        }

        String holdingsPath = "players." + player.getUniqueId() + ".holdings." + stockId;
        List<GoldShareLot> lots = getGoldShareLots(player, stockId);
        addGoldShareLot(lots, amount, price, subtotal);
        int newHolding = getGoldShares(player, stockId) + amount;
        marketData.set(holdingsPath, newHolding);
        saveGoldShareLots(player, stockId, lots);
        marketData.set(path + ".gold_reserve",
                marketData.getDouble(path + ".gold_reserve", 0.0) + subtotal);
        adjustGoldStockPrice(stockId, amount, true);
        saveMarketData();
        player.sendMessage(ChatColor.GREEN + "Bought " + amount + " share(s) for " + cost
                + " gold ingot(s), including " + formatGoldPrice(taxPercent) + "% tax.");
        openGoldStockDetail(player, stockId, page);
    }

    private void promptGoldShareAmount(Player player, String stockId, int page) {
        pendingMarketPurchases.put(player.getUniqueId(),
                new PendingMarketPurchase(MarketType.GOLD, stockId, page));
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Type how many shares to buy (1-64), or type cancel.");
    }

    private void sellGoldShares(Player player, String stockId, int requestedAmount, int page) {
        if (!marketData.isConfigurationSection("stocks." + stockId)) {
            player.sendMessage(ChatColor.RED + "That stock is no longer available.");
            openGoldStocks(player, page);
            return;
        }
        int ownedShares = getGoldShares(player, stockId);
        int amount = requestedAmount < 0 ? ownedShares : Math.min(requestedAmount, ownedShares);
        if (amount < 1) {
            player.sendMessage(ChatColor.RED + "You do not have any shares to sell.");
            return;
        }

        double price = marketData.getDouble("stocks." + stockId + ".price", 1.0);
        int payout = (int) Math.max(1, Math.floor(price * amount));
        String path = "stocks." + stockId;
        removeGoldShareLots(player, stockId, amount);
        marketData.set("players." + player.getUniqueId() + ".holdings." + stockId, ownedShares - amount);
        marketData.set(path + ".gold_reserve", marketData.getDouble(path + ".gold_reserve", 0.0) - payout);
        adjustGoldStockPrice(stockId, amount, false, player.getUniqueId());
        giveGoldIngots(player, payout);
        saveMarketData();
        player.sendMessage(ChatColor.GREEN + "Sold " + amount + " share(s) for " + payout + " gold ingot(s).");
        openGoldStockDetail(player, stockId, page);
    }

    private void buyEnchantedAppleShares(Player player, String stockId, int amount, int page) {
        MarketType marketType = MarketType.ENCHANTED_GOLDEN_APPLE;
        String path = marketStockPath(marketType, stockId);
        if (!marketData.isConfigurationSection(path)) {
            player.sendMessage(ChatColor.RED + "That stock is no longer available.");
            openMarketStocks(player, marketType, page, false);
            return;
        }
        if (amount < 1 || amount > 64) {
            player.sendMessage(ChatColor.RED + "You can buy between 1 and 64 shares at a time.");
            openMarketStockDetail(player, marketType, stockId, page);
            return;
        }

        double price = marketData.getDouble(path + ".price", 1.0);
        double taxPercent = marketData.getDouble(path + ".tax_percent", 0.1);
        double subtotal = price * amount;
        int cost = getGoldPurchaseCost(price, amount, taxPercent);
        if (countEnchantedGoldenApples(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " enchanted golden apple(s) to buy those shares.");
            return;
        }
        if (!removeEnchantedGoldenApples(player, cost)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        List<GoldShareLot> lots = getMarketShareLots(player, marketType, stockId);
        addGoldShareLot(lots, amount, price, subtotal);
        int newHolding = getMarketShares(player, marketType, stockId) + amount;
        marketData.set(marketHoldingPath(marketType, playerId, stockId), newHolding);
        saveMarketShareLots(player, marketType, stockId, lots);
        marketData.set(path + ".apple_reserve", marketData.getDouble(path + ".apple_reserve", 0.0) + subtotal);
        adjustMarketStockPrice(marketType, stockId, amount, true);
        saveMarketData();
        player.sendMessage(ChatColor.GREEN + "Bought " + amount + " share(s) for " + cost
                + " enchanted golden apple(s), including " + formatGoldPrice(taxPercent) + "% tax.");
        openMarketStockDetail(player, marketType, stockId, page);
    }

    private void sellEnchantedAppleShares(Player player, String stockId, int requestedAmount, int page) {
        MarketType marketType = MarketType.ENCHANTED_GOLDEN_APPLE;
        String path = marketStockPath(marketType, stockId);
        if (!marketData.isConfigurationSection(path)) {
            player.sendMessage(ChatColor.RED + "That stock is no longer available.");
            openMarketStocks(player, marketType, page, false);
            return;
        }
        int ownedShares = getMarketShares(player, marketType, stockId);
        int amount = requestedAmount < 0 ? ownedShares : Math.min(requestedAmount, ownedShares);
        if (amount < 1) {
            player.sendMessage(ChatColor.RED + "You do not have any shares to sell.");
            return;
        }

        double price = marketData.getDouble(path + ".price", 1.0);
        int payout = (int) Math.max(1, Math.floor(price * amount));
        removeMarketShareLots(player, marketType, stockId, amount);
        marketData.set(marketHoldingPath(marketType, player.getUniqueId(), stockId), ownedShares - amount);
        marketData.set(path + ".apple_reserve", marketData.getDouble(path + ".apple_reserve", 0.0) - payout);
        adjustMarketStockPrice(marketType, stockId, amount, false, player.getUniqueId());
        giveEnchantedGoldenApples(player, payout);
        saveMarketData();
        player.sendMessage(ChatColor.GREEN + "Sold " + amount + " share(s) for " + payout
                + " enchanted golden apple(s).");
        openMarketStockDetail(player, marketType, stockId, page);
    }

    private void adjustGoldStockPrice(String stockId, int shares, boolean purchase) {
        adjustGoldStockPrice(stockId, shares, purchase, null);
    }

    private void adjustGoldStockPrice(String stockId, int shares, boolean purchase, UUID sellerId) {
        adjustMarketStockPrice(MarketType.GOLD, stockId, shares, purchase, sellerId);
    }

    private void adjustMarketStockPrice(
            MarketType marketType, String stockId, int shares, boolean purchase) {
        adjustMarketStockPrice(marketType, stockId, shares, purchase, null);
    }

    private void adjustMarketStockPrice(
            MarketType marketType, String stockId, int shares, boolean purchase, UUID sellerId) {
        double price = marketData.getDouble(marketStockPath(marketType, stockId) + ".price", 1.0);
        double movement = Math.min(0.10, shares * 0.0025);
        updateMarketStockPrice(marketType, stockId, price * (purchase ? 1.0 + movement : 1.0 - movement),
                sellerId == null ? null : sellerId.toString());
    }

    private void locateItem(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /locateitem <item name>");
            return;
        }

        String itemKey = getNaturalLootItemKey(String.join(" ", args));
        if (itemKey == null) {
            sender.sendMessage(ChatColor.RED
                    + "Invalid item. Eligible items: Soul Fragment, Demonic Initiator, Heart Remover.");
            return;
        }

        for (String serializedLocation : naturalLootLocations.getStringList("containers." + itemKey)) {
            String[] coordinates = serializedLocation.split(";", -1);
            if (coordinates.length != 4) {
                continue;
            }
            try {
                org.bukkit.World world = Bukkit.getWorld(UUID.fromString(coordinates[0]));
                if (world == null) {
                    continue;
                }
                int x = Integer.parseInt(coordinates[1]);
                int y = Integer.parseInt(coordinates[2]);
                int z = Integer.parseInt(coordinates[3]);
                Block block = world.getBlockAt(x, y, z);
                if (!(block.getState() instanceof InventoryHolder holder)
                        || Arrays.stream(holder.getInventory().getContents())
                                .noneMatch(item -> isNaturalLootItem(item, itemKey))) {
                    continue;
                }
                sender.sendMessage(ChatColor.GREEN + "Found " + getNaturalLootItemName(itemKey)
                        + " in a container at " + world.getName() + " " + x + ", " + y + ", " + z + ".");
                return;
            } catch (IllegalArgumentException exception) {
                continue;
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState state : chunk.getTileEntities()) {
                    if (!(state instanceof InventoryHolder holder)
                            || Arrays.stream(holder.getInventory().getContents())
                                    .noneMatch(item -> isNaturalLootItem(item, itemKey))) {
                        continue;
                    }
                    Location location = state.getLocation();
                    sender.sendMessage(ChatColor.GREEN + "Found " + getNaturalLootItemName(itemKey)
                            + " in a container at " + world.getName() + " "
                            + location.getBlockX() + ", " + location.getBlockY() + ", "
                            + location.getBlockZ() + ".");
                    return;
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "No container with " + getNaturalLootItemName(itemKey)
                + " was found. Loot is indexed when its container generates loot.");
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
        awardAdvancement(player, octopiAdvancementKey);
        player.sendMessage(ChatColor.RED + "You sacrificed one heart for an extra life. This cannot be undone.");
    }

    private void consumeHeldItem(Player player, EquipmentSlot hand) {
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
    }

    private void registerCustomItemFactories() {
        registerCustomItem("Demonic Initiator", this::createDemonicInitiator);
        registerCustomItem("Death's Head", this::createDeathsHead, "deathshead");
        registerCustomItem("Soul Fragments", this::createSoulFragment, "soulfragment");
        registerCustomItem("Heart Remover", this::createHeartRemover, "heartremover");
        registerCustomItem("Soul Table", this::createSoulTable, "soultable");
        registerCustomItem("Soul Mace", this::createSoulMace, "soulmace");
        registerCustomItem("Strength 3 Potion", this::createEmpoweredStrengthPotion, "strength3potion");
        registerCustomItem("Speed 3 Potion", this::createEmpoweredSpeedPotion, "speed3potion");
        registerCustomItem("Empowered Instant Health Potion", this::createEmpoweredInstantHealthPotion,
                "empoweredhealthpotion");
        registerCustomItem("Empowered Bottle O' Enchanting", this::createEmpoweredBottle,
                "empoweredbottle");
        registerCustomItem("Empowered Spear", this::createEmpoweredSpear, "empoweredspear");
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
                    .ifPresent(effect -> player.addPotionEffect(effect, false));
            XPotion.matchXPotion("REGENERATION")
                    .map(potion -> potion.buildPotionEffect(40, 2))
                    .ifPresent(effect -> player.addPotionEffect(effect, false));
        }
    }

    private void refreshWitherBootsEffects(Player player) {
        if (isWitherBoots(player.getInventory().getBoots())) {
            XPotion.matchXPotion("SPEED")
                    .map(potion -> potion.buildPotionEffect(40, 2))
                    .ifPresent(effect -> player.addPotionEffect(effect, false));
        }
    }

    private void refreshEmpoweredSpearEffect(Player player) {
        if (!hasEmpoweredSpearInInventory(player)) {
            return;
        }
        XPotion.matchXPotion("STRENGTH")
                .map(potion -> potion.buildPotionEffect(40, 1))
                .ifPresent(effect -> player.addPotionEffect(effect, false));
    }

    private boolean hasEmpoweredSpearInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isEmpoweredSpear(item)) {
                return true;
            }
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (isEmpoweredSpear(item)) {
                return true;
            }
        }
        return isEmpoweredSpear(player.getInventory().getItemInOffHand());
    }

    private void refreshDragonChestplateEffects(Player player) {
        if (isDragonChestplate(player.getInventory().getChestplate())) {
            XPotion.matchXPotion("RESISTANCE")
                    .map(potion -> potion.buildPotionEffect(40, 2))
                    .ifPresent(effect -> player.addPotionEffect(effect, false));
        }
    }

    private void refreshElderGuardianHelmetEffects(Player player) {
        if (isElderGuardianHelmet(player.getInventory().getHelmet())) {
            XPotion.matchXPotion("WEAVING")
                    .map(potion -> potion.buildPotionEffect(40, 5))
                    .ifPresent(effect -> player.addPotionEffect(effect, false));
            XPotion.matchXPotion("NIGHT_VISION")
                    .map(potion -> potion.buildPotionEffect(40, 0))
                    .ifPresent(effect -> player.addPotionEffect(effect, false));
            XPotion.matchXPotion("HASTE")
                    .map(potion -> potion.buildPotionEffect(40, 3))
                    .ifPresent(effect -> player.addPotionEffect(effect, false));
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
        } else if (findDeathsHeadRecipe(holder.grid) >= 0) {
            holder.inventory.setItem(53, createDeathsHead());
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

        awardAdvancement(player, bringMeTheirSoulsAdvancementKey);
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(createSoulMace());
        overflow.values().forEach(item -> player.getWorld()
                .dropItemNaturally(player.getLocation(), item));
        renderSoulTable(holder);
    }

    private void craftDeathsHead(Player player, SoulTableHolder holder, int recipeStart) {
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) {
                int slot = recipeStart + row * 9 + column;
                ItemStack ingredient = holder.grid[slot];
                ingredient.setAmount(ingredient.getAmount() - 1);
                if (ingredient.getAmount() <= 0) {
                    holder.grid[slot] = null;
                }
            }
        }

        awardAdvancement(player, deathIsAtTheDoorAdvancementKey);
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(createDeathsHead());
        overflow.values().forEach(item -> player.getWorld()
                .dropItemNaturally(player.getLocation(), item));
        renderSoulTable(holder);
    }

    private int findDeathsHeadRecipe(ItemStack[] grid) {
        if (grid.length != 81) {
            return -1;
        }
        for (int topRow = 0; topRow <= 4; topRow++) {
            for (int leftColumn = 0; leftColumn <= 4; leftColumn++) {
                boolean matches = true;
                for (int row = 0; row < 5 && matches; row++) {
                    for (int column = 0; column < 5; column++) {
                        int slot = (topRow + row) * 9 + leftColumn + column;
                        if (!isDeathsHeadIngredient(grid[slot], row, column)) {
                            matches = false;
                            break;
                        }
                    }
                }
                if (matches) {
                    return topRow * 9 + leftColumn;
                }
            }
        }
        return -1;
    }

    private boolean isDeathsHeadIngredient(ItemStack item, int row, int column) {
        if (isEmpty(item)) {
            return false;
        }
        if (column == 2) {
            if (row == 2) {
                return "SCULK_CATALYST".equals(item.getType().name());
            }
            return isDemonicInitiator(item);
        }
        if (column == 0 || column == 4 || row == 0 || row == 4) {
            return isSoulFragment(item);
        }
        return isHeartRemover(item);
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

    private ItemStack createDeathsHead() {
        ItemStack head = XMaterial.matchXMaterial("WITHER_SKELETON_SKULL")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Wither skeleton skull is unavailable"));
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Death's Head");
        meta.getPersistentDataContainer().set(deathsHeadKey, PersistentDataType.BYTE, (byte) 1);
        head.setItemMeta(meta);
        return head;
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

    private void registerAdvancement(NamespacedKey key, String resourcePath) {
        if (Bukkit.getAdvancement(key) != null) {
            return;
        }
        try (InputStream input = getResource(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Missing advancement resource: " + resourcePath);
            }
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            if (Bukkit.getUnsafe().loadAdvancement(key, json) == null) {
                throw new IllegalStateException("Could not load advancement: " + key);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read advancement resource: " + resourcePath, exception);
        }
    }

    private void refreshSoulGatheringAdvancement(Player player) {
        boolean hasSoulFragment = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isSoulFragment(item)) {
                hasSoulFragment = true;
                break;
            }
        }
        if (!hasSoulFragment) {
            return;
        }
        awardAdvancement(player, lastLifeRootAdvancementKey);
        awardAdvancement(player, soulGatheringAdvancementKey);
    }

    private void refreshRiskyResetAdvancement(Player player) {
        Advancement parent = Bukkit.getAdvancement(soulGatheringAdvancementKey);
        AttributeInstance attribute = player.getAttribute(maxHealthAttribute);
        if (parent != null && player.getAdvancementProgress(parent).isDone()
                && attribute != null && attribute.getValue() >= 20.0) {
            awardAdvancement(player, riskyResetAdvancementKey);
        }
    }

    private void refreshBackToSquareOneAdvancement(Player player) {
        Advancement riskyReset = Bukkit.getAdvancement(riskyResetAdvancementKey);
        Advancement octopi = Bukkit.getAdvancement(octopiAdvancementKey);
        AttributeInstance attribute = player.getAttribute(maxHealthAttribute);
        if (riskyReset != null && octopi != null && attribute != null
                && player.getAdvancementProgress(riskyReset).isDone()
                && player.getAdvancementProgress(octopi).isDone()
                && attribute.getValue() == 2.0) {
            awardAdvancement(player, backToSquareOneAdvancementKey);
        }
    }

    private void refreshTheTotemTotemAdvancement(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isEvolvedTotem(item)) {
                awardAdvancement(player, theTotemTotemAdvancementKey);
                return;
            }
        }
    }

    private void refreshShhhAdvancement(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && "SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE".equals(item.getType().name())) {
                awardAdvancement(player, shhhAdvancementKey);
                return;
            }
        }
    }

    private boolean isCustomBossArmor(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(wardenArmorKey, PersistentDataType.BYTE)
                || meta.getPersistentDataContainer().has(witherArmorKey, PersistentDataType.BYTE)
                || meta.getPersistentDataContainer().has(dragonArmorKey, PersistentDataType.BYTE)
                || meta.getPersistentDataContainer().has(elderGuardianArmorKey, PersistentDataType.BYTE);
    }

    private void refreshWearingTheFallensFleshAdvancement(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (isCustomBossArmor(item)) {
                awardAdvancement(player, wearingTheFallensFleshAdvancementKey);
                return;
            }
        }
    }

    private void refreshRiseAdvancement(Player player) {
        if (hasFullBossArmorSet(player, wardenArmorKey)) {
            awardAdvancement(player, riseAdvancementKey);
        }
    }

    private void refreshDecayingAdvancement(Player player) {
        if (hasFullBossArmorSet(player, witherArmorKey)) {
            awardAdvancement(player, decayingAdvancementKey);
        }
    }

    private void refreshOceanManAdvancement(Player player) {
        if (hasFullBossArmorSet(player, elderGuardianArmorKey)) {
            awardAdvancement(player, oceanManAdvancementKey);
        }
    }

    private void refreshIHateYouAdvancement(Player player) {
        if (hasFullBossArmorSet(player, dragonArmorKey)) {
            awardAdvancement(player, iHateYouAdvancementKey);
        }
    }

    private boolean hasFullBossArmorSet(Player player, NamespacedKey armorKey) {
        Set<String> pieces = new HashSet<>();
        for (ItemStack item : player.getInventory().getContents()) {
            addBossArmorPiece(pieces, item, armorKey);
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            addBossArmorPiece(pieces, item, armorKey);
        }
        addBossArmorPiece(pieces, player.getInventory().getItemInOffHand(), armorKey);
        return pieces.containsAll(Arrays.asList(
                "NETHERITE_HELMET", "NETHERITE_CHESTPLATE", "NETHERITE_LEGGINGS", "NETHERITE_BOOTS"));
    }

    private void addBossArmorPiece(Set<String> pieces, ItemStack item, NamespacedKey armorKey) {
        if (item != null && !item.getType().isAir() && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(armorKey, PersistentDataType.BYTE)) {
            pieces.add(item.getType().name());
        }
    }

    private void awardAdvancement(Player player, NamespacedKey key) {
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        for (String criterion : progress.getRemainingCriteria()) {
            progress.awardCriteria(criterion);
        }
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

    private ItemStack createEmpoweredStrengthPotion() {
        return createEmpoweredPotion("Strength 3 Potion", empoweredStrengthPotionKey, "STRENGTH", 3);
    }

    private ItemStack createEmpoweredSpeedPotion() {
        return createEmpoweredPotion("Speed 3 Potion", empoweredSpeedPotionKey, "SPEED", 3);
    }

    private ItemStack createEmpoweredPotion(String name, NamespacedKey key, String effectName, int amplifier) {
        ItemStack potion = XMaterial.matchXMaterial("POTION")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Potion is unavailable"));
        if (!(potion.getItemMeta() instanceof PotionMeta meta)) {
            throw new IllegalStateException("Potion metadata is unavailable");
        }
        PotionEffect effect = XPotion.matchXPotion(effectName)
                .map(xPotion -> xPotion.buildPotionEffect(3600, amplifier))
                .orElseThrow(() -> new IllegalStateException(effectName + " is unavailable"));
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + name);
        meta.setLore(List.of(ChatColor.GRAY + "A powerful, rare brew."));
        meta.addCustomEffect(effect, true);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        potion.setItemMeta(meta);
        return potion;
    }

    private ItemStack createEmpoweredInstantHealthPotion() {
        ItemStack potion = XMaterial.matchXMaterial("POTION")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Potion is unavailable"));
        ItemMeta meta = potion.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Empowered Instant Health Potion");
        meta.setLore(List.of(ChatColor.GRAY + "Instantly restores you to full health."));
        meta.getPersistentDataContainer().set(
                empoweredInstantHealthPotionKey, PersistentDataType.BYTE, (byte) 1);
        potion.setItemMeta(meta);
        return potion;
    }

    private ItemStack createEmpoweredBottle() {
        ItemStack bottle = XMaterial.matchXMaterial("EXPERIENCE_BOTTLE")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Bottle o' enchanting is unavailable"));
        ItemMeta meta = bottle.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Empowered Bottle O' Enchanting");
        meta.setLore(List.of(ChatColor.GRAY + "Repairs damaged Mending items, or grants 50 levels."));
        meta.getPersistentDataContainer().set(empoweredBottleKey, PersistentDataType.BYTE, (byte) 1);
        bottle.setItemMeta(meta);
        return bottle;
    }

    private ItemStack createEmpoweredSpear() {
        ItemStack spear = XMaterial.matchXMaterial("NETHERITE_SPEAR")
                .map(XMaterial::parseItem)
                .orElseThrow(() -> new IllegalStateException("Spear is unavailable"));
        ItemMeta meta = spear.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Empowered Spear");
        meta.setLore(List.of(
                ChatColor.GRAY + "Grants Strength II while in your inventory.",
                ChatColor.GRAY + "Prevents fall and kinetic damage while held."));
        meta.getPersistentDataContainer().set(empoweredSpearKey, PersistentDataType.BYTE, (byte) 1);
        XEnchantment.matchXEnchantment("SHARPNESS")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 9, true));
        XEnchantment.matchXEnchantment("UNBREAKING")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 5, true));
        XEnchantment.matchXEnchantment("LUNGE")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 4, true));
        XEnchantment.matchXEnchantment("FIRE_ASPECT")
                .ifPresent(enchantment -> meta.addEnchant(enchantment.getEnchant(), 5, true));
        spear.setItemMeta(meta);
        return spear;
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

    private boolean isEmpoweredInstantHealthPotion(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(empoweredInstantHealthPotionKey, PersistentDataType.BYTE);
    }

    private boolean isEmpoweredBottle(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(empoweredBottleKey, PersistentDataType.BYTE);
    }

    private boolean isEmpoweredSpear(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(empoweredSpearKey, PersistentDataType.BYTE);
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

    private boolean isDeathsHead(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(deathsHeadKey, PersistentDataType.BYTE);
    }

    private boolean isDeathsHeadBlock(Block block) {
        return block.getState() instanceof Skull skull
                && skull.getPersistentDataContainer().has(deathsHeadKey, PersistentDataType.BYTE);
    }

    private boolean hasEnchantment(ItemStack item, String enchantmentName, int minimumLevel) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        return XEnchantment.matchXEnchantment(enchantmentName)
                .map(enchantment -> item.getItemMeta().getEnchantLevel(enchantment.getEnchant()) >= minimumLevel)
                .orElse(false);
    }

    private boolean hasFullProtectionFour(Player player) {
        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (!hasEnchantment(armorPiece, "PROTECTION", 4)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpoweredDragon(EnderDragon dragon) {
        return dragon.getPersistentDataContainer().has(empoweredDragonKey, PersistentDataType.BYTE);
    }

    private boolean isEmpoweredWither(Wither wither) {
        return wither.getPersistentDataContainer().has(empoweredWitherKey, PersistentDataType.BYTE);
    }

    private boolean isEmpoweredWarden(Warden warden) {
        return warden.getPersistentDataContainer().has(empoweredWardenKey, PersistentDataType.BYTE);
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

    private void saveMarketData() {
        try {
            marketData.save(marketDataFile);
        } catch (IOException exception) {
            getLogger().severe("Could not save market data: " + exception.getMessage());
        }
    }

    private enum MarketView {
        MARKET_SELECT,
        STOCK_SELECT,
        STOCK_EDIT_OPTIONS,
        STOCK_DETAIL
    }

    private enum MarketType {
        GOLD("stocks", "holdings", "share_lots", "Gold Market", "GOLD_INGOT", "gold", "gold ingot(s)"),
        ENCHANTED_GOLDEN_APPLE("enchanted_apple_stocks", "enchanted_apple_holdings",
                "enchanted_apple_share_lots", "Enchanted Golden Apple Market", "ENCHANTED_GOLDEN_APPLE",
                "enchanted golden apple", "enchanted golden apple(s)");

        private final String stockRoot;
        private final String holdingsKey;
        private final String shareLotsKey;
        private final String displayName;
        private final String currencyItem;
        private final String currencyName;
        private final String currencyUnit;

        MarketType(String stockRoot, String holdingsKey, String shareLotsKey, String displayName,
                String currencyItem, String currencyName, String currencyUnit) {
            this.stockRoot = stockRoot;
            this.holdingsKey = holdingsKey;
            this.shareLotsKey = shareLotsKey;
            this.displayName = displayName;
            this.currencyItem = currencyItem;
            this.currencyName = currencyName;
            this.currencyUnit = currencyUnit;
        }
    }

    private enum MarketEditStep {
        SHARE_PRICE,
        BUY_TAX,
        PLAYER_NAME,
        SHARE_AMOUNT
    }

    private final class MarketMenuHolder implements InventoryHolder {
        private final MarketView view;
        private final String stockId;
        private final int page;
        private final MarketType marketType;
        private final boolean editMode;
        private final Map<Integer, String> stockSlots = new HashMap<>();
        private Inventory inventory;

        private MarketMenuHolder(MarketView view, String stockId, int page) {
            this(view, stockId, page, MarketType.GOLD, false);
        }

        private MarketMenuHolder(MarketView view, String stockId, int page, boolean editMode) {
            this(view, stockId, page, MarketType.GOLD, editMode);
        }

        private MarketMenuHolder(
                MarketView view, String stockId, int page, MarketType marketType, boolean editMode) {
            this.view = view;
            this.stockId = stockId;
            this.page = page;
            this.marketType = marketType;
            this.editMode = editMode;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    private static final class PendingMarketPurchase {
        private final MarketType marketType;
        private final String stockId;
        private final int page;

        private PendingMarketPurchase(MarketType marketType, String stockId, int page) {
            this.marketType = marketType;
            this.stockId = stockId;
            this.page = page;
        }
    }

    private static final class PendingMarketEdit {
        private final MarketType marketType;
        private final String stockId;
        private final int page;
        private final MarketEditStep step;
        private final UUID targetPlayerId;
        private final String targetPlayerName;

        private PendingMarketEdit(MarketType marketType, String stockId, int page, MarketEditStep step,
                UUID targetPlayerId, String targetPlayerName) {
            this.marketType = marketType;
            this.stockId = stockId;
            this.page = page;
            this.step = step;
            this.targetPlayerId = targetPlayerId;
            this.targetPlayerName = targetPlayerName;
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