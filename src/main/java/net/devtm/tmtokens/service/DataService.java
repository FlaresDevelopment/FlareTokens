package net.devtm.tmtokens.service;

import net.devtm.tmtokens.API.TokensPlayer;
import net.devtm.tmtokens.files.FilesManager;
import net.devtm.tmtokens.util.Utils;
import net.tmtokens.lib.Lib;

import java.util.Locale;
import java.util.UUID;

public class DataService {

    String storageType = null;
    public String table = null;
    public int firstMobcoins = 0;

    public TokensPlayer warpPlayer(String name) {
        UUID uuid = Utils.UTILS.getPlayerUUID(name);
        TokensPlayer mobcoinsPlayer = new TokensPlayer();
        mobcoinsPlayer.setPlayer(uuid);
        mobcoinsPlayer.setTokens(getTokens(uuid));
        return mobcoinsPlayer;
    }

    public TokensPlayer warpPlayer(UUID uuid) {
        TokensPlayer mobcoinsPlayer = new TokensPlayer();
        mobcoinsPlayer.setPlayer(uuid);
        mobcoinsPlayer.setTokens(getTokens(uuid));
        return mobcoinsPlayer;
    }

    public void reloadDataService() {
        try {
           storageType = FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.type").toLowerCase(Locale.ROOT);
           table = FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.table");
           firstMobcoins = FilesManager.ACCESS.getConfig().getConfig().getInt("first_join_give_mobcoins");
        } catch (Exception e) {

        }
    }

    public boolean hasAccount(UUID uuid) {
        switch (storageType) {
            case "file":
                return FilesManager.ACCESS.getData().getConfig().contains("account." + uuid);
            case "mysql":
                return Lib.LIB.getMySQL().sqlIO.exists("uuid", uuid.toString(), table);
            case "sqlite":
                break;
        }
        return false;
    }

    public int getTokens(UUID uuid) {
        createPlayerProfile(uuid);
            switch (storageType) {
                case "file":
                    return FilesManager.ACCESS.getData().getConfig().getInt("account." + uuid + ".tokens");
                case "mysql":
                    return Integer.parseInt(Lib.LIB.getMySQL().sqlIO.get("tokens", new String[]{"uuid = '" + uuid + "'"}, table).toString());
                case "sqlite":
                    break;
            }
        return 0;
    }

    public void setTokens(UUID uuid, int amount) {
        createPlayerProfile(uuid);
        switch (storageType) {
            case "file":
                FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".tokens", amount);
                FilesManager.ACCESS.getData().saveConfig();
                break;
            case "mysql":
                Lib.LIB.getMySQL().sqlIO.set("tokens", amount, "uuid", "=", uuid.toString(), table);
                break;
            case "sqlite":
                break;
        }
    }

    /**
     * This will create a player account ONLY if the player dose not have an account!
     * @param uuid Player
     */
    public void createPlayerProfile(UUID uuid) {
        switch (storageType) {
            case "file":
                if (!FilesManager.ACCESS.getData().getConfig().contains("account." + uuid)) {
                    FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".tokens", firstMobcoins);
                    FilesManager.ACCESS.getData().saveConfig();
                }
                break;
            case "mysql":
                if(!Lib.LIB.getMySQL().sqlIO.exists("uuid", uuid.toString(), table))
                    Lib.LIB.getMySQL().sqlIO.insertData("player, uuid, tokens",
                            "'" + Utils.UTILS.getPlayerName(uuid) + "', '" + uuid + "', "
                                    + firstMobcoins, table);
                break;
            case "sqlite":
        }
    }
}

