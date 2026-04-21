package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.MenuView;

public record StaticButton(@NotNull ItemStack item, @NotNull DataRegistry data) implements Button {

    @Override
    public @NotNull StaticButton withItem(@NotNull ItemStack item) {
        return new StaticButton(item, data);
    }

    @Override
    public void dispatch(@NotNull MenuView<?, ?> view, @NotNull InventoryClickEvent event) {
        // no-op
    }
}
