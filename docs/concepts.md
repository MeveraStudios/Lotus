# Core Concepts

The vocabulary Lotus uses everywhere. All of these are immutable values — safe to share, store, and pass around.

## `Capacity`

The grid dimensions of a menu.

```java
Capacity chest = Capacity.ofRows(3);              // 3×9
Capacity hopper = Capacity.of(InventoryType.HOPPER); // 1×5
Capacity custom = new Capacity(6, 9);             // 6×9 (double chest)
```

Factory for fixed-layout inventory types exists only for types with a natural grid: `CHEST`, `HOPPER`, `DROPPER`, `DISPENSER`, `WORKBENCH`, `FURNACE` family. Anything else → use `ofRows(int)` or the constructor.

## `Slot`

A position, identified by raw inventory index.

```java
Slot s = Slot.at(1, 4, Capacity.ofRows(3));   // row 1, col 4 → index 13
Slot first = Slot.first();                    // 0
Slot last = Slot.last(Capacity.ofRows(3));    // 26
int row = s.row(capacity);
int col = s.column(capacity);
```

`Slot` is a record — equal by index, `Comparable`, easy to use as a map key.

## `SlotMask`

A declarative description of "which slots" — sealed algebra, not a mutable range.

```java
SlotMask all   = SlotMask.full(capacity);
SlotMask row   = SlotMask.range(capacity, Slot.of(9), Slot.of(17));
SlotMask picks = SlotMask.of(capacity, Set.of(Slot.of(0), Slot.of(4)));
SlotMask fill  = SlotMask.full(capacity).excluding(Slot.of(22), Slot.of(26));
```

You can chain `.excluding(...)` to subtract slots. Size is always derived, never mutated.

Used whenever you say "fill these" — `fill`, pagination fill masks, page layouts.

## `Content`

The button map for one menu view. Has read (`ContentView`) and write (`ContentEditor`) halves:

```java
Content c = Content.empty(Capacity.ofRows(3));
c.set(Slot.of(4), Button.of(item));
c.get(Slot.of(4));       // Optional<Button>
c.forEach((slot, btn) -> ...);
c.fill(mask, button);
```

For construction, prefer the fluent [`ContentBuilder`](./content.md) over direct mutation.

## `Key<T>` and `DataRegistry`

Typed storage for per-view state. Covered in detail in [Data Registry](./data-registry.md):

```java
Key<Integer> PAGE = Key.of("page", Integer.class);
view.data().put(PAGE, 2);
int page = view.data().require(PAGE);
```

## How they connect

```
Menu           → defines Capacity + Content
Lotus.openMenu → builds a MenuView<M> for a Player
MenuView       → holds the live Content and Inventory
Button clicks  → dispatch through the view, mutate Content, repaint
```

Next: [Menus](./menus.md).
