package studio.mevera.lotus.api.slot;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Declarative description of which slots are usable within a {@link Capacity}.
 * <p>
 * Replaces the legacy {@code FillRange}: instead of "start + end + forbidden", a mask is a sealed
 * algebra of composable shapes whose count is always derived, never mutated.
 */
public sealed interface SlotMask {

    @NotNull Capacity capacity();

    boolean contains(@NotNull Slot slot);

    int size();

    @NotNull Stream<Slot> stream();

    default @NotNull SlotMask excluding(@NotNull Slot... slots) {
        return new Excluding(this, Set.of(slots));
    }

    default @NotNull SlotMask excluding(@NotNull Set<Slot> slots) {
        return new Excluding(this, Set.copyOf(slots));
    }

    static @NotNull SlotMask full(@NotNull Capacity capacity) {
        return new Full(capacity);
    }

    static @NotNull SlotMask range(@NotNull Capacity capacity, @NotNull Slot from, @NotNull Slot to) {
        if (from.index() > to.index())
            throw new IllegalArgumentException("from > to: " + from + " > " + to);
        if (!capacity.contains(to.index()))
            throw new IllegalArgumentException("end out of capacity: " + to);
        return new Range(capacity, from, to);
    }

    static @NotNull SlotMask of(@NotNull Capacity capacity, @NotNull Set<Slot> slots) {
        return new Custom(capacity, Set.copyOf(slots));
    }

    record Full(@NotNull Capacity capacity) implements SlotMask {
        @Override public boolean contains(@NotNull Slot slot) { return capacity.contains(slot.index()); }
        @Override public int size() { return capacity.totalSize(); }
        @Override public @NotNull Stream<Slot> stream() {
            return IntStream.range(0, capacity.totalSize()).mapToObj(Slot::of);
        }
    }

    record Range(@NotNull Capacity capacity, @NotNull Slot from, @NotNull Slot to) implements SlotMask {
        @Override public boolean contains(@NotNull Slot slot) {
            return slot.index() >= from.index() && slot.index() <= to.index();
        }
        @Override public int size() { return to.index() - from.index() + 1; }
        @Override public @NotNull Stream<Slot> stream() {
            return IntStream.rangeClosed(from.index(), to.index()).mapToObj(Slot::of);
        }
    }

    record Custom(@NotNull Capacity capacity, @NotNull Set<Slot> slots) implements SlotMask {
        public Custom {
            slots = Set.copyOf(slots);
        }
        @Override public boolean contains(@NotNull Slot slot) { return slots.contains(slot); }
        @Override public int size() { return slots.size(); }
        @Override public @NotNull Stream<Slot> stream() { return slots.stream(); }
    }

    record Excluding(@NotNull SlotMask base, @NotNull Set<Slot> excluded) implements SlotMask {
        public Excluding {
            excluded = Set.copyOf(excluded);
        }
        @Override public @NotNull Capacity capacity() { return base.capacity(); }
        @Override public boolean contains(@NotNull Slot slot) {
            return base.contains(slot) && !excluded.contains(slot);
        }
        @Override public int size() {
            int hidden = (int) excluded.stream().filter(base::contains).count();
            return base.size() - hidden;
        }
        @Override public @NotNull Stream<Slot> stream() {
            Set<Slot> ex = excluded;
            return base.stream().filter(s -> !ex.contains(s));
        }
        @Override public @NotNull SlotMask excluding(@NotNull Set<Slot> slots) {
            var merged = new LinkedHashSet<>(excluded);
            merged.addAll(slots);
            return new Excluding(base, merged);
        }
    }
}
