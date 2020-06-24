package com.johnymuffin.beta.ipaccountlimiter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class IPAccountListener extends PlayerListener {
    private IPAccountLimiter plugin;
    private IPAccountStorage storage;
    private int accountLimit;

    public IPAccountListener(IPAccountLimiter plugin) {
        this.plugin = plugin;
        this.storage = plugin.getIpAccountStorage();
        accountLimit = plugin.getIpAccountConfig().getConfigInteger("maximum-accounts-per-ip");
    }

    public void onPlayerLogin(PlayerLoginEvent event) {
        if(plugin.isPoseidonPresent()) {
            if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
                return;
            }

            Player player = event.getPlayer();
            String ip = event.getAddress().getHostAddress();
            UUID uuid = plugin.getUUIDFromPlayer(player);

            int accountCount = storage.otherAccounts(uuid, ip);

            if (accountCount >= accountLimit) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Sorry, are over the account limit, " + accountCount + "/" + accountLimit);
                plugin.logInfo(player.getName() + " has been blocked from connecting as they have over " + accountLimit + "accounts, " + accountCount + "/" + accountLimit + ".");
                return;
            }
            plugin.logInfo(player.getName() + " has been allowed to join as they only have " + accountCount + " accounts, " + accountCount + "/" + accountLimit + ".");
            storage.updateUserDetails(uuid, ip);
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!plugin.isPoseidonPresent()) {

            Player player = event.getPlayer();
            String ip = player.getAddress().getAddress().getHostAddress();
            UUID uuid = plugin.getUUIDFromPlayer(player);

            int accountCount = storage.otherAccounts(uuid, ip);

            if (accountCount >= accountLimit) {
                player.kickPlayer("Sorry, are over the account limit, " + accountCount + "/" + accountLimit);
                plugin.logInfo(player.getName() + " has been blocked from connecting as they have over " + accountLimit + "accounts, " + accountCount + "/" + accountLimit + ".");
                return;
            }
            plugin.logInfo(player.getName() + " has been allowed to join as they only have " + accountCount + " accounts, " + accountCount + "/" + accountLimit + ".");
            storage.updateUserDetails(uuid, ip);
        }

    }


}
