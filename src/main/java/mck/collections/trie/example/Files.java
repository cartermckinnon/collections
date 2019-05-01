
package mck.collections.trie.example;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author carter
 */
public class Files
{
    protected static List<String> readLines( String file ) throws IOException
    {
        long start = System.nanoTime();
        List<String> words = new LinkedList<>(); // for optimal remove() performance
        try( Scanner scanner = new Scanner( new File(  file )))
        {
            while( scanner.hasNextLine() )
            {
                words.add( scanner.nextLine().trim() );
            }
        }
        long stop = System.nanoTime();
        System.out.println( "Loaded " + words.size() + " word(s) in " + ((stop - start) / 1000000.0) + " milliseconds" );
        return words;
    }
}
