# Menus

A `Menu` is the **template**. A `MenuView<M>` is a **live instance** for one player.

## `Menu` — the template

Every menu declares its title, capacity, and content. All three receive the current `MenuView` so they can vary per player.

```java
public final class ProfileMenu implements Menu {
    @Override public @NotNull Component title(MenuView<?> view) {
        return Component.text(view.viewer().getName() + "'s profile");
    }
    @Override public @NotNull Capacity capacity(MenuView<?> view) {
        return Capacity.ofRows(3);
    }
    @Override public @NotNull Content content(MenuView<?> view) {
        return Content.builder(capacity(view))
            .set(1, 4, Button.of(head(view.viewer())))
            .build();
    }
    @Override public @NotNull String name() { return "profile"; }
}
```

Optional overrides:
- `name()` — used by `lotus.registerMenu(...)` and `lotus.openMenu(player, "name")`. Default: class simple name.
- `type()` — Bukkit `InventoryType`. Default: `CHEST`.

## `MenuView<M>` — the live view

Handed to every menu/button hook. Exposes:

| Method           | Description                         |
|------------------|-------------------------------------|
| `menu()`         | The template                        |
| `viewer()`       | The player                          |
| `content()`      | Current `Content` (read + edit)     |
| `data()`         | Per-view `DataRegistry`             |
| `title()`        | Resolved title                      |
| `capacity()`     | Resolved capacity                   |
| `getInventory()` | The Bukkit `Inventory` (nullable)   |
| `isOpen()`       | Still showing?                      |
| `refresh()`      | Re-run `Menu.content(view)` + repaint |

Call `view.refresh()` when your underlying model changes and you want the menu to re-resolve everything.

## `MenuHandler` — optional hooks

Implement `MenuHandler` (or `InteractiveMenu = Menu + MenuHandler`) to react to lifecycle events.

```java
public final class ShopMenu implements InteractiveMenu {
    @Override public @NotNull Component title(MenuView<?> v) { ... }
    @Override public @NotNull Capacity capacity(MenuView<?> v) { ... }
    @Override public @NotNull Content content(MenuView<?> v) { ... }

    @Override public boolean onPreClick(MenuView<?> view, InventoryClickEvent event) {
        event.setCancelled(true);  // cancel stealing items
        return true;               // return false to skip button dispatch
    }

    @Override public void onPostClick(MenuView<?> view, InventoryClickEvent event) {
        // after all buttons fired
    }

    @Override public void onOpen(MenuView<?> view, InventoryOpenEvent event) { ... }
    @Override public void onClose(MenuView<?> view, InventoryCloseEvent event) { ... }
    @Override public void onDrag(MenuView<?> view, InventoryDragEvent event) { ... }
}
```

### Click flow

```
click → onPreClick → (if true) button.dispatch + repaint → onPostClick
```

If `onPreClick` returns `false`, no button dispatch runs. `onPostClick` always fires after dispatch (whether a button was clicked or not).

## Opening

```java
lotus.openMenu(player, menu);                   // fresh DataRegistry
lotus.openMenu(player, menu, dataRegistry);     // seed with state
lotus.openMenu(player, "menuName");             // requires registerMenu()
```

Each call returns the `MenuView<M>`. Lotus tracks it by `player.getUniqueId()`:

```java
lotus.viewOf(player);      // Optional<MenuView<?>>
lotus.openViews();         // Collection of all open views
```

Next: [Buttons](./buttons.md).
