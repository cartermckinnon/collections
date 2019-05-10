
package mck.collections.trie.impl;

/**
 * a data strcuture for suffix trie
 *
 * @param <V> a generic type
 */
class ImmutableSuffixTrieImpl<V extends Comparable<V>> extends AbstractImmutableTrie<V>
{
    /**
     * constructor to be used only by TrieBuilder.
     */
    protected ImmutableSuffixTrieImpl()
    {
        super();
    }
    
    @Override
    public boolean put( String word, V value )
    {
        TrieNode<V> node = root;
        char[] chars = word.toCharArray();
        int[] indices = lookupIndices( chars );
        if( indices == null )
        {
            // not allowed to add this word if one of the chars is unsupported
            return false;
        }
        int level = 0;
        for( int i = chars.length - 1; i >= 0; i-- )
        {
            level++;
            int index = indices[i];
            if( node.children[index] == null )
            {
                TrieNode<V> temp = new TrieNode<V>( characters, chars[i], level );
                node.addChildIndex( index );
                node.children[index] = temp;
                temp.parent = node;
                if( node.level + 1 != temp.level )
                {
                    throw new RuntimeException( "SuffixTrie: Bugs occurred: "
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
                throw new RuntimeException( "SuffixTrie: Bugs occurred: "
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
     *
     * 1. For input word "abcde",
     * if the node that has the longest common suffix with level &lt;= maxSuffixLength is "r4de",
     * return node 'd'
     * <p>
     * 2. Equivalent to getNodeWithLongestCommonPart(word.substring(word.length() - maxSuffixLength, word.length()))
     *
     * @param word            : a word
     * @param maxSuffixLength : as described above
     * @return the node that has the longest common prefix with word
     */
    @Override
    protected TrieNode<V> getNodeWithLongestCommonPart( String word, int maxSuffixLength )
    {
        if( maxSuffixLength < 0 )
        {
            throw new IllegalArgumentException(
                    "IllegalArgumentException: the argument 'maxSuffixLength' (" + maxSuffixLength + ") should be non-negative." );
        }
        else if( maxSuffixLength > word.length() )
        {
            throw new IllegalArgumentException(
                    "IllegalArgumentException: the argument 'maxSuffixLength' (" + maxSuffixLength + ") should not be larger than word.length()." );
        }
        TrieNode<V> node = root;
        int start = word.length() - 1, end = word.length() - maxSuffixLength;
        for( int i = start; i >= end; i-- )
        {
            int index = characters.charToIndex( word.charAt( i ) );
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
