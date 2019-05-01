
package mck.collections.trie;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import static mck.collections.trie.TrieNode.charToIndex;

/**
 *
 * abstract class currently for PrefixTrie and SuffixTrie
 *
 * @param <V> a generic type
 */
public abstract class AbstractTrie<V>
{
    /**
     * the root node of this trie
     */
    protected TrieNode<V> root;
    /**
     * number of key-value nodes added to this trie
     */
    protected int size;

    /**
     * an internal constructor
     */
    protected AbstractTrie()
    {
        root = new TrieNode<>( (char) 0, 0 );
    }

    /**
     * @param length : the array length
     * @return a int array in which all elements are 1
     */
    private static int[] ones( int length )
    {
        int[] ones = new int[length];
        Arrays.fill( ones, 1 );
        return ones;
    }

    /**
     * look up char indices
     *
     * @param chars : the chars of a prefix or suffix, from .toCharArray()
     * @return indices of the chars according to TrieNode.CHAR_TO_INDEX_MAP
     */
    protected int[] lookupIndices( char[] chars )
    {
        int[] result = new int[chars.length];
        for( int i = 0; i < chars.length; i++ )
        {
            int index = charToIndex( chars[i] );
            if( index == -1 )
            {
                return null;
            }
            else
            {
                result[i] = index;
            }
        }
        return result;
    }

    /**
     * @return number of key-value nodes added to this trie
     */
    public int size()
    {
        return size;
    }

    /**
     * @return the root
     */
    public TrieNode<V> getRoot()
    {
        return root;
    }

    /**
     * inserts a key and its value, a key-value pair, into this trie.
     *
     * @param key   : the key
     * @param value : the value
     */
    protected boolean put( String key, V value )
    {
        return put( key, value, 1 );
    }

