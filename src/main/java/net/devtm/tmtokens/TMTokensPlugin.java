package net.devtm.tmtokens;

import org.bukkit.plugin.java.JavaPlugin;

public class TMTokensPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    TMTokens.PLUGIN.start(this);
  }

  @Override
  public void onDisable() {
    TMTokens.PLUGIN.stop(this);
  }
}
