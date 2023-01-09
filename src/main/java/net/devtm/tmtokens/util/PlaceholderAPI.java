package net.devtm.tmtokens.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.devtm.tmtokens.files.FilesManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class PlaceholderAPI extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "tmtokens";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TMDevelopment";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player p, String params) {
        if (p == null)
            return "";
        if (params.contains("get_tokens_"))
            return String.valueOf(Utils.UTILS.getPlayerCache(Utils.UTILS.getPlayerUUID(params.replace("get_tokens_", ""))).getTokens());
        if (params.contains("get_tokens"))
            return String.valueOf(Utils.UTILS.getPlayerCache(p.getUniqueId()).getTokens());
        else if(params.contains("get_commas_tokens"))
            return String.format("%,d", Utils.UTILS.getPlayerCache(p.getUniqueId()).getTokens());
        else if(params.contains("get_formmated_tokens")) {
            int mobcoins = Utils.UTILS.getPlayerCache(p.getUniqueId()).getTokens();
            if (mobcoins < 1000) return mobcoins + "";
            int exp = (int) (Math.log(mobcoins) / Math.log(1000));
            DecimalFormat format = new DecimalFormat("0.#");
            String value = format.format(mobcoins / Math.pow(1000, exp));
            return String.format("%s%c", value, "kMBT".charAt(exp - 1));
        }
        return null;
    }

}
