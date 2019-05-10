
package mck.collections.trie.util;

import java.util.Arrays;
import static java.util.Objects.requireNonNull;

public class ASCIICharacterSet implements CharacterSet
{
    private static final String DEFAULT = "abcdefghijklmnopqrstuvwxyz0123456789";
    
    private final char[] chars;
    private final char   charToIndexOffset;
    private final int[]  charToIndexMap;
    
    /**
     * Default character set consisting of a small subset (a-z0-9) of ASCII.
     */
    public ASCIICharacterSet()
    {
        this( DEFAULT );
    }
    
    /**
     * Character set consisting of ASCII characters.
     * 
     * @param characters string of unique ASCII characters to be included in the set.
     */
    public ASCIICharacterSet( String characters )
    {
        requireNonNull( characters, "character string cannot be null" );
        if( characters.isEmpty() )
        {
            throw new IllegalArgumentException( "character string cannot be empty" );
        }
        if( !uniqueASCIICharacters( characters ))
        {
            throw new IllegalArgumentException( "character string must contain unique, ASCII (0-255) characters: '" + characters + "'" );
        }
        chars = characters.toCharArray();
        char maxChar = Character.MIN_VALUE;
        char minChar = Character.MAX_VALUE;
        for( char c : chars )
        {
            if( c < minChar )
            {
                minChar = c;
            }
            if( c > maxChar )
            {
                maxChar = c;
            }
        }
        charToIndexOffset = minChar;
        int[] ciMap = new int[maxChar - minChar + 1];
        Arrays.fill( ciMap, -1 );
        for( int i = 0; i < chars.length; i++ )
        {
            ciMap[chars[i] - charToIndexOffset] = i;
        }
        charToIndexMap = ciMap;
    }
    
    /**
     * a fast way to convert char to index at the 'children' field of TrieNode
     *
     * @param c : a char
     * @return index of c according to CHAR_TO_INDEX_MAP
     */
    @Override
    public final int charToIndex( char c )
    {
        final int adjusted = c - charToIndexOffset;
        if( adjusted < 0 || adjusted >= charToIndexMap.length )
        {
            return -1;
        }
        return charToIndexMap[adjusted];
    }
    
    /**
     * Size of character-to-index map.
     * 
     * @return 
     */
    public final int size()
    {
        return charToIndexMap.length;
    }
    
    /**
     * Verify that a String contains unique, ASCII characters.
     * 
     * @param s
     * @return true if every character is ASCII and unique; false otherwise.
     */
    // visible for testing
    protected static boolean uniqueASCIICharacters( String s )
    {
        boolean[] charsSeen = new boolean[256];
        for( Character c : s.toCharArray() )
        {
            if( c < 0 || c > 255 )
            {
                return false;
            }
            if( charsSeen[c] )
            {
                return false;
            }
            charsSeen[c] = true;
        }
        return true;
    }
}
