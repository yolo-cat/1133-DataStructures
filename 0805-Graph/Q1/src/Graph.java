import java.awt.*;
import java.util.*;
import java.util.List;

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

    // Kruskal's algorithm for MST
    public List<Edge> getMST() {
        List<Edge> result = new ArrayList<>();
        Map<String, String> parent = new HashMap<>();
        for (String node : nodes.keySet()) parent.put(node, node);
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(e -> e.cost));
        int count = 0;
        for (Edge edge : sortedEdges) {
            String root1 = find(parent, edge.from);
            String root2 = find(parent, edge.to);
            if (!root1.equals(root2)) {
                result.add(edge);
                parent.put(root1, root2);
                count++;
                if (count == nodes.size() - 1) break;
            }
        }
        return result;
    }

    private String find(Map<String, String> parent, String node) {
        if (!parent.get(node).equals(node)) {
            parent.put(node, find(parent, parent.get(node)));
        }
        return parent.get(node);
    }

    // Dijkstra's algorithm for shortest path
    public List<Edge> getShortestPath(String from, String to) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        for (String node : nodes.keySet()) dist.put(node, Integer.MAX_VALUE);
        dist.put(from, 0);
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(from);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            for (Edge edge : edges) {
                String v = null;
                if (edge.from.equals(u)) v = edge.to;
                else if (edge.to.equals(u)) v = edge.from;
                if (v != null) {
                    int alt = dist.get(u) + edge.cost;
                    if (alt < dist.get(v)) {
                        dist.put(v, alt);
                        prev.put(v, u);
                        pq.add(v);
                    }
                }
            }
        }

        // 回溯路徑
        List<Edge> path = new ArrayList<>();
        String curr = to;
        while (prev.containsKey(curr)) {
            String p = prev.get(curr);
            // 找到對應的邊
            for (Edge edge : edges) {
                if ((edge.from.equals(curr) && edge.to.equals(p)) ||
                    (edge.from.equals(p) && edge.to.equals(curr))) {
                    path.add(0, edge);
                    break;
                }
            }
            curr = p;
        }
        if (!from.equals(to) && path.isEmpty()) return null; // 無路徑
        return path;
    }
}
