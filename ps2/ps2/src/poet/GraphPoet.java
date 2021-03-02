/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import graph.Graph;

/**
 * A graph-based poetry generator.
 * 
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 * 
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 * 
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {
    
    private final Graph<String> graph = Graph.empty();
    
    // Abstraction function:
    //   A function that takes in a string and convert it into a poetry
    //   using a corpus of text provided
    // Representation invariant:
    //   All vertex label must be lower case, non-empty, and contains no empty space
    // Safety from rep exposure:
    //   The graph is private field, the returned values and method parameters are immutable.
    //   The constructor takes in a file object which is used to read from a file and create the
    //   the rep, and after constructor returns, there is no way to access or modify the rep
    //   through this file object.
    
    /**
     * Create a new poet with the graph from corpus (as described above).
     * 
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
        final Scanner scanner = new Scanner(corpus);
        String current;
        String prev;

        if (!scanner.hasNext()) {
            scanner.close();
            return;
        }

        current = scanner.next().toLowerCase();
        graph.add(current);

        while (scanner.hasNext()) {
            prev = current;
            current = scanner.next().toLowerCase();
            int previousWeight = graph.targets(prev).getOrDefault(current, 0);
            graph.set(prev, current, previousWeight+1);
        }

        scanner.close();
    }
    
    public void checkRep() {
        assert checkVertex();
    }

    private boolean checkVertex() {
        for (String vertex : graph.vertices()) {
            if (vertex.isEmpty()) return false; // vertex label must be non-empty
            if (!vertex.equals(vertex.toLowerCase())) return false; // vertex label must be lower case
            if (vertex.length() > vertex.replaceAll("\\s+", "").length()) return false; // vertex label must not contain spaces
        }
        return true;
    }

    
    /**
     * Generate a poem.
     * 
     * @param input string from which to create the poem
     * @return poem (as described above)
     */
    public String poem(String input) {
        final String[] words = input.trim().split("\\s+");
        final StringBuilder poemBuilder = new StringBuilder();

        if (words.length > 0) {
            poemBuilder.append(words[0]);
        }

        for (int i = 0; i+1 < words.length; i++) {
            String bridgeWord = getMaximalBridgeWord(words[i].toLowerCase(), words[i+1].toLowerCase());
            if (!bridgeWord.equals("")) {
                poemBuilder.append(" " + bridgeWord);
            }
            poemBuilder.append(" " + words[i+1]);
        }
        final String poem = poemBuilder.toString();
        return poem;
    }
    
    public String toString() {
        return graph.toString();
    }

    /**
     * Return bridge word with the maximum weight between w1 and w2
     * @param w1 the previous word
     * @param w2 the next word
     * @return word if there is a bridge word connecting two words, otherwise return empty string
     */
    private String getMaximalBridgeWord(String w1, String w2) {
        String bridgeWord = "";
        Set<String> word1Targets = graph.targets(w1).keySet();
        Set<String> word2Sources = graph.sources(w2).keySet();

        Set<String> bridgeWords = word1Targets;
        bridgeWords.retainAll(word2Sources);

        // find the maximum weight of two edges w1-b-w2
        int maximumWeight = 0;
        int sumTwoEdgeWeight = 0;
        for (String b : bridgeWords) {
            sumTwoEdgeWeight = graph.targets(w1).get(b) + graph.sources(w2).get(b);
            if (sumTwoEdgeWeight > maximumWeight) {
                maximumWeight = sumTwoEdgeWeight;
                bridgeWord = b;
            }
        }
        return bridgeWord;
    }

}
