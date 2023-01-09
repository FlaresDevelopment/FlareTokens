package net.tmtokens.lib.CBA.utils;

import net.devtm.tmtokens.service.ServiceHandler;
import net.tmtokens.lib.Lib;
import net.tmtokens.lib.base.ColorAPI;
import net.tmtokens.lib.base.MessageHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

public class RequireParser {

    ClickType clickType;

    public RequireParser() {}

    public boolean getResult(String[] args, Player player) {
        return compile(args, player);
    }

    private boolean compile(String[] arguments, Player player) {
        switch (arguments[0].toLowerCase(Locale.ROOT)) {
            case "javascript":
                break;
            case "expression":
                return expressionParser(player, arguments[1], arguments[2], arguments[3]);
            case "permission":
                return player.hasPermission(arguments[1]);
            case "xp":
                if (player.getLevel() >= Integer.parseInt(arguments[1])) {
                    return true;
                }
                break;
            case "tokens":
                if (ServiceHandler.SERVICE.getDataService().getTokens(player.getUniqueId()) >= Double.parseDouble(arguments[1])) {
                    if(arguments.length == 3) {
                        if(arguments[2].equalsIgnoreCase("remove")) {
                            ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).removeTokens(Integer.parseInt(arguments[1]));
                        }
                    }
                    return true;
                }
                break;
            case "money":
                System.out.println("Not Now!");
                break;
            case "items":
                if(arguments.length == 4) {
                    int amount = Integer.parseInt(arguments[2]);
                    Material mat = Material.matchMaterial(arguments[1]);
                    ItemStack itemStack = new ItemStack(mat, amount);
                    if(player.getInventory().contains(itemStack)) {
                        if(Boolean.parseBoolean(arguments[3])) {
                            player.getInventory().removeItem(itemStack);
                        }
                        return true;
                    }
                } else {
                    Lib.LIB.getPlugin().getLogger().log(Level.INFO, ColorAPI.process("Failed! Please check: https://panels.devtm.net/component-based-actions/requirement-syntax"));
                }
                break;
            case "!permission":
                return !player.hasPermission(arguments[1]);
            case "click_type":
                if(clickType != null)
                    return ClickType.valueOf(arguments[1]).equals(clickType);
                else return true;
        }
        return false;
    }
    private boolean expressionParser(Player player, String operation1, String operator, String operation2) {
        String op1 = MessageHandler.chat(operation1).placeholderAPI(player).toString();
        String op2 = MessageHandler.chat(operation2).placeholderAPI(player).toString();
        try {
            switch (operator) {
                case "==":
                    return Objects.equals(op1, op2);
                case "!=":
                    return !Objects.equals(op1, op2);
                case ">=":
                    return Integer.parseInt(op1) >= Integer.parseInt(op2);
                case "<=":
                    return Integer.parseInt(op1) <= Integer.parseInt(op2);
            }
        } catch (Exception e) {
            Lib.LIB.getPlugin().getLogger().log(Level.INFO, ColorAPI.process("&7(( &cERROR &7)) &cTMPL Error expression is wrong! Please check the documentations and the online checker: &f" + e.getMessage()));
        }
        return false;
    }
    public RequireParser provideClickType(ClickType clickType) {
        this.clickType = clickType;
        return this;
    }

}
