package maze_escape;

import java.util.*;
import java.util.function.BiFunction;

public abstract class AbstractGraph<V> {

    /** Graph representation:
     *  this class implements graph search algorithms on a graph with abstract vertex type V
     *  for every vertex in the graph, its neighbours can be found by use of abstract method getNeighbours(fromVertex)
     *  this abstraction can be used for both directed and undirected graphs
     **/

    public AbstractGraph() { }

    /**
     * retrieves all neighbours of the given fromVertex
     * if the graph is directed, the implementation of this method shall follow the outgoing edges of fromVertex
     * @param fromVertex
     * @return
     */
    public abstract Set<V> getNeighbours(V fromVertex);

    /**
     * retrieves all vertices that can be reached directly or indirectly from the given firstVertex
     * if the graph is directed, only outgoing edges shall be traversed
     * firstVertex shall be included in the result as well
     * if the graph is connected, all vertices shall be found
     * @param firstVertex   the start vertex for the retrieval
     * @return
     */
    public Set<V> getAllVertices(V firstVertex) {
        Set<V> visited = new HashSet<>();
        Stack<V> stack = new Stack<>();

        stack.push(firstVertex);
        while (!stack.isEmpty()) {
            V current = stack.pop();
            visited.add(current);

            for (V neighbour : getNeighbours(current)) {
                if (!visited.contains(neighbour)) {
                    stack.push(neighbour);
                }
            }
        }

        return visited;
    }

    /**
     * Formats the adjacency list of the subgraph starting at the given firstVertex
     * according to the format:
     *  	vertex1: [neighbour11,neighbour12,…]
     *  	vertex2: [neighbour21,neighbour22,…]
     *  	…
     * Uses a pre-order traversal of a spanning tree of the sub-graph starting with firstVertex as the root
     * if the graph is directed, only outgoing edges shall be traversed
     * , and using the getNeighbours() method to retrieve the roots of the child subtrees.
     * @param firstVertex
     * @return
     */
    public String formatAdjacencyList(V firstVertex) {
        StringBuilder stringBuilder = new StringBuilder("Graph adjacency list:\n");
        formatAdjacencyListHelper(firstVertex, stringBuilder);

        return stringBuilder.toString();
    }

    private void formatAdjacencyListHelper(V firstVertex, StringBuilder stringBuilder) {
        Queue<V> queue = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        queue.offer(firstVertex);

        while (!queue.isEmpty()) {
            V vertex = queue.poll();
            if (!visited.contains(vertex)) {
                visited.add(vertex);

                String neighbours = getNeighbours(vertex).toString().replaceAll("\\s+", "");

                stringBuilder.append(vertex).append(": ");
                stringBuilder.append(neighbours).append("\n");

                getNeighbours(vertex).forEach(queue::offer);
            }
        }
    }


