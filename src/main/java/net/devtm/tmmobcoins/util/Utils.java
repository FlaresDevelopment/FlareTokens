package net.devtm.tmmobcoins.util;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.service.LocaleService;
import net.tmmobcoins.lib.base.MessageHandler;
import net.tmmobcoins.lib.menu.Menu;
import net.tmmobcoins.lib.menu.item.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class Utils {

  /**
   * IF you are watching this I will change it soon it's a mess
   */

  public int firstJoinGiveMobcoins = FilesManager.ACCESS.getConfig().getConfig().getInt("first_join_give_mobcoins");

  public String table = FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.table");

  public HashMap<UUID, Double> mobcoinsHistory = new HashMap<>();
  public HashMap<UUID, Long> mobcoinsTime = new HashMap<>();

  public HashMap<UUID, Double> multiplierHistory = new HashMap<>();
  public HashMap<UUID, Long> multiplierTime = new HashMap<>();

  public static FileConfiguration readConfig(String file) {
        /*System.out.println(file);
        System.out.println(new File(TMPanels.PLUGIN.getPlugin().getDataFolder(), file));
        System.out.println(YamlConfiguration.loadConfiguration(new File(TMPanels.PLUGIN.getPlugin().getDataFolder(), file)).contains("menu_command"));
        System.out.println(TMPanels.PLUGIN.getPlugin().getConfig().contains("menu_command"));*/
    return YamlConfiguration.loadConfiguration(new File(TMMobCoins.PLUGIN.getPlugin().getDataFolder(), file));
  }
  public static FileConfiguration readConfig(Path file) {
        /*System.out.println(file);
        System.out.println(new File(TMPanels.PLUGIN.getPlugin().getDataFolder(), file));
        System.out.println(YamlConfiguration.loadConfiguration(new File(TMPanels.PLUGIN.getPlugin().getDataFolder(), file)).contains("menu_command"));
        System.out.println(TMPanels.PLUGIN.getPlugin().getConfig().contains("menu_command"));*/
    return YamlConfiguration.loadConfiguration(new File(file.toString()));
  }

  public void runnable(JavaPlugin plugin) {
    /*new BukkitRunnable() {
      @Override
      public void run() {
        if(!FilesManager.ACCESS.getConfig().getConfig().getString("shop.settings.shop_type").equalsIgnoreCase("rotating")) return;
        if(FilesManager.ACCESS.getData().getConfig().getLong("refresh_data.normal") <= System.currentTimeMillis()) {
          regenerateItems(FilesManager.ACCESS.getConfig().getConfig(), "normal");
          FilesManager.ACCESS.getData().getConfig().set("refresh_data.normal", System.currentTimeMillis()
                  + (FilesManager.ACCESS.getConfig().getConfig().getInt("shop.settings.rotating_item.settings.normal.refresh_time") * 1000L));
          FilesManager.ACCESS.getData().saveConfig();
        }
        if(FilesManager.ACCESS.getData().getConfig().getLong("refresh_data.special")  <= System.currentTimeMillis()) {
          regenerateItems(FilesManager.ACCESS.getConfig().getConfig(), "special");
          FilesManager.ACCESS.getData().getConfig().set("refresh_data.special", System.currentTimeMillis()
                  + (FilesManager.ACCESS.getConfig().getConfig().getInt("shop.settings.rotating_item.settings.special.refresh_time") * 1000L));
          FilesManager.ACCESS.getData().saveConfig();
        }
      }
    }.runTaskTimerAsynchronously(plugin, 1, 1);*/
  }

  /**
   * Used to not spam the Database or File with requests
   * It will provide an old mobcoins value like 10 seconds and it will be updated every time someone change sometihng in database
   * @param uuid
   * @return
   */
  public double getMobCoinsInDelay(UUID uuid) {
    int factor = FilesManager.ACCESS.getConfig().getConfig().getInt("storage_type.read_delay");
    if(mobcoinsTime.containsKey(uuid)) {
      if(mobcoinsTime.get(uuid) < System.currentTimeMillis()) {
        mobcoinsTime.put(uuid, System.currentTimeMillis() + factor);
        mobcoinsHistory.put(uuid, StorageAccess.getAccount(uuid).getMobcoins());
      }
      return mobcoinsHistory.get(uuid);
    } else {
      mobcoinsTime.put(uuid, System.currentTimeMillis() + factor);
      mobcoinsHistory.put(uuid, StorageAccess.getAccount(uuid).getMobcoins());
    }
    return mobcoinsHistory.get(uuid);
  }

  public double getMultiplierInDelay(UUID uuid) {
    int factor = FilesManager.ACCESS.getConfig().getConfig().getInt("storage_type.read_delay");
    if(multiplierTime.containsKey(uuid)) {
      if(multiplierTime.get(uuid) < System.currentTimeMillis()) {
        multiplierTime.put(uuid, System.currentTimeMillis() + factor);
        multiplierHistory.put(uuid, StorageAccess.getAccount(uuid).getMultiplier());
      }
      return multiplierHistory.get(uuid);
    } else {
      multiplierTime.put(uuid, System.currentTimeMillis() + factor);
      multiplierHistory.put(uuid, StorageAccess.getAccount(uuid).getMultiplier());
    }
    return multiplierHistory.get(uuid);
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
    public String findDifference(long start_date, long end_date) {
      long difference_In_Time = start_date - end_date;
      long difference_In_Seconds = (difference_In_Time / 1000) % 60;
      long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
      long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
      long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
      long difference_In_Days = (difference_In_Time  / (1000 * 60 * 60 * 24))  % 365;
      return difference_In_Days + " days "
              + difference_In_Hours + " hours " + difference_In_Minutes  + " minutes " + difference_In_Seconds + " seconds";
    }
}
