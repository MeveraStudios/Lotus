package studio.mevera.menus.base.serialization;

import studio.mevera.menus.misc.DataRegistry;

/**
 * Interface for serializing and deserializing menu objects
 * into {@link DataRegistry} or from {@link DataRegistry}
 */
public interface MenuSerializer {
	
	/**
	 * Serializes a {@link SerializableMenu} object into a {@link DataRegistry}.
	 *
	 * @param menu the menu to be serialized
	 * @return the data registry containing the serialized menu data
	 */
	DataRegistry serialize(SerializableMenu menu);
	
	/**
	 * Deserializes a {@link DataRegistry} into a {@link SerializableMenu} object.
	 *
	 * @param dataRegistry The data registry containing the serialized menu data to convert.
	 * @return The deserialized {@link SerializableMenu} object.
	 */
	SerializableMenu deserialize(DataRegistry dataRegistry);
	
	/**
	 * Creates a new default implementation of the MenuSerializer.
	 *
	 * @return a new instance of SimpleMenuSerializer
	 */
	static MenuSerializer newDefaultSerializer() {
		return new SimpleMenuSerializer();
	}
}
