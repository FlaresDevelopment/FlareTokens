package net.flares.flaretokens;

import org.bukkit.plugin.java.JavaPlugin;

public class TMMobCoinsPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    FlareTokens.PLUGIN.start(this);
  }

  @Override
  public void onDisable() {
    FlareTokens.PLUGIN.stop(this);
  }
}
