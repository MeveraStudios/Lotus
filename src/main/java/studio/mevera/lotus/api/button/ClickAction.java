package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.menu.MenuView;

/**
 * Behaviour invoked when a {@link ClickableButton} is clicked.
 */
@FunctionalInterface
public interface ClickAction {

    void onClick(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event);

    default @NotNull ClickAction andThen(@NotNull ClickAction next) {
        return (view, event) -> {
            this.onClick(view, event);
            next.onClick(view, event);
        };
    }
}
