# Buttons

`Button` is a **sealed interface** — six variants, each a record. You never implement `Button` yourself; you pick a variant and configure it.

| Variant              | Purpose                                               |
|----------------------|-------------------------------------------------------|
| `StaticButton`       | Display only, click does nothing                      |
| `ClickableButton`    | Runs a `ClickAction` on click                         |
| `TransformingButton` | Replaces itself in the view on click                  |
| `SpanningButton`     | One logical button across multiple slots              |
| `CompositeButton`    | Dispatches click to several children in order         |
| `CycleButton`        | N-state button; each click advances to the next state |

All six share `item()`, `data()`, `withItem(...)`, and `dispatch(view, event)`. Factories live on the `Button` interface.

## Static

```java
Button b = Button.of(new ItemStack(Material.PAPER));
```

No behaviour. Use for labels, decorations, borders.

## Clickable

```java
Button b = Button.clickable(
    new ItemStack(Material.DIAMOND),
    (view, event) -> view.viewer().sendMessage("clicked!")
);
```

`ClickAction` is a functional interface. Compose actions:

```java
ClickAction playSound = (v, e) -> v.viewer().playSound(...);
ClickAction sendMsg   = (v, e) -> v.viewer().sendMessage("hi");
ClickAction both      = playSound.andThen(sendMsg);
```

## Transforming

The button decides, at click time, what should replace it in the view.

```java
Button toggle = Button.transforming(
    new ItemStack(Material.REDSTONE_LAMP),
    (view, event) -> Button.of(new ItemStack(Material.GLOWSTONE))  // new button
);
```

Return `null` from the transformer to leave the slot unchanged.

Useful for one-shot state flips. For repeating cycles, prefer `CycleButton`.

## Spanning

A single button whose visual and click target occupy **multiple slots** — banners, multi-cell icons, region selectors.

```java
SpanningButton banner = Button.spanning(
    new ItemStack(Material.ORANGE_BANNER),
    Set.of(Slot.of(0), Slot.of(1), Slot.of(2)),   // footprint
    (view, event) -> view.viewer().sendMessage("banner!")
);

content.placeSpanning(banner);   // writes the same instance to every footprint slot
```

Click fires **once per event**, no matter which slot inside the footprint was clicked. `event.getSlot()` still tells you where the player clicked.

Footprint is validated: empty → `IllegalArgumentException`.

## Composite (GoF Composite pattern)

One display item, many effects. Each child dispatches in declaration order.

```java
Button button = Button.composite(
    new ItemStack(Material.EMERALD),
    Button.clickable(item, (v, e) -> playSound(v)),
    Button.clickable(item, (v, e) -> message(v)),
    Button.clickable(item, (v, e) -> runCommand(v))
);
```

The composite's item is displayed; children's items are ignored for rendering.

**Caveat:** don't mix `TransformingButton` or `CycleButton` children — the first one that mutates the slot wins and later children will dispatch against a stale button.

## Cycle

Multi-state button. Each click advances to the next state, wrapping at the end.

```java
Button filter = Button.cycle(
    Button.clickable(off(),       (v, e) -> setFilter(v, "OFF")),
    Button.clickable(whitelist(), (v, e) -> setFilter(v, "WHITELIST")),
    Button.clickable(blacklist(), (v, e) -> setFilter(v, "BLACKLIST"))
);
```

- Displayed item comes from the active state.
- On click, the active state's own `dispatch` runs **first** (so each state can have its own side-effect), then the view's content is updated to an advanced copy.
- `CycleButton` is immutable — "advancing" creates a new instance and writes it back to the slot.

Use for toggles, difficulty selectors, page-size pickers.

## Data on buttons

Every button carries a `DataRegistry`. Attach metadata for later lookup:

```java
Key<UUID> TARGET = Key.of("target", UUID.class);

ClickableButton b = Button.clickable(item, (view, event) -> {
    UUID target = event.getCurrentItem() != null
        ? view.content().get(Slot.of(event.getSlot()))
            .map(Button::data).flatMap(d -> d.get(TARGET)).orElseThrow()
        : null;
    ...
});
b.data().put(TARGET, somePlayer.getUniqueId());
```

See [Data Registry](./data-registry.md) for the full pattern.

Next: [Content Builder](./content.md).
