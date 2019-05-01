
package mck.collections.trie;

import static mck.collections.trie.TrieNode.charToIndex;

/**
 *
 * a data strcuture for prefix trie
 *
 * @param <V> a generic type
 */
public class PrefixTrie<V> extends AbstractTrie<V>
{
    /**
     * constructor for building a trie with TrieBuilder.
     */
    protected PrefixTrie()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected boolean put( String key, V value, int score )
    {
        TrieNode<V> node = root;
        char[] chars = key.toCharArray();
        int[] indices = lookupIndices( chars );
        if( indices == null )
        {
            // not allowed to add this word if one of the chars is unsupported
            return false;
        }
        int level = 0;
        for( int i = 0; i < chars.length; i++ )
        {
            level++;
            int index = indices[i];
            if( node.children[index] == null )
            {
                TrieNode<V> temp = new TrieNode<V>( chars[i], level );
                node.addChildIndex( index );
                node.children[index] = temp;
                temp.parent = node;
                if( node.level + 1 != temp.level )
                {
                    throw new RuntimeException( "PrefixTrie: Bugs occurred: "
                                                + "node.level + 1 should be equal to temp.level, while node.level = "
                                                + node.level + ", temp.level = " + temp.level );
                }
                node = temp;
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
        node.score = score;
        return true;
    }

    /**
     * 1. For input word "abcde", if the node that has the longest common prefix
     * with level &lt;= maxPrefixLength is "abc3", return node 'c'
     * <p>
     * 2. Equivalent to getNodeWithLongestCommonPart(word.substring(0, maxPrefixLength))
     *
     * @param word            : a word
     * @param maxPrefixLength : as described above
     * @return the node that has the longest common prefix with word
     */
    @Override
    protected TrieNode<V> getNodeWithLongestCommonPart( String word, int maxPrefixLength )
    {
        if( maxPrefixLength < 0 )
        {
            throw new IllegalArgumentException(
                    "IllegalArgumentException: the argument 'maxPrefixLength' (" + maxPrefixLength + ") should be non-negative." );
        }
        else if( maxPrefixLength > word.length() )
        {
            throw new IllegalArgumentException(
                    "IllegalArgumentException: the argument 'maxPrefixLength' (" + maxPrefixLength + ") should not be larger than word.length()." );
        }
        TrieNode<V> node = root;
        for( int i = 0; i < maxPrefixLength; i++ )
        {
            int index = charToIndex( word.charAt( i ) );
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
