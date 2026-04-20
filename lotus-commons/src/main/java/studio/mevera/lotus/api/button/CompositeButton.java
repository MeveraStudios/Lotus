package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.MenuView;

import java.util.List;

/**
 * A button that aggregates several child buttons under a single display item, dispatching each
 * child in declaration order on click. Implements the Composite pattern over the {@link Button}
 * algebra: the composite IS-A Button and HAS-A list of Buttons.
 * <p>
 * Use this when one click should perform several independent effects without forcing the caller
 * to manually compose {@link ClickAction}s — for example "play a sound + send a message + run
 * a command" can be three small {@link ClickableButton}s combined into one composite.
 * <p>
 * The composite owns its own {@link #item()}; child items are ignored for rendering. This keeps
 * "what the user sees" decoupled from "what happens on click", which is the whole point of the
 * pattern in this context.
 * <p>
 * <b>Caveats:</b> children that mutate the view (notably {@link TransformingButton}, which
 * replaces the slot's button on click) compose unpredictably — the first such child wins and
 * subsequent children dispatch against a button that is no longer in the view. Prefer
 * {@link StaticButton} and {@link ClickableButton} children for predictable composition.
 */
public record CompositeButton(
    @NotNull ItemStack item,
    @NotNull List<Button> children,
    @NotNull DataRegistry data
) implements Button {

    public CompositeButton {
        children = List.copyOf(children);
    }

    @Override
    public @NotNull CompositeButton withItem(@NotNull ItemStack item) {
        return new CompositeButton(item, children, data);
    }

    @Override
    public void dispatch(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
        for (Button child : children) {
            child.dispatch(view, event);
        }
    }
}
