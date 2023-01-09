package net.devtm.tmtokens.util;

import net.devtm.tmtokens.TMTokens;
import net.devtm.tmtokens.service.ServiceHandler;
import net.tmtokens.lib.base.CustomPlaceholders;
import org.bukkit.entity.Player;

public class PlaceholdersClass implements CustomPlaceholders {
    @Override
    public String process(String text, Player player) {
        text = text.replace("%pl_tokens%", ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).getTokens() + "");
        return text;
    }
}
