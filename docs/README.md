# Lotus Documentation

A modern, type-safe Paper GUI framework.

## Contents

1. [Getting Started](./getting-started.md) — install, boot, first menu
2. [Core Concepts](./concepts.md) — `Capacity`, `Slot`, `SlotMask`, `Content`
3. [Menus](./menus.md) — `Menu`, `InteractiveMenu`, `MenuView`, handler hooks
4. [Buttons](./buttons.md) — the six button variants and when to use each
5. [Content Builder](./content.md) — fluent layout DSL
6. [Data Registry](./data-registry.md) — type-safe per-view state
7. [Pagination](./pagination.md) — immutable definition + per-player session
8. [Advanced](./advanced.md) — custom openers, options, lifecycle

## One-minute tour

```java
Lotus lotus = Lotus.builder(this).debug(true).build();

public final class ShopMenu implements Menu {
    public @NotNull Capacity capacity(MenuView<?> view) { return Capacity.ofRows(3); }
    public @NotNull Component title(MenuView<?> view) { return Component.text("Shop"); }

    public @NotNull Content content(MenuView<?> view) {
        return Content.builder(capacity(view))
            .fillBorder(Button.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
            .set(1, 4, Button.clickable(
                new ItemStack(Material.DIAMOND),
                (v, e) -> v.viewer().sendMessage("Bought!")))
            .build();
    }
}

lotus.openMenu(player, new ShopMenu());
```

That's a bordered 3-row chest with a clickable diamond in the center. Read on for the pieces.
