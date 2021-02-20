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
public class ConcreteVerticesGraph<L> implements Graph<String> {
    
    private final List<Vertex<L>> vertices = new ArrayList<>();
    
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
        for (Vertex v : vertices) {
            // reference: https://www.runoob.com/java/java-hashmap-foreach.html
            v.getTargets().forEach((key, value) -> {
                assert vertices().contains(key);
            });
        }
    }
    
    @Override public boolean add(String vertex) {
        Vertex v = new Vertex(vertex);
        if (vertices().contains(vertex)) {
            return false;
        } else {
            vertices.add(v);
            return true;
        }
    }
    
    @Override public int set(String source, String target, int weight) {
        int previousWeight = 0;
        final int indexOfSource;
        if (weight != 0) {
            add(source);
            add(target);
            indexOfSource = indexOf(source);
            previousWeight = vertices.get(indexOfSource).setTarget(target, weight);
        } else {
            if (vertices().contains(source) && vertices().contains(target)) {
                indexOfSource = indexOf(source);
                previousWeight = vertices.get(indexOfSource).setTarget(target, weight);
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
            final int indexOfVertex = indexOf(vertex);
            Vertex<L> v = vertices.get(indexOfVertex);

            // remove edges from the vertex
            for (String t : targets.keySet()) {
                v.setTarget(t, 0);
            }
            // remove edges to the vertex
            for (String s : sources.keySet()) {
                vertices.get(indexOf(s)).setTarget(vertex, 0);
            }
            // remove the vertex from the graph
            vertices.remove(v);

            return true;
        }
    }
    
    @Override public Set<String> vertices() {
        Set<String> labels = new HashSet<>();
        vertices.stream().forEach(vertex -> labels.add(vertex.getLabel()));
        return labels;
    }
    
    @Override public Map<String, Integer> sources(String target) {
        Map<String, Integer> sources = new HashMap<>();
        vertices.stream().forEach(vertex -> {
            Map<String, Integer> targets = vertex.getTargets();
            if (targets.containsKey(target)) {
                sources.put(vertex.getLabel(), targets.get(target));
            }
        });
        return sources;
    }
    
    @Override public Map<String, Integer> targets(String source) {
        // streams of maps to maps: https://stackoverflow.com/questions/26752919/stream-of-maps-to-map
        return vertices.stream()
                .filter(vertex -> vertex.getLabel().equals(source))
                .map(vertex -> vertex.getTargets())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private int indexOf(String vertex) {
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).getLabel().equals(vertex)) {
                return i;
            }
        }
        return -1; // the vertex does not exist in this graph
    }

    /**
     * Returns a string representation of this vertices graph, e.g.,
     * ({a, b}, {(a, b, 1), (b, a, 2)})
     *
     * @return
     */
    @Override public String toString() {
        final Set<String> edgeSet = new HashSet<>();

        for (Vertex<L> vertex : vertices) {
            for (Map.Entry<String, Integer> entry :
                    vertex.getTargets().entrySet()) {

                final String edgeTuple = String.format("(%s, %s, %s)", vertex.getLabel(),
                        entry.getKey(), entry.getValue());

                edgeSet.add(edgeTuple);
            }
        }

        final String toStringEdgeSet = edgeSet.toString().replace('[', '{').replace(']', '}');
        final String toStringVertexSet = vertices().toString().replace('[', '{').replace(']', '}');
        final String toString = "(" + toStringVertexSet + ", " +
                toStringEdgeSet + ")";

        checkRep();
        return toString;
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
class Vertex<L> {

    private final String label;
    private final Map<String, Integer> targets = new HashMap<>();
    
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
        for (Map.Entry<String, Integer> entry : targets.entrySet()) {
            assert entry.getValue() > 0;
            String target = entry.getKey();
            assert target != null;
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
    public int setTarget(String v, Integer weight) {
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
    public Map<String, Integer> getTargets() {
        final Map<String, Integer> targets = new HashMap<>(this.targets);
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
        for (Map.Entry<String, Integer> target : getTargets().entrySet()) {
            String targetString = String.format("(%s, %s)", target.getKey(), target.getValue().toString());
            targets.add(targetString);
        }
        s.append(targets.toString() + ")");
        return s.toString();
    }

}
