
package mck.collections.trie.impl;

import mck.collections.trie.ImmutableTrie;

/**
 * ImmutableTrieBuilder-s only support put operations during the construction process.
 * 
 * After a retrieval call is made, no further put operations can be performed.
 *
 * @author carter
 * @param <T>
 * @param <V>
 */
public class ImmutableTrieBuilder<V extends Comparable<V>>
{
    private AbstractImmutableTrie<V> trie; // the trie being built
    private long failedToAdd = 0;

    /**
     * Get a builder for a new PrefixTrie.
     *
     * @param <V>
     * @return
     */
    public static <V extends Comparable<V>> ImmutableTrieBuilder<V> prefix()
    {
        return new ImmutableTrieBuilder<>( new ImmutablePrefixTrieImpl<V>() );
    }

    /**
     * Get a builder for a new SuffixTrie.
     *
     * @param <V>
     * @return
     */
    public static <V extends Comparable<V>> ImmutableTrieBuilder<V> suffix()
    {
        return new ImmutableTrieBuilder<>( new ImmutableSuffixTrieImpl<V>() );
    }

    private ImmutableTrieBuilder( AbstractImmutableTrie<V> trie )
    {
        this.trie = trie;
    }

    /**
     * Get the built trie.
     * <p>
     * This method can only be called once, so the builder should
     * be considered exhausted after this method returns.
     *
     * @return
     */
    public ImmutableTrie<V> getTrie()
    {
        if( trie == null )
        {
            throw new IllegalStateException( "getTrie has already been called, and it can only be called once!" );
        }
        ImmutableTrie<V> tmp = trie;
        trie = null; // after the trie's public API is accessible, its protected methods can no longer be
        return tmp;
    }

    /**
     * Keys that contain one or more unsupported chars are not added to the trie.
     *
     * @return the number of keys which were rejected during the building process.
     */
    public long getFailedToPut()
    {
        return failedToAdd;
    }

    /**
     * Add a key-value pair to the trie.
     *
     * @param key
     * @param value
     * @return
     */
    public boolean put( String key, V value )
    {
        if( trie == null )
        {
            throw new IllegalStateException( "you cannot modify a trie after it's been used!" );
        }
        if( !trie.put( key, value ) )
        {
            failedToAdd++;
            return false;
        }
        return true;
    }
}
