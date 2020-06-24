package com.johnymuffin.beta.ipaccountlimiter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public class IPAccountLimiter extends JavaPlugin {
    //Basic Plugin Info
    private static IPAccountLimiter plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    //Config
    private IPAccountStorage ipAccountStorage;
    private IPAccountConfig ipAccountConfig;
    //Poseidon
    private boolean poseidonPresent = false;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());
        ipAccountConfig = new IPAccountConfig(plugin);

        ipAccountStorage = new IPAccountStorage(plugin);
        IPAccountListener ipAccountListener = new IPAccountListener(plugin);
        Bukkit.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, ipAccountListener, Event.Priority.Low, plugin);
        Bukkit.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, ipAccountListener, Event.Priority.Low, plugin);

        if (testClassExistence("com.projectposeidon.api.PoseidonUUID")) {
            poseidonPresent = true;
            logInfo("Project Poseidon detected, using valid UUIDs.");
        } else {
            logInfo("Project Poseidon support disabled.");
        }


    }


    public UUID getUUIDFromPlayer(Player player) {
        if (poseidonPresent) {
            try {
                UUID uuid = player.getUniqueId();
                return uuid;
            } catch (Exception e) {
                plugin.logInfo("Error getting UUID from player " + player.getName() + " with Project Poseidon support, using offline uuid instead.");
            }
        }
        return generateOfflineUUID(player.getName());
    }

    private UUID generateOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(username.getBytes());
    }


    private boolean testClassExistence(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] Is Disabling, Version: " + pdf.getVersion());
        ipAccountStorage.saveData();
    }


    public void logInfo(String s) {
        log.info("[" + pluginName + "] " + s);
    }

    public IPAccountStorage getIpAccountStorage() {
        return ipAccountStorage;
    }

    public IPAccountConfig getIpAccountConfig() {
        return ipAccountConfig;
    }

    public boolean isPoseidonPresent() {
        return poseidonPresent;
    }
}
