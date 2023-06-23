package net.flares.flaretokens.util.components;

import net.flarepowered.TML.components.Component;
import net.flarepowered.TML.objects.TMLState;
import net.flarepowered.base.MessageHandler;
import net.flarepowered.exceptions.ComponentException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class OpenMenuComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[openmenu(\\s+(.+))?] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(3) == null)
                throw new ComponentException("The component [CONSOLE] has no console command. We are skipping this item.");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), MessageHandler.chat(matcher.group(3)).placeholderAPI(player).prefix().toStringColor());
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
