
package mck.collections.trie.impl;

/**
 *
 * a data strcuture for prefix trie
 *
 * @param <V> a generic type
 */
class ImmutablePrefixTrieImpl<V extends Comparable<V>> extends AbstractImmutableTrie<V>
{
    protected ImmutablePrefixTrieImpl()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean put( String key, V value )
    {
        TrieNode<V> node = root;
        char[] chars = key.toCharArray();
        int[] indices = lookupIndices( chars );
        if( indices == null )
        {
            return false; // key contains unsupported characters
        }
        int level = 0;
        for( int i = 0; i < chars.length; i++ )
        {
            level++;
            int index = indices[i];
            if( node.children[index] == null )
            {
                TrieNode<V> child = new TrieNode<>( characters, chars[i], level );
                node.addChildIndex( index );
                node.children[index] = child;
                child.parent = node;
                if( node.level + 1 != child.level )
                {
                    throw new RuntimeException( "PrefixTrie: Bugs occurred: "
                                                + "node.level + 1 should be equal to temp.level, while node.level = "
                                                + node.level + ", temp.level = " + child.level );
                }
                node = child;
            }
            else
            {
                node = node.children[index];
            }
            if( node.level < 0 )
            {
                throw new RuntimeException( "PrefixTrie: Bugs occurred: "
                                            + "node.level should be nonnegative, while node.level = " + node.level );
            }
        }
        if( node.isKeyValueNode == false )
        {
            size++;
        }
        node.isKeyValueNode = true;
        node.value = value;
        return true;
    }

    /**
     * 1. For input word "abcde", if the node that has the longest common prefix
     * with level &lt;= maxPrefixLength is "abc3", return node 'c'
     * <p>
     * 2. Equivalent to getNodeWithLongestCommonPart(word.substring(0, maxPrefixLength))
     *
     * @param key            : a word
     * @param fragmentLength : as described above
     * @return the node that has the longest common prefix with word
     */
    @Override
    protected TrieNode<V> getNodeWithLongestCommonPart( String key, int fragmentLength )
    {
        if( fragmentLength < 0 )
        {
            throw new IllegalArgumentException(
                    "IllegalArgumentException: the argument 'maxPrefixLength' (" + fragmentLength + ") should be non-negative." );
        }
        else if( fragmentLength > key.length() )
        {
            throw new IllegalArgumentException(
                    "IllegalArgumentException: the argument 'maxPrefixLength' (" + fragmentLength + ") should not be larger than word.length()." );
        }
        TrieNode<V> node = root;
        for( int i = 0; i < fragmentLength; i++ )
        {
            int index = characters.charToIndex(key.charAt( i ) );
            if( index >= 0 && node.children[index] != null )
            {
                node = node.children[index];
            }
            else
            {
                break;
            }
        }
        return node;
    }
}
