package studio.mevera.menus.misc.itembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class ComponentItemBuilder extends ItemBuilder<Component, ComponentItemBuilder> {

	private final MiniMessage miniMessage ;
	ComponentItemBuilder(ItemStack itemStack, MiniMessage miniMessage) {
		super(itemStack);
        this.miniMessage = miniMessage;
    }
	ComponentItemBuilder(Material material,
	                             int amount, short data, MiniMessage miniMessage) {
		super(material, amount, data);
        this.miniMessage = miniMessage;
    }


	ComponentItemBuilder(Material material,
	                     int amount, MiniMessage miniMessage) {
		super(material, amount);
        this.miniMessage = miniMessage;
    }


	ComponentItemBuilder(Material material, MiniMessage miniMessage) {
		super(material);
        this.miniMessage = miniMessage;
    }


	public ComponentItemBuilder setDisplay(MiniMessage miniMessage, String richText) {
		return setDisplay(miniMessage.deserialize(richText));
	}
	public ComponentItemBuilder setDisplay(String richText) {
		return setDisplay(miniMessage, richText);
	}


	public ComponentItemBuilder setRichLore(List<String> richLore) {
		return setLore(richLore.stream()
				.map(miniMessage::deserialize)
				.toList());
	}

	public ComponentItemBuilder setRichLore(MiniMessage miniMessage, List<String> richLore) {
		return setLore(richLore.stream()
				.map(miniMessage::deserialize)
				.toList());
	}

	public ComponentItemBuilder setRichLore(String... richLore) {
		return setRichLore(Arrays.asList(richLore));
	}

	public ComponentItemBuilder setRichLore(MiniMessage miniMessage, String... richLore) {
		return setRichLore(miniMessage, Arrays.asList(richLore));
	}


	@Override
	protected String toString(Component component) {
		return LegacyComponentSerializer.legacySection().serialize(component);
	}
}
