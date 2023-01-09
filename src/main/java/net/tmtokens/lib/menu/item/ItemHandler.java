package net.tmtokens.lib.menu.item;

import net.tmtokens.lib.CBA.TMPL;
import net.tmtokens.lib.CBA.utils.CodeArray;
import net.tmtokens.lib.base.MessageHandler;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemHandler {

    public ItemStack item;

    public String displayName;
    public String viewRequirement;
    public String material;
    public int customData;
    public List<String> lore;
    public List<String> onClickCommands;
    public List<Integer> slot = new ArrayList<>();
    public List<String> viewRequirementList = new ArrayList<>();
    public boolean view = true;
    private Player player;
    public boolean update;
    public boolean glow;
    public int priority = 99;
    public int amount = 1;

    public ItemHandler() {}

    public static ItemHandler item() {
        return new ItemHandler();
    }

    public ItemHandler autoGetter(Configuration config, String pre, String itemName) {

            setMaterial(config.getString(pre + "." + itemName + ".material"));
            setDisplay(config.getString(pre + "." + itemName + ".display_name"), config.getStringList(pre + "." + itemName + ".lore"));
            setInformation(config.getInt(pre + "." + itemName + ".data"),config.getBoolean(pre + "." + itemName + ".update"));
            if(config.contains(pre + "." + itemName + ".click_commands"))
                setClickCommands(config.getStringList(pre + "." + itemName + ".click_commands"));
            else
                setClickCommands(null);
            if(config.contains(pre + "." + itemName + ".view_requirement"))
                this.viewRequirement = config.getString(pre + "." + itemName + ".view_requirement");
            else
                this.viewRequirement = null;

            if(config.contains(pre + "." + itemName + ".view_requirement_list"))
                this.viewRequirementList = config.getStringList(pre + "." + itemName + ".view_requirement_list");
            else
                this.viewRequirementList = null;

            if(viewRequirement != null)
                this.view = getViewRequirement(viewRequirement);
            if(viewRequirementList != null)
                this.view = getViewRequirementList(viewRequirementList);

            if(config.contains(pre + "." + itemName + ".priority"))
                this.priority = config.getInt(pre + "." + itemName + ".priority");

            if(config.contains(pre + "." + itemName + ".glow"))
                this.glow = config.getBoolean(pre + "." + itemName + ".glow");

            if(config.contains(pre + "." + itemName + ".amount"))
                this.amount = config.getInt(pre + "." + itemName + ".amount");

            if(config.contains(pre + "." + itemName + ".slots")) {
                setSlots(config.getIntegerList(pre + "." + itemName + ".slots"));
            } else if(config.contains(pre + "." + itemName + ".slot")) {
                setSlots(config.getInt(pre + "." + itemName + ".slot"));
            } else if(config.contains(pre + "." + itemName + ".slot")) {
                setSlots(config.getInt(pre + "." + itemName + ".slot"));
            }
        return this;
    }

    public ItemHandler setViewRequirement(String viewRequirement) {
        this.viewRequirement = viewRequirement;
        return this;
    }

    public ItemHandler setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemHandler setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public ItemHandler setSlots(List<Integer> slot) {
        this.slot = slot;
        return this;
    }

    public ItemHandler setSlots(int slot) {
        this.slot.add(slot);
        return this;
    }

    public ItemHandler setInformation(int customData,boolean update) {
        this.update = update;
        this.customData = customData;
        return this;
    }
    public ItemHandler setMaterial(String material) {
        this.material = material;
        return this;
    }

    public ItemHandler setDisplay(String displayName, List<String> lore) {
        this.lore = lore; this.displayName = displayName;
        return this;
    }
    public ItemHandler setClickCommands(List<String> onClickCommands) {
            this.onClickCommands = onClickCommands;
        return this;
    }

    public boolean getViewRequirement(String s) {
        CodeArray ca = new CodeArray();
        ca.setConditions(Collections.singletonList(s));
        return ca.checkRequierment(player);
    }

    public boolean getViewRequirementList(List<String> s) {
        CodeArray ca = new CodeArray();
        ca.setConditions(s);
        return ca.checkRequierment(player);
    }

    public void onItemClick(Player player, ClickType clickType) {
        if (onClickCommands != null) {
            TMPL tmpl = new TMPL();
            tmpl.setCode(onClickCommands); tmpl.process(player); tmpl.provideClickType(clickType);
        }
    }

    public ItemStack build() {
        if(item == null)
            if(material.contains("[HEAD]"))
                item = SkullHandler.getHeadFromValue(material.replace("[HEAD] ", ""));
            else
                item = new ItemStack(Material.matchMaterial(material));
        List<String> l = new ArrayList<>();
        for (String s : this.lore)
            l.add(MessageHandler.chat(s).placeholderAPI(player).toStringColor());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageHandler.chat(displayName).placeholderAPI(player).toStringColor());
        meta.setLore(l);
        item.setAmount(amount);
        meta.setCustomModelData(customData);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        if(glow)
            item.addEnchantment(Enchantment.ARROW_FIRE, 1);
        return this.item;
    }

}
