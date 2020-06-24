package com.johnymuffin.beta.ipaccountlimiter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class IPAccountListener implements Listener {
    private IPAccountLimiter plugin;
    private IPAccountStorage storage;
    private int accountLimit;

    public IPAccountListener(IPAccountLimiter plugin) {
        this.plugin = plugin;
        this.storage = plugin.getIpAccountStorage();
        accountLimit = 6;
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        Player player = event.getPlayer();
        String ip = event.getAddress().getHostAddress();
        UUID uuid = player.getUniqueId();

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
