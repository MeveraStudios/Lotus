# Advanced

## `Lotus.Options`

Immutable runtime configuration, set once at build time.

```java
Lotus lotus = Lotus.builder(plugin)
    .allowBottomInventoryClick(true)   // let hotbar shift-clicks through
    .dynamicButtonAction(false)        // advanced: allow buttons to be picked up / moved
    .debug(false)                      // verbose [lotus] logs
    .build();
```

| Option                      | Default | Effect                                               |
|-----------------------------|---------|------------------------------------------------------|
| `allowBottomInventoryClick` | `true`  | Clicks in the player inventory pass through normally |
| `dynamicButtonAction`       | `false` | Opt-in: allows pickup/place interactions on buttons  |
| `debug`                     | `false` | Gates `[lotus]` debug log lines                      |

Read them back with `lotus.options()`.

## Custom `ViewOpener`

The opener strategy decides **how** a Bukkit `Inventory` is created and opened for a given `MenuView`. Lotus ships a sensible default that uses Adventure-native `Bukkit.createInventory(...)`.

Replace per inventory type:

```java
lotus.registerOpener(InventoryType.HOPPER, (l, view) -> {
    Inventory inv = Bukkit.createInventory(view, InventoryType.HOPPER, view.title());
    // custom rendering, decorations, sound, etc.
    ((BaseMenuView<?>) view).renderInto(inv);
    view.viewer().openInventory(inv);
    return inv;
});
```

`ViewOpener` lives in `studio.mevera.lotus.spi.opener` — it's an SPI boundary, stable to extend.

## Menu registration & command-driven opens

```java
lotus.registerMenu(new ShopMenu());
lotus.openMenu(player, "shopmenu");          // case-insensitive lookup
lotus.registeredMenu("shopmenu");            // Optional<Menu>
```

Great for config-driven menus where commands like `/menu <name>` resolve by string.

## Tracking open views

Lotus keeps a live map of `UUID → MenuView<?>`.

```java
lotus.viewOf(player);             // Optional<MenuView<?>>
lotus.openViews();                // Collection<MenuView<?>>
lotus.track(player, view);        // internal helpers
lotus.untrack(player);
```

Common use: broadcast a `view.refresh()` when your backing data changes.

```java
for (MenuView<?> v : lotus.openViews()) {
    if (v.menu() instanceof ShopMenu) v.refresh();
}
```

## Refresh vs manual mutation

- **`view.refresh()`** — re-runs `Menu.content(view)` from scratch. Use when the underlying model changed.
- **`view.content().set(...)` / `update(...)`** — surgical edits. Use inside click handlers when you only need to swap one slot. Lotus auto-repaints after click dispatch.

If you mutate content **outside** a click handler (e.g., from a scheduler task), call `view.refresh()` yourself — Lotus only auto-repaints on click.

## `MenuView` data

Per-view `DataRegistry` is seeded on open and mutable for the life of the view:

```java
lotus.openMenu(player, menu, DataRegistry.empty().put(Keys.CATEGORY, "weapons"));
```

Use it to carry "which tab was I on?" or "who am I inspecting?" without closing the menu.

## Spanning buttons lifecycle

Removing a spanning button cleanly means clearing **every slot** in its footprint. Clearing one slot leaves orphaned copies visible in the others. Either keep a reference to the `SpanningButton` and iterate its footprint, or use `content.clear()` + rebuild.

## Repaint after button mutation

`BaseMenuView.handleClick` always repaints after dispatch. This matters for:
- `TransformingButton` — the new button's item appears immediately.
- `CycleButton` — the advanced state's item appears immediately.

You don't have to call anything. If you wrote a custom `Button` variant that mutates `view.content()`, the same auto-repaint covers you.

## Errors in click handlers

If your button dispatch throws a `RuntimeException`, Lotus catches it, logs via `lotus.logger().warn(...)`, and continues. The menu stays usable. Check your plugin logs for `[lotus] button dispatch failed in menu <name>`.

Nothing to handle in user code — just don't swallow exceptions silently upstream if you need to see them.

## Package layout (for plugin authors browsing the jar)

```
studio.mevera.lotus
├── Lotus, LotusBuilder          ← facade
├── api/                         ← what you import
│   ├── button/                  ← Button, variants, ClickAction
│   ├── content/                 ← Content, ContentView/Editor, ContentBuilder
│   ├── data/                    ← Key, DataRegistry
│   ├── menu/                    ← Menu, InteractiveMenu, MenuView, MenuHandler
│   ├── pagination/              ← Pagination, Session, Layout, Source, Renderer
│   └── slot/                    ← Capacity, Slot, SlotMask, Direction, Iterator
├── spi/                         ← extension points (ViewOpener)
└── internal/                    ← implementation details; do not import
```

Rule of thumb: import from `api.*` and `spi.*`. Stay out of `internal.*` — no compatibility guarantees.
