package com.johnymuffin.beta.ipaccountlimiter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class IPAccountLimiter extends JavaPlugin {
    //Basic Plugin Info
    private static IPAccountLimiter plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    //Config
    private IPAccountStorage ipAccountStorage;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());
        ipAccountStorage = new IPAccountStorage(plugin);
        IPAccountListener ipAccountListener = new IPAccountListener(plugin);
        Bukkit.getServer().getPluginManager().registerEvents(ipAccountListener, plugin);


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
}
