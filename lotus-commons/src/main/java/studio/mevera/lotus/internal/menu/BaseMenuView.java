package studio.mevera.lotus.internal.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.Menu;
import studio.mevera.lotus.api.menu.MenuHandler;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.spi.opener.ViewOpener;

import java.util.Objects;

/**
 * Reference {@link MenuView} implementation. Holds the resolved capacity/content and the
 * Bukkit {@link Inventory} once opened. Title extraction is delegated entirely to the
 * platform-specific {@link ViewOpener}.
 */
public class BaseMenuView<C, M extends Menu<C>> implements MenuView<C, M> {

    private final Lotus lotus;
    private final M menu;
    private final Player viewer;
    private final DataRegistry data;

    private @Nullable C title;
    private @Nullable Capacity capacity;
    private @Nullable Content content;
    private @Nullable Inventory inventory;
    private boolean open;

    public BaseMenuView(@NotNull Lotus lotus, @NotNull M menu, @NotNull Player viewer, @NotNull DataRegistry data) {
        this.lotus = Objects.requireNonNull(lotus);
        this.menu = Objects.requireNonNull(menu);
        this.viewer = Objects.requireNonNull(viewer);
        this.data = Objects.requireNonNull(data);
    }

    @Override public @NotNull Lotus lotus() { return lotus; }
    @Override public @NotNull M menu() { return menu; }
    @Override public @NotNull Player viewer() { return viewer; }
    @Override public @NotNull DataRegistry data() { return data; }
    @Override public @NotNull Capacity capacity() { return require(capacity, "capacity"); }
    @Override public @NotNull C title() {return require(title, "title");}
    @Override public @NotNull Content content() { return require(content, "content"); }
    @Override public @Nullable Inventory getInventory() { return inventory; }
    @Override public boolean isOpen() { return open; }

    /**
     * Resolves capacity and content from the template. Idempotent — safe to call before each open.
     * Title extraction is handled by the {@link ViewOpener} at inventory-creation time.
     */
    protected void resolve() {
        this.title = menu.title(this);
        this.capacity = menu.capacity(this);
        this.content = menu.content(this);
    }

    public void open(@NotNull ViewOpener opener) {
        resolve();
        this.inventory = opener.open(lotus, this);
        this.open = true;
    }

    @Override
    public void refresh() {
        if (!open || inventory == null) return;
        this.content = menu.content(this);
        repaint();
    }

    private void repaint() {
        if (inventory == null || content == null) return;
        inventory.clear();
        content.forEach((slot, button) -> inventory.setItem(slot.index(), button.item()));
    }

    public void renderInto(@NotNull Inventory target) {
        if (content == null) return;
        content.forEach((slot, button) -> target.setItem(slot.index(), button.item()));
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        if (handler() instanceof MenuHandler h && !h.onPreClick(this, event)) return;
        Slot slot = Slot.of(event.getSlot());
        content().get(slot).ifPresent(button -> {
            dispatchButton(button, event);
            repaint();
        });
        if (handler() instanceof MenuHandler h) h.onPostClick(this, event);
    }

    public void handleDrag(@NotNull InventoryDragEvent event) {
        if (handler() instanceof MenuHandler h) h.onDrag(this, event);
    }

    public void handleOpen(@NotNull InventoryOpenEvent event) {
        this.open = true;
        if (handler() instanceof MenuHandler h) h.onOpen(this, event);
    }

    public void handleClose(@NotNull InventoryCloseEvent event) {
        try {
            if (handler() instanceof MenuHandler h) h.onClose(this, event);
        } finally {
            this.open = false;
            this.inventory = null;
        }
    }

    private void dispatchButton(@NotNull Button button, @NotNull InventoryClickEvent event) {
        try {
            button.dispatch(this, event);
        } catch (RuntimeException e) {
            lotus.logger().warn("button dispatch failed in menu " + menu.name(), e);
        }
    }

    private @Nullable MenuHandler handler() {
        return menu instanceof MenuHandler h ? h : null;
    }

    private static <T> T require(@Nullable T value, String name) {
        if (value == null) throw new IllegalStateException("menu view not opened yet — '" + name + "' unresolved");
        return value;
    }
}
