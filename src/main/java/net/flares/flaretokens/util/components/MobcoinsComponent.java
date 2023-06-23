package net.flares.flaretokens.util.components;

import net.flarepowered.TML.components.Component;
import net.flarepowered.TML.objects.TMLState;
import net.flarepowered.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class MobcoinsComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[tokens_(.+)] (\\d+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [tokens] you used has no tags, use [tokens_remove], [tokens_set], [tokens_add]");
            if(matcher.group(2) == null)
                throw new ComponentException("The component [tokens_" + matcher.group(1) + "] has no value entered, please use [tokens_" + matcher.group(1) + "] <value>");

            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
