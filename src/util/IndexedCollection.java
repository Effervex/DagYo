package util;

import java.util.Collection;

/**
 * An indexed collection stores each of its values in an efficient manner with
 * the assumption that the indexing method only ever returns a single result (or
 * null).
 * 
 * @author Sam Sarjant
 */
public interface IndexedCollection<T> extends Collection<T> {

	/**
	 * Gets an element by its ID.
	 * 
	 * @param id
	 *            The ID of the element.
	 * @return The element with the given ID.
	 */
	public T get(long id);

	public void setSize(int size);

	/**
	 * Called when an element inside the indexed collection changes.
	 * 
	 * @param element
	 *            The element that changed.
	 */
	public void update(T element);
}
