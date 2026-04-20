# Lotus Documentation

A modern, type-safe GUI framework for Paper 1.21+ and Spigot 1.8.8.

## Module layout

| Module          | Use when…             |
|-----------------|-----------------------|
| `lotus-commons` | Internal base (never import directly) |
| `lotus-paper`   | Paper 1.21+           |
| `lotus-spigot`  | Spigot 1.8.8          |

## Contents

1. [Getting Started](./getting-started.md) — install, boot, first menu
2. [Core Concepts](./concepts.md) — `Capacity`, `Slot`, `SlotMask`, `Content`
3. [Menus](./menus.md) — `Menu<C>`, `InteractiveMenu<C>`, `MenuView`, handler hooks
4. [Buttons](./buttons.md) — the six button variants and when to use each
5. [Content Builder](./content.md) — fluent layout DSL
6. [Data Registry](./data-registry.md) — type-safe per-view state
7. [Pagination](./pagination.md) — immutable definition + per-player session
8. [Advanced](./advanced.md) — custom openers, options, lifecycle

## One-minute tour (Paper)

```java
// In onEnable:
Lotus lotus = PaperLotus.create(this);

public final class ShopMenu implements PaperMenu {
    public @NotNull Capacity capacity(MenuView<?> view) { return Capacity.ofRows(3); }
    public @NotNull Component title(MenuView<?> view)   { return Component.text("Shop"); }

    public @NotNull Content content(MenuView<?> view) {
        return Content.builder(view.capacity())
            .fillBorder(Button.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
            .set(1, 4, Button.clickable(
                new ItemStack(Material.DIAMOND),
                (v, e) -> v.viewer().sendMessage("Bought!")))
            .build();
    }
}

lotus.openMenu(player, new ShopMenu());
```

## One-minute tour (Spigot 1.8.8)

```java
// In onEnable:
Lotus lotus = SpigotLotus.create(this);

public final class ShopMenu implements Menu<String> {
    public @NotNull Capacity capacity(MenuView<?> view) { return Capacity.ofRows(3); }
    public @NotNull String title(MenuView<?> view)      { return "&6Shop"; }

    public @NotNull Content content(MenuView<?> view) {
        return Content.builder(view.capacity())
            .fillBorder(Button.of(new ItemStack(Material.STAINED_GLASS_PANE)))
            .set(1, 4, Button.clickable(
                new ItemStack(Material.DIAMOND),
                (v, e) -> v.viewer().sendMessage("Bought!")))
            .build();
    }
}

lotus.openMenu(player, new ShopMenu());
```
