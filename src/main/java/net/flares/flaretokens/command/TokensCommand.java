package net.flares.flaretokens.command;

import net.flares.flaretokens.FlareTokens;
import net.flares.flaretokens.files.FilesManager;
import net.flares.flaretokens.service.LocaleService;
import net.flares.flaretokens.service.ServiceHandler;
import net.flares.flaretokens.util.Utils;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class TokensCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 1) {
            help(commandSender);
        } else {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "reload":
                    if(!commandSender.hasPermission("flaretokens.command.reload")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    FilesManager.ACCESS.reload();
                    commandSender.sendMessage(MessageHandler.message("commands.reload.success").prefix().toStringColor());
                    commandSender.sendMessage(MessageHandler.message("commands.reload.commands").prefix().toStringColor());
                    FlareTokens.PLUGIN.startStorage();
                    commandSender.sendMessage(MessageHandler.message("commands.reload.mysql").prefix().toStringColor());
                    break;
                case "resetshopdata":
                    if(!commandSender.hasPermission("flaretokens.admin.resetshopdata")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    FilesManager.ACCESS.getData().getConfig().set("rotating_shop", null);
                    FilesManager.ACCESS.getData().saveConfig();
                    break;
                case "set":
                    if(!commandSender.hasPermission("flaretokens.command.set")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    if(args.length > 2) {
                        try {
                            int amount = Integer.parseInt(args[2]);
                            ServiceHandler.SERVICE.getDataService().setTokens(Utils.UTILS.getPlayerUUID(args[1]), amount);
                            commandSender.sendMessage(MessageHandler.message("commands.set.success").prefix()
                                    .replace("%pl_player%", Utils.UTILS.getPlayerName(args[1])).replace("%pl_tokens%", amount + "")
                                    .placeholderAPI(commandSender).toStringColor());
                            if(!args[1].equalsIgnoreCase(commandSender.getName()) && Utils.UTILS.getPlayer(args[1]) instanceof Player)
                                Bukkit.getPlayer(args[1]).sendMessage(MessageHandler.message("commands.set.received").prefix()
                                        .replace("%pl_player%", commandSender.getName()).replace("%pl_tokens%", amount + "")
                                        .placeholderAPI(commandSender).toStringColor());
                        } catch (Exception e) {
                            ServiceHandler.SERVICE.getLoggerService().log(Level.SEVERE, e, "Some set command faild view the logs for more info!");
                            commandSender.sendMessage(MessageHandler.message("commands.set.help").prefix().toStringColor());
                        }
                    } else commandSender.sendMessage(MessageHandler.message("commands.set.help").prefix().toStringColor());
                    break;
                case "remove":
                    if(!commandSender.hasPermission("flaretokens.command.remove")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    if(args.length > 2) {
                        try {
                            int amount = Integer.parseInt(args[2]);
                            UUID uuid = Utils.UTILS.getPlayerUUID(args[1]);
                            commandSender.sendMessage(MessageHandler.message("commands.remove.success").prefix()
                                    .replace("%pl_player%", Utils.UTILS.getPlayerName(args[1])).replace("%pl_tokens%", amount + "")
                                    .placeholderAPI(commandSender).toStringColor());
                            if(!args[1].equalsIgnoreCase(commandSender.getName()) && Utils.UTILS.getPlayer(args[1]) instanceof Player)
                                Bukkit.getPlayer(args[1]).sendMessage(MessageHandler.message("commands.remove.received").prefix()
                                        .replace("%pl_player%", commandSender.getName()).replace("%pl_tokens%", amount + "")
                                        .placeholderAPI(commandSender).toStringColor());
                            ServiceHandler.SERVICE.getDataService().warpPlayer(uuid).removeTokens(amount);
                        } catch (Exception e) {
                            commandSender.sendMessage(MessageHandler.message("commands.remove.help").prefix().toStringColor());
                        }
                    } else commandSender.sendMessage(MessageHandler.message("commands.remove.help").prefix().toStringColor());
                    break;
                case "give":
                    if(!commandSender.hasPermission("flaretokens.command.give")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    if(args.length > 2) {
                        try {
                            int amount = Integer.parseInt(args[2]);
                            UUID uuid = Utils.UTILS.getPlayerUUID(args[1]);
                            commandSender.sendMessage(MessageHandler.message("commands.give.success").prefix()
                                    .replace("%pl_player%", Utils.UTILS.getPlayerName(args[1])).replace("%pl_tokens%", amount + "")
                                    .placeholderAPI(commandSender).toStringColor());
                            if(!args[1].equalsIgnoreCase(commandSender.getName()) && Utils.UTILS.getPlayer(args[1]) instanceof Player)
                                Bukkit.getPlayer(args[1]).sendMessage(MessageHandler.message("commands.give.received").prefix()
                                        .replace("%pl_player%", commandSender.getName()).replace("%pl_tokens%", amount + "")
                                        .placeholderAPI(commandSender).toStringColor());
                            ServiceHandler.SERVICE.getDataService().warpPlayer(uuid).giveTokens(amount);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ServiceHandler.SERVICE.getLoggerService().log(Level.SEVERE, e, "Give command errors");
                            commandSender.sendMessage(MessageHandler.message("commands.give.help").prefix().toStringColor());
                        }
                    } else commandSender.sendMessage(MessageHandler.message("commands.give.help").prefix().toStringColor());
                    break;
                case "balance":
                    if(!commandSender.hasPermission("flaretokens.command.balance")) { commandSender.sendMessage(MessageHandler.message(LocaleService.BASIC_no_permission.get).prefix().toStringColor()); return true;}
                    try {
                        if(args.length == 1) {
                            commandSender.sendMessage(MessageHandler.message("commands.balance.player").prefix()
                                    .replace("%pl_player%", commandSender.getName()).replace("%pl_tokens%", "" + ServiceHandler.SERVICE.getDataService().warpPlayer(((Player) commandSender).getUniqueId()).getTokens())
                                    .placeholderAPI(commandSender).toStringColor());
                        } else if(commandSender.hasPermission("flaretokens.command.balance.others")) {
                            commandSender.sendMessage(MessageHandler.message("commands.balance.otherplayer").prefix()
                                    .replace("%pl_player%", args[1]).replace("%pl_tokens%", "" + ServiceHandler.SERVICE.getDataService().warpPlayer(Utils.UTILS.getPlayerUUID(args[1])).getTokens()).placeholderAPI(commandSender).toStringColor());
                        }
                    } catch (Exception e) {
                        commandSender.sendMessage(MessageHandler.message("commands.balance.help").prefix().toStringColor());
                    }
                    break;
                case "pay":
                    if(!commandSender.hasPermission("flaretokens.command.pay")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    try {
                        if(args.length == 3) {
                            try {
                                int amount = Integer.parseInt(args[2]);
                                if(commandSender.getName().equalsIgnoreCase(args[1])) {
                                    commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix().replace("%pl_player%", args[1]).replace("%pl_mobcoins%", args[2]).placeholderAPI(commandSender).toStringColor());
                                    return false;
                                }
                                commandSender.sendMessage(MessageHandler.message("commands.pay.success").prefix()
                                        .replace("%pl_player%", args[1]).replace("%pl_tokens%", amount + "").placeholderAPI(commandSender).toStringColor());
                                ServiceHandler.SERVICE.getDataService().warpPlayer(Utils.UTILS.getPlayerUUID(args[1])).giveTokens(amount);
                                ServiceHandler.SERVICE.getDataService().warpPlayer(((Player) commandSender).getUniqueId()).removeTokens(amount);
                            } catch (Exception e) {
                                commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix()
                                        .replace("%pl_player%", args[1]).replace("%pl_tokens%", args[2]).placeholderAPI(commandSender).toStringColor());
                            }
                        } else {
                            commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix().placeholderAPI(commandSender).toStringColor());
                        }
                    } catch (Exception e) {
                        commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix().toStringColor());
                    }
                    break;
                case "help":
                    help(commandSender);
                    break;
            }
        }
        return false;
    }

    private void help(CommandSender commandSender) {
        if(!commandSender.hasPermission("flaretokens.command.help")) {
            commandSender.sendMessage(MessageHandler.chat("\n  &7(v" + FlareTokens.PLUGIN.getPlugin().getDescription().getVersion() + ")").toStringColor());
            commandSender.sendMessage(MessageHandler.message("basic.no_permission").replace("%pl_prefix%", "").toStringColor());
            commandSender.sendMessage("\n");
        } else {
            commandSender.sendMessage(MessageHandler
                    .chat("\n  <GRADIENT:#FFC837-#FFC837>Flare Tokens</GRADIENT> &7(v" + FlareTokens.PLUGIN.getPlugin().getDescription().getVersion() + ")\n \n  &f&nArguments&f: &7[] Required, () Optional." +
                                "\n \n  &#f7971e▸ &7/tokens give [player] [amount]\n  &#f9a815▸ &7/tokens set [player] [amount]" +
                            "\n  &#fab011▸ &7/tokens remove [player] [amount]\n  &#fcb90d▸ &7/tokens balance (player)\n  &#fdc109▸ &7/tokens reload <files/database/all> " +
                            "\n")
                    .toStringColor());

            if (VersionCheckers.getVersion() >= 16) {
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸ Support: \",\"color\":\"#F72B2B\"},{\"text\":\"[Wiki]\",\"color\":\"#F72B2B\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://mobcoins.devtm.net/\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click To Open\"}},{\"text\":\" \",\"color\":\"#F72B2B\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click To Open\"}},{\"text\":\"[Discord]\",\"color\":\"#F72B2B\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.com/invite/XFtV7qgajR\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click To Open\"}}]"));
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸ \",\"color\":\"red\"},{\"text\":\" Plugin: \",\"color\":\"red\"},{\"text\":\"[SpigotMC]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/authors/xmaikiyt.508656/\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click to open\"}}]"));
            } else{
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸ Support: \",\"color\":\"red\"},{\"text\":\"[Wiki]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://mobcoins.devtm.net/\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}},{\"text\":\" \",\"color\":\"red\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}},{\"text\":\"[Discord]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.com/invite/XFtV7qgajR\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}}]"));
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸\",\"color\":\"red\"},{\"text\":\" Plugin: \",\"color\":\"red\"},{\"text\":\"[SpigotMC]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/authors/xmaikiyt.508656/\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}}]"));
            }
            commandSender.sendMessage(MessageHandler.chat("\n\n&7&oNote: If you need help using our plugin check our wiki or join our discord server (https://discord.com/invite/SqXCR9QvXj).").toStringColor());
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            case 1:
                List<String> a = new ArrayList<>();
                for(String s : Arrays.asList("give", "set", "remove", "balance", "help", "multiplier"))
                    if(sender.hasPermission("flaretokens.command." + s)) a.add(s);
                return a;
            case 2:
                if(args[0].equalsIgnoreCase("multiplier")) {
                    return Arrays.asList("set", "reset", "global");
                }
                if(!args[0].equalsIgnoreCase("help")) {
                    List<String> list = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(pl -> list.add(pl.getName()));
                    return list;
                }
            case 3:
                if(args[0].equalsIgnoreCase("multiplier")) {
                    if(args[1].equalsIgnoreCase("global")) {
                        return Collections.singletonList("[amount]");
                    } else {
                        List<String> list = new ArrayList<>();
                        Bukkit.getOnlinePlayers().forEach(pl -> list.add(pl.getName()));
                        return list;
                    }
                }
                if(!args[0].equalsIgnoreCase("help")) {
                    return Collections.singletonList("[amount]");
                }
            case 4:
                if(args[0].equalsIgnoreCase("multiplier")) {
                    return Collections.singletonList("[amount]");
                }
        }
        return null;
    }
}