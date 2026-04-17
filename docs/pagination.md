# Pagination

Lotus pagination separates **definition** (shared, immutable) from **session** (per-player, live).

- `Pagination<T>` — the recipe: layout + data source + renderer. Safe to store in a `static final`, share across players, reuse forever.
- `PaginationSession<T>` — a live navigation, tied to one player. Knows the current page and snapshot of items.

## Ingredients

### 1. `PageLayout`

Defines shared shape of every page: capacity, fill area, navigation buttons, title, decorations.

```java
PageLayout layout = PageLayout.builder(Capacity.ofRows(6))
    .title(ctx -> Component.text("Shop — page " + (ctx.pageIndex() + 1) + "/" + ctx.totalPages()))
    .previousButton(Slot.at(5, 3, capacity), ctx -> Button.of(arrow("←")))
    .nextButton(Slot.at(5, 5, capacity),     ctx -> Button.of(arrow("→")))
    .decorations(ctx -> Content.builder(capacity)
        .fillBorder(Button.of(filler()))
        .build())
    .build();
```

Defaults:
- `title` → `"Page N"`.
- `fillMask` → full capacity minus the two nav slots.
- nav button slots → bottom-left and bottom-right corners.
- `decorations` → empty.

Customise the fill mask for more nuanced layouts:

```java
.fillMask(SlotMask.range(capacity, Slot.of(10), Slot.of(43))
    .excluding(Slot.of(17), Slot.of(26), Slot.of(35)))
```

### 2. `ContentSource<T>`

Supplies the items. Captured once per session (snapshot).

```java
ContentSource<ShopItem> source = ContentSource.of(shopItems);             // fixed list
ContentSource<ShopItem> dynamic = ContentSource.dynamic(player -> db.itemsFor(player));
```

### 3. `ComponentRenderer<T>`

Converts one item into a `Button` for display. Receives the `PageContext` so it can react to page state.

```java
ComponentRenderer<ShopItem> renderer = (item, ctx) -> Button.clickable(
    item.icon(),
    (view, event) -> buy(view.viewer(), item)
);
```

## Putting it together

```java
Pagination<ShopItem> pagination = Pagination.<ShopItem>builder()
    .layout(layout)
    .source(source)
    .renderer(renderer)
    .trimOverflow(true)     // items past fillMask.size() are discarded (default true)
    .build();

// Open for a specific player
PaginationSession<ShopItem> session = pagination.open(lotus, player);
session.goTo(0);            // or session.next() / session.previous()
```

`pagination.open(...)` returns a session positioned at page 0 but **not yet opened**. Call `goTo(0)` (or any valid page) to actually open the inventory.

## Session control

```java
session.next();
session.previous();
session.goTo(3);
session.close();

session.currentIndex();
session.totalPages();
session.isFirst();
session.isLast();
session.viewer();
session.definition();
```

- `next()` / `previous()` are bounded: no-op at edges.
- `goTo(i)` validates against `totalPages` — out of range throws `IndexOutOfBoundsException`.
- `close()` is idempotent.
- After close, further `goTo` throws `IllegalStateException`.

## `PageContext`

Passed to every layout/renderer hook. Read-only snapshot:

```java
public record PageContext(
    int pageIndex,
    int totalPages,
    Player viewer,
    PaginationSession<?> session
) {
    boolean isFirst();
    boolean isLast();
}
```

Use it to skip nav on edges, show "Page 3/12" in titles, or re-open the parent menu from a decoration button.

## Reusing a definition

A `Pagination<T>` is immutable — share it everywhere:

```java
public final class Shop {
    public static final Pagination<ShopItem> PAGINATION = build();

    private static Pagination<ShopItem> build() {
        return Pagination.<ShopItem>builder()...build();
    }

    public static void open(Lotus lotus, Player p) {
        PAGINATION.open(lotus, p).goTo(0);
    }
}
```

Each `open` spawns a fresh session with its own snapshot and page cursor. No shared state to corrupt.

## Dynamic item lists

Use `ContentSource.dynamic(...)` when items depend on the viewer:

```java
ContentSource<Friend> friends = ContentSource.dynamic(player -> friendService.friendsOf(player));
```

The source is called **once per session** (at construction), not per page. If you need real-time updates, close and re-open the session.

Next: [Advanced](./advanced.md).
