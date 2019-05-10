
package mck.collections.trie.impl;

import java.util.Comparator;
import mck.collections.trie.ImmutableTrie;

/**
 * 
 * @author carter
 */
public class ImmutableTrieEntryComparatorAdapter<V extends Comparable<V>> implements Comparator<TrieNode<V>>
{
    private final Comparator<ImmutableTrie.Entry<V>> comparator;
    
    public ImmutableTrieEntryComparatorAdapter( Comparator<ImmutableTrie.Entry<V>> comparator )
    {
        this.comparator = comparator;
    }

    @Override
    public int compare( TrieNode<V> o1, TrieNode<V> o2 )
    {
        if( o1 == null || o2 == null )
            return 0;
        return comparator.compare( new ImmutableTrieEntryImpl<>( o1 ),
                                   new ImmutableTrieEntryImpl<>( o2 ));
    }
}
