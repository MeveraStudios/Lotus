package studio.mevera.lotus.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.item.AbstractItemBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Paper 1.21+ fluent {@link ItemStack} builder with Adventure {@link Component} display names
 * and lore. Extends {@link AbstractItemBuilder} with {@code C = Component}.
 * <p>
 * Uses the Paper 1.21+ API:
 * <ul>
 *   <li>{@link ItemStack#of(Material)} for construction</li>
 *   <li>{@link ItemStack#editMeta(java.util.function.Consumer)} for meta mutation</li>
 *   <li>{@code meta.displayName(Component)} and {@code meta.lore(List<Component>)} for Adventure fidelity</li>
 *   <li>{@code meta.setUnbreakable(true)} for the unbreakable flag</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>{@code
 * ItemStack item = ItemBuilder.of(Material.DIAMOND)
 *     .displayName(Component.text("Legendary Gem").color(NamedTextColor.AQUA))
 *     .lore(List.of(Component.text("Rare find").color(NamedTextColor.GRAY)))
 *     .amount(3)
 *     .unbreakable(true)
 *     .build();
 * }</pre>
 */
public final class ItemBuilder extends AbstractItemBuilder<ItemBuilder, Component> {

    private Component displayName;
    private List<Component> lore = new ArrayList<>();

    private ItemBuilder(@NotNull Material material) {
        super(material);
    }

    public static @NotNull ItemBuilder of(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    @Override
    public @NotNull ItemBuilder displayName(@NotNull Component name) {
        this.displayName = name;
        return this;
    }

    @Override
    public @NotNull ItemBuilder lore(@NotNull List<Component> lines) {
        this.lore = new ArrayList<>(lines);
        return this;
    }

    @Override
    public @NotNull ItemStack build() {
        ItemStack item = ItemStack.of(material);
        item.editMeta(meta -> {
            if (displayName != null) meta.displayName(displayName);
            if (!lore.isEmpty()) meta.lore(lore);
            if (unbreakable) meta.setUnbreakable(true);
        });
        item.setAmount(amount);
        return item;
    }
}
