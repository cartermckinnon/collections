
package mck.collections.trie.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import mck.collections.trie.ImmutableTrie;
import mck.collections.trie.util.ASCIICharacterSet;
import mck.collections.trie.util.CharacterSet;

/**
 *
 * abstract class currently for PrefixTrie and SuffixTrie
 *
 * @param <V> a generic type
 */
public abstract class AbstractImmutableTrie<V extends Comparable<V>> implements ImmutableTrie<V>
{
    protected TrieNode<V> root;
    protected int size;
    protected final CharacterSet characters;

    protected AbstractImmutableTrie()
    {
        this( new ASCIICharacterSet() );
    }
    
    protected AbstractImmutableTrie( CharacterSet characters )
    {
        this.characters = characters;
        root = new TrieNode<>( characters, (char) 0, 0 );
    }

    @Override
    public Optional<Entry<V>> best()
    {
        return getRoot()
                .getBestKeyValueNode()
                .map( ImmutableTrieEntryImpl::new );
    }

    @Override
    public Optional<Entry<V>> best( Comparator<Entry<V>> comparator )
    {
        return bestNode( new ImmutableTrieEntryComparatorAdapter<>( comparator ))
                .map( ImmutableTrieEntryImpl::new );
    }

    @Override
    public List<Entry<V>> best( Comparator<Entry<V>> comparator, int n )
    {
        return bestNodes( new ImmutableTrieEntryComparatorAdapter<>( comparator ), n )
                .stream()
                .map( ImmutableTrieEntryImpl::new )
                .collect( toList() );
    }

    @Override
    public Optional<TrieNode<V>> bestNode( Comparator<TrieNode<V>> comparator )
    {
        List<TrieNode<V>> nodes = bestNodes( comparator, 1 );
        if( nodes.size() > 1 )
        {
            throw new IllegalStateException( "retrieved more than 1 best node" );
        }
        if( nodes.isEmpty() )
        {
            return Optional.empty();
        }
        return Optional.of( nodes.get( 0 ));
    }

    @Override
    public List<TrieNode<V>> bestNodes( Comparator<TrieNode<V>> comparator, int n )
    {
        return getRoot().getBestKeyValueNodes( n, comparator );
    }

    @Override
    public Optional<Entry<V>> bestWith( String fragment )
    {
        return bestNodeWith( fragment ).map( ImmutableTrieEntryImpl::new );
    }

    @Override
    public Optional<Entry<V>> bestWith( String fragment, Comparator<TrieNode<V>> comparator )
    {
        return bestNodeWith( fragment, comparator ).map( ImmutableTrieEntryImpl::new );
    }

    @Override
    public List<Entry<V>> bestWith( String fragment, Comparator<TrieNode<V>> comparator, int n )
    {
        return bestWith( fragment, fragment.length(), comparator, n );
    }
    
    @Override
    public List<Entry<V>> bestWith( String fragment, int fragmentLength, Comparator<TrieNode<V>> comparator, int n )
    {
        return bestNodesWith( fragment, fragmentLength, comparator, n )
                .stream()
                .map( ImmutableTrieEntryImpl::new )
                .collect( toList() );
    }
    
    @Override
    public List<Entry<V>> with( String fragment )
    {
        return with( fragment, fragment.length() );
    }

    @Override
    public List<Entry<V>> with( String fragment, int n )
    {
        Optional<TrieNode<V>> node = getNode( fragment, n );
        if( node.isEmpty() )
        {
            return Collections.emptyList();
        }
        return node.get()
                .getKeyValueChildren()
                .stream()
                .map( ImmutableTrieEntryImpl::new )
                .collect( toList() );
    }

    /**
     * Get the index for each character in an array.
     *
     * @param chars : the chars of a prefix or suffix, from .toCharArray()
     * @return indices of the chars according to TrieNode.CHAR_TO_INDEX_MAP, or
     *         null if chars contains an unsupported character.
     */
    protected int[] lookupIndices( char[] chars )
    {
        int[] indices = new int[chars.length];
        for( int i = 0; i < chars.length; i++ )
        {
            int index = characters.charToIndex( chars[i] );
            if( index == -1 ) // unsupported character
            {
                return null;
            }
            else
            {
                indices[i] = index;
            }
        }
        return indices;
    }

