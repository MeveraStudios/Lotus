package studio.mevera.menus.base.style;

import studio.mevera.menus.base.Content;
import org.jetbrains.annotations.NotNull;


/**
 * Represents a pane that will be applied 
 * to an {@link Content} through the method 
 * {@link Pane#applyOn(Content)}
 */
public interface Pane {

	/**
	 * Applies the pane on the content
	 * @param content the content to apply the pane on
	 */
	void applyOn(@NotNull Content content);
	
}
