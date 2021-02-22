/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph implements Graph<String> {

    private final Set<String> vertices = new HashSet<>();
    private final List<Edge> edges = new ArrayList<>();
    
    // Abstraction function:
    //   AF(r) = an ordered pair (V, E)
    //   where V = { all v in r.vertices }
    //   and E = { (v, v') for all (v, v') pair in r.vertices where v has an edge to v' }
    //   and there exists a W such that W(v, v') for all (v, v') in E.
    // Representation invariant:
    //   vertices must contain every vertex incident to an edge in edges.
    //   for any vertices v, v', only one edge in edges can have v as head and v' as tail
    // Safety from rep exposure:
    //   all fields are private, the vertices point to a mutable object with immutable elements
    //   the edges point to mutable list object with immutable elements. The set and list objects
    //   are mutable but they are never passed in by any operation, as all operation either have
    //   immutable parameters or non. vertices() returns a fresh defensive copy. The mutable map
    //   objects returned by sources() and targets() do not have access to vertices and edges as
    //   they are newly constructed HashMaps.
    
    public ConcreteEdgesGraph() {
        checkRep();
    }
    
    // checkRep
    public void checkRep() {
        checkEdgeVertexInVertices();
        checkDistinctEdge();
    }

    // asserts that every edge's vertices are in vertices
    private void checkEdgeVertexInVertices() {
        for (Edge e : edges) {
            String source = e.getSource();
            String target = e.getTarget();
            assert vertices.contains(source);
            assert vertices.contains(target);
        }
    }

    // asserts that no more than two edges that have the same source and target could exists
    private void checkDistinctEdge() {
        Map<String, String> edgeMap = new HashMap<>();
        for (Edge e : edges) {
            if (edgeMap.get(e.getSource()) != null) {
                assert !edgeMap.get(e.getSource()).equals(e.getTarget());
            }
            edgeMap.put(e.getSource(), e.getTarget());
        }
    }
    
    @Override public boolean add(String vertex) {
        if (vertices.contains(vertex)) {
            return false;
        } else {
            vertices.add(vertex);
            checkRep();
            return true;
        }
    }
    
    @Override public int set(String source, String target, int weight) {
        int previousWeight = 0;
        Iterator<Edge> iter = edges.iterator();
        while (iter.hasNext()) {
            Edge e = iter.next();
            if (e.getSource().equals(source) && e.getTarget().equals(target)) {
                previousWeight = e.getWeight();
                iter.remove();
            }
        }

        if (weight > 0) {
            edges.add(new Edge(source, target, weight));
            vertices.add(source);
            vertices.add(target);
        }

        checkRep();
        return previousWeight;
    }
    
    @Override public boolean remove(String vertex) {
        if (!vertices.contains(vertex)) {
            return false;
        } else {
            Iterator<Edge> iter = edges.iterator();
            while (iter.hasNext()) {
                Edge e = iter.next();
                // remove the edge from or to the vertex
                if (e.getSource().equals(vertex) || e.getTarget().equals(vertex)) {
                    iter.remove();
                }
            }
            // remove the vertex from the graph
            vertices.remove(vertex);
            checkRep();
            return true;
        }
    }
    
    @Override public Set<String> vertices() {
        return new HashSet<>(vertices);
    }
    
    @Override public Map<String, Integer> sources(String target) {
        // reference: https://www.baeldung.com/java-list-to-map
        return edges.stream().
                filter(edge -> edge.getTarget().equals(target)).
                collect(Collectors.toMap(Edge::getSource, Edge::getWeight));
    }
    
    @Override public Map<String, Integer> targets(String source) {
        return edges.stream().
                filter(edge -> edge.getSource().equals(source)).
                collect(Collectors.toMap(Edge::getTarget, Edge::getWeight));
    }

    /**
     * Returns a string representation of this vertices graph, e.g.,
     * ({a, b}, {(a, b, 1), (b, a, 2)})
     *
     * @return vertices followed by edges
     */
    @Override public String toString() {
        final Set<String> edgeSet = new HashSet<>();
        edges.stream().forEach(edge -> {
            final String edgeTuple = String.format("(%s, %s, %s)", edge.getSource(), edge.getTarget(), edge.getWeight());
            edgeSet.add(edgeTuple);
        });
        final String toStringVertexSet = vertices.toString().replace("[", "{").replace("]", "}");
        final String toStringEdgeSet = edgeSet.toString().replace("[", "{").replace("]", "}");
        final StringBuilder toString = new StringBuilder();
        toString.append("(").append(toStringVertexSet).append(",").append(" ").append(toStringEdgeSet).append(")");
        return toString.toString();
    }
    
}

/**
 * Edge is a immutable type representing an edge in directed weighted graph. Each edge
 * consists of source vertex, target vertex and associated weight, vertices are represented
 * by immutable String type, weight is positive integer. Only a single edge exist
 * between any pair of vertices, and each edge has exactly one positive weight.
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Edge {

    private final String source;
    private final String target;
    private final int weight;
    
    // Abstraction function:
    //   represents an edge with source vertex = this.source, target vertex = this.target
    //   and the associated weight = this.weight
    // Representation invariant:
    //   source and target are strings that must not be null
    //   weight is positive integer
    // Safety from rep exposure:
    //   all fields are private, this.source is immutable, this.target is immutable,
    //   this.weight is immutable.
    
    // constructor

    /**
     * Create an edge with positive weight
     * @param source source vertex
     * @param target target vertex
     * @param weight positive integer
     */
    public Edge(String source, String target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;

        checkRep();
    }
    // checkRep
    public void checkRep() {
        assert this.source != null;
        assert this.target != null;
        assert this.weight > 0;
    }

    /**
     * @return source vertex associated with the edge
     */
    public String getSource() {
        return this.source;
    }

    /**
     * @return target vertex associated with edge
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * @return weight associated with the edge
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Returns a string representation of the edge, e.g. (a, b, 2)
     *
     * @return the source vertex, followed by target vertex and the weight
     */
    @Override
    public String toString() {
        StringBuilder edge = new StringBuilder();
        edge.append("(").append(getSource()).append(",").append(" ").append(getTarget())
                .append(",").append(" ").append(getWeight()).append(")");
        return edge.toString();
    }
    
}
