/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for ConcreteEdgesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteEdgesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteEdgesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteEdgesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteEdgesGraph();
    }
    
    /*
     * Testing ConcreteEdgesGraph...
     */
    
    // Testing strategy for ConcreteEdgesGraph.toString()
    //   Partitions: empty graph
    //   number of vertices: 0, 1, >1
    //   number of edges: 0, 1, >1

    @Test
    public void testEmptyGraph() {
        Graph<String> G = emptyInstance();
        assertTrue(G.toString().equals("({}, {})"));
    }

    @Test
    public void testOneVertexNoEdge() {
        Graph<String> G = emptyInstance();
        G.add("a");
        assertEquals(G.toString(), "({a}, {})");
    }

    @Test
    public void testOneVertexOneEdge() {
        Graph<String> G = emptyInstance();
        G.add("a");
        G.set("a", "a", 1);
        assertEquals(G.toString(), "({a}, {(a, a, 1)})");
    }

    @Test
    public void testTwoVerticesNoEdge() {
        Graph<String> G = emptyInstance();
        G.add("a");
        G.add("b");
        assertTrue(G.toString().equals("({a, b}, {})") ||
                G.toString().equals("({b, a}, {})"));
    }

    @Test
    public void testTwoVerticesOneEdge() {
        Graph<String> G = emptyInstance();
        G.add("a");
        G.add("b");
        G.set("a", "b", 1);
        assertTrue(G.toString().equals("({a, b}, {(a, b, 1)})") ||
                G.toString().equals("({b, a}, {(a, b, 1)})"));
    }

    @Test
    public void testTwoVerticesTwoEdges() {
        Graph<String> G = emptyInstance();
        G.add("a");
        G.add("b");
        G.set("a", "b", 1);
        G.set("b", "a", 2);
        assertTrue(G.toString().equals("({a, b}, {(a, b, 1), (b, a, 2)})") ||
                G.toString().equals("({b, a}, {(a, b, 1), (b, a, 2)})") ||
                G.toString().equals("({a, b}, {(b, a, 2), (a, b, 1)})") ||
                G.toString().equals("({b, a}, {(b, a, 2), (a, b, 1)})"));

    }
    
    /*
     * Testing Edge...
     */
    
    // Testing strategy for Edge
    //   Partitions on vertices: source = target, source != target
    //   Partitions on weight: 1, >1

    @Test
    public void testDistinctVerticesWeightOne() {
        Edge e = new Edge("a", "b", 1);

        assertEquals(1, e.getWeight());
        assertEquals("a", e.getSource());
        assertEquals("b", e.getTarget());
        assertEquals("(a, b, 1)", e.toString());
    }

    @Test
    public void testSameVertexGreaterThanOne() {
        Edge e = new Edge("a", "a", 2);

        assertEquals(2, e.getWeight());
        assertEquals("a", e.getSource());
        assertEquals("a", e.getTarget());
        assertEquals("(a, a, 2)", e.toString());
    }
    
}
