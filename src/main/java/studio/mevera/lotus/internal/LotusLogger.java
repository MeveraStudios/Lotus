package studio.mevera.lotus.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin logger wrapper that gates {@code debug} on a flag from {@link studio.mevera.lotus.Lotus.Options}.
 */
public final class LotusLogger {

    private final Logger delegate;
    private final boolean debug;

    public LotusLogger(@NotNull Logger delegate, boolean debug) {
        this.delegate = delegate;
        this.debug = debug;
    }

    public void debug(@NotNull String message) {
        if (debug) delegate.info("[lotus] " + message);
    }

    public void warn(@NotNull String message) {
        delegate.warning("[lotus] " + message);
    }

    public void warn(@NotNull String message, @Nullable Throwable cause) {
        delegate.log(Level.WARNING, "[lotus] " + message, cause);
    }
}
