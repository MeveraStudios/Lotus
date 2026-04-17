package studio.mevera.lotus;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Builder for {@link Lotus}. Required: {@link Plugin}. All other fields default to
 * {@link Lotus.Options#defaults()}.
 */
public final class LotusBuilder {

    private final Plugin plugin;
    private boolean allowBottomInventoryClick = true;
    private boolean debug = false;

    LotusBuilder(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    public @NotNull LotusBuilder allowBottomInventoryClick(boolean allow) {
        this.allowBottomInventoryClick = allow;
        return this;
    }

    public @NotNull LotusBuilder debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public @NotNull Lotus build() {
        return new Lotus(plugin, new Lotus.Options(allowBottomInventoryClick, debug));
    }
}