    /**
     * @param word : prefix for PrefixTrie and suffix for SuffixTrie
     * @return the node that is prefixed or suffixed with word; it may be a
     *         non-key-value node or a leaf node
     */
    public TrieNode<V> getNode( String word )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart( word );
        return node != null && node.level == word.length() ? node : null;
    }

    /**
     * @param word            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie;
     * @return the node that is prefixed with word.substring(0, length) or
     *         suffixed with word.substring(word.length() - length,
     *         word.length()); it may be a key-value node or not a key-value
     *         node
     */
    public TrieNode<V> getNode( String word, int substringLength )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart( word, substringLength );
        return node != null && node.level == word.length() ? node : null;
    }

    /**
     * @param word : prefix for PrefixTrie and suffix for SuffixTrie
     * @return either a key-value node or a non key-value node
     */
    public TrieNode<V> getNodeWithLongestCommonPart( String word )
    {
        return getNodeWithLongestCommonPart( word, word.length() );
    }

    /**
     * @param word : prefix for PrefixTrie and suffix for SuffixTrie
     * @return all key-value nodes prefixed or suffixed with this word
     */
    public List<TrieNode<V>> getKeyValueNodes( String word )
    {
        return getKeyValueNodes( word, word.length() );
    }

    /**
     * Equivalent to getKeyValueNodes(word.substring(0, maxLength)) for
     * PrefixTrie, and equivalent to
     * getKeyValueNodes(word.substring(word.length() - maxLength)) for
     * SuffixTrie
     *
     * @param word            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @return the key-value nodes with level &gt;= substringLength
     */
    protected List<TrieNode<V>> getKeyValueNodes( String word, int substringLength )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart( word, substringLength );
        if( node == null || node.level < substringLength )
        {
            return Collections.emptyList();
        }
        return node.getKeyValueNodes();
    }

    /**
     * @param word      : prefix for PrefixTrie and suffix for SuffixTrie
     * @param condition : for selecting key-value nodes that matches this
     *                  condition
     * @return all key-value nodes that match 'condition'
     */
    public List<TrieNode<V>> getKeyValueNodes( String word, Function<TrieNode<V>, Boolean> condition )
    {
        return getKeyValueNodes( word, word.length(), condition );
    }

    /**
     * @param word            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @param condition       : for selecting key-value nodes that matches this
     *                        condition
     * @return the matched key-value nodes with level &gt;= substringLength
     */
    public List<TrieNode<V>> getKeyValueNodes( String word, int substringLength, Function<TrieNode<V>, Boolean> condition )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart( word, substringLength );
        if( node == null || node.level < substringLength )
        {
            return Collections.emptyList();
        }
        return node.getKeyValueNodes( condition );
    }

    /**
     * @param word : prefix for PrefixTrie and suffix for SuffixTrie
     * @return the key-value node for the exact word, or null if it does not
     *         exist
     */
    public TrieNode<V> getkeyValueNode( String word )
    {
        TrieNode<V> theNode = getNodeWithLongestCommonPart( word );
        return (theNode.isKeyValueNode && theNode.level == word.length()) ? theNode : null;
    }

    /**
     * @return the top scored leaf node
     */
    public TrieNode<V> getBestKeyValueNode()
    {
        return getRoot().getBestKeyValueNode();
    }

    /**
     * @param comparator : comparator for key-value nodes comparison and
     *                   selection
     * @return the top scored key-value node according to the comparator
     */
    public TrieNode<V> getBestKeyValueNode( Comparator<TrieNode<V>> comparator )
    {
        return getRoot().getBestKeyValueNode( comparator );
    }

    /**
     * see getBestKeyValueNode(word, length)
     *
     * @param word : prefix for PrefixTrie and suffix for SuffixTrie
     * @return the highest scored key-value node
     */
    public TrieNode<V> getBestKeyValueNode( String word )
    {
        return getBestKeyValueNode( word, word.length() );
    }

    /**
     *
     * Equivalent to getBestKeyValueNode(word.substring(0, maxLength)) for
     * PrefixTrie, and equivalent to
     * getBestKeyValueNode(word.substring(word.length() - maxLength)) for
     * SuffixTrie
     *
     * @param word            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @return the key-value node with the highest score among key-value nodes
     *         with level &gt;= substringLength
     */
    protected TrieNode<V> getBestKeyValueNode( String word, int substringLength )
    {
        return getBestKeyValueNode( word, substringLength, ( a, b ) -> (a.score - b.score) );
    }

    /**
     * @param word       : prefix for PrefixTrie and suffix for SuffixTrie
     * @param comparator : a normal comparator      <pre> 
	 * if a &lt; b the comparator returns a negative value
     * if a == b the comparator returns 0
     * if a &gt; b the comparator returns a positive value
     *                   </pre>
     *
     * @return the best key-value node with level &gt;= word.length();
     */
    public TrieNode<V> getBestKeyValueNode( String word, Comparator<TrieNode<V>> comparator )
    {
        return getBestKeyValueNode( word, word.length(), comparator );
    }

    /**
     * Similar to getBestKeyValueNode, with TrieNode comparator 'comparator'
     *
     * @param word            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @param comparator      : comparator for selecting the best key-value node
     * @return the best key-value node with level &gt;= substringLength
     */
    protected TrieNode<V> getBestKeyValueNode( String word, int substringLength, Comparator<TrieNode<V>> comparator )
    {
        TrieNode<V> tempSubtreeRoot = getNodeWithLongestCommonPart( word, substringLength );
        if( tempSubtreeRoot == null || tempSubtreeRoot.level < substringLength )
        {
            return null;
        }
        return tempSubtreeRoot.getBestKeyValueNode( comparator );
    }

    //////////////////////////////////////////////////////////
    /**
     * @param word                : prefix for PrefixTrie and suffix for
     *                            SuffixTrie
     * @param numTopKeyValueNodes : number of top key-value nodes
     * @return best key-value nodes
     */
    public List<TrieNode<V>> getBestKeyValueNodes( String word, int numTopKeyValueNodes )
    {
        return getBestKeyValueNodes( word, word.length(), numTopKeyValueNodes, ( a, b ) -> (a.score - b.score) );
    }

    public List<TrieNode<V>> getBestKeyValueNodes( String word, int numTopKeyValueNodes, Comparator<TrieNode<V>> comparator )
    {
        return getBestKeyValueNodes( word, word.length(), numTopKeyValueNodes, comparator );
    }

    /**
     * @param word                : a word
     * @param substringLength     : substring length of 'word' for prefix for
     *                            PrefixTrie and suffix for SuffixTrie; for
     *                            example, if word is "abcde" and
     *                            substringLength is 3, then it's "abc" for
     *                            PrefixTrie and "cde" for SuffixTrie
     * @param numTopKeyValueNodes : number of top key-value nodes to retrieve
     * @param comparator          : a comparator for comparison of key-value
     *                            nodes
     * @return the best key-value nodes with level &gt;= substringLength
     *         according to comparator 'comparator'
     */
    private List<TrieNode<V>> getBestKeyValueNodes( String word, int substringLength, int numTopKeyValueNodes, Comparator<TrieNode<V>> comparator )
    {
        TrieNode<V> tempSubtreeRoot = getNodeWithLongestCommonPart( word, substringLength );
        if( tempSubtreeRoot.level < substringLength )
        {
            return Collections.emptyList();
        }
        return (tempSubtreeRoot == null) ? null : tempSubtreeRoot.getBestKeyValueNodes( numTopKeyValueNodes, comparator );
    }

    /**
     * inserts a key and its value, a key-value pair, with score 'score', into
     * this trie.
     *
     * @param key   : the key
     * @param value : the value
     * @param score : the score of the key value pair
     * @return true if succesfully added, and false if the word contains
     *         unsupported characters
     */
    protected abstract boolean put( String key, V value, int score );

    /**
     * See implementation in PrefixTrie.java or SuffixTrie.java
     *
     * @param word            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @return the node that has the longest common suffix with word
     */
    protected abstract TrieNode<V> getNodeWithLongestCommonPart( String word, int substringLength );
}
