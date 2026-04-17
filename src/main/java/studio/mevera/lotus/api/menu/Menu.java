package studio.mevera.lotus.api.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.slot.Capacity;

/**
 * Declarative template for a menu. Implementations supply title, capacity and content for a given
 * {@link MenuView}. Stateless by convention — per-view state belongs in {@code view.data()}.
 * <p>
 * For lifecycle hooks (open/close/click), additionally implement {@link MenuHandler} or extend
 * {@link InteractiveMenu}.
 */
public interface Menu {

    @NotNull Component title(@NotNull MenuView<?> view);

    @NotNull Capacity capacity(@NotNull MenuView<?> view);

    @NotNull Content content(@NotNull MenuView<?> view);

    default @NotNull String name() {
        return getClass().getSimpleName();
    }

    default @NotNull InventoryType type() {
        return InventoryType.CHEST;
    }
}
