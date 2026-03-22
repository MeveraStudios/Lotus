package studio.mevera.menus.misc.button.actions.impl;

import studio.mevera.menus.base.MenuView;
import studio.mevera.menus.misc.button.actions.ButtonClickAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class CloseMenuAction implements ButtonClickAction {

    @Override
    public String tag() {
        return "CLOSE";
    }

    @Override
    public void execute(MenuView<?> menu, InventoryClickEvent event) {
        event.getWhoClicked().closeInventory();
    }
}
