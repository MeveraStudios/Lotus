package studio.mevera.lotus.api.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract fluent builder for {@link ItemStack} instances. Parameterized over:
 * <ul>
 *   <li>{@code B} — the concrete builder type (self-type for fluent method chaining)</li>
 *   <li>{@code C} — the display-name/lore component type:
 *       {@code Component} on Paper, {@code String} on Spigot</li>
 * </ul>
 * <p>
 * Each platform module provides a concrete {@code ItemBuilder} class (same simple name,
 * different package) extending this base:
 * <ul>
 *   <li>{@code studio.mevera.lotus.paper.ItemBuilder} — Paper 1.21+: uses
 *       {@code ItemStack.of()}, {@code editMeta()} lambda, Adventure {@code Component}</li>
 *   <li>{@code studio.mevera.lotus.spigot.ItemBuilder} — Spigot 1.8.8: uses
 *       {@code new ItemStack(Material)}, classic {@code getItemMeta()/setItemMeta()},
 *       {@code String} with {@code ChatColor} translation</li>
 * </ul>
 *
 * @param <B> the concrete builder type
 * @param <C> the display-name/lore component type
 */
public abstract class AbstractItemBuilder<B extends AbstractItemBuilder<B, C>, C> {

    protected final Material material;
    protected int amount = 1;
    protected boolean unbreakable = false;

    protected AbstractItemBuilder(@NotNull Material material) {
        this.material = material;
    }



    /**
     * Returns this builder cast to {@code B}, enabling fluent method chaining in subclasses.
     */
    @SuppressWarnings("unchecked")
    protected final @NotNull B self() {
        return (B) this;
    }

    /**
     * Sets the item stack size.
     */
    public @NotNull B amount(int amount) {
        this.amount = amount;
        return self();
    }

    /**
     * Sets whether this item is unbreakable.
     */
    public @NotNull B unbreakable(boolean value) {
        this.unbreakable = value;
        return self();
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name in the platform's component type
     */
    public abstract @NotNull B displayName(@NotNull C name);

    /**
     * Sets the lore of the item.
     *
     * @param lines the lore lines in the platform's component type
     */
    public abstract @NotNull B lore(@NotNull List<C> lines);

    @SafeVarargs
    public final @NotNull B lore(C... lines) {
        return lore(Arrays.asList(lines));
    }

    /**
     * Builds and returns the configured {@link ItemStack}.
     */
    public abstract @NotNull ItemStack build();
}
