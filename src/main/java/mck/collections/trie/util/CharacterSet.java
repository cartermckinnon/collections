
package mck.collections.trie.util;

/**
 * Abstraction for the "character lookup" logic of an AbstractTrie.
 * 
 * @author carter
 */
public interface CharacterSet
{
    /**
     * Look up a character's proper index in a node's set of children.
     * 
     * @param c
     * @return 
     */
    public int charToIndex( char c );
    
    /**
     * Number of characters in this set.
     * 
     * @return 
     */
    public int size();
}
