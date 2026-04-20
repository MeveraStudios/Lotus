# Content Builder

`ContentBuilder` is the fluent DSL for laying out buttons. It wraps a mutable `Content`; you call `.build()` when done.

```java
Capacity size = Capacity.ofRows(3);

Content c = Content.builder(size)
    .fillBorder(Button.of(glassPane()))
    .set(1, 4, Button.clickable(diamond(), buyAction))
    .build();
```

## Primitives

```java
.set(slot, button)                     // by Slot
.set(row, column, button)              // by coordinates
.buttons(Map<Slot, Button>)            // bulk write
```

## Fills

```java
.fill(mask, button)                    // every slot in the mask
.fillAll(button)                       // every slot
.fillBorder(button)                    // perimeter only
```

`fill(mask, button)` is the most flexible — combine with `SlotMask` operations:

```java
.fill(SlotMask.range(size, Slot.of(9), Slot.of(17)), button)             // middle row
.fill(SlotMask.full(size).excluding(Slot.of(13)), button)                // everything but center
```

## Drawing lines

```java
.draw(start, Direction.RIGHT, button)                    // until edge
.draw(start, end, Direction.DOWN_RIGHT, button)          // bounded
```

`Direction` supports cardinal and all four diagonals. The iterator stops at the grid edge or the bounded end.

## Escape hatch

When the DSL doesn't fit, `apply` hands you the `Content` directly:

```java
.apply(content -> {
    for (int i = 0; i < 9; i++) {
        if (isEven(i)) content.set(Slot.of(i), evenButton);
        else content.set(Slot.of(i), oddButton);
    }
})
```

## Complete example

```java
public @NotNull Content content(MenuView<?> view) {
    Capacity size = view.capacity();

    return Content.builder(size)
        .fillBorder(Button.of(filler()))
        .draw(Slot.at(1, 1, size), Slot.at(1, 7, size), Direction.RIGHT, Button.of(divider()))
        .set(2, 4, Button.clickable(confirm(), (v, e) -> { commit(v); v.viewer().closeInventory(); }))
        .set(2, 3, Button.clickable(cancel(), (v, e) -> v.viewer().closeInventory()))
        .build();
}
```

## Runtime edits

After build, you're in `ContentEditor` territory — use it to mutate during click handling:

```java
view.content().set(slot, newButton);
view.content().update(slot, current -> current.withItem(brighter));
view.content().remove(slot);
view.content().clear();
```

Mutations are reflected on the next repaint. Lotus repaints automatically after button dispatch; call `view.refresh()` if you mutated outside a click handler.

## Merge & trim

Combine two contents (overlay wins):

```java
Content base = ...;
Content overlay = ...;
Content merged = base.mergeWith(overlay);
```

Cap total button count (useful for bounded pagination):

```java
content.trimTo(25);
```

Next: [Data Registry](./data-registry.md).
