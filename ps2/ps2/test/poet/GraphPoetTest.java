/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {
    
    // Testing strategy
    //   Number of brides: 0, 1, >1
    //   Number of associations (in graph): 1, >1
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // test cases copied from: https://github.com/Lalee10/6.005-SC-ps2/blob/e1eec137562258b2b7c49465b06896663029a0a6/test/poet/GraphPoetTest.java
    @Test
    public void noBridge() throws IOException {
        File f = new File("test/poet/poet.txt");
        GraphPoet gp = new GraphPoet(f);
        String inputText = "No word here is present in the graph";
        String expectedOutput = "No word here is present in the graph";
        assertTrue(gp.poem(inputText).equals(expectedOutput));
    }

    @Test
    public void singleBridge() throws IOException {
        File f = new File("test/poet/poet.txt");
        GraphPoet gp = new GraphPoet(f);
        String inputText = "Seek to explore new plus exciting synergies!";
        String expectedOutput = "Seek to explore strange new plus exciting synergies!";
        assertTrue(gp.poem(inputText).equals(expectedOutput));
    }

    @Test
    public void multipleAssociations() throws IOException {
        File f = new File("test/poet/poet1.txt");
        GraphPoet gp = new GraphPoet(f);
        String inputText = "A B D";
        String expectedOutput = "A B c D";
        gp.poem(inputText);
        assertTrue(gp.poem(inputText).equals(expectedOutput));

    }

    @Test
    public void twoBridges() throws IOException {
        File f = new File("test/poet/poet.txt");
        GraphPoet gp = new GraphPoet(f);
        String inputText = "Seek to explore new and exciting synergies!";
        String expectedOutput = "Seek to explore strange new life and exciting synergies!";
        assertTrue(gp.poem(inputText).equals(expectedOutput));
    }
    
}
