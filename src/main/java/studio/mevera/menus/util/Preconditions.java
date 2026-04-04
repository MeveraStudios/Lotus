package studio.mevera.menus.util;

public final class Preconditions {

    private Preconditions() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void checkArgument(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkState(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static <T> T checkNotNull(final T value, final String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

    public static <T> T checkNotNull(final T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }

}
