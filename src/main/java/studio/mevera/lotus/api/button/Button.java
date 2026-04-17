package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Slot;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Sealed root of the button algebra. A button is the union of its display item, attached data,
 * and (variant-specific) interaction behaviour.
 * <p>
 * Variants:
 * <ul>
 *   <li>{@link StaticButton} — display only, click is a no-op (caller may still cancel).</li>
 *   <li>{@link ClickableButton} — runs a {@link ClickAction} on click.</li>
 *   <li>{@link TransformingButton} — replaces itself in the view on click via a transformer.</li>
 *   <li>{@link SpanningButton} — single logical button occupying multiple adjacent slots.</li>
 *   <li>{@link CompositeButton} — fan-outs a click to several child buttons in order.</li>
 *   <li>{@link CycleButton} — N-state button; each click advances to the next state.</li>
 * </ul>
 */
public sealed interface Button
    permits StaticButton, ClickableButton, TransformingButton,
            SpanningButton, CompositeButton, CycleButton {

    @NotNull ItemStack item();

    @NotNull DataRegistry data();

    @NotNull Button withItem(@NotNull ItemStack item);

    void dispatch(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event);

    static @NotNull StaticButton of(@NotNull ItemStack item) {
        return new StaticButton(item, DataRegistry.empty());
    }

    static @NotNull ClickableButton clickable(@NotNull ItemStack item, @NotNull ClickAction action) {
        return new ClickableButton(item, action, DataRegistry.empty());
    }

    static @NotNull TransformingButton transforming(
        @NotNull ItemStack item,
        @NotNull BiFunction<MenuView<?>, InventoryClickEvent, Button> transformer
    ) {
        return new TransformingButton(item, transformer, DataRegistry.empty());
    }

    static @NotNull SpanningButton spanning(
        @NotNull ItemStack item,
        @NotNull Set<Slot> footprint,
        @NotNull ClickAction action
    ) {
        return new SpanningButton(item, Set.copyOf(footprint), action, DataRegistry.empty());
    }

    static @NotNull CompositeButton composite(@NotNull ItemStack item, @NotNull Button... children) {
        return new CompositeButton(item, List.of(children), DataRegistry.empty());
    }

    static @NotNull CycleButton cycle(@NotNull Button... states) {
        return new CycleButton(List.of(states), 0, DataRegistry.empty());
    }
}
