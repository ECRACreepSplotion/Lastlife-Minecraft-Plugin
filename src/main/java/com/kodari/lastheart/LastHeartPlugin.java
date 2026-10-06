package com.kodari.lastheart;

import com.cryptomorin.xseries.XAttribute;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class LastHeartPlugin extends JavaPlugin implements Listener, CommandExecutor {
    private static final int MAX_HEARTS = 10;
    private static final String BAN_REASON = "You lost all 10 hearts.";

    private File playerDataFile;
    private FileConfiguration playerData;
    private Attribute maxHealthAttribute;

    @Override
    public void onEnable() {
        maxHealthAttribute = XAttribute.of("max_health")
                .map(XAttribute::get)
                .orElseThrow(() -> new IllegalStateException("Could not find the max health attribute"));

        playerDataFile = new File(getDataFolder(), "players.yml");
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            throw new IllegalStateException("Could not create the plugin data folder");
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("heartunban").setExecutor(this);
        getCommand("sethearts").setExecutor(this);
        getCommand("checkhearts").setExecutor(this);
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

        double maxHealth = getHearts(player.getUniqueId()) * 2.0;
        attribute.setBaseValue(maxHealth);
        if (restoreHealth) {
            player.setHealth(maxHealth);
        } else if (player.getHealth() > maxHealth) {
            player.setHealth(maxHealth);
        }
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