package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.MenuView;

public record ClickableButton(@NotNull ItemStack item, @NotNull ClickAction action, @NotNull DataRegistry data)
    implements Button {

    @Override
    public @NotNull ClickableButton withItem(@NotNull ItemStack item) {
        return new ClickableButton(item, action, data);
    }

    public @NotNull ClickableButton withAction(@NotNull ClickAction action) {
        return new ClickableButton(item, action, data);
    }

    @Override
    public void dispatch(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
        action.onClick(view, event);
    }
}
