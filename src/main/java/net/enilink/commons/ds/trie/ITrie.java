package net.enilink.commons.ds.trie;

public interface ITrie<V> {

	/**
	 * Maps the key to the specified value in this trie. Neither the key, nor
	 * the value can be <code>null</code>.
	 * 
	 * @return the previous value to which the key was mapped, or
	 *         <code>null</null> if the key did not
	    have a previous mapping in the trie.
	 */
	public abstract V put(SiString key, V value);

	/**
	 * Gets the object associated with the specified key in the trie.
	 * 
	 * @return the value to which the key is mapped in the trie, or
	 *         <code>null</null> if the key is not mapped to any value in this
	 *         trie.
	 */

	public abstract V get(SiString key);

	/**
	 * Finds the object associated with a matching prefix in the trie.
	 * 
	 * @return the value to which the key is mapped in the trie, or
	 *         <code>null</null> if the key is not mapped to any value in this
	 *         trie.
	 */

	public abstract V findPrefix(SiString key);

	/*
	 * Returns <code>true</code> if this trie contains no mappings.
	 */
	public abstract boolean isEmpty();

	/**
	 * Removes tke key (and its corresponding value) from this trie. This method
	 * does nothing if the key is not in the trie.
	 * 
	 * @return the value to which the key had been mapped in this trie, or
	 *         <code>null</code> if the key did not have a mapping.
	 */
	public abstract Object remove(SiString key);

	/**
	 * Clear the trie so that it contains no mappings.
	 */
	public abstract void clear();

	/**
	 * Returns the number of keys in this trie.
	 */
	public abstract int size();

}