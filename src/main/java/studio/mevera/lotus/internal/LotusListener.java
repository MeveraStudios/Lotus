package studio.mevera.lotus.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
 * Single Bukkit listener that bridges inventory events into the Lotus runtime. Resolves the
 * {@link MenuView} either from the per-player open-view registry or from the inventory holder.
 */
public final class LotusListener implements Listener {

    private final Lotus lotus;
    private volatile @Nullable Pair<Slot, Button> lastPickedUpButton;

    public LotusListener(@NotNull Lotus lotus) {
        this.lotus = Objects.requireNonNull(lotus);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        MenuView<?> view = resolve(player, event.getView().getTopInventory());
        if (view == null) return;

        Inventory clicked = event.getClickedInventory();
        if (clicked == null || clicked.equals(event.getView().getBottomInventory())) {
            event.setCancelled(!lotus.options().allowBottomInventoryClick());
            return;
        }
        event.setCancelled(true);
        handleDynamicButtonAction(view, event);
        if (view instanceof BaseMenuView<?> base) base.handleClick(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        MenuView<?> view = resolve(player, event.getView().getTopInventory());
        if (view == null) return;
        event.setCancelled(true);
        if (view instanceof BaseMenuView<?> base) base.handleDrag(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuView<?> view)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        lotus.track(player, view);
        if (view instanceof BaseMenuView<?> base) base.handleOpen(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClose(@NotNull InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        MenuView<?> view = lotus.resolveView(player);
        if (view == null) return;
        try {
            if (view instanceof BaseMenuView<?> base) base.handleClose(event);
        } finally {
            lotus.untrack(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        lotus.untrack(event.getPlayer());
    }

    private MenuView<?> resolve(@NotNull Player player, @NotNull Inventory topInventory) {
        MenuView<?> tracked = lotus.resolveView(player);
        if (tracked != null) return tracked;
        if (topInventory.getHolder() instanceof MenuView<?> holder) {
            lotus.track(player, holder);
            return holder;
        }
        return null;
    }

    private void handleDynamicButtonAction(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
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
