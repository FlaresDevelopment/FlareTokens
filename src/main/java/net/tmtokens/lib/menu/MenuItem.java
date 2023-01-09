package net.tmtokens.lib.menu;

import net.tmtokens.lib.menu.item.ItemHandler;

import java.util.List;

public class MenuItem {

    public List<ItemHandler> items;

    public List<ItemHandler> getItems() {
        return items;
    }

    public void setItems(List<ItemHandler> items) {
        this.items = items;
    }

}
