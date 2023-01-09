package net.devtm.tmtokens.service;

import net.devtm.tmtokens.TMTokens;
import net.devtm.tmtokens.files.FilesManager;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public class LoggerService {

    public void fileSetup() {
        File f = new File(TMTokens.PLUGIN.getPlugin().getDataFolder(), "data/logs.yml");
        f.delete();
        FilesManager.ACCESS.getLogs().saveDefaultConfig();
    }

    public void log(Level level, Exception e, String s) {
        TMTokens.PLUGIN.getPlugin().getLogger().log(level,s);
        FilesManager.ACCESS.getLogs().getConfig().set(s.replace(" ", "_") + "TIME:" + System.currentTimeMillis(), Arrays.toString(e.getStackTrace()));
        FilesManager.ACCESS.getLogs().getConfig().set(s.replace(" ", "_") + "TIME:" + System.currentTimeMillis(), e.getCause());

        FilesManager.ACCESS.getLogs().saveConfig();
    }

    public void log(Level level, String s) {
        TMTokens.PLUGIN.getPlugin().getLogger().log(level,s);
        FilesManager.ACCESS.getLogs().getConfig().set(s.replace(" ", "_") + "TIME:" + System.currentTimeMillis(), s);
        FilesManager.ACCESS.getLogs().saveConfig();
    }

}
