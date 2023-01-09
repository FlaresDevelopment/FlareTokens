package net.devtm.tmtokens.files;

import lombok.Getter;
import net.devtm.tmtokens.files.files.*;
import net.devtm.tmtokens.TMTokensPlugin;
import net.tmtokens.lib.Lib;

import java.io.File;

@Getter
public enum FilesManager {
    ACCESS;

    private dataFile data;
    private LogsFile logs;
    private localeFile locale;
    private configFile config;

    public void initialization() {
        if (!new File(TMTokensPlugin.getPlugin(TMTokensPlugin.class).getDataFolder(),"config.yml").exists()) {
            TMTokensPlugin.getPlugin(TMTokensPlugin.class).saveResource("shop/main.yml", false);
        }
        this.data = new dataFile(TMTokensPlugin.getPlugin(TMTokensPlugin.class));
        this.locale = new localeFile(TMTokensPlugin.getPlugin(TMTokensPlugin.class));
        this.config = new configFile(TMTokensPlugin.getPlugin(TMTokensPlugin.class));
        this.logs = new LogsFile(TMTokensPlugin.getPlugin(TMTokensPlugin.class));
        loadConfig();
    }
    public void reload() {
        Lib.LIB.setLocales(FilesManager.ACCESS.getLocale().getConfig());
        this.data.reloadConfig();
        this.config.reloadConfig();
        this.locale.reloadConfig();
    }
    private void loadConfig() {
        this.data.saveDefaultConfig();
        this.config.saveDefaultConfig();
        this.locale.saveDefaultConfig();
    }
}
