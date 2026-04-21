package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Slot;

import java.util.function.BiFunction;

public record TransformingButton(
    @NotNull ItemStack item,
    @NotNull BiFunction<MenuView<?, ?>, InventoryClickEvent, Button> transformer,
    @NotNull DataRegistry data
) implements Button {

    @Override
    public @NotNull TransformingButton withItem(@NotNull ItemStack item) {
        return new TransformingButton(item, transformer, data);
    }

    @Override
    public void dispatch(@NotNull MenuView<?, ?> view, @NotNull InventoryClickEvent event) {
        Button replacement = transformer.apply(view, event);
        if (replacement != null) {
            view.content().set(Slot.of(event.getSlot()), replacement);
        }
    }
}
