
package mck.collections.trie.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import lombok.ToString;
import mck.collections.trie.util.CharacterSet;

/**
 * a class for nodes of the trie graph
 *
 */
@ToString( onlyExplicitlyIncluded = true )
public class TrieNode<V extends Comparable<V>> implements Comparable<TrieNode<V>>
{
    TrieNode<V>[] children;
    TrieNode<V> parent;
    boolean isKeyValueNode;
    V value;
    /** the level of root is 0,
     * and the level of other nodes are 1, 2, 3, etc.
     */
    final int level;
    final char c;
    /**
     * current number of children;
     * the current offset at 'childrenIndices'
     */
    int numChildren;
    /** the children indices;
     * children[childrenIndices[i]] where 0 &lt;= i &lt; numChildren is the ith children.
     */
    transient int[] childrenIndices;
    List<TrieNode<V>> keyValueChildren;

    /**
     * @param charSet
     * @param c     : char c field of this node
     * @param level : the level of this node in the trie
     */
    protected TrieNode( CharacterSet charSet, char c, int level )
    {
        // english characters, and -'._
        this.children = new TrieNode[charSet.size()];
        this.value = null;
        this.level = level;
        this.c = c;
        this.childrenIndices = new int[charSet.size()];
        Arrays.fill( childrenIndices, -1 );
    }

    /**
     * @return the value
     */
    @ToString.Include
    public Optional<V> getValue()
    {
        return Optional.ofNullable( value );
    }

    /**
     * @param value : the value of this node
     */
    public void setValue( V value )
    {
        this.value = value;
    }

    /**
     * the level of root is 0,
     * and the level of other nodes are 1, 2, 3, etc.
     *
     * @return the level
     */
    @ToString.Include
    public int getLevel()
    {
        return level;
    }

    /**
     * @return wether this node is a key-value or not
     */
    @ToString.Include
    public boolean isKeyValueNode()
    {
        return isKeyValueNode;
    }

    /**
     * @return the parent
     */
    public Optional<TrieNode<V>> getParent()
    {
        return Optional.ofNullable( parent );
    }

    /**
     * @return ancestors including the root and exclusive of this, in descending order of level;
     * For example, if the level of this is 5, the returned list is of size 5 and the levels
     * of the elements in the result would be 4, 3, 2, 1, 0 (in that order).
     */
    public List<TrieNode<V>> getAncestors()
    {
        TrieNode<V> node = parent;
        List<TrieNode<V>> result = new ArrayList<>( level );
        while( node != null )
        {
            result.add( node );
            node = node.parent;
        }
        return result;
    }

    /**
     * @return key based on ancestors and this node
     */
    @ToString.Include
    public String getKey()
    {
        char[] keyChars = new char[level];
        int offset = level - 1;
        TrieNode<V> node = this;
        while( node.level > 0 )
        {
            keyChars[offset--] = node.c;
            node = node.parent;
        }
        return new String( keyChars );
    }

    /**
     * @return the current number of children of this node
     */
    @ToString.Include
    public int getNumChildren()
    {
        return numChildren;
    }

    /**
     * @return non-null elements of 'children'
     */
    public List<TrieNode<V>> getChildren()
    {
        List<TrieNode<V>> result = new ArrayList<>( numChildren );
        for( int i = 0; i < numChildren; i++ )
        {
            TrieNode<V> child = children[childrenIndices[i]];
            result.add( child );
        }
        return result;
    }

    @Override
    public int compareTo( TrieNode<V> o )
    {
        if( o != null
            &&
            getValue().isPresent()
            &&
            o.getValue().isPresent() )
        {
            return getValue().get().compareTo( o.getValue().get() );
        }
        return 0;
    }

    /**
     * @param index : the child index to add;
     *              the index is according CHAR_TO_INDEX_MAP
     */
    public void addChildIndex( int index )
    {
        childrenIndices[numChildren++] = index;
    }

    /**
     * @return whether this is the root node of the trie or not
     */
    @ToString.Include
    public boolean isRoot()
    {
        return level == 0;
    }

    /**
     * @return a list, which contains all key-value nodes among sub-tree nodes
     */
    public List<TrieNode<V>> getKeyValueChildren()
    {
        if( keyValueChildren == null )
        {
            ArrayList<TrieNode<V>> keyVals = new ArrayList<>();
            if( isKeyValueNode )
            {
                keyVals.add( this );
            }
            for( int i = 0; i < numChildren; i++ )
            {
                TrieNode<V> child = children[childrenIndices[i]];
                keyVals.addAll( child.getKeyValueChildren() );
            }
            keyValueChildren = Collections.unmodifiableList( keyVals );
        }
        return keyValueChildren;
    }

    /**
     * @param condition : a condition for key-value nodes selection
     * @return all key-value nodes that match 'condition'
     */
    public List<TrieNode<V>> getKeyValueChildren( Function<TrieNode<V>, Boolean> condition )
    {
        return getKeyValueChildren()
                .stream()
                .filter( condition::apply )
                .collect( toList() );
    }

    /**
     * @return the key-value node with the highest value at the sub tree succeeding 'this'
     */
    public Optional<TrieNode<V>> getBestKeyValueNode()
    {
        return getBestKeyValueNode( TrieNode::compareTo );
    }

    /**
     * @param comparator : for comparison of nodes
     * @return the best key-value node according to the comparator
     */
    public Optional<TrieNode<V>> getBestKeyValueNode( Comparator<TrieNode<V>> comparator )
    {
        List<TrieNode<V>> keyVals = getKeyValueChildren();
        return keyVals.isEmpty() ?
               Optional.empty()
               :
               Optional.of( Collections.max( keyVals, comparator ));
    }

    /**
     * @param n : number of top key-value nodes to select
     * @return the top key-value nodes according to scores
     */
    public List<TrieNode<V>> getBestKeyValueNodes( int n )
    {
        return getBestKeyValueNodes( n, TrieNode::compareTo );
    }

    /** @param n : number of top key-value nodes to select
     * @param comparator          : comparator
     * @return the top key-value nodes according to the comparator
     */
    public List<TrieNode<V>> getBestKeyValueNodes( int n, Comparator<TrieNode<V>> comparator )
    {
        if( n <= 0 )
        {
            throw new IllegalArgumentException( "IllegalArgumentException: numTopKeyValueNodes (" + n + ") should be positive " );
        }
        List<TrieNode<V>> keyVals = getKeyValueChildren();
        if( n == 1 )
        {
            TrieNode<V> best = Collections.max( keyVals, comparator );
            return Collections.singletonList( best );
        }
        else
        {
            List<TrieNode<V>> modifiable = new ArrayList<>( keyVals );
            Collections.sort( modifiable, comparator.reversed() );
            return modifiable.subList( 0, Math.min( n, modifiable.size() ));
        }
    }
}
