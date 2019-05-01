
package mck.collections.trie.example;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import mck.collections.trie.PrefixTrie;
import mck.collections.trie.TrieBuilder;
import mck.collections.trie.TrieNode;

/**
 *
 * @author carter
 */
public class InteractivePrefixTrie
{
    public static void main( String[] args ) throws IOException, InterruptedException
    {
        if( args.length != 2 )
        {
            System.err.println( "usage: Suggest <wordFile> <n>" );
            System.exit( 1 );
        }
        String file = args[0];
        int n = Integer.parseInt( args[1] );
        
        List<String> words = Files.readLines( file );
        
        TrieBuilder<PrefixTrie<Integer>,Integer> builder = TrieBuilder.prefix();
        int i = 0;
        int size = words.size();
        Iterator<String> it = words.iterator();
        long start = System.nanoTime();
        while( it.hasNext() )
        {
            if( !builder.put( it.next(), i, size - i ))
            {
                it.remove();
                size--;
                i--;
            }
            i++;
        }
        long stop = System.nanoTime();
        System.out.println( "Constructed trie in " + ((stop - start )/1000000.0) + " milliseconds");
        
        PrefixTrie<Integer> trie = builder.getTrie();
        
        try( Scanner scanner = new Scanner( System.in ))
        {
            while( true )
            {
                System.out.println( "Enter a prefix, or ':q' to exit." );
                String line = scanner.nextLine();
                if( line.equals( ":q" ))
                {
                    System.err.println( "Exiting..." );
                    break;
                }
                System.out.println( "> Prefix: '" + line + "'" );
                start = System.nanoTime();
                List<TrieNode<Integer>> nodes = trie.getBestKeyValueNodes( line, n );
                StringBuilder result = new StringBuilder();
                for( TrieNode<Integer> node : nodes )
                {
                    result.append( node.getKey() )
                            .append( '\n' );
                }
                stop = System.nanoTime();
                System.out.print( result.toString() );
                System.out.println( nodes.size() + " word(s)\t" + (( stop - start )/1000000.0) + " milliseconds");
                NaiveSuggestion.get( words, line, n );
            }
        }
    }
}
