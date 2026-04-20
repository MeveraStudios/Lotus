# Getting Started

## Pick your module

Lotus has two platform modules:

| Module         | Server         | Artifact                     |
|----------------|----------------|------------------------------|
| `lotus-paper`  | Paper 1.21+    | `studio.mevera:lotus-paper`  |
| `lotus-spigot` | Spigot 1.8.8   | `studio.mevera:lotus-spigot` |

## Add the dependency

### Paper 1.21+ (Gradle Kotlin DSL)

```kotlin
repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation("studio.mevera:lotus-paper:2.0.0")
}
```

### Spigot 1.8.8 (Gradle Kotlin DSL)

```kotlin
repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenCentral()
}

dependencies {
    implementation("studio.mevera:lotus-spigot:2.0.0")
}
```

Shade or relocate as appropriate — Lotus is a library, not a plugin.

## Boot once per plugin

### Paper

```java
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.paper.PaperLotus;

public final class MyPlugin extends JavaPlugin {
    private Lotus lotus;

    @Override public void onEnable() {
        this.lotus = PaperLotus.create(this);
    }

    public Lotus lotus() { return lotus; }
}
```

### Spigot 1.8.8

```java
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.spigot.SpigotLotus;

public final class MyPlugin extends JavaPlugin {
    private Lotus lotus;

    @Override public void onEnable() {
        this.lotus = SpigotLotus.create(this);
    }

    public Lotus lotus() { return lotus; }
}
```

One `Lotus` instance per plugin. It tracks every open view and dispatches clicks.
Both factories register their own Bukkit listener automatically.

## Customising the builder

Pass a `Consumer<LotusBuilder>` as a second argument:

```java
this.lotus = PaperLotus.create(this, b -> b
    .allowBottomInventoryClick(true)   // let players shift-click their own hotbar
    .debug(false));                    // [lotus] log lines when true
```

The same overload is available on `SpigotLotus.create(this, b -> { ... })`.

## Your first menu

### Paper

```java
public final class WelcomeMenu implements PaperMenu {
    @Override public @NotNull Component title(MenuView<?> view) {
        return Component.text("Welcome, " + view.viewer().getName());
    }
    @Override public @NotNull Capacity capacity(MenuView<?> view) {
        return Capacity.ofRows(3);
    }
    @Override public @NotNull Content content(MenuView<?> view) {
        return Content.builder(view.capacity())
            .set(1, 4, Button.of(new ItemStack(Material.PAPER)))
            .build();
    }
}
```

### Spigot

```java
public final class WelcomeMenu implements Menu<String> {
    @Override public @NotNull String title(MenuView<?> view) {
        return "&6Welcome, " + view.viewer().getName();
    }
    @Override public @NotNull Capacity capacity(MenuView<?> view) {
        return Capacity.ofRows(3);
    }
    @Override public @NotNull Content content(MenuView<?> view) {
        return Content.builder(view.capacity())
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
