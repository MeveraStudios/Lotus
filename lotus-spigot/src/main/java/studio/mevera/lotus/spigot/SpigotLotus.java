package studio.mevera.lotus.spigot;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.LotusBuilder;
import studio.mevera.lotus.spigot.internal.opener.SpigotViewOpener;

import java.util.function.Consumer;

/**
 * Spigot 1.8.8 static factory for {@link Lotus}. Returns a {@link Lotus} instance pre-configured
 * with {@link SpigotViewOpener} as the default {@link studio.mevera.lotus.spi.opener.ViewOpener}.
 * <p>
 * The default {@link studio.mevera.lotus.spi.PaginationSessionFactory}
 * ({@code DefaultPaginationSession::new}) already produces {@code String}-titled page menus —
 * no override is needed on Spigot.
 * <p>
 * Usage:
 * <pre>{@code
 * Lotus lotus = SpigotLotus.create(this); // 'this' is your JavaPlugin
 * }</pre>
 */
public final class SpigotLotus {

    private SpigotLotus() {}

    /**
     * Creates a {@link Lotus} facade pre-configured for Spigot 1.8.8 with default options.
     */
    public static @NotNull Lotus create(@NotNull Plugin plugin) {
        return create(plugin, b -> {});
    }

    /**
     * Creates a {@link Lotus} facade pre-configured for Spigot 1.8.8. The {@code customizer}
     * receives the {@link LotusBuilder} after Spigot defaults have been applied.
     */
    public static @NotNull Lotus create(@NotNull Plugin plugin, @NotNull Consumer<LotusBuilder> customizer) {
        LotusBuilder builder = Lotus.builder(plugin).defaultViewOpener(new SpigotViewOpener());
        customizer.accept(builder);
        return builder.build();
    }
}
