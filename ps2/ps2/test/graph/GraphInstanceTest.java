/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for instance methods of Graph.
 * 
 * <p>PS2 instructions: you MUST NOT add constructors, fields, or non-@Test
 * methods to this class, or change the spec of {@link #emptyInstance()}.
 * Your tests MUST only obtain Graph instances by calling emptyInstance().
 * Your tests MUST NOT refer to specific concrete implementations.
 */
public abstract class GraphInstanceTest {
    
    // Testing strategy
    // Partition:
    //   add():
    //      graph already contains a vertex with the given label
    //      graph does not contain the vertex being added
    //   set():
    //      add an edge:
    //         graph contains source but not target
    //         graph contains target but not source
    //         graph contains neither
    //         graph contains both vertices
    //         add reflexive edge
    //      change an edge:
    //         change the weight of an existing edge to a different value
    //         set the weight of the existing edge to the same value
    //         change reflexive edge
    //      remove a weighted edge:
    //         the edge exists
    //         the edge does not exist:
    //            both vertices exist
    //            at least one vertices in missing
    //         remove reflexive edge
    //   remove():
    //       graph does not contain the vertex
    //       graph contains the vertex:
    //          the in degree of the vertex: 0, 1, >1
    //          the out degree of the vertex: 0, 1, >1
    //   vertices():
    //       number of vertices in the graph: 0, 1, >1
    //   sources():
    //       graph contains the target:
    //          the number of sources from a directed edge to the given target: 0, 1, >1
    //       graph does not contain the target
    //       graph contains a target with reflexive edge
    //   targets():
    //       graph contains the source:
    //           the number of targets from the given source: 0, 1, >1
    //       graph does not contain the source
    //       graph contains a source with reflexive edge
    
    /**
     * Overridden by implementation-specific test classes.
     * 
     * @return a new empty graph of the particular implementation being tested
     */
    public abstract Graph<String> emptyInstance();
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testInitialVerticesEmpty() {
        // TODO you may use, change, or remove this test
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }
    
    @Test
    public void testAddWithExistingVertex() {
        Graph<String> G = emptyInstance();
        G.add("a");
        assertTrue(!G.add("a"));
        assertEquals("expect graph to have one vertex", 1, G.vertices().size());
    }

    @Test
    public void testAddNewVertexToGraph() {
        Graph<String> G = emptyInstance();
        assertTrue(G.add("a"));
        assertTrue(G.add("b"));
        assertEquals("expect graph to have two vertices", 2, G.vertices().size());
    }


    @Test
    // source exists, target does not exist
    public void testSetNewEdge1() {
        Graph<String> G = emptyInstance();
        assertTrue(G.add("a"));
        assertEquals("expect no such edge in graph", 0, G.set("a", "b", 2));
        assertEquals("expect two vertices", 2, G.vertices().size());
        assertTrue("expect an edge from a to b with weight 2", G.targets("a").containsKey("b") &&
                G.targets("a").containsValue(2) && G.sources("b").containsKey("a") &&
                G.sources("b").containsValue(2));
    }

    @Test
    // target exists, source does not exist
    public void testSetNewEdge2() {
        Graph<String> G = emptyInstance();
        assertTrue(G.add("b"));
        assertEquals("expect no such edge in graph", 0, G.set("a", "b", 1));
        assertEquals("expect two vertices", 2, G.vertices().size());
        assertTrue("expect an edge from a to b with weight 2", G.targets("a").containsKey("b") &&
                G.targets("a").containsValue(2) && G.sources("b").containsKey("a") &&
                G.sources("b").containsValue(2));
    }

    @Test
    // neither source nor target exists
    public void testSetNewEdge3() {
        Graph<String> G = emptyInstance();
        assertEquals("expect no such edge in graph", 0, G.set("a", "b", 2));
        assertEquals("expect two vertices", 2, G.vertices().size());
        assertTrue("expect an edge from a to b with weight 2", G.targets("a").containsKey("b") &&
                G.targets("a").containsValue(2) && G.sources("b").containsKey("a") &&
                G.sources("b").containsValue(2));
    }

    @Test
    // both source and target exist
    public void testSetNewEdge4() {
        Graph<String> G = emptyInstance();
        assertTrue(G.add("a"));
        assertTrue(G.add("b"));
        assertEquals("expect no such edge in graph", 0, G.set("a", "b", 2));
        assertEquals("expect two vertices", 2, G.vertices().size());
        assertTrue("expect an edge from a to b with weight 2", G.targets("a").containsKey("b") &&
                G.targets("a").containsValue(2) && G.sources("b").containsKey("a") &&
                G.sources("b").containsValue(2));
    }

    @Test
    // set a reflexive edge
    public void testSetNewEdge5() {
        Graph<String> G = emptyInstance();
        assertEquals("expect no such edge in graph", 0, G.set("a", "a", 2));
        assertEquals("expect one vertex", 1, G.vertices().size());
        assertEquals("expect a reflexive edge from a to a", G.targets("a").containsKey("a") &&
                G.targets("a").containsValue(2) && G.sources("a").containsKey("a") &&
                G.sources("a").containsValue(2));
    }

    @Test
    // set existing edge to a different value
    public void testSetExistingEdge1() {
        Graph<String> G = emptyInstance();
        G.set("a", "b", 1);
        assertEquals("expect previous weight to be 1", G.set("a", "b", 2));
        assertTrue("expect the edge weight to be 2 after setting", G.targets("a").get("b").equals(2));
//        assertEquals("expect edge weight to be 2", 2, G.targets("a").get("b"));
        assertEquals("expect the previous edge weight to be 2", 2, G.set("a", "b", 2));

    }

    @Test
    // set existing edge to the same value
    public void testSetExistingEdge2() {
        Graph<String> G = emptyInstance();
        G.set("a", "b", 1);
        assertEquals("expect previous weight to be 1", G.set("a", "b", 1));
        assertTrue(G.targets("a").get("b").equals(1));
    }

    @Test
    // change reflexive edge
    public void testSetExistingEdge3() {
        Graph<String> G = emptyInstance();
        G.set("a", "a", 1);
        assertEquals("expect previous weight to be 1", 1, G.set("a", "a", 2));
        assertTrue(G.targets("a").get("a").equals(2));
    }

    @Test
    // the edge exists
    public void testSetEdgeToZero1() {
        Graph<String> G = emptyInstance();
        G.set("a", "b", 1);
        assertEquals("expect previous edge weight to be 1", 1, G.set("a", "b", 0));
        assertTrue("expect no edge between a and b",G.targets("a").isEmpty() && G.sources("b").isEmpty() &&
                G.targets("b").isEmpty() && G.sources("a").isEmpty());
    }

    @Test
    public void testSetEdgeToZero2() {
        // both vertices are missing
        Graph<String> G = emptyInstance();
        assertEquals("expect weight to be 0 if no edge exists between two vertices", 0, G.set("a", "b", 0));
        assertEquals("expect the graph to have no vertices", Collections.emptySet(), G.vertices());
        // one vertex in missing
        G.add("a");
        assertEquals("expect weight to be 0 if no edge exists between two vertices", 0, G.set("a", "b", 0));
        assertTrue("expect a in the graph and b excluded from the graph vertices", !G.vertices().contains("b") && G.vertices().contains("a"));
        // bot two vertices are present
        G.add("a");
        G.add("b");
        assertEquals("expect no edge between a and b", 0, G.set("a", "b", 0));
        assertTrue(G.targets("a").isEmpty() && G.sources("b").isEmpty() &&
                G.targets("b").isEmpty() && G.sources("a").isEmpty());
    }

    @Test
    public void testRemoveNonexistentVertex() {
        Graph<String> G = emptyInstance();
        G.add("a");
        assertTrue(!G.remove("b"));
        // the graph is not modified after removing nonexistent vertex
        assertTrue(G.vertices().contains("a"));
    }

    @Test
    public void testRemoveExistingVertex() {
        Graph<String> G = emptyInstance();
        G.add("a");
        // the in degree is 0, the out degree is 0
        assertTrue(G.remove("a") && !G.vertices().contains(new Vertex("a")));
        // the in degree is 0, the out degree is 1 (a); the in degree is 1, the out degree is 0
        G.add("a");
        G.set("a", "b", 1);
        assertTrue(G.remove("a"));
        assertTrue(!G.vertices().contains("a") && !G.targets("a").containsKey("b") &&
                !G.sources("b").containsKey("a") &&
                G.vertices().contains("b"));
        // the in degree is 0, the out degree is 2 (a); the out degree is 2, the in degree is 0;
        G.add("a");
        assertEquals(0, G.set("a", "b", 1));
        assertEquals(0, G.set("a", "c", 2));
        assertTrue(G.remove("a"));
        assertTrue(!G.vertices().contains("a") && G.vertices().contains("b") &&
                G.vertices().contains("c") && !G.targets("a").containsKey("b") &&
                !G.targets("a").containsKey("c") && !G.sources("b").containsKey("a") &&
                !G.sources("c").containsKey("a"));
        // test in degree is 1, out degree is 1
        assertEquals(0, G.set("a", "b", 1));
        assertEquals(0, G.set("c", "a", 1));
        assertTrue(G.remove("a"));
        assertTrue(!G.vertices().contains("a") && G.vertices().contains("b") &&
                G.vertices().contains("c") && !G.targets("a").containsKey("b") &&
                !G.targets("c").containsKey("a") && !G.sources("a").containsKey("c") &&
                !G.sources("b").containsKey("a"));
        // test in degree is 2, out degree is 1
        assertEquals(0, G.set("b", "a", 1));
        assertEquals(0, G.set("c", "a", 1));
        assertEquals(0, G.set("a", "b", 2));
        assertTrue(G.remove("a"));
        assertTrue(G.vertices().contains("b") && G.vertices().contains("c") &&
                !G.vertices().contains("a") && G.sources("a").size() == 0 && !G.targets("a").containsKey("b") &&
                !G.sources("b").containsKey("a") && !G.targets("c").containsKey("a"));
        // test in degree is 2, out degree is 2
        assertEquals(0, G.set("b", "a", 1));
        assertEquals(0, G.set("c", "a", 1));
        assertEquals(0, G.set("a", "b", 2));
        assertEquals(0, G.set("a", "c", 3));
        assertTrue(G.remove("a"));
        assertTrue(!G.vertices().contains("a") && G.sources("a").size() == 0 &&
                G.targets("a").size() == 0 && !G.targets("b").containsKey("a") &&
                !G.targets("c").containsKey("a") && !G.sources("b").containsKey("a") &&
                !G.sources("c").containsKey("a") && G.vertices().size()==3);
        // remove reflexive vertex
        assertEquals(0, G.set("b", "b", 1));
        assertTrue(G.remove("b"));
        assertTrue(!G.vertices().contains("b") && !G.targets("b").containsKey("b") &&
                !G.sources("b").containsKey("b"));
    }

    @Test
    public void testSourceContainsTarget() {
        Graph<String> G = emptyInstance();
        assertEquals(0 , G.set("a", "b", 1));
        assertEquals(1, G.sources("b").size());
        assertTrue(G.sources("b").containsKey("a") && G.sources("b").containsValue(1));
        assertTrue(G.sources("a").size() == 0);
        assertEquals(0, G.set("c", "b", 2));
        assertTrue(G.sources("b").size() == 2 && G.sources("b").containsKey("c") &&
                G.sources("b").containsValue(2));
        // graph contains a target with reflexive edge
        assertEquals(0, G.set("d", "d", 1));
        assertTrue(G.sources("d").containsKey("d") &&
                G.sources("d").size() == 1);
    }

    @Test
    public void testSourceNotContainTarget() {
        Graph<String> G = emptyInstance();
        assertTrue(G.sources("d").size() == 0);
        G.add("d");
        assertTrue(G.sources("d").isEmpty());
    }

    @Test
    public void testTargetContainsSource() {
        Graph<String> G = emptyInstance();
        assertEquals(0, G.targets("a").size());
        assertEquals(0 , G.set("a", "b", 1));
        assertEquals(1, G.targets("a").size());
        assertTrue(G.targets("a").containsKey("b") && G.targets("a").containsValue(1));
        assertEquals(0, G.targets("b").size());
        assertEquals(0, G.set("a", "c", 2));
        assertTrue(G.targets("a").size() == 2 && G.targets("a").containsKey("b") &&
                G.targets("a").containsKey("c"));
        // graph contains a source with reflexive edge
        assertEquals(0, G.set("a", "a", 3));
        assertTrue(G.targets("a").size() == 3 && G.targets("a").containsKey("a") &&
                G.targets("a").containsValue(3) && G.targets("a").containsKey("b") &&
                G.targets("a").containsKey("c"));
    }

    @Test
    public void testTargetNotContainsSource() {
        Graph<String> G = emptyInstance();
        assertTrue(G.targets("d").size() == 0);
        G.add("d");
        assertTrue(G.targets("d").isEmpty());
    }
}
