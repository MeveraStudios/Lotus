package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.menu.MenuView;

/**
 * Action that runs when a button is clicked.
 */
@FunctionalInterface
public interface ClickAction {

    /**
     * Handles the click.
     */
    void onClick(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event);

    /**
     * Returns an action that runs this action first, then {@code next}.
     */
    default @NotNull ClickAction andThen(@NotNull ClickAction next) {
        return (view, event) -> {
            this.onClick(view, event);
            next.onClick(view, event);
        };
    }
}
