# Getting Started

## Add the dependency

Gradle (Kotlin DSL):

```kotlin
repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation("studio.mevera:lotus:2.0.0")
}
```

Shade or relocate as appropriate — Lotus is a library, not a plugin.

## Boot once per plugin

Construct a `Lotus` instance in `onEnable`. It registers its Bukkit listener automatically.

```java
public final class MyPlugin extends JavaPlugin {

    private Lotus lotus;

    @Override
    public void onEnable() {
        this.lotus = Lotus.builder(this)
            .allowBottomInventoryClick(true)   // let players shift-click their own hotbar
            .debug(false)                      // [lotus] log lines when true
            .build();
    }

    public Lotus lotus() { return lotus; }
}
```

One `Lotus` instance per plugin. It tracks every open view and dispatches clicks.

## Your first menu

A `Menu` is a **template**: title, capacity, and content. Lotus resolves it per player when opened.

```java
public final class WelcomeMenu implements Menu {
    @Override public @NotNull Component title(MenuView<?> view) {
        return Component.text("Welcome, " + view.viewer().getName());
    }
    @Override public @NotNull Capacity capacity(MenuView<?> view) {
        return Capacity.ofRows(3);
    }
    @Override public @NotNull Content content(MenuView<?> view) {
        return Content.builder(capacity(view))
            .set(1, 4, Button.of(new ItemStack(Material.PAPER)))
            .build();
    }
}
```

Open it:

```java
lotus.openMenu(player, new WelcomeMenu());
```

## Naming and registration (optional)

If you want to open menus by name from commands or config:

```java
lotus.registerMenu(new WelcomeMenu());        // uses menu.name(); default = class simple name
lotus.openMenu(player, "welcomemenu");        // lookup is case-insensitive
```

## Next

- [Core Concepts](./concepts.md) for the vocabulary (`Slot`, `Capacity`, `SlotMask`).
- [Menus](./menus.md) for click/open/close hooks.
- [Buttons](./buttons.md) for interactive elements.
