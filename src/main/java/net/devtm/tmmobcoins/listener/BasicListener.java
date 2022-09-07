package net.devtm.tmmobcoins.listener;

import net.devtm.tmmobcoins.API.MobCoinReceiveEvent;
import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.exceptions.FailedToFindInConfig;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.service.ServiceHandler;
import net.devtm.tmmobcoins.util.MobCoinsPlayer;
import net.devtm.tmmobcoins.util.StorageAccess;
import net.tmmobcoins.lib.CBA.TMPL;
import net.tmmobcoins.lib.CBA.utils.CodeArray;
import net.tmmobcoins.lib.base.VersionCheckers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;

public class BasicListener implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) { StorageAccess.createAccount(e.getPlayer().getUniqueId()); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void mobcoinsReceiveEvent(MobCoinReceiveEvent event) {
        /* Checkers */
        if (event.isCancelled()) return;
        if (event.getEntity() == null) return;
        Configuration drops = FilesManager.ACCESS.getDrops().getConfig();

        /* Drop Actions */
        if (!FilesManager.ACCESS.getDrops().getConfig().contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_action")) return;
        List<String> l = new ArrayList<>();
        for (String miniList : drops.getStringList("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_action"))
            l.add(miniList.replace("%pl_mobcoins%", event.getObtainedAmount() + ""));
        TMPL tmpl = new TMPL();
        tmpl.setCode(l);
        tmpl.process(event.getPlayer());

        /* Drop Value */
        if (!drops.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_value")) return;
        event.getMobCoinsPlayer().addMobcoins(event.getObtainedAmount());
        event.getMobCoinsPlayer().uploadPlayer();
    }

    @EventHandler
    private void onPlayerKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        /* Check if the mob exists in config */
        String configPath = ServiceHandler.SERVICE.getEventService().mobVerify(event);
        if(configPath == null) return;

        MobCoinsPlayer profile = StorageAccess.getAccount(event.getEntity().getKiller().getUniqueId());
        Configuration drops = FilesManager.ACCESS.getDrops().getConfig();
        Entity entity = event.getEntity();
        Player player = event.getEntity().getKiller();

        if(profile == null)
            TMMobCoins.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "The player profile could not be found!");

        double amount = drops.contains("entity." + entity.getName().toUpperCase(Locale.ROOT) + ".drop_value") ?
                Double.parseDouble(String.format("%.2f", generateNumber(drops, entity.getName()) * profile.getMultiplier() * FilesManager.ACCESS.getData().getConfig().getDouble("global_multiplier"))) : 0;

        MobCoinReceiveEvent eventMobcoins = new MobCoinReceiveEvent(player, profile, entity, amount);

        if(drops.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".requirement")) {
            CodeArray ca = new CodeArray();
            ca.addConditions(drops.getString("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".requirement"));
            if (!ca.checkRequierment(player))
                eventMobcoins.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(eventMobcoins);
    }

    @EventHandler
    private void playerFireworkDamage(EntityDamageByEntityEvent event) {
        if(VersionCheckers.getVersion() <= 9) return;
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
    }

    private double generateNumber(@NotNull Configuration config, @NotNull String entityName) {
        final double defaultNumber = 0;
        final String entityConfigPath = String.format(
                "entity.%s.drop_value",
                entityName.toUpperCase(Locale.ROOT)
        );

        if (!config.contains(entityConfigPath)) {
            return defaultNumber;
        }

        String dropValue = config.getString(entityConfigPath);
        if (dropValue == null) {
            return defaultNumber;
        }

        String[] definition = dropValue.substring(0, dropValue.length() - 1).split("\\(");
        String[] interval = definition[1].split(";");
        Random rand = new Random();

        // When an end-user fills in a decimal number for random_number or random_decimal throw error in console that
        // a decimal number is not supported
        Integer parsedIntervalToIntegerOne = null;
        Integer parsedIntervalToIntegerTwo = null;
        try {
            parsedIntervalToIntegerOne = Integer.parseInt(interval[0]);
            parsedIntervalToIntegerTwo = Integer.parseInt(interval[1]);
        } catch (NumberFormatException e) {
            sendConsoleError(entityConfigPath);
        }

        if (parsedIntervalToIntegerOne == null || parsedIntervalToIntegerTwo == null) {
            return defaultNumber;
        }

        int randomNextInt = rand.nextInt(
                (parsedIntervalToIntegerTwo - parsedIntervalToIntegerOne) + 1
        ) + parsedIntervalToIntegerOne;

        switch (definition[0].toLowerCase(Locale.ROOT)) {
            case "random_number":
                return randomNextInt;
             case "random_decimal":
                return rand.nextDouble() * randomNextInt;
        }
        return defaultNumber;
    }

    private void sendConsoleError(String entityConfigPath) {
        ServiceHandler.SERVICE.getLoggerService().log(Level.SEVERE, String.format(
                "There is a not supported number used in your drops.yml config at %s%s%s. "
                        + "Only rounded numbers are supported for this option",
                ChatColor.GOLD,
                entityConfigPath,
                ChatColor.RED
        ));
    }
}