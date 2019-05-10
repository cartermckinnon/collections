/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mck.collections.trie;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import mck.collections.trie.impl.TrieNode;

/**
 * An unmodifiable, key-value tree focused on speed.
 * 
 * @author carter
 */
public interface ImmutableTrie<V extends Comparable<V>>
{
    public Optional<Entry<V>> best();
    public Optional<Entry<V>> best( Comparator<Entry<V>> comparator );
    public List<Entry<V>> best( Comparator<Entry<V>> comparator, int n );
    
    public Optional<TrieNode<V>> bestNode();
    public Optional<TrieNode<V>> bestNode( Comparator<TrieNode<V>> comparator );
    public List<TrieNode<V>> bestNodes( Comparator<TrieNode<V>> comparator, int n );
    
    public Optional<Entry<V>> bestWith( String fragment );
    public Optional<Entry<V>> bestWith( String fragment, Comparator<TrieNode<V>> comparator );
    public List<Entry<V>> bestWith( String fragment, Comparator<TrieNode<V>> comparator, int n );
    public List<Entry<V>> bestWith( String fragment, int fragmentLength, Comparator<TrieNode<V>> comparator, int n );
    
    public Optional<TrieNode<V>> bestNodeWith( String fragment );
    public Optional<TrieNode<V>> bestNodeWith( String fragment, Comparator<TrieNode<V>> comparator );
    public Optional<TrieNode<V>> bestNodeWith( String fragment, int fragmentLength, Comparator<TrieNode<V>> comparator );
    public List<TrieNode<V>> bestNodesWith( String fragment, int n );
    public List<TrieNode<V>> bestNodesWith( String fragment, Comparator<TrieNode<V>> comparator, int n );
    public List<TrieNode<V>> bestNodesWith( String fragment, int fragmentLength, Comparator<TrieNode<V>> comparator, int n );
    
    public Optional<V> get( String key );
    public Optional<TrieNode<V>> getNode( String key );
    
    public List<Entry<V>> with( String fragment );
    public List<Entry<V>> with( String fragment, int n );
    public List<TrieNode<V>> nodesWith( String fragment );
    public List<TrieNode<V>> nodesWith( String fragment, int n );
    
    public int size();
    
    public interface Entry<V>
    {
        public String getKey();
        public V getValue();
    }
}
