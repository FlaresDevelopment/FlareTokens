package net.tmtokens.lib.CBA;

import net.tmtokens.lib.CBA.utils.CodeArray;
import net.tmtokens.lib.CBA.utils.CodeCompiler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TMPL {

    private List<String> codeList = new ArrayList<>();

    ClickType clickType;

    public TMPL() {}

    public void setCode(String s) {
        codeList.add(s);
    }

    public void setCode(List<String> codeList) {
        this.codeList = codeList;
    }

    public boolean process(Player player) {
        HashMap<Integer, CodeArray> codeCompilerOutput;
        boolean test = true;
        if(!codeList.isEmpty()) {
            codeCompilerOutput = new CodeCompiler().process(codeList);
            for(CodeArray s : codeCompilerOutput.values()) {
                s.provideClickType(clickType);
                test = test && s.checkRequierment(player);
            }
        }
        return test;
    }

    public void provideClickType(ClickType clickType) {
        this.clickType = clickType;
    }

}
