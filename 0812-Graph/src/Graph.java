import java.awt.*;
import java.util.*;
import java.util.List;

public class Graph {
    private Map<String, Point> nodes = new HashMap<>();
    private List<Edge> edges = new ArrayList<>();

    public Map<String, Point> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
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

    public void addEdge(String from, String to, int cost) {
        Edge edge = new Edge(from, to, cost);
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }

    public boolean containsNode(String name) {
        return nodes.containsKey(name);
    }

    public int[][] getAdjacencyMatrix() {
        List<String> nodeNames = new ArrayList<>(nodes.keySet());
        Collections.sort(nodeNames); // 排序確保矩陣順序一致
        int n = nodeNames.size();
        int[][] matrix = new int[n][n];

        // 初始化矩陣
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = 0;
            }
        }

        // 填入邊的權重
        for (Edge edge : edges) {
            int fromIndex = nodeNames.indexOf(edge.from);
            int toIndex = nodeNames.indexOf(edge.to);
            if (fromIndex >= 0 && toIndex >= 0) {
                matrix[fromIndex][toIndex] = edge.cost;
                matrix[toIndex][fromIndex] = edge.cost; // 無向圖
            }
        }

        return matrix;
    }

    public String getAdjacencyMatrixString() {
        if (nodes.isEmpty()) return "No nodes in graph";

        List<String> nodeNames = new ArrayList<>(nodes.keySet());
        Collections.sort(nodeNames);
        int[][] matrix = getAdjacencyMatrix();

        StringBuilder sb = new StringBuilder();

        // 標題行
        sb.append("    ");
        for (String name : nodeNames) {
            sb.append(String.format("%4s", name));
        }
        sb.append("\n");

        // 矩陣行
        for (int i = 0; i < nodeNames.size(); i++) {
            sb.append(String.format("%4s", nodeNames.get(i)));
            for (int j = 0; j < nodeNames.size(); j++) {
                sb.append(String.format("%4d", matrix[i][j]));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public Map<String, List<String>> getAdjacencyList() {
        Map<String, List<String>> graph = new HashMap<>();
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

    public List<String> dfs(String start) {
        Map<String, List<String>> graph = getAdjacencyList();
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsHelper(start, graph, visited, result);
        return result;
    }

    private void dfsHelper(String node, Map<String, List<String>> graph, Set<String> visited, List<String> result) {
        if (!visited.contains(node)) {
            visited.add(node);
            result.add(node);
            for (String neighbor : graph.get(node)) {
                dfsHelper(neighbor, graph, visited, result);
            }
        }
    }

    public List<String> bfs(String start) {
        Map<String, List<String>> graph = getAdjacencyList();
        List<String> result = new ArrayList<>();
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

    // Floyd-Warshall Algorithm: 回傳所有點對點最短距離矩陣
    public int[][] getFloydWarshallMatrix() {
        List<String> nodeNames = new ArrayList<>(nodes.keySet());
        Collections.sort(nodeNames);
        int n = nodeNames.size();
        int[][] dist = new int[n][n];
        final int INF = 1000000000;
        // 初始化
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) dist[i][j] = 0;
                else dist[i][j] = INF;
            }
        }
        for (Edge edge : edges) {
            int u = nodeNames.indexOf(edge.from);
            int v = nodeNames.indexOf(edge.to);
            if (u >= 0 && v >= 0) {
                dist[u][v] = Math.min(dist[u][v], edge.cost);
                dist[v][u] = Math.min(dist[v][u], edge.cost); // 無向圖
            }
        }
        // Floyd-Warshall 主迴圈
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < INF && dist[k][j] < INF) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }
        return dist;
    }

    // 輔助：回傳 Floyd-Warshall 結果字串
    public String getFloydWarshallMatrixString() {
        if (nodes.isEmpty()) return "No nodes in graph";
        List<String> nodeNames = new ArrayList<>(nodes.keySet());
        Collections.sort(nodeNames);
        int[][] dist = getFloydWarshallMatrix();
        StringBuilder sb = new StringBuilder();

        // 標題
        sb.append("Floyd-Warshall 最短路徑矩陣\n");
        sb.append("========================================\n");
        sb.append("節點數量: ").append(nodeNames.size()).append("\n");
        sb.append("========================================\n\n");

        // 條列式顯示所有頂點對的最短距離
        int pairCount = 0;
        for (int i = 0; i < nodeNames.size(); i++) {
            for (int j = 0; j < nodeNames.size(); j++) {
                if (i != j) { // 排除自己到自己的距離
                    pairCount++;
                    String from = nodeNames.get(i);
                    String to = nodeNames.get(j);
                    if (dist[i][j] >= 1000000000) {
                        sb.append(String.format("%s -> %s：無法到達\n", from, to));
                    } else {
                        sb.append(String.format("%s -> %s：%d\n", from, to, dist[i][j]));
                    }
                }
            }
        }

        sb.append("\n========================================");
        sb.append(String.format("\n共有 %d 個頂點對的最短路徑", pairCount));
        sb.append("\n說明: 列表顯示所有頂點對之間的最短距離");
        sb.append("\n      「無法到達」表示兩點間沒有連通路徑");

        return sb.toString();
    }

    // 輔助：回傳 Floyd-Warshall 結果字串（僅矩陣，不含標題）
    public String getFloydWarshallMatrixOnlyString() {
        if (nodes.isEmpty()) return "No nodes in graph";
        List<String> nodeNames = new ArrayList<>(nodes.keySet());
        Collections.sort(nodeNames);
        int[][] dist = getFloydWarshallMatrix();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodeNames.size(); i++) {
            for (int j = 0; j < nodeNames.size(); j++) {
                if (dist[i][j] >= 1000000000)
                    sb.append("INF ");
                else
                    sb.append(String.format("%3d ", dist[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
