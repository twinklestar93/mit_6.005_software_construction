/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for static methods of Graph.
 * 
 * To facilitate testing multiple implementations of Graph, instance methods are
 * tested in GraphInstanceTest.
 */
public class GraphStaticTest {
    
    // Testing strategy
    //   empty()
    //     no inputs, only output is empty graph
    //     observe with vertices()
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testEmptyVerticesEmpty() {
        assertEquals("expected empty() graph to have no vertices",
                Collections.emptySet(), Graph.empty().vertices());
    }

    // Testing strategy
    //    Graph instances with different types of immutable labels:
    //        types: integer, String(tested in GraphInstanceTest)
    @Test
    public void testIntLabelAdd() {
        Graph<Integer> G = Graph.empty();
        assertTrue(G.add(1));
        assertFalse(G.add(1));
        assertEquals(1, G.vertices().size());
        assertTrue(G.vertices().contains(1));
        assertEquals("({1}, {})", G.toString());
        assertTrue(G.sources(1).isEmpty());
        assertTrue(G.targets(1).isEmpty());
        assertTrue(G.add(2));
        assertEquals(2, G.vertices().size());
        assertTrue(G.toString().equals("({1, 2}, {})") ||
                G.toString().equals("({2, 1}, {})"));
        assertTrue(G.sources(2).isEmpty() && G.targets(2).isEmpty());
    }

    @Test
    public void tesIntegerLabelSet(){
        Graph<Integer> graph = Graph.empty();

        //add an edge
        assertEquals(0, graph.set(1, 2, 3));

        assertEquals(2, graph.vertices().size());
        assertTrue(graph.vertices().contains(1) && graph.vertices().contains(2));
        assertEquals(0, graph.sources(1).size());
        assertEquals(1, graph.targets(1).size());
        assertEquals(new Integer(3), graph.targets(1).get(2));
        assertEquals(0, graph.targets(2).size());
        assertEquals(1, graph.sources(2).size());
        assertEquals(new Integer(3), graph.sources(2).get(1));
        assertTrue(graph.toString().equals("({1, 2}, {(1, 2, 3)})") ||
                graph.toString().equals("({2, 1}, {(1, 2, 3)})"));

        //remove an edge
        assertEquals(3, graph.set(1, 2, 0));

        assertEquals(2, graph.vertices().size());
        assertTrue(graph.vertices().contains(1) && graph.vertices().contains(2));
        assertEquals(0, graph.sources(1).size());
        assertEquals(0, graph.targets(1).size());
        assertEquals(0, graph.sources(2).size());
        assertEquals(0, graph.targets(2).size());
        assertTrue(graph.toString().equals("({1, 2}, {})") ||
                graph.toString().equals("({2, 1}, {})"));
    }

    @Test
    public void testIntegerLabelRemove(){
        Graph<Integer> graph = Graph.empty();

        graph.set(1, 2, 3);
        assertTrue(graph.remove(1));
        assertFalse(graph.remove(1));

        assertEquals(1, graph.vertices().size());
        assertTrue(graph.vertices().contains(2));
        assertEquals(0, graph.sources(2).size());
        assertEquals(0, graph.targets(2).size());
        assertEquals("({2}, {})", graph.toString());
    }
}
