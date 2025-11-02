package com.johnymuffin.beta.ipaccountlimiter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public class IPAccountLimiter extends JavaPlugin implements CommandExecutor {
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
        Bukkit.getPluginCommand("ipremove").setExecutor(this);
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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.isOp() || commandSender.hasPermission("ipaccountlimiter.remove"))) {
            commandSender.sendMessage(ChatColor.RED + "Sorry, you are not authorized to use that command.");
            return true;
        }
        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.RED + "Invalid usage, /ipremove (ip)");
            return true;
        }
        String ip = strings[0];

        if (ipAccountStorage.deleteIPRecords(ip)) {
            commandSender.sendMessage(ChatColor.BLUE + "Successfully removed records for " + ip);
        } else {
            commandSender.sendMessage(ChatColor.RED + "Couldn't find records for " + ip);
        }

        return true;


    }

}
