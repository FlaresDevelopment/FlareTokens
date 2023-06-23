package net.flares.flaretokens.util.components;

import net.flares.flaretokens.API.TokensPlayer;
import net.flares.flaretokens.service.ServiceHandler;
import net.flarepowered.Lib;
import net.flarepowered.TML.components.Component;
import net.flarepowered.TML.objects.TMLState;
import net.flarepowered.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyComponent implements Component {

    Pattern pattern = Pattern.compile("(?i)\\[buy\\((tokens|money|mobcoins)\\)] (\\d+\\.?\\d*)\\s?(rotating_shop_name=(\\w+))?\\s?(rotating_shop_item=(\\w+))?");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = this.pattern.matcher(string);
        if (matcher.find()) {
            if (matcher.group(1) == null)
                throw new ComponentException("The component [buy(" + matcher.group(1) + ")] has no message. We are skipping this item.");
            if (matcher.group(2) == null)
                throw new ComponentException("The component [buy(" + matcher.group(1) + ")] has no message. We are skipping this item.");
            switch (matcher.group(1).toLowerCase(Locale.ROOT)) {
                case "mobcoins":
                    TokensPlayer tp = ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId());
                    if(tp.getTokens() >= Integer.parseInt(matcher.group(2))) {
                        if(matcher.group(6) != null)
                            if(!ServiceHandler.SERVICE.getMenuService().buyItem(player, matcher.group(4), matcher.group(6)))
                                return TMLState.FORCED_QUIT;
                        tp.setTokens(Integer.parseInt(matcher.group(2)));
                        return TMLState.COMPLETED;
                    }
                    break;
                case "money":
                    if(Lib.LIB.getEcon().getBalance(player) >= Double.parseDouble(matcher.group(2))) {
                        Lib.LIB.getEcon().withdrawPlayer(player, Double.parseDouble(matcher.group(2)));
                        return TMLState.COMPLETED;
                    }
                    break;
            }
            return TMLState.FORCED_QUIT;
        } else {
            return TMLState.NOT_A_MATCH;
        }
    }
}
