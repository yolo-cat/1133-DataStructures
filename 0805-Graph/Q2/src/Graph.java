import java.awt.*;
import java.util.*;

public class Graph {
    private Map<String, Point> nodes = new HashMap<>();
    private java.util.List<Edge> edges = new ArrayList<>();

    public Map<String, Point> getNodes() {
        return nodes;
    }

    public java.util.List<Edge> getEdges() {
        return edges;
    }

    public void clear() {
        nodes.clear();
        edges.clear();
    }

    public void addNode(String name, Point p) {
        nodes.put(name, p);
    }

    public void removeNode(String name) {
        nodes.remove(name);
        edges.removeIf(edge -> edge.from.equals(name) || edge.to.equals(name));
    }

    public void addEdge(String from, String to) {
        Random rand = new Random();
        int cost = rand.nextInt(99) + 1; // 1~99
        Edge edge = new Edge(from, to, cost);
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }

    public boolean containsNode(String name) {
        return nodes.containsKey(name);
    }

    public Map<String, java.util.List<String>> getAdjacencyList() {
        Map<String, java.util.List<String>> graph = new HashMap<>();
        for (String name : nodes.keySet()) {
            graph.put(name, new ArrayList<>());
        }
        for (Edge edge : edges) {
            if (graph.containsKey(edge.from) && graph.containsKey(edge.to)) {
                graph.get(edge.from).add(edge.to);
                graph.get(edge.to).add(edge.from);
            }
        }
        return graph;
    }

    public java.util.List<String> dfs(String start) {
        Map<String, java.util.List<String>> graph = getAdjacencyList();
        java.util.List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsHelper(start, graph, visited, result);
        return result;
    }

    private void dfsHelper(String node, Map<String, java.util.List<String>> graph, Set<String> visited, java.util.List<String> result) {
        if (!visited.contains(node)) {
            visited.add(node);
            result.add(node);
            for (String neighbor : graph.get(node)) {
                dfsHelper(neighbor, graph, visited, result);
            }
        }
    }

    public java.util.List<String> bfs(String start) {
        Map<String, java.util.List<String>> graph = getAdjacencyList();
        java.util.List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            String node = queue.poll();
            result.add(node);
            for (String neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return result;
    }
}
