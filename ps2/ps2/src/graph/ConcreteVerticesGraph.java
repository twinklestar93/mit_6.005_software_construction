/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.*;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteVerticesGraph<L> implements Graph<String> {
    
    private final List<Vertex> vertices = new ArrayList<>();
    
    // Abstraction function:
    //   AF(r) = an ordered pair (V, E)
    //   where V = { all v in r.vertices }
    //   and E = { (v, v') for all (v, v') pair in r.vertices where v has an edge to v' }
    //   and there exists a W such that W(v, v') for all (v, v') in E.
    // Representation invariant:
    //   Each element of vertices has a distinct label from the rest.
    //   Any element in vertices can only have an edge between the corresponding elements of vertices.
    // Safety from rep exposure:
    //   vertices is a private field, pointing to mutable list with mutable elements.
    //   All parameters of public methods are immutable. Mutable objects returned by vertices()
    //   sources() and targets() are fresh HashMap/HashSet constructions with immutable parameters.
    
    public void checkRep() {
        checkDistinctLabel();
        checkEdgeInvariant();
    }

    // asserts that every element in vertices has a label distinct from the other labels
    private void checkDistinctLabel() {
        Set<String> labels = new HashSet<>();
        for (Vertex v : vertices) {
            assert  !labels.contains(v.getLabel());
            labels.add(v.getLabel());
        }
    }

    // asserts that every element in vertices has only edges to elements in vertices
    private void checkEdgeInvariant() {
        int V = vertices.size();
        for (Vertex v : vertices) {
            for (Vertex target : v.getTargets().keySet()) {
                assert vertices.contains(target);
            }
        }
    }
    
    @Override public boolean add(String vertex) {
        Vertex v = new Vertex(vertex);
        if (vertices.contains(v)) {
            return false;
        } else {
            vertices.add(v);
            return true;
        }
    }
    
    @Override public int set(String source, String target, int weight) {
        Vertex t = new Vertex(target);
        Vertex s = new Vertex(source);
        int previousWeight;
        if (weight != 0) {
            add(source);
            add(target);
            previousWeight = vertices.get(vertices.indexOf(s)).setTarget(t, weight);
        } else {
            if (vertices.contains(s) && vertices.contains(t)) {
                previousWeight = vertices.get(vertices.indexOf(s)).setTarget(t, weight);
            } else {
                previousWeight = 0;
            }
        }
        return previousWeight;
    }
    
    @Override public boolean remove(String vertex) {
        if (!vertices().contains(vertex)) {
            return false;
        } else {
            Map<String, Integer> sources = sources(vertex);
            Map<String, Integer> targets = targets(vertex);
            Vertex vertexToBeRemoved = new Vertex(vertex);
            // remove edges from the vertex
            for (String t : targets.keySet()) {
                Vertex target = new Vertex(t);
                vertices.get(vertices.indexOf(vertexToBeRemoved)).setTarget(target, 0);
            }
            // remove edges to the vertex
            for (String s : sources.keySet()) {
                Vertex source = new Vertex(s);
                vertices.get(vertices.indexOf(source)).setTarget(vertexToBeRemoved, 0);
            }
            // remove the vertex from the graph
            vertices.remove(vertexToBeRemoved);
            return true;
        }
    }
    
    @Override public Set<String> vertices() {
        Set<String> labels = new HashSet<>();
        vertices.stream().forEach(vertex -> labels.add(vertex.getLabel()));
        return labels;
    }
    
    @Override public Map<String, Integer> sources(String target) {
        Vertex t = new Vertex(target);
        Map<String, Integer> sources = new HashMap<>();
        vertices.stream().forEach(vertex -> {
            Map<Vertex, Integer> targets = vertex.getTargets();
            if (targets.containsKey(t)) {
                sources.put(vertex.getLabel(), targets.get(t));
            }
        });
        return sources;
    }
    
    @Override public Map<String, Integer> targets(String source) {
        Vertex s = new Vertex(source);
        Map<String, Integer> targets = new HashMap<>();
        for (Map.Entry<Vertex, Integer> entry : vertices.get(vertices.indexOf(s)).getTargets().entrySet()) {
            targets.put(entry.getKey().getLabel(), entry.getValue());
        }
        return targets;
    }

    /**
     * Returns a string representation of this vertices graph, e.g.,
     * [(a, [(c, 2), (b, 1)]), (b, [(a, 2), (c, 1)])]
     *
     * @return a list of vertices, with each element represented by @Vertex
     */
    @Override public String toString() {
        StringBuilder s = new StringBuilder();
        List<String> graph = new ArrayList<>();
        for (Vertex v : this.vertices) {
            graph.add(v.toString());
        }
        s.append(graph.toString());
        return s.toString();
    }
    
}

/**
 * Vertex is a mutable type representing a labeled vertex in directed weighted graph.
 * Each instance of vertex has an associated immutable label. Only a single edge can
 * exist between any pair of vertices, and each edge has exactly one corresponding weight.
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex {

    private final String label;
    private final Map<Vertex, Integer> targets = new HashMap<>();
    
    // Abstraction function:
    //   Represents a vertex with label = this.label and targets
    //   that constitute the edges.
    // Representation invariant:
    //   vertex label is a string that must not be null
    //   the value of the target entries are non-negative integers
    // Safety from rep exposure:
    //   All fields are private. this.label is immutable. this.targets are mutable, but these
    //   Maps are never directly passed in and on every return of data of type map, a defensive
    //   copy is made before the return.
    //
    
    // constructor
    /**
     * Create a labeled vertex with no edge attached to it.
     * @param label the label assigned to the vertex
     */
    public Vertex(String label) {
        this.label = label;
    }
    
    public void checkRep() {
        assert this.label != null;
        for (Map.Entry<Vertex, Integer> entry : targets.entrySet()) {
            assert entry.getValue() > 0;
            Vertex target = entry.getKey();
            assert target.getLabel() != null;
            assert getTargets().get(target).equals(entry.getValue());
        }
    }

    /**
     * @return the label associated with the vertex
     */
    public String getLabel() {
        return label;
    }

    /**
     * add, change, or remove edge from this to target vertex v
     *
     * @param v target vertex at the head of the edge
     * @param weight non-negative weight of the edge
     * @return the previous weight of the edge, or zero if there was no such edge
     * @throws IllegalArgumentException if weight is a negative integer
     */
    public int setTarget(Vertex v, Integer weight) {
        if (weight < 0) throw new IllegalArgumentException("Invalid weight");
        int previousWeight = 0;
        if (targets.containsKey(v)) {
            previousWeight = targets.get(v);
        }
        if (weight > 0) {
            targets.put(v, weight);
        } else {
            targets.remove(v);
        }
        checkRep();
        return previousWeight;
    }

    /**
     * Get all vertices with an edge from this
     * @return a map with keys containing set of all vertices that are head of an edge from
     * this, the values are the associated weight of that edge.
     */
    public Map<Vertex, Integer> getTargets() {
        final Map<Vertex, Integer> targets = new HashMap<>(this.targets);
        return targets;
    }

    /**
     * Returns a string representation of this vertex, e.g., (a, [(c, 2), (b, 1)])
     *
     * @return the label of this vertex, followed by the edges from this to targets
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        List<String> targets = new ArrayList<>();
        s.append("(" + getLabel() + ", ");
        for (Map.Entry<Vertex, Integer> target : getTargets().entrySet()) {
            String targetString = String.format("(%s, %s)", target.getKey().getLabel(), target.getValue().toString());
            targets.add(targetString);
        }
        s.append(targets.toString() + ")");
        return s.toString();
    }

}
