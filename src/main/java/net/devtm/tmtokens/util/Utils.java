package net.devtm.tmtokens.util;

import lombok.Getter;
import net.devtm.tmtokens.API.TokensPlayer;
import net.devtm.tmtokens.TMTokens;
import net.devtm.tmtokens.files.FilesManager;
import net.devtm.tmtokens.service.ServiceHandler;
import net.tmtokens.lib.menu.item.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Getter
public enum Utils {
  UTILS;

  long delayFactor = 0;
  HashMap<UUID, DelayPlayer> delayPlayers = new HashMap<>();

  public void reloadUtils() {
    delayFactor = FilesManager.ACCESS.getConfig().getConfig().getInt("storage_type.read_delay");
  }

  public DelayPlayer warpDelayPlayer(UUID uuid, long rate) {
    DelayPlayer delayPlayer = new DelayPlayer();
    delayPlayer.tokens = ServiceHandler.SERVICE.getDataService().getTokens(uuid);
    delayPlayer.lastUpdateTime = rate;
    return delayPlayer;
  }

  public TokensPlayer warpPlayer(UUID uuid, DelayPlayer dp) {
    return new TokensPlayer(uuid, dp.tokens);
  }

  /**
   * Used to not spam the Database or File with requests
   * It will provide an old mobcoins value like 10 seconds and it will be updated every time someone change sometihng in database
   * @param uuid
   * @return
   */
  public TokensPlayer getPlayerCache(UUID uuid) {
    if(delayPlayers.containsKey(uuid)) {
      if(delayPlayers.get(uuid).tokens != ServiceHandler.SERVICE.getDataService().getTokens(uuid)) {
        delayPlayers.replace(uuid, warpDelayPlayer(uuid, System.currentTimeMillis() + (delayFactor * 1000)));
        return warpPlayer(uuid, delayPlayers.get(uuid));
      }
    }
    if(delayPlayers.containsKey(uuid)) {
      if(delayPlayers.get(uuid).lastUpdateTime < System.currentTimeMillis()) {
        delayPlayers.replace(uuid, warpDelayPlayer(uuid, System.currentTimeMillis()+(delayFactor*1000)));
      }
      return warpPlayer(uuid, delayPlayers.get(uuid));
    } else {
      delayPlayers.put(uuid, warpDelayPlayer(uuid, System.currentTimeMillis()+(delayFactor*1000)));
      return warpPlayer(uuid, delayPlayers.get(uuid));
    }
  }

  public Object getPlayer(String s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s); else return Bukkit.getOfflinePlayer(s);
  }
  public UUID getPlayerUUID(String s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s).getUniqueId(); else return Bukkit.getOfflinePlayer(s).getUniqueId();
  }
  public String getPlayerName(String s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s).getName(); else return Bukkit.getOfflinePlayer(s).getName();
  }
  public String getPlayerName(UUID s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s).getName(); else return Bukkit.getOfflinePlayer(s).getName();
  }

  public static FileConfiguration readConfig(String file) {
    return YamlConfiguration.loadConfiguration(new File(TMTokens.PLUGIN.getPlugin().getDataFolder(), file));
  }

  private String coinVariables(String s, int amount, Player player, long time) {
     s = s.replace("%pl_amount%", amount + "");
     s = s.replace("%pl_created_by%", player.getName());
    return s;
  }

  public ItemStack createPhysicalToken(int amount, Player player, long time) {
    ItemHandler itemHandler = new ItemHandler();
    Configuration config = FilesManager.ACCESS.getConfig().getConfig();
    ArrayList<String> lore = new ArrayList<>();
    config.getStringList("physical_coin.lore").forEach(s -> lore.add(coinVariables(s, amount, player, time)));
    itemHandler.setMaterial(config.getString("physical_coin.material"));
    itemHandler.setDisplay(coinVariables(config.getString("physical_coin.display_name"), amount, player, time), lore);
    if(config.contains("physical_coin.glow"))
      itemHandler.glow = config.getBoolean("physical_coin.glow");
    if(config.contains("physical_coin.data"))
      itemHandler.customData = config.getInt("physical_coin.data");
    // Adding the key
    ItemStack is = itemHandler.build();
    NamespacedKey key = new NamespacedKey(TMTokens.PLUGIN.getPlugin(), "coin");
    ItemMeta im = is.getItemMeta();
    im.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, amount);
    is.setItemMeta(im);
    return is;
  }
}
