package studio.mevera.lotus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.menu.Menu;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.internal.LotusLogger;
import studio.mevera.lotus.internal.menu.BaseMenuView;
import studio.mevera.lotus.spi.PaginationSessionFactory;
import studio.mevera.lotus.spi.opener.ViewOpener;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Facade and runtime for the Lotus menu framework. One instance per plugin: holds open-view
 * registry, configurable opener strategies, and the bound Bukkit listener.
 * <p>
 * Construct via platform factories such as {@code PaperLotus.create(...)} or
 * {@code SpigotLotus.create(...)}. All public mutators must be called on the server main
 * thread.
 */
public final class Lotus<C> {

    private final Plugin plugin;
    private final Options options;
    private final LotusLogger logger;
    private final ViewOpener<C> defaultViewOpener;
    private PaginationSessionFactory<C> sessionFactory;
    private final Map<InventoryType, ViewOpener<C>> openers = new EnumMap<>(InventoryType.class);
    private final Map<UUID, MenuView<C, ?>> openViews = new HashMap<>();
    private final Map<String, Menu<C>> registeredMenus = new HashMap<>();

    Lotus(
        @NotNull Plugin plugin,
        @NotNull Options options,
        @NotNull ViewOpener<C> defaultViewOpener,
        @NotNull PaginationSessionFactory<C> sessionFactory,
        @NotNull Consumer<Lotus<C>> bootstrap
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.options = Objects.requireNonNull(options);
        this.defaultViewOpener = Objects.requireNonNull(defaultViewOpener);
        this.logger = new LotusLogger(plugin.getLogger(), options.debug());
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        Objects.requireNonNull(bootstrap).accept(this);
    }

    public @NotNull Plugin plugin() {
        return plugin;
    }

    public @NotNull Options options() {
        return options;
    }

    public @NotNull LotusLogger logger() {
        return logger;
    }

    public void registerOpener(@NotNull InventoryType type, @NotNull ViewOpener opener) {
        openers.put(type, opener);
    }

    public @NotNull ViewOpener<C> openerFor(@NotNull InventoryType type) {
        return openers.getOrDefault(type, defaultViewOpener);
    }

    public @NotNull PaginationSessionFactory<C> sessionFactory() {
        return sessionFactory;
    }

    public void sessionFactory(@NotNull PaginationSessionFactory<C> factory) {
        this.sessionFactory = Objects.requireNonNull(factory);
    }

    public void registerMenu(@NotNull Menu<C> menu) {
        registeredMenus.put(menu.name().toLowerCase(), menu);
    }

    public @NotNull Optional<Menu<C>> registeredMenu(@NotNull String name) {
        return Optional.ofNullable(registeredMenus.get(name.toLowerCase()));
    }

    public @NotNull Optional<MenuView<C, ?>> viewOf(@NotNull Player player) {
        return Optional.ofNullable(openViews.get(player.getUniqueId()));
    }

    public @NotNull Collection<MenuView<C, ?>> openViews() {
        return openViews.values();
    }

    public <M extends Menu<C>> @NotNull MenuView<C, M> openMenu(@NotNull Player viewer, @NotNull M menu) {
        return openMenu(viewer, menu, DataRegistry.empty());
    }

    public <M extends Menu<C>> @NotNull MenuView<C, M> openMenu(@NotNull Player viewer, @NotNull M menu, @NotNull DataRegistry data) {
        var view = new BaseMenuView<>(this, menu, viewer, data);
        view.open(openerFor(menu.type()));
        openViews.put(viewer.getUniqueId(), view);
        return view;
    }

    public void openMenu(@NotNull Player viewer, @NotNull String menuName) {
        registeredMenu(menuName).ifPresentOrElse(
            menu -> openMenu(viewer, menu),
            () -> logger.warn("no menu registered with name " + menuName)
        );
    }

    /**
     * Internal: registers/replaces the open view for a player. Called from platform listeners
     * when a click arrives for an inventory whose holder is a Lotus view but the player isn't yet
     * tracked (e.g. opened directly by another plugin).
     */
    public void track(@NotNull Player player, @NotNull MenuView<C, ?> view) {
        openViews.put(player.getUniqueId(), view);
    }

    public void untrack(@NotNull Player player) {
        openViews.remove(player.getUniqueId());
    }

    public @Nullable MenuView<C, ?> resolveView(@NotNull Player player) {
        return openViews.get(player.getUniqueId());
    }


    /**
     * Immutable runtime configuration.
     */
    public record Options(boolean allowBottomInventoryClick, boolean dynamicButtonAction, boolean debug) {

        public static @NotNull Options defaults() {
            return new Options(true, false, false);
        }
    }
}
