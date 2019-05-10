
package mck.collections.trie;

import java.util.Optional;

/**
 * A modifiable, key-value tree focused on speed.
 * 
 * @author carter
 * @param <V> 
 */
public interface Trie<V extends Comparable<V>> extends ImmutableTrie<V>
{
    public boolean put( String key, V value );    
    public void set( String key, V value );
    public Optional<V> remove( String key );
    
    public interface Entry<V> extends ImmutableTrie.Entry<V>
    {
        public void set( V value );
        public void remove();
    }
}
