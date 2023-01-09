package net.devtm.tmtokens.service;

import net.devtm.tmtokens.util.Utils;
import net.tmtokens.lib.CBA.TMPL;
import net.tmtokens.lib.menu.Menu;
import net.tmtokens.lib.menu.item.ItemHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.logging.Level;

public class MenuService {

    public void openMenu(Player player, String s) {
        try {
            FileConfiguration config = Utils.readConfig("shop/" + s + ".yml");
            if(config.contains("menu_permission"))
                if(!player.hasPermission(config.getString("menu_permission"))) return;
            if(config.contains("open_requirement")) {
                TMPL tmpl = new TMPL();
                tmpl.setCode(Utils.readConfig("shops/" + s).getStringList("open_requirement"));
                if(!tmpl.process(player)) return;
            }
            Menu menu = config.contains("menu_type") ? new Menu(player, config.getString("menu_title"), InventoryType.valueOf(config.getString("menu_type"))) :
                    new Menu(player, config.getString("menu_title"), config.getInt("size"));
            for (String s1 : config.getConfigurationSection("items").getKeys(false))
                menu.assignItems(new ItemHandler().setPlayer(player).autoGetter(config, "items", s1));
            menu.updateContent();
            player.openInventory(menu.getInventory());
        }catch (Exception e) {
            ServiceHandler.SERVICE.getLoggerService().log(Level.SEVERE, e, "Tried to open a menu but failed error message: " + e.getMessage());
        }
    }

}
