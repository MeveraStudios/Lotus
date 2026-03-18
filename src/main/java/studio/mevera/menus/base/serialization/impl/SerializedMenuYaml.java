package studio.mevera.menus.base.serialization.impl;

import studio.mevera.menus.base.Content;
import studio.mevera.menus.base.serialization.SerializedMenuIO;
import studio.mevera.menus.misc.Capacity;
import studio.mevera.menus.misc.DataRegistry;
import studio.mevera.menus.misc.Slot;
import studio.mevera.menus.misc.button.Button;
import studio.mevera.menus.misc.button.actions.ButtonActionRegistry;
import studio.mevera.menus.misc.button.actions.ButtonClickAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class SerializedMenuYaml implements SerializedMenuIO<YamlConfiguration> {
	
	
	@Override
	public Class<YamlConfiguration> fileType() {
		return YamlConfiguration.class;
	}
	
	@Override
	public void write(@NotNull DataRegistry registry,
	                  @NotNull YamlConfiguration configuration) {
		
		String name = registry.getData("name");
		Capacity capacity = registry.getData("capacity");
		String title = registry.getData("title");
		
		configuration.set("name", name);
		configuration.set("properties.capacity", capacity.getRows());
		configuration.set("properties.title", title);
		
		YamlConfiguration buttonsSection = new YamlConfiguration();
		
		Content content = registry.getData("content");
		if(content == null) {
			return;
		}
		Map<Slot, Button> buttonMap = content.getButtonMap();
		for (Slot slot : buttonMap.keySet()) {
			//since there's no input, the default button name will be lowercase ("slot_{num}");
			String key = "slot_" + slot.getSlot();
			buttonsSection.set(key + ".slot",slot.getSlot());
			buttonsSection.set(key + ".item", buttonMap.get(slot).getItem());
			buttonsSection.set(key + ".actions", registry.getData("BTN:" + slot.getSlot()));
		}
		configuration.set("buttons", buttonsSection);
	}
	
	@Override
	public @NotNull DataRegistry read(@NotNull YamlConfiguration file) {
		DataRegistry registry = new DataRegistry();
		
		String name = file.getString("name");
		registry.setData("name", name);
		
		Capacity capacity = Capacity.ofRows(file.getInt("properties.capacity"));
		registry.setData("capacity", capacity);
		
		String menuTitle = file.getString("properties.title");
		registry.setData("title", menuTitle);
		
		Content content = Content.builder(capacity).build();
		ConfigurationSection section = file.getConfigurationSection("buttons");
		
		if(section == null) {
			registry.setData("content", content);
			return registry;
		}
		
		for(String key : section.getKeys(false)) {
			int slotPosition = section.getInt(key + ".slot");
			ItemStack itemStack = section.getItemStack(key + ".item");
			List<String> actions = section.getStringList(key + ".actions");
			registry.setData("BTN:" + slotPosition, actions);
			content.setButton(slotPosition, Button.clickable(itemStack,
							ButtonClickAction.plain((menu, event)-> {
				for(String action : actions) {
					int index = action.indexOf("(");
					String tag = action.substring(0, index);
					ButtonClickAction clickAction = ButtonActionRegistry.getInstance().getAction(tag);
					if(clickAction != null)
						clickAction.execute(menu, event);
				}
			})));
		}
		registry.setData("content", content);
		return registry;
	}
}
