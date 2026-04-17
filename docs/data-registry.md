# Data Registry

Type-safe per-view state. Replaces the error-prone `Map<String, Object>` pattern.

## `Key<T>`

A typed identifier. Type is metadata; identity is the name.

```java
public final class Keys {
    public static final Key<Integer> PAGE       = Key.of("page", Integer.class);
    public static final Key<UUID>    TARGET     = Key.of("target", UUID.class);
    public static final Key<String>  CATEGORY   = Key.of("category", String.class);
}
```

Declare keys as `public static final` constants. Two keys are equal if their names match — define each key once per concept.

## `DataRegistry`

Type-checked container.

```java
DataRegistry data = DataRegistry.empty();

data.put(Keys.PAGE, 2);
data.put(Keys.TARGET, player.getUniqueId());

int page = data.require(Keys.PAGE);       // throws if missing
Optional<UUID> t = data.get(Keys.TARGET); // null-safe
boolean has = data.contains(Keys.PAGE);
data.remove(Keys.PAGE);
```

Type checks run **both** at insert and at read. A wrong-typed value → `ClassCastException` immediately, not later as a mysterious NPE.

## Where registries live

### Per-view data

Every `MenuView` has its own registry. Seed it on open:

```java
DataRegistry seed = DataRegistry.empty().put(Keys.TARGET, target.getUniqueId());
lotus.openMenu(player, menu, seed);
```

Read inside the menu or buttons:

```java
@Override public @NotNull Content content(MenuView<?> view) {
    UUID target = view.data().require(Keys.TARGET);
    ...
}
```

### Per-button data

Every `Button` has its own registry too — attach metadata to an item and read it back in the click handler.

```java
Button entry = Button.clickable(head(someone), (view, event) -> {
    view.content().get(Slot.of(event.getSlot()))
        .flatMap(b -> b.data().get(Keys.TARGET))
        .ifPresent(targetUuid -> inspect(targetUuid));
});
entry.data().put(Keys.TARGET, someone.getUniqueId());
```

This pattern scales well in pagination: the renderer stamps each item button with its backing model's ID.

## Idioms

**Defaults.** Use `get(...)` + `orElse`:

```java
int page = view.data().get(Keys.PAGE).orElse(0);
```

**Required invariants.** Use `require(...)`:

```java
UUID target = view.data().require(Keys.TARGET);   // programming error if missing
```

**Mutate in a click handler.** The registry is mutable; changes persist for the life of the view:

```java
view.data().put(Keys.PAGE, view.data().require(Keys.PAGE) + 1);
view.refresh();   // re-resolve content with new state
```

## What not to do

- **Don't share a single `DataRegistry` between views.** One per view, one per button. The framework handles this for you — just don't smuggle references around.
- **Don't use it for cross-player state.** It's per-view. Global state lives in your plugin, not in Lotus.
- **Don't abuse keys as message passing.** If it's a click→action flow, a `ClickAction` closure captures its data naturally.

Next: [Pagination](./pagination.md).
