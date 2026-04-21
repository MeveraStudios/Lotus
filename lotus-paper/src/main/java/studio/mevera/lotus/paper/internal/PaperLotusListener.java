package studio.mevera.lotus.paper.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.internal.AbstractLotusListener;

public final class PaperLotusListener<C> extends AbstractLotusListener<C> implements Listener {

    public PaperLotusListener(@NotNull Lotus<C> lotus) {
        super(lotus);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onClick(@NotNull InventoryClickEvent event) {
        handleClick(event, event.getView().getTopInventory(), event.getView().getBottomInventory());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrag(@NotNull InventoryDragEvent event) {
        handleDrag(event, event.getView().getTopInventory());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onOpen(@NotNull InventoryOpenEvent event) {
        handleOpen(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClose(@NotNull InventoryCloseEvent event) {
        handleClose(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }
}
