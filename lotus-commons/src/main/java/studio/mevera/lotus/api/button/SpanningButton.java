package studio.mevera.lotus.api.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Slot;

import java.util.Set;

/**
 * A single logical button whose visual presence and click target span multiple slots — typically
 * used for banners, multi-cell icons, region selectors or "tile" art that should read as one
 * element to the user.
 * <p>
 * The {@link #footprint()} is the immutable set of slots this button claims. Placement is most
 * conveniently done via {@link studio.mevera.lotus.api.content.ContentEditor#placeSpanning(SpanningButton)},
 * which writes the same instance to every slot in the footprint. Because the {@code Content}
 * map is keyed by {@link Slot}, every footprint slot resolves back to this same instance, and
 * Bukkit will render {@link #item()} in each one.
 * <p>
 * On click, {@link #action()} fires once per click event regardless of which slot in the
 * footprint was clicked. The originating slot remains available via
 * {@link InventoryClickEvent#getSlot()} should the action wish to discriminate.
 * <p>
 * <b>Lifecycle note:</b> removing a spanning button should be done by clearing every slot in
 * its footprint — clearing only one leaves the remainder visible but orphaned.
 */
public record SpanningButton(
    @NotNull ItemStack item,
    @NotNull Set<Slot> footprint,
    @NotNull ClickAction action,
    @NotNull DataRegistry data
) implements Button {

    public SpanningButton {
        if (footprint.isEmpty()) {
            throw new IllegalArgumentException("spanning button footprint must contain at least one slot");
        }
        footprint = Set.copyOf(footprint);
    }

    @Override
    public @NotNull SpanningButton withItem(@NotNull ItemStack item) {
        return new SpanningButton(item, footprint, action, data);
    }

    @Override
    public void dispatch(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
        action.onClick(view, event);
    }
}
