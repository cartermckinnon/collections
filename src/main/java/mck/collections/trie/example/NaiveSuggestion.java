
package mck.collections.trie.example;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author carter
 */
public class NaiveSuggestion
{
    public static List<String> get( List<String> words, String prefix, int n )
    {
        final long start = System.nanoTime();
        List<String> result = new ArrayList<>( n );
        for( String word : words )
        {
            if( word.startsWith( prefix ))
            {
                result.add( word );
            }
            if( result.size() >= n )
            {
                break;
            }
        }
        final long stop = System.nanoTime();
        System.out.println( result.size() + " word(s)\t" + (( stop - start )/1000000.0) + " milliseconds (naive)");
        return result;
    }
}
