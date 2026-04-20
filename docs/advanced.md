# Advanced

## `Lotus.Options`

Immutable runtime configuration, set via the factory's customizer argument.

```java
// Paper — same options available on SpigotLotus.create(this, builder -> { ... })
Lotus lotus = PaperLotus.create(this, builder -> {
    builder.allowBottomInventoryClick(true)   // let hotbar shift-clicks through
           .dynamicButtonAction(false)        // advanced: allow buttons to be picked up / moved
           .debug(false);                     // verbose [lotus] logs
});
```

| Option                      | Default | Effect                                               |
|-----------------------------|---------|------------------------------------------------------|
| `allowBottomInventoryClick` | `true`  | Clicks in the player inventory pass through normally |
| `dynamicButtonAction`       | `false` | Opt-in: allows pickup/place interactions on buttons  |
| `debug`                     | `false` | Gates `[lotus]` debug log lines                      |

Read them back with `lotus.options()`.

## `ItemBuilder`

Both platform modules ship a concrete `ItemBuilder` class with the same simple name but
different packages. Both extend `AbstractItemBuilder<B, C>` from `lotus-commons`.

### Paper — `studio.mevera.lotus.paper.ItemBuilder`

```java
import studio.mevera.lotus.paper.ItemBuilder;

ItemStack sword = ItemBuilder.of(Material.DIAMOND_SWORD)
    .displayName(Component.text("Legendary Blade").color(NamedTextColor.AQUA))
    .lore(Component.text("A very sharp blade").color(NamedTextColor.GRAY))
    .amount(1)
    .unbreakable(true)
    .build();
```

### Spigot — `studio.mevera.lotus.spigot.ItemBuilder`

```java
import studio.mevera.lotus.spigot.ItemBuilder;

ItemStack sword = ItemBuilder.of(Material.DIAMOND_SWORD)
    .displayName("&bLegendary Blade")    // & color codes translated automatically
    .lore("&7A very sharp blade")
    .amount(1)
    .unbreakable(true)
    .build();
```

## Custom `ViewOpener`

The opener strategy decides **how** a Bukkit `Inventory` is created and opened for a given
`MenuView`. `PaperLotus.create()` wires `PaperViewOpener` (Adventure-native) and
`SpigotLotus.create()` wires `SpigotViewOpener` (legacy String title). To replace per inventory type:

```java
lotus.registerOpener(InventoryType.HOPPER, (l, view) -> {
    Inventory inv = Bukkit.createInventory(view, InventoryType.HOPPER, "Custom");
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
lotus.registeredMenu("shopmenu");            // Optional<Menu<?>>
```

## Tracking open views

```java
lotus.viewOf(player);             // Optional<MenuView<?>>
lotus.openViews();                // Collection<MenuView<?>>
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

If you mutate content **outside** a click handler (e.g., a scheduler task), call `view.refresh()` yourself.

## Spanning buttons lifecycle

Removing a spanning button cleanly means clearing **every slot** in its footprint. Clearing one slot leaves orphaned copies visible. Either keep a reference and iterate its footprint, or use `content.clear()` + rebuild.

## Errors in click handlers

If button dispatch throws a `RuntimeException`, Lotus catches it, logs via `lotus.logger().warn(...)`, and keeps the menu usable. Check plugin logs for `[lotus] button dispatch failed in menu <name>`.

## Package layout

Three artifacts:

```
studio.mevera:lotus-commons
└── studio.mevera.lotus
    ├── Lotus, LotusBuilder          ← main facade
    ├── api/                         ← what you import from commons
    │   ├── button/                  ← Button, variants, ClickAction
    │   ├── content/                 ← Content, ContentView/Editor, ContentBuilder
    │   ├── data/                    ← Key, DataRegistry
    │   ├── item/                    ← AbstractItemBuilder<B, C>
    │   ├── menu/                    ← Menu<C>, InteractiveMenu<C>, MenuView, MenuHandler
    │   ├── pagination/              ← Pagination, PaginationSession, PageLayout<C>,
    │   │                               PageLayoutBuilder, ContentSource, ComponentRenderer
    │   └── slot/                    ← Capacity, Slot, SlotMask, Direction, SlotIterator
    ├── spi/                         ← extension points (ViewOpener, PaginationSessionFactory)
    └── internal/                    ← do NOT import

studio.mevera:lotus-paper            ← Paper 1.21+
└── studio.mevera.lotus.paper
    ├── PaperLotus                   ← static factory
    ├── ItemBuilder                  ← extends AbstractItemBuilder<ItemBuilder, Component>
    └── api/
        ├── menu/
        │   ├── PaperMenu            ← alias for Menu<Component>
        │   └── PaperInteractiveMenu ← alias for InteractiveMenu<Component>
        └── pagination/
            ├── PaperPageLayout      ← alias for PageLayout<Component>
            └── PaperPageLayoutBuilder

studio.mevera:lotus-spigot           ← Spigot 1.8.8
└── studio.mevera.lotus.spigot
    ├── SpigotLotus                  ← static factory
    └── ItemBuilder                  ← extends AbstractItemBuilder<ItemBuilder, String>
```

Rule: import from `api.*` and `spi.*`. Stay out of `internal.*` — no compatibility guarantees.
