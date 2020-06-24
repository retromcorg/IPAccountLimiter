package com.johnymuffin.beta.ipaccountlimiter;

import com.johnymuffin.beta.ipaccountlimiter.simplejson.JSONArray;
import com.johnymuffin.beta.ipaccountlimiter.simplejson.JSONObject;
import com.johnymuffin.beta.ipaccountlimiter.simplejson.parser.JSONParser;
import com.johnymuffin.beta.ipaccountlimiter.simplejson.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class IPAccountStorage {
    private JSONObject jsonData;
    private File storageFile;
    private int accountTimeout;
    private IPAccountLimiter plugin;

    public IPAccountStorage(IPAccountLimiter plugin) {
        this.plugin = plugin;
        storageFile = new File(plugin.getDataFolder(), "storage.json");

        accountTimeout = plugin.getIpAccountConfig().getConfigInteger("forget-ip-after");

        if (!storageFile.exists()) {
            storageFile.getParentFile().mkdirs();
            try (FileWriter file = new FileWriter(storageFile)) {
                plugin.logInfo("Generating new storage.json file");
                jsonData = new JSONObject();
                file.write(jsonData.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            plugin.logInfo("Reading storage.json file");
            JSONParser parser = new JSONParser();
            jsonData = (JSONObject) parser.parse(new FileReader(storageFile));
        } catch (ParseException e) {
            plugin.logInfo("storage.json file is corrupt: " + e + ": " + e.getMessage());
            jsonData = new JSONObject();
            saveData();
        } catch (Exception e) {
            System.out.println("Error reading storage.json, changing to memory only cache: " + e + ": " + e.getMessage());
            jsonData = new JSONObject();
        }


    }

    private JSONArray getIPArray(String ip) {
        if (jsonData.containsKey(ip)) {
            return (JSONArray) jsonData.get(ip);
        }
        return new JSONArray();
    }


    public int otherAccounts(UUID uuid, String ip) {
        int count = 0;
        JSONArray ipArray = getIPArray(ip);
        for (int i = 0; i < ipArray.size(); i++) {
            JSONObject userObject = (JSONObject) ipArray.get(i);
            if (userObject.containsKey("unix") && Long.valueOf(String.valueOf(userObject.get("unix"))) > ((System.currentTimeMillis() / 1000L) - accountTimeout)) {
                if (!(userObject.containsKey("uuid") && userObject.get("uuid").equals(uuid.toString()))) {
                    count = count + 1;
                }

            }
        }
        return count;
    }


    public void updateUserDetails(UUID uuid, String ip) {
        if (!jsonData.containsKey(ip)) {
            addUserEntry(uuid, ip);
        }
        JSONArray ipArray = getIPArray(ip);
        for (int i = 0; i < ipArray.size(); i++) {
            JSONObject userObject = (JSONObject) ipArray.get(i);
            if (userObject.containsKey("uuid") && userObject.get("uuid").equals(uuid.toString())) {
                userObject.put("unix", (System.currentTimeMillis() / 1000L));
                ipArray.remove(i);
                ipArray.add(userObject);
                jsonData.put(ip, ipArray);
                return;
            }
        }
        addUserEntry(uuid, ip);
    }


    private void addUserEntry(UUID uuid, String ip) {
        JSONArray ipObject = getIPArray(ip);
        JSONObject userObject = new JSONObject();
        userObject.put("uuid", uuid.toString());
        userObject.put("unix", (System.currentTimeMillis() / 1000L));
        ipObject.add(userObject);
        jsonData.put(ip, ipObject);
    }


    public void saveData() {
        saveJsonArray();
    }


    private void saveJsonArray() {
        try (FileWriter file = new FileWriter(storageFile)) {
            System.out.println("Saving storage.json for IP Account Limiter");
            file.write(jsonData.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
