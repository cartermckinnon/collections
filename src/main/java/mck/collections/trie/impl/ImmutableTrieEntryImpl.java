
package mck.collections.trie.impl;

import static java.util.Objects.requireNonNull;
import mck.collections.trie.ImmutableTrie;

/**
 *
 * @author carter
 */
class ImmutableTrieEntryImpl<V extends Comparable<V>> implements ImmutableTrie.Entry<V>
{
    private final String key;
    private final V value;
    
    protected ImmutableTrieEntryImpl( TrieNode<V> node )
    {
        requireNonNull( node, "node cannot be null" );
        if( !node.isKeyValueNode() )
        {
            throw new IllegalArgumentException( "node is not a key-value node: " + node );
        }
        value = node.getValue()
                .orElseThrow( () -> new IllegalArgumentException( "node is a key-value node, but has no value: " + node ));
        key = node.getKey();
    }
    
    @Override
    public String getKey()
    {
        return key;
    }

    @Override
    public V getValue()
    {
        return value;
    }
}