    /**
     * @return number of key-value nodes added to this trie
     */
    @Override
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
     * @param key : prefix for PrefixTrie and suffix for SuffixTrie
     * @return the node that is prefixed or suffixed with word; it may be a
     *         non-key-value node or a leaf node
     */
    @Override
    public Optional<TrieNode<V>> getNode( String key )
    {
        return getNode(key, key.length() );
    }

    /**
     * @param key            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie;
     * @return the node that is prefixed with word.substring(0, length) or
     *         suffixed with word.substring(word.length() - length,
     *         word.length()); it may be a key-value node or not a key-value
     *         node
     */
    public Optional<TrieNode<V>> getNode( String key, int substringLength )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart(key, substringLength );
        return node != null && node.level == substringLength ?
               Optional.of( node )
               :
               Optional.empty();
    }

    /**
     * @param fragment : prefix for PrefixTrie and suffix for SuffixTrie
     * @return all key-value nodes prefixed or suffixed with this word
     */
    @Override
    public List<TrieNode<V>> nodesWith( String fragment )
    {
        return nodesWith(fragment, fragment.length() );
    }

    /**
     * Equivalent to getKeyValueNodes(word.substring(0, maxLength)) for
     * PrefixTrie, and equivalent to
     * getKeyValueNodes(word.substring(word.length() - maxLength)) for
     * SuffixTrie
     *
     * @param fragment            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @return the key-value nodes with level &gt;= substringLength
     */
    @Override
    public List<TrieNode<V>> nodesWith( String fragment, int substringLength )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart( fragment, substringLength );
        if( node == null || node.level < substringLength )
        {
            return Collections.emptyList();
        }
        return node.getKeyValueChildren();
    }

    /**
     * @param fragment      : prefix for PrefixTrie and suffix for SuffixTrie
     * @param condition : for selecting key-value nodes that matches this
     *                  condition
     * @return all key-value nodes that match 'condition'
     */
    public List<TrieNode<V>> nodesWith( String fragment, Function<TrieNode<V>, Boolean> condition )
    {
        return AbstractImmutableTrie.this.nodesWith(fragment, fragment.length(), condition );
    }

    /**
     * @param fragment            : a word
     * @param substringLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @param condition       : for selecting key-value nodes that matches this
     *                        condition
     * @return the matched key-value nodes with level &gt;= substringLength
     */
    public List<TrieNode<V>> nodesWith( String fragment, int substringLength, Function<TrieNode<V>, Boolean> condition )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart(fragment, substringLength );
        if( node == null || node.level < substringLength )
        {
            return Collections.emptyList();
        }
        return node.getKeyValueChildren( condition );
    }

    /**
     * @return the top scored leaf node
     */
    @Override
    public Optional<TrieNode<V>> bestNode()
    {
        return getRoot().getBestKeyValueNode();
    }

    /**
     * @param comparator : comparator for key-value nodes comparison and
     *                   selection
     * @return the top scored key-value node according to the comparator
     */
    public Optional<TrieNode<V>> getBestKeyValueNode( Comparator<TrieNode<V>> comparator )
    {
        return getRoot().getBestKeyValueNode( comparator );
    }

    /**
     * see getBestKeyValueNode(word, length)
     *
     * @param key : prefix for PrefixTrie and suffix for SuffixTrie
     * @return the highest scored key-value node
     */
    @Override
    public Optional<TrieNode<V>> bestNodeWith( String key )
    {
        return bestNodeWith( key, key.length() );
    }

    /**
     *
     * Equivalent to getBestKeyValueNode(word.substring(0, maxLength)) for
     * PrefixTrie, and equivalent to
     * getBestKeyValueNode(word.substring(word.length() - maxLength)) for
     * SuffixTrie
     *
     * @param key            : a word
     * @param fragmentLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @return the key-value node with the highest score among key-value nodes
     *         with level &gt;= substringLength
     */
    public Optional<TrieNode<V>> bestNodeWith( String key, int fragmentLength )
    {
        return bestNodeWith( key, fragmentLength, TrieNode::compareTo );
    }

    /**
     * @param key       : prefix for PrefixTrie and suffix for SuffixTrie
     * @param comparator : a normal comparator      <pre>
     * if a &lt; b the comparator returns a negative value
     * if a == b the comparator returns 0
     * if a &gt; b the comparator returns a positive value
     *                   </pre>
     *
     * @return the best key-value node with level &gt;= word.length();
     */
    public Optional<TrieNode<V>> bestNodeWith( String key, Comparator<TrieNode<V>> comparator )
    {
        return bestNodeWith( key, key.length(), comparator );
    }

    /**
     * Similar to getBestKeyValueNode, with TrieNode comparator 'comparator'
     *
     * @param fragment            : a word
     * @param fragmentLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @param comparator      : comparator for selecting the best key-value node
     * @return the best key-value node with level &gt;= substringLength
     */
    @Override
    public Optional<TrieNode<V>> bestNodeWith( String fragment, int fragmentLength, Comparator<TrieNode<V>> comparator )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart( fragment, fragmentLength );
        if( node == null || node.level < fragmentLength )
        {
            return null;
        }
        return node.getBestKeyValueNode( comparator );
    }

    /**
     * @param key                : prefix for PrefixTrie and suffix for
     *                            SuffixTrie
     * @param n : number of top key-value nodes
     * @return best key-value nodes
     */
    @Override
    public List<TrieNode<V>> bestNodesWith( String key, int n )
    {
        return bestNodesWith( key, key.length(), TrieNode::compareTo, n );
    }
    
    @Override
    public List<TrieNode<V>> bestNodesWith( String key, Comparator<TrieNode<V>> comparator, int n )
    {
        return bestNodesWith( key, key.length(), comparator, n );
    }

    /**
     * @param key                : a word
     * @param fragmentLength     : substring length of 'word' for prefix for
     *                            PrefixTrie and suffix for SuffixTrie; for
     *                            example, if word is "abcde" and
     *                            substringLength is 3, then it's "abc" for
     *                            PrefixTrie and "cde" for SuffixTrie
     * @param n : number of top key-value nodes to retrieve
     * @param comparator          : a comparator for comparison of key-value
     *                            nodes
     * @return the best key-value nodes with level &gt;= substringLength
     *         according to comparator 'comparator'
     */
    @Override
    public List<TrieNode<V>> bestNodesWith( String key, int fragmentLength, Comparator<TrieNode<V>> comparator, int n )
    {
        TrieNode<V> node = getNodeWithLongestCommonPart( key, fragmentLength );
        if( node == null
            ||
            node.level < fragmentLength )
        {
            return Collections.emptyList();
        }
        return node.getBestKeyValueNodes( n, comparator );
    }
    
    @Override
    public Optional<V> get( String key )
    {
        return getNode( key )
                .map( TrieNode::getValue )
                .orElse( Optional.empty() );
    }

    /**
     * inserts a key and its value, a key-value pair into
     * this trie -- this is only meant to be called by builders.
     * 
     * This method cannot be utilized after the first retrieval method call.
     *
     * @param key   : the key
     * @param value : the value
     * @return true if succesfully added, and false if the word contains
     *         unsupported characters
     */
    protected abstract boolean put( String key, V value );

    /**
     * See implementation in PrefixTrie.java or SuffixTrie.java
     *
     * @param key            : a word
     * @param fragmentLength : substring length of 'word' for prefix for
     *                        PrefixTrie and suffix for SuffixTrie; for example,
     *                        if word is "abcde" and substringLength is 3, then
     *                        it's "abc" for PrefixTrie and "cde" for SuffixTrie
     * @return the node that has the longest common suffix with word
     */
    protected abstract TrieNode<V> getNodeWithLongestCommonPart( String key, int fragmentLength );
}
