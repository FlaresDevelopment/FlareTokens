package net.tmtokens.lib.menu;

import net.tmtokens.lib.StringUtils.StringTools;
import net.tmtokens.lib.base.ColorAPI;
import net.tmtokens.lib.menu.item.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class Menu {

    Player holder;
    public HashMap<Integer, List<ItemHandler>> menuContent = new HashMap<>();
    public HashMap<Integer, ItemHandler> itemsInMenu = new HashMap<>();

    public HashMap<Integer, Integer> updateRate = new HashMap<>();
    public int slots;
    public Inventory inventory;
    private InventoryType type = null;

    public Menu(Player player, String name, int slots) {
        this.holder = player;
        this.slots = slots;
        createInventory(name, slots);
    }

    public Menu(Player player, String name, InventoryType type) {
        this.holder = player;
        this.type = type;
        createInventory(name, slots);
    }

    private void createInventory(String name, int slots) {
        if(type != null) inventory = Bukkit.createInventory(null, type, ColorAPI.process(name));
        else inventory = Bukkit.createInventory(null, slots, ColorAPI.process(name));
        getID();

    }

    public void deleteInventory() {
        GUI.menuHolder.remove(holder.getUniqueId());
        holder.closeInventory();
        menuContent.clear();
    }

    /**
     * This method is used to take the whole inventory and update every single item, but if you
     *  want to update only the items that have the update on true use ...
     */
    public void updateContent() {
        for(Map.Entry<Integer, List<ItemHandler>> content : menuContent.entrySet()) {
            List<ItemHandler> menuItem = content.getValue();
            if (menuItem.size() == 1) {
                ItemHandler ih = menuItem.get(0);
                if(ih != null) {
                    inventory.setItem(content.getKey(), ih.build());
                    itemsInMenu.put(content.getKey(), ih);
                }
            } else if(menuItem.size() > 1) {
                for(ItemHandler ih : menuItem) {
                    if(ih != null && ih.view) {
                        inventory.setItem(content.getKey(), ih.build());
                        itemsInMenu.put(content.getKey(), ih);
                    }
                }
            }
        }
    }

    /**
     * Use this to update only the items with update turned on!
     */
    public void updateSpecialItems() {
        for(Map.Entry<Integer, List<ItemHandler>> content : menuContent.entrySet()) {
            try {
            List<ItemHandler> menuItem = content.getValue();
            if (menuItem.size() == 1) {
                ItemHandler ih = menuItem.get(0);
                if(ih.update) {
                    inventory.setItem(content.getKey(), ih.build());
                    itemsInMenu.put(content.getKey(), ih);
                }
            } else if(menuItem.size() > 1) {
                if(updateRate.get(content.getKey()) == null) setItemsInMenuInUpdates();
                ItemHandler ih = content.getValue().get(updateRate.get(content.getKey()));
                if(ih.update && ih.view) {
                    inventory.setItem(content.getKey(), ih.build());
                    itemsInMenu.put(content.getKey(), ih);
                }
            }
        } catch (Exception e) {
            updateRate.put(content.getKey(), 0);
        }
    }
    }

    public void setItemsInMenuInUpdates() {
        for(Map.Entry<Integer, List<ItemHandler>> content : menuContent.entrySet()) {
            try {
                List<ItemHandler> menuItem = content.getValue();
                if(menuItem.size() > 1) {
                    if(!updateRate.containsKey(content.getKey()))
                        updateRate.put(content.getKey(), 0);
                    ItemHandler ih = content.getValue().get(updateRate.get(content.getKey()));
                    if(updateRate.get(content.getKey()) + 1 >= menuItem.size()) {
                        updateRate.put(content.getKey(), 0);
                    } else {
                        updateRate.put(content.getKey(), updateRate.get(content.getKey()) + 1);
                    }
                }
            } catch (Exception e) {
                updateRate.put(content.getKey(), 0);
            }
        }
    }

    private void getID() {
        GUI.menuHolder.put(holder.getUniqueId(), this);
    }

    public void assignItems(ItemHandler item) {
        if(!item.slot.isEmpty())
            if(item.slot.size() == 1) {
                if(item.slot.get(0) != -1) {
                    if(menuContent.containsKey(item.slot.get(0))) {
                        ItemHandler[] list = menuContent.get(item.slot.get(0)).toArray(new ItemHandler[menuContent.get(item.slot.get(0)).size() + 1]);
                        list[menuContent.get(item.slot.get(0)).size()] = item;
                        menuContent.put(item.slot.get(0), Arrays.asList(list));
                    } else
                        menuContent.put(item.slot.get(0), Collections.singletonList(item));
                } else {
                    fillInventory(item);
                }
            } else if (item.slot.size() > 1) {
                for(int i : item.slot) {
                    if(menuContent.get(i) != null) {
                        menuContent.get(i).add(item);
                    } else {
                        menuContent.put(i, StringTools.createList(item));
                    }
                }
            }
    }

    private void fillInventory(ItemHandler item) {
        for(int i = 0; i < slots; i++) {
            if(menuContent.get(i) != null)
                menuContent.get(i).add(item);
            else
                menuContent.put(i, Collections.singletonList(item));
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

}
