package net.devtm.tmtokens.files.files;

import net.devtm.tmtokens.TMTokens;
import net.devtm.tmtokens.TMTokensPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class localeFile {
  private final TMTokensPlugin plugin;
  private final String file = "locale/en.yml";
  private FileConfiguration dataConfig = null;
  private File configFile = null;

  public localeFile(TMTokensPlugin plugin) {
    this.plugin = plugin;
    saveDefaultConfig();
  }

  /**
   * This method is used to reload the config
   */
  public void reloadConfig() {
    if (this.configFile == null) {
      this.configFile = new File(this.plugin.getDataFolder(), file);
    }
    this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
    InputStream defaultStream = this.plugin.getResource(file);
    if (defaultStream != null) {
      YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
              new InputStreamReader(defaultStream));
      this.dataConfig.setDefaults(defaultConfig);
    }
  }

  /**
   * This method is used to get the config file
   */
  public FileConfiguration getConfig() {
    if (this.dataConfig == null) {
      reloadConfig();
    }
    return this.dataConfig;
  }

  /**
   * This method is used to save the file
   */
  public void saveConfig() {
    if (this.dataConfig == null || this.configFile == null) {
      return;
    }
    try {
      this.getConfig().save(this.configFile);
    } catch (IOException e) {
      TMTokens.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "Failed to save: " + file);
    }
  }

  /**
   * This method is used to create the file for the first time the file
   */
  public void saveDefaultConfig() {
    if (this.configFile == null) {
      this.configFile = new File(this.plugin.getDataFolder(), file);
    }
    if (!this.configFile.exists()) {
      this.plugin.saveResource(file, false);
    }
  }
}