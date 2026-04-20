package studio.mevera.lotus.spigot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.item.AbstractItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spigot 1.8.8 fluent {@link ItemStack} builder with legacy {@link String} display names and lore.
 * Extends {@link AbstractItemBuilder} with {@code C = String}.
 * <p>
 * Uses the Spigot 1.8.8 API:
 * <ul>
 *   <li>{@code new ItemStack(Material, amount)} for construction</li>
 *   <li>{@link ItemStack#getItemMeta()} + {@link ItemStack#setItemMeta(ItemMeta)} for meta mutation</li>
 *   <li>{@code meta.setDisplayName(String)} / {@code meta.setLore(List<String>)} — accepts
 *       {@code &}-prefixed color codes, which are auto-translated via
 *       {@link ChatColor#translateAlternateColorCodes(char, String)}</li>
 *   <li>{@code meta.spigot().setUnbreakable(true)} for the unbreakable flag (Spigot 1.8.8 API)</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>{@code
 * ItemStack item = ItemBuilder.of(Material.DIAMOND)
 *     .displayName("&bLegendary Gem")
 *     .lore(List.of("&7Rare find"))
 *     .amount(3)
 *     .unbreakable(true)
 *     .build();
 * }</pre>
 */
public final class ItemBuilder extends AbstractItemBuilder<ItemBuilder, String> {

    private String displayName;
    private List<String> lore = new ArrayList<>();

    private ItemBuilder(@NotNull Material material) {
        super(material);
    }

    public static @NotNull ItemBuilder of(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    @Override
    public @NotNull ItemBuilder displayName(@NotNull String name) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    @Override
    public @NotNull ItemBuilder lore(@NotNull List<String> lines) {
        this.lore = lines.stream()
            .map(l -> ChatColor.translateAlternateColorCodes('&', l))
            .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }

    @Override
    // spigot().setUnbreakable is legacy-only
    public @NotNull ItemStack build() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (displayName != null) meta.setDisplayName(displayName);
            if (!lore.isEmpty()) meta.setLore(lore);
            if (unbreakable) meta.spigot().setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }
}
