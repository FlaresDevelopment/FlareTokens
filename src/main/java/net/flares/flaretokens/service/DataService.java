package net.flares.flaretokens.service;

import net.flares.flaretokens.FlareTokens;
import net.flarepowered.database.MySQL.SQLHandler;
import net.flares.flaretokens.API.TokensPlayer;
import net.flares.flaretokens.files.FilesManager;
import net.flares.flaretokens.util.Utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class DataService {

    int read = 0;
    final HashMap<UUID, TokensPlayer> cache = new HashMap<>();
    final List<UUID> hasAccounts = new ArrayList<>();
    final HashMap<UUID, TokensPlayer> cacheUpdates = new HashMap<>();
    String storageType = null;
    public String table = null;
    public int firstTokens = 0;

    public TokensPlayer warpPlayer(UUID uuid) {
        // get player if the player don't exist (in cache)
        if(cache.containsKey(uuid)) {
            if(cache.get(uuid).updatedTime < System.currentTimeMillis()) {
                cache.get(uuid).updatedTime = (FilesManager.ACCESS.getConfig().getConfig().getInt("storage.cache_time") * 1000L) + System.currentTimeMillis();
                cache.get(uuid).objectSetTokens(getTokens(uuid));
            }
            return cache.get(uuid);
        } else {
            if(hasAccount(uuid)) {
                return new TokensPlayer(uuid, getTokens(uuid));
            } else {
                return new TokensPlayer(uuid, firstTokens);
            }
        }
    }
    public TokensPlayer warpPlayer(String name) {
        UUID uuid = Utils.UTILS.getPlayerUUID(name);
        // get player if the player don't exist (in cache)
        if(cache.containsKey(uuid)) {
            return cache.get(uuid);
        } else {
            if(hasAccount(uuid)) {
                return new TokensPlayer(uuid, getTokens(uuid));
            } else {
                return new TokensPlayer(uuid, firstTokens);
            }
        }
    }
    public TokensPlayer warpPlayerNoCache(UUID uuid) {
        return new TokensPlayer(uuid, getTokens(uuid));
    }

    public void reloadDataService() {
        storageType = FilesManager.ACCESS.getConfig().getConfig().getString("storage.type").toLowerCase(Locale.ROOT);
        table = FilesManager.ACCESS.getConfig().getConfig().getString("storage.mysql.table");
        firstTokens = FilesManager.ACCESS.getConfig().getConfig().getInt("first_join_give_tokens");
    }

    public boolean hasAccount(UUID uuid) {
        if(hasAccounts.contains(uuid))
            return true;
        switch (storageType) {
            case "file":
                if(FilesManager.ACCESS.getData().getConfig().contains("account." + uuid))
                    hasAccounts.add(uuid);
                return hasAccounts.contains(uuid);
            case "mysql":
                try (Connection connection = FlareTokens.PLUGIN.getDatabase().getConnection()) {
                    if(SQLHandler.exists(connection, table, "uuid", uuid.toString()))
                        hasAccounts.add(uuid);
                    return hasAccounts.contains(uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            case "sqlite":
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + new File(FlareTokens.PLUGIN.getPlugin().getDataFolder(), "data/data.db").getAbsolutePath())) {
                    if(SQLHandler.exists(conn, table, "uuid", uuid.toString()))
                        hasAccounts.add(uuid);
                    return hasAccounts.contains(uuid);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
        }
        return false;
    }

    public int getTokens(UUID uuid) {
        int tokens = 0;
        switch (storageType) {
            case "file":
                tokens = FilesManager.ACCESS.getData().getConfig().getInt("account." + uuid + ".tokens");
                break;
            case "mysql":
                try(Connection connection = FlareTokens.PLUGIN.getDatabase().getConnection()) {
                    createPlayerProfile(connection, uuid);
                    tokens = Integer.parseInt(SQLHandler.get(connection, table, "tokens", new String[]{"uuid='" + uuid + "'"}).toString());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "sqlite":
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + new File(FlareTokens.PLUGIN.getPlugin().getDataFolder(), "data/data.db").getAbsolutePath())) {
                    tokens = Integer.parseInt(SQLHandler.get(conn, table, "tokens", new String[]{"uuid='" + uuid + "'"}).toString());
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
        if(!cache.containsKey(uuid)) {
            cache.put(uuid, new TokensPlayer(uuid, tokens));
        } else
            cache.get(uuid).objectSetTokens(tokens);
        return tokens;
    }

    public void setTokens(UUID uuid, int amount) {
        if(!cache.containsKey(uuid)) {
            cache.put(uuid, new TokensPlayer(uuid, amount));
        } else
            cache.get(uuid).objectSetTokens(amount);
        switch (storageType) {
            case "file":
                FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".tokens", amount);
                FilesManager.ACCESS.getData().saveConfig();
                break;
            case "mysql":
                try(Connection connection = FlareTokens.PLUGIN.getDatabase().getConnection()) {
                    createPlayerProfile(connection, uuid);
                    System.out.println("Setting...");
                    SQLHandler.set(connection, table, "tokens", amount, new String[]{"uuid = '" + uuid + "'"});
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "sqlite":
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + new File(FlareTokens.PLUGIN.getPlugin().getDataFolder(), "data/data.db").getAbsolutePath())) {
                    createPlayerProfile(conn, uuid);
                    SQLHandler.set(conn, table, "tokens", amount, new String[]{"uuid = '" + uuid + "'"});
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
    }

    /**
     * This will create a player account ONLY if the player dose not have an account!
     * @param uuid Player
     */
    public void createPlayerProfile(Connection connection, UUID uuid) {
        switch (storageType) {
            case "file":
                if (!FilesManager.ACCESS.getData().getConfig().contains("account." + uuid)) {
                    FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".tokens", firstTokens);
                    FilesManager.ACCESS.getData().saveConfig();
                }
                break;
            case "mysql":
            case "sqlite":
                if(!SQLHandler.exists(connection, table, "uuid", uuid.toString()))
                    SQLHandler.insertData(connection, table,
                            "player, uuid, tokens", "'" + Utils.UTILS.getPlayerName(uuid) + "', '" + uuid + "', '" + firstTokens + "'");
                break;
        }
    }
}

