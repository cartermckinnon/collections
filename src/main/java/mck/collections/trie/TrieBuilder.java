
package mck.collections.trie;

/**
 * Selectively exposes the put method of AbstractTrie, allowing much more flexible
 * trie construction while still disallowing modifications after first use.
 * 
 * @author carter
 */
public class TrieBuilder<T extends AbstractTrie<V>,V>
{
    private long failedToAdd = 0;
    
    /**
     * Get a builder for a new PrefixTrie.
     * 
     * @param <V>
     * @return 
     */
    public static <V> TrieBuilder<PrefixTrie<V>,V> prefix()
    {
        return new TrieBuilder<>( new PrefixTrie<>() );
    }
    
    /**
     * Get a builder for a new SuffixTrie.
     * 
     * @param <V>
     * @return 
     */
    public static <V> TrieBuilder<SuffixTrie<V>,V> suffix()
    {
        return new TrieBuilder<>( new SuffixTrie<>() );
    }
    
    private T trie; // the trie being built
    
    private TrieBuilder( T trie )
    {
        this.trie = trie;
    }
    
    /**
     * Get the built trie.
     * 
     * This method can only be called once, so the builder should
     * be considered exhausted after this method returns.
     * 
     * @return 
     */
    public T getTrie()
    {
        if( trie == null )
        {
            throw new IllegalStateException( "getTrie has already been called, and it can only be called once!" );
        }
        T tmp = trie;
        trie = null; // after the trie's public API is accessible, its protected methods can no longer be
        return tmp;
    }
    
    public long getFailedToPut()
    {
        return failedToAdd;
    }
    
    /**
     * Add a key-value pair to the trie, weighted by a score.
     * 
     * @param key
     * @param value
     * @param score
     * @return 
     */
    public boolean put( String key, V value, int score )
    {
        if( trie == null )
        {
            throw new IllegalStateException( "you cannot modify a trie after it's been used!" );
        }
        if( !trie.put( key, value, score ))
        {
            failedToAdd++;
            return false;
        }
        return true;
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
        if( !trie.put( key, value ))
        {
            failedToAdd++;
            return false;
        }
        return true;
    }
}
