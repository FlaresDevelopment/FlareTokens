package net.devtm.tmmobcoins.command;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.service.LocaleService;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ShopMenuCommand extends BukkitCommand {

    String description;

    public ShopMenuCommand(String cmdName, String description) {
        super(cmdName);
        this.description = description;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        return true;
    }
}
