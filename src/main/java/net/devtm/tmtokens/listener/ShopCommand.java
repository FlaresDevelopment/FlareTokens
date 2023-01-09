package net.devtm.tmtokens.listener;

import net.devtm.tmtokens.files.FilesManager;
import net.devtm.tmtokens.service.ServiceHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.logging.Level;

// Class code: 13
public class ShopCommand implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] array = event.getMessage().replaceFirst("/", "").split(" ");
        try {
            if(FilesManager.ACCESS.getConfig().getConfig().getString("shop.open_command").equalsIgnoreCase(array[0]))
                ServiceHandler.SERVICE.getMenuService()
                        .openMenu(event.getPlayer(), FilesManager.ACCESS.getConfig().getConfig().getString("shop.main_shop"));
        } catch (Exception e) {
            ServiceHandler.SERVICE.getLoggerService().log(Level.SEVERE, e, "You made a mistake in config.yml at the shop configurator: " + e.getMessage());
        }
    }
}
