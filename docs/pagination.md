# Pagination

Lotus pagination separates **definition** (shared, immutable) from **session** (per-player, live).

- `Pagination<T>` — the recipe: layout + data source + renderer. Safe to store in a `static final`, share across players, reuse forever.
- `PaginationSession<T>` — a live navigation, tied to one player. Knows the current page and snapshot of items.

## Ingredients

### 1. `PageLayout<C>`

Defines shared shape of every page: capacity, fill area, navigation buttons, title, decorations.

On **Paper**, use `PaperPageLayoutBuilder` (title is an Adventure `Component`).
On **Spigot**, use `PageLayout.builder()` (title is a `String`).

#### Paper

```java
import studio.mevera.lotus.paper.api.pagination.PaperPageLayoutBuilder;

Capacity capacity = Capacity.ofRows(6);

PaperPageLayout layout = new PaperPageLayoutBuilder(capacity)
    .title(ctx -> Component.text("Shop — page "
        + (ctx.pageIndex() + 1) + "/" + ctx.totalPages()))
    .previousButton(Slot.at(5, 3, capacity), ctx -> Button.of(arrow("←")))
    .nextButton(Slot.at(5, 5, capacity),     ctx -> Button.of(arrow("→")))
    .decorations(ctx -> Content.builder(capacity)
        .fillBorder(Button.of(filler()))
        .build())
    .build();
```

#### Spigot

```java
Capacity capacity = Capacity.ofRows(6);

PageLayout<String> layout = PageLayout.builder(capacity)
    .title(ctx -> "Shop - page " + (ctx.pageIndex() + 1) + "/" + ctx.totalPages())
    .previousButton(Slot.at(5, 3, capacity), ctx -> Button.of(arrow()))
    .nextButton(Slot.at(5, 5, capacity),     ctx -> Button.of(arrow()))
    .build();
```

Defaults (both platforms):
- `title` → `"Page N"` (String) / `Component.text("Page N")` (Paper).
- `fillMask` → full capacity minus the two nav slots.
- nav button slots → bottom-left and bottom-right corners.
- `decorations` → empty.

Customise the fill mask:

```java
.fillMask(SlotMask.range(capacity, Slot.of(10), Slot.of(43))
    .excluding(Slot.of(17), Slot.of(26), Slot.of(35)))
```

### 2. `ContentSource<T>`

Supplies the items. Captured once per session (snapshot).

```java
ContentSource<ShopItem> source  = ContentSource.of(shopItems);
ContentSource<ShopItem> dynamic = ContentSource.dynamic(player -> db.itemsFor(player));
```

### 3. `ComponentRenderer<T>`

Converts one item into a `Button`. Receives the `PageContext`.

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

// Open for a specific player — navigates to page 0 automatically
pagination.open(lotus, player);
```

`pagination.open(lotus, player)` creates the session, navigates to page 0, and opens the inventory.
If you need to navigate to a specific starting page:

```java
PaginationSession<ShopItem> session = pagination.open(lotus, player);
session.goTo(2);    // navigate from page 0 to page 2
```

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
        PAGINATION.open(lotus, p);   // opens at page 0
    }
}
```

Each `open` spawns a fresh session with its own snapshot and page cursor. No shared state to corrupt.

## Dynamic item lists

Use `ContentSource.dynamic(...)` when items depend on the viewer:

```java
ContentSource<Friend> friends = ContentSource.dynamic(p -> friendService.friendsOf(p));
```

The source is called **once per session** (at construction), not per page. If you need real-time updates, close and re-open the session.

Next: [Advanced](./advanced.md).
