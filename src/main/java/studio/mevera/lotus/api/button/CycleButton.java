package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Slot;

import java.util.List;

/**
 * A multi-state button that advances through its {@link #states()} on each click. The displayed
 * item is delegated to the active state, so any {@link Button} variant — static label, clickable
 * with side-effect, etc. — can serve as a state.
 * <p>
 * On click the active state's own {@link Button#dispatch(MenuView, InventoryClickEvent)} runs
 * first (so users may attach per-state behaviour such as "play this sound"); the button then
 * replaces itself in the view's {@link studio.mevera.lotus.api.content.Content} with a copy
 * positioned at the next index, modulo {@code states.size()}. The cycle wraps.
 * <p>
 * Typical uses: tri-state filter toggles ("OFF / WHITELIST / BLACKLIST"), difficulty selectors,
 * page-size pickers — anywhere a single slot needs to step through a small enumeration.
 * <p>
 * <b>Immutability:</b> the record itself is immutable; "advancing state" produces a new
 * {@code CycleButton} and writes it to the slot. Do not rely on a particular {@code CycleButton}
 * instance persisting across clicks — observe the slot's current button via
 * {@link studio.mevera.lotus.api.content.ContentView#get(Slot)}.
 */
public record CycleButton(
    @NotNull List<Button> states,
    int currentIndex,
    @NotNull DataRegistry data
) implements Button {

    public CycleButton {
        if (states.isEmpty()) {
            throw new IllegalArgumentException("cycle button must declare at least one state");
        }
        states = List.copyOf(states);
        if (currentIndex < 0 || currentIndex >= states.size()) {
            throw new IndexOutOfBoundsException("currentIndex out of states range: " + currentIndex);
        }
    }

    @Override
    public @NotNull ItemStack item() {
        return states.get(currentIndex).item();
    }

    /**
     * Returns the currently active child button.
     */
    public @NotNull Button current() {
        return states.get(currentIndex);
    }

    /**
     * @return a new {@link CycleButton} positioned one step forward (wrapping at the end).
     */
    public @NotNull CycleButton advanced() {
        int next = (currentIndex + 1) % states.size();
        return new CycleButton(states, next, data);
    }

    /**
     * Replaces the displayed item. Because the rendered item is normally derived from the active
     * state, this overrides only the current state's item by wrapping it in a {@link StaticButton}
     * with the new item; behaviour of the active state is preserved by copying it first.
     */
    @Override
    public @NotNull CycleButton withItem(@NotNull ItemStack item) {
        var rebuilt = new java.util.ArrayList<>(states);
        rebuilt.set(currentIndex, states.get(currentIndex).withItem(item));
        return new CycleButton(List.copyOf(rebuilt), currentIndex, data);
    }

    @Override
    public void dispatch(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
        current().dispatch(view, event);
        view.content().set(Slot.of(event.getSlot()), advanced());
    }
}
