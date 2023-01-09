package net.devtm.tmtokens.util;

import net.devtm.tmtokens.service.ServiceHandler;
import net.tmtokens.lib.CBA.CBAMethods;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomCBA implements CBAMethods {

    List<String> comp = Arrays.asList("open_menu", "tokens_give", "tokens_set", "tokens_remove");

    @Override
    public void process(Player player, String component, Object obj) {
        Pattern pattern = Pattern.compile("\\[(\\w+)\\]");
        if(component.matches(pattern.pattern())) return;
        Matcher matcher = pattern.matcher(component);
        matcher.find();
        String actionContent = component.replaceFirst(pattern.pattern(), "").replaceFirst("\\s+", "");
        switch (matcher.group(1).toLowerCase(Locale.ROOT)) {
            case "open_menu":
                player.closeInventory();
                ServiceHandler.SERVICE.getMenuService().openMenu(player, actionContent + ".yml");
                break;
            case "tokens_give":
                ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).giveTokens(Integer.parseInt(actionContent));
                break;
            case "tokens_set":
                ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).setTokens(Integer.parseInt(actionContent));
                break;
            case "tokens_remove":
                ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).removeTokens(Integer.parseInt(actionContent));
                break;
        }
    }

    @Override
    public List<String> getComponents() {
        return comp;
    }
}
