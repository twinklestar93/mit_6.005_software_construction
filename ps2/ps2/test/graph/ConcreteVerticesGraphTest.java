/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Map;

/**
 * Tests for ConcreteVerticesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteVerticesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteVerticesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteVerticesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteVerticesGraph();
    }
    
    /*
     * Testing ConcreteVerticesGraph...
     */

    // Testing strategy for ConcreteVerticesGraph.toString()
    // Partitions: Empty graph
    // Number of vertices: 0 vertex, 1 vertex, >1 vertices
    // Number of edges: 0 edge, 1 edge, >1 edges
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
     * Testing Vertex...
     */
    
    // Testing strategy for Vertex
    //  getTargets():
    //     number of targets: 0, 1, >1
    //     v is in the targets, v is not in the targets
    //  setTarget(v, w):
    //     set a new target with value
    //     set an existent target to zero - delete a target
    //     set a nonexistent target to zero
    //     set an existent target to new value
    //  toString():
    //     targets: 0, 1, >1
    @Test
    public void testSetTargetToNewTarget() {
        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");

        assertTrue(a.getLabel().equals("a"));

        a.setTarget(b.getLabel(), 1);
        Map<String, Integer> targets = a.getTargets();
        assertTrue("expect a to have b as a target", targets.containsKey(b.getLabel()));
        assertTrue("expect a to have an edge to b of weight 1", targets.containsValue(1));
        assertEquals("expect one target in a.targets", 1, targets.size());
        assertTrue(a.toString().equals("(a, [(b, 1)])"));

        Vertex c = new Vertex("c");
        a.setTarget(c.getLabel(), 2);
        targets = a.getTargets();
        assertTrue("expect a to have c as a target", targets.containsKey(c.getLabel()));
        assertTrue("expect a to have an edge to c of weight 2", targets.get(c.getLabel()).equals(2));
        assertEquals("expect two targets in a.targets", 2, targets.size());
        assertTrue(a.toString().equals("(a, [(b, 1), (c, 2)])") ||
                a.toString().equals("(a, [(c, 2), (b, 1)])"));
    }

    @Test
    public void testSetExistingTargetToZero() {
        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");

        a.setTarget(b.getLabel(), 1);
        a.setTarget(b.getLabel(), 0);
        Map<Vertex, Integer> targets = a.getTargets();
        assertEquals("expect a to have no targets", 0, targets.size());
        assertTrue(a.toString().equals("(a, [])"));
    }

    @Test
    public void testSetExistingTargetToNewValue() {
        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");

        a.setTarget(b.getLabel(), 1);
        a.setTarget(b.getLabel(), 2);
        Map<String, Integer> targets = a.getTargets();
        assertTrue("expect a to have target b", targets.containsKey(b.getLabel()));
        assertTrue("expect a to have a target with weight 2", targets.containsValue(2));
        assertEquals("expect a to have 1 target", 1, targets.size());
        assertTrue(a.toString().equals("(a, [(b, 2)])"));
    }

    @Test
    public void testSetNonExistingTargetToZero() {
        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");
        a.setTarget(b.getLabel(), 0);
        Map<Vertex, Integer> targets = a.getTargets();
        assertTrue("expect no targets in a.targets", targets.isEmpty());
        assertTrue(a.toString().equals("(a, [])"));
    }
    
}
