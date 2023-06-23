package net.flares.flaretokens.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.flares.flaretokens.files.FilesManager;
import net.flares.flaretokens.service.ServiceHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderAPI extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "flaretokens";
    }

    @Override
    public @NotNull String getAuthor() {
        return "FlareDevelopment";
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
        if(params.matches("rotating_shop_(\\w+)_(normal|premium)")) {
            Matcher matcher = Pattern.compile("rotating_shop_(\\w+)_(normal|premium)").matcher(params);
            matcher.find();
            ServiceHandler.SERVICE.getMenuService().updateRotatingShop(matcher.group(1));
            if(!FilesManager.ACCESS.getData().getConfig().contains("rotating_shop." + matcher.group(1) + "." + matcher.group(2) + "_last_time"))
                return params.replace(matcher.group(),"");
            return params.replace(matcher.group(),
                    Utils.UTILS.findDifference(FilesManager.ACCESS.getData().getConfig().getLong("rotating_shop." + matcher.group(1) + "." + matcher.group(2) + "_last_time"), System.currentTimeMillis()));
        }
        if (params.contains("get_tokens"))
            return String.valueOf(ServiceHandler.SERVICE.getDataService().warpPlayer(p.getUniqueId()).getTokens());
        else if(params.contains("get_formmated_tokens")) {
            int mobcoins = ServiceHandler.SERVICE.getDataService().warpPlayer(p.getUniqueId()).getTokens();
            if (mobcoins < 1000) return mobcoins + "";
            int exp = (int) (Math.log(mobcoins) / Math.log(1000));
            DecimalFormat format = new DecimalFormat("0.#");
            String value = format.format(mobcoins / Math.pow(1000, exp));
            return String.format("%s%c", value, "kMBT".charAt(exp - 1));
        }
        return null;
    }

}