    /**
     * represents a directed path of connected vertices in the graph
     */
    public class GPath {
        private Deque<V> vertices = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. vertices contains a sequence of vertices that are neighbours in the graph,
         *    i.e. FOR ALL i: 1 < i < vertices.length: getNeighbours(vertices[i-1]).contains(vertices[i])
         * 2. a path with one vertex equal start and target vertex
         * 3. a path without vertices is empty, does not have a start nor a target
         * totalWeight is a helper attribute to capture total path length from a function on two neighbouring vertices
         * visited is a helper set to be able to track visited vertices in searches, only for analysis purposes
         **/
        private static final int DISPLAY_CUT = 10;
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%.2f Length=%d visited=%d (",
                            this.totalWeight, this.vertices.size(), this.visited.size()));
            String separator = "";
            int count = 0;
            final int tailCut = this.vertices.size()-1 - DISPLAY_CUT;
            for (V v : this.vertices) {
                // limit the length of the text representation for long paths.
                if (count < DISPLAY_CUT || count > tailCut) {
                    sb.append(separator + v.toString());
                    separator = ", ";
                } else if (count == DISPLAY_CUT) {
                    sb.append(separator + "...");
                }
                count++;
            }
            sb.append(")");
            return sb.toString();
        }

        /**
         * recalculates the total weight of the path from a given weightMapper that calculates the weight of
         * the path segment between two neighbouring vertices.
         * @param weightMapper
         */
        public void reCalculateTotalWeight(BiFunction<V,V,Double> weightMapper) {
            this.totalWeight = 0.0;
            V previous = null;
            for (V v: this.vertices) {
                // the first vertex of the iterator has no predecessor and hence no weight contribution
                if (previous != null) this.totalWeight += weightMapper.apply(previous, v);
                previous = v;
            }
        }

        public Queue<V> getVertices() {
            return this.vertices;
        }

        public double getTotalWeight() {
            return this.totalWeight;
        }

        public Set<V> getVisited() { return this.visited; }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     * @param startVertex
     * @param targetVertex
     * @return  the path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath depthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null) return null;

        List<V> path = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        Stack<V> stack = new Stack<>();

        stack.push(startVertex);

        while (!stack.isEmpty()) {
            V current = stack.pop();
            visited.add(current);
            path.add(current);

            if (current.equals(targetVertex)) {
                GPath gPath = new GPath();
                gPath.vertices.addAll(path);
                gPath.visited.addAll(visited);
                return gPath;
            }

            for (V neighbour : getNeighbours(current)) {
                if (!visited.contains(neighbour)) {
                    stack.push(neighbour);
                }
            }
        }
        return null;
    }


    /**
     * Uses a breadth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     * @param startVertex
     * @param targetVertex
     * @return  the path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath breadthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null) return null;

        Map<V, V> parentMap = new HashMap<>();
        Queue<V> queue = new LinkedList<>();
        Set<V> visited = new HashSet<>();

        queue.offer(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            V current = queue.poll();

            if (current.equals(targetVertex)) {
                GPath gPath = new GPath();
                gPath.vertices.add(current);

                while (parentMap.containsKey(current)) {
                    current = parentMap.get(current);
                    gPath.vertices.addFirst(current);
                }

                gPath.visited.addAll(visited);
                return gPath;
            }

            for (V neighbour : getNeighbours(current)) {
                if (!visited.contains(neighbour)) {
                    parentMap.put(neighbour, current);
                    queue.offer(neighbour);
                    visited.add(neighbour);
                }
            }
        }
        return null;
    }

    // helper class to build the spanning tree of visited vertices in dijkstra's shortest path algorithm
    // your may change this class or delete it altogether follow a different approach in your implementation
    private class MSTNode implements Comparable<MSTNode> {
        protected V vertex;                // the graph vertex that is concerned with this MSTNode
        protected V parentVertex = null;     // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false;  // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path towards this node's vertex

        private MSTNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, sofar
        @Override
        public int compareTo(MSTNode otherMSTNode) {
            return Double.compare(weightSumTo, otherMSTNode.weightSumTo);
        }
    }

    private GPath checkForTarget(MSTNode nearestMSTNode, V targetVertex, GPath path, Map<V, MSTNode> minimumSpanningTree) {
        if (nearestMSTNode.vertex == targetVertex) {
            path.totalWeight = nearestMSTNode.weightSumTo;

            while (nearestMSTNode.vertex != null) {
                path.vertices.addFirst(nearestMSTNode.vertex);
                nearestMSTNode.vertex = minimumSpanningTree.get(nearestMSTNode.vertex).parentVertex;
            }
            return path;
        }
        return null;
    }

    /**
     * Calculates the edge-weighted shortest path from the startVertex to targetVertex in the subgraph
     * according to Dijkstra's algorithm of a minimum spanning tree
     * @param startVertex
     * @param targetVertex
     * @param weightMapper   provides a function(v1,v2) by which the weight of an edge from v1 to v2
     *                       can be retrieved or calculated
     * @return  the shortest path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath dijkstraShortestPath(V startVertex, V targetVertex,
                                      BiFunction<V, V, Double> weightMapper) {

        if (startVertex == null || targetVertex == null) return null;

        // initialise the result path of the search
        GPath path = new GPath();
        path.visited.add(startVertex);

        // easy target
        if (startVertex.equals(targetVertex)) {
            path.vertices.add(startVertex);
            return path;
        }

        Map<V, MSTNode> minimumSpanningTree = new HashMap<>();

        // initialise the minimum spanning tree with the startVertex
        MSTNode nearestMSTNode = new MSTNode(startVertex);
        nearestMSTNode.weightSumTo = 0.0;
        minimumSpanningTree.put(startVertex, nearestMSTNode);

        while (nearestMSTNode != null) {
            nearestMSTNode.marked = true;

            for (V neighbour : getNeighbours(nearestMSTNode.vertex)) {

                MSTNode current = new MSTNode(neighbour);
                double weight = weightMapper.apply(nearestMSTNode.vertex, neighbour);

                current.parentVertex = nearestMSTNode.vertex;
                current.weightSumTo = nearestMSTNode.weightSumTo + weight;
                double currentWeight = current.weightSumTo;

                if (!minimumSpanningTree.containsKey(neighbour) ||
                            currentWeight < minimumSpanningTree.get(neighbour).weightSumTo) {
                    minimumSpanningTree.put(neighbour, current);
                }

                path.visited.add(neighbour);
            }

            GPath targetPath = checkForTarget(nearestMSTNode, targetVertex, path, minimumSpanningTree);
            if (targetPath != null) return targetPath;

            nearestMSTNode = minimumSpanningTree.values().stream().filter(n -> n.marked == false)
                    .min(MSTNode::compareTo).orElse(null);
        }

        return null;
    }
}
