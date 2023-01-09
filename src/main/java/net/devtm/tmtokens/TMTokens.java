package net.devtm.tmtokens;

import lombok.Getter;
import net.devtm.tmtokens.command.TokensCommand;
import net.devtm.tmtokens.files.FilesManager;
import net.devtm.tmtokens.listener.BasicListener;
import net.devtm.tmtokens.listener.ShopCommand;
import net.devtm.tmtokens.service.ServiceHandler;
import net.devtm.tmtokens.util.CustomCBA;
import net.devtm.tmtokens.util.PlaceholderAPI;
import net.devtm.tmtokens.util.PlaceholdersClass;
import net.devtm.tmtokens.util.Utils;
import net.tmtokens.lib.Lib;
import net.tmtokens.lib.base.ColorAPI;
import net.tmtokens.lib.base.VersionCheckers;
import net.tmtokens.lib.base.bStatsMetrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

@Getter
public enum TMTokens {
  PLUGIN;
  private TMTokensPlugin plugin;
  private final boolean enabledMenu = true;

  public void start(final TMTokensPlugin plugin) {
    FilesManager.ACCESS.initialization();
    Lib.LIB.libStart(plugin);
    Lib.LIB.setCustomPlaceholders(new PlaceholdersClass());
    Lib.LIB.setLocales(FilesManager.ACCESS.getLocale().getConfig());
    Lib.LIB.enableCBA();
    Lib.LIB.getComponentBasedAction().registerMethod(new CustomCBA());
    this.plugin = plugin;
    startStorage();
    assert plugin != null : "Something went wrong! Plugin was null.";
    this.init();
    startLog();
    usebStats();
    commandsSetup();
    ServiceHandler.SERVICE.getLoggerService().fileSetup();
    ServiceHandler.SERVICE.getDataService().reloadDataService();
    Utils.UTILS.reloadUtils();
  }

  /**
   * Stop method for the plugin - {@link JavaPlugin}
   *
   * @param plugin the plugin instance
   */
  public void stop(final TMTokensPlugin plugin) {
    this.plugin = plugin;
    stopLog();
  }

  /**
   * Initialize everything
   */
  private void init() {
    this.registerListener();
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
      new PlaceholderAPI().register();
    if(this.enabledMenu) {
      Lib.LIB.enableGUI();
    }
  }

  public void commandsSetup() {
    plugin.getCommand("tokens").setExecutor(new TokensCommand());
  }

  public void startStorage() {
    if(FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.type").equalsIgnoreCase("mysql")) {
      Lib.LIB.enableMySQL(
              FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.host"),
              FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.username"),
              FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.password"),
              FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.database"),
              FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.port"),
              FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.driver")
      );
      Lib.LIB.getMySQL().sqlIO.createTable(FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.table"),
              "player VARCHAR(100), uuid VARCHAR(100), tokens INT(10)");
    }
  }

  private void startLog() {
    plugin.getLogger().log(Level.INFO, ColorAPI.process("Loading TMTokens v" + plugin.getDescription().getVersion()));
    plugin.getLogger().log(Level.INFO, ColorAPI.process("Saving data using: " + FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.type")));
    plugin.getLogger().log(Level.INFO, ColorAPI.process("Hooking into supported plugins"));

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
      plugin.getLogger().log(Level.INFO, ColorAPI.process("PlaceholderAPI is not on the server or not enabled! (( Placeholder support is disabled ))"));
    else plugin.getLogger().log(Level.INFO, ColorAPI.process("PlaceholderAPI is supported!"));

    if (Bukkit.getPluginManager().getPlugin("Vault") == null)
      Bukkit.getLogger().log(Level.WARNING, ColorAPI.process("Vault is not on the server or not enabled!  (( Economy support is disabled ))"));
    else plugin.getLogger().log(Level.INFO, ColorAPI.process("Vault is supported!"));

    plugin.getLogger().log(Level.INFO, ColorAPI.process("Checking version..."));
    new VersionCheckers(getPlugin(), 91848).getUpdate(version -> {
      if (getPlugin().getDescription().getVersion().equals(version)) {
        plugin.getLogger().log(Level.INFO, ColorAPI.process("Running latest build (" + version + ")"));
      } else {
        Bukkit.getLogger().log(Level.WARNING, ColorAPI.process("Running an old build (" + getPlugin().getDescription().getVersion()
                + ") Latest build is (" + version + "). Please try to update to the last version!"));
      }
      plugin.getLogger().log(Level.INFO, ColorAPI.process("Made with love in Romania"));
    });
  }

  private void stopLog() {
    plugin.getLogger().log(Level.INFO, ColorAPI.process("Disabling TMTokens v" + plugin.getDescription().getVersion()));
    plugin.getLogger().log(Level.INFO, ColorAPI.process("Closing databases connections"));
    plugin.getLogger().log(Level.INFO, ColorAPI.process("Thanks for using our plugin!"));
    plugin.getLogger().log(Level.INFO, ColorAPI.process("Made with love in Romania"));

  }

  /**
   * Register all listener
   */
  private void registerListener() {
    final Listener[] listeners = new Listener[]{
        new BasicListener(), new ShopCommand()
    };

    Arrays.stream(listeners)
        .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this.plugin));
  }

  private void usebStats() {
    if(FilesManager.ACCESS.getConfig().getConfig().getBoolean("allow_bstats")) {
      bStatsMetrics metrics = new bStatsMetrics(getPlugin(), 14217);
    }
  }

}
