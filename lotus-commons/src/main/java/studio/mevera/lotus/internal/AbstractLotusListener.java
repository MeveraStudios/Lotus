package studio.mevera.lotus.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.util.Pair;
import studio.mevera.lotus.internal.menu.BaseMenuView;

import java.util.Objects;

/**
 * Shared listener logic that is agnostic to the platform-specific InventoryView ABI.
 */
public abstract class AbstractLotusListener<C> {

    protected final Lotus<C> lotus;
    private volatile @Nullable Pair<Slot, Button> lastPickedUpButton;

    protected AbstractLotusListener(@NotNull Lotus<C> lotus) {
        this.lotus = Objects.requireNonNull(lotus);
    }

    protected void handleClick(
        @NotNull InventoryClickEvent event,
        @NotNull Inventory topInventory,
        @NotNull Inventory bottomInventory
    ) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        MenuView<?, ?> view = resolve(player, topInventory);
        if (view == null) return;

        Inventory clicked = event.getClickedInventory();
        if (clicked == null || clicked.equals(bottomInventory)) {
            event.setCancelled(!lotus.options().allowBottomInventoryClick());
            return;
        }
        event.setCancelled(true);
        handleDynamicButtonAction(view, event);
        if (view instanceof BaseMenuView<?, ?> base) base.handleClick(event);
    }

    protected void handleDrag(@NotNull InventoryDragEvent event, @NotNull Inventory topInventory) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        MenuView<?, ?> view = resolve(player, topInventory);
        if (view == null) return;
        event.setCancelled(true);
        if (view instanceof BaseMenuView<?, ?> base) base.handleDrag(event);
    }

    @SuppressWarnings("unchecked")
    protected void handleOpen(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuView<?, ?> view)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        lotus.track(player, (MenuView<C, ?>) view);
        if (view instanceof BaseMenuView<?, ?> base) base.handleOpen(event);
    }

    protected void handleClose(@NotNull InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        MenuView<?, ?> view = lotus.resolveView(player);
        if (view == null) return;
        try {
            if (view instanceof BaseMenuView<?, ?> base) base.handleClose(event);
        } finally {
            lotus.untrack(player);
        }
    }

    protected void handleQuit(@NotNull Player player) {
        lotus.untrack(player);
    }

    @SuppressWarnings("unchecked")
    private @Nullable MenuView<?, ?> resolve(@NotNull Player player, @NotNull Inventory topInventory) {
        MenuView<?, ?> tracked = lotus.resolveView(player);
        if (tracked != null) return tracked;
        if (topInventory.getHolder() instanceof MenuView<?, ?> holder) {
            lotus.track(player, (MenuView<C, ?>) holder);
            return holder;
        }
        return null;
    }

    private void handleDynamicButtonAction(@NotNull MenuView<?, ?> view, @NotNull InventoryClickEvent event) {
        if (!lotus.options().dynamicButtonAction()) return;

        InventoryAction action = event.getAction();
        Slot clickedSlot = Slot.of(event.getSlot());
        Button button = view.content().get(clickedSlot).orElse(null);

        if (isPickupAction(action) && button != null) {
            lastPickedUpButton = Pair.of(clickedSlot, button);
            return;
        }

        if (!isPlaceAction(action) || lastPickedUpButton == null) return;

        Slot oldSlot = Objects.requireNonNull(lastPickedUpButton).left();
        Button pickedUpButton = Objects.requireNonNull(lastPickedUpButton).right();

        view.content().set(oldSlot, null);
        view.content().set(clickedSlot, pickedUpButton);

        Inventory inventory = view.getInventory();
        if (inventory != null) {
            inventory.setItem(oldSlot.index(), null);
            inventory.setItem(clickedSlot.index(), pickedUpButton.item());
        }
        lastPickedUpButton = null;
    }

    private static boolean isPickupAction(@NotNull InventoryAction action) {
        return action == InventoryAction.PICKUP_ONE
            || action == InventoryAction.PICKUP_SOME
            || action == InventoryAction.PICKUP_HALF
            || action == InventoryAction.PICKUP_ALL;
    }

    private static boolean isPlaceAction(@NotNull InventoryAction action) {
        return action == InventoryAction.PLACE_ONE
            || action == InventoryAction.PLACE_SOME
            || action == InventoryAction.PLACE_ALL;
    }
}
