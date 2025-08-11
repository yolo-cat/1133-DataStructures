// Main.java
    import java.util.*;

    public class Main {
        public static void main(String[] args) {
            int V = 5;
            BruteForceArticulationPoints brute = new BruteForceArticulationPoints(V);
            TarjanArticulationPoints tarjan = new TarjanArticulationPoints(V);

            // 建立圖
            brute.addEdge(0, 1);
            brute.addEdge(0, 2);
            brute.addEdge(1, 2);
            brute.addEdge(1, 3);
            brute.addEdge(3, 4);

            tarjan.addEdge(0, 1);
            tarjan.addEdge(0, 2);
            tarjan.addEdge(1, 2);
            tarjan.addEdge(1, 3);
            tarjan.addEdge(3, 4);

            // 測試暴力法
            System.out.println("Brute Force Articulation Points: " + brute.findArticulationPoints());

            // 測試 Tarjan
            System.out.println("Tarjan Articulation Points: " + tarjan.findArticulationPoints());

            // 效能比較
            for (int v = 1; v <= 100; v++) {
                int maxEdge = v * (v - 1) / 2;
                int[] edgeCases = {Math.max(v - 1, 0), v, maxEdge};
                for (int e : edgeCases) {
                    if (e > maxEdge) continue; // 避免超過最大邊數
                    runBenchmark(v, e);
                }
            }
        }

        // 效能測試與比較
        public static void runBenchmark(int V, int E) {
            long bruteTotal = 0, tarjanTotal = 0;
            for (int t = 0; t < 10; t++) {
                Random rand = new Random();
                BruteForceArticulationPoints brute = new BruteForceArticulationPoints(V);
                TarjanArticulationPoints tarjan = new TarjanArticulationPoints(V);
                Set<String> edgeSet = new HashSet<>();
                // 先產生一棵連通生成樹（v-1條邊）
                for (int i = 1; i < V; i++) {
                    int u = i;
                    int v = rand.nextInt(i); // 連接到前面任一頂點
                    String key = u < v ? u + "," + v : v + "," + u;
                    brute.addEdge(u, v);
                    tarjan.addEdge(u, v);
                    edgeSet.add(key);
                }
                // 若 E > V-1，隨機補邊
                while (edgeSet.size() < E) {
                    int u = rand.nextInt(V);
                    int v = rand.nextInt(V);
                    if (u != v) {
                        String key = u < v ? u + "," + v : v + "," + u;
                        if (!edgeSet.contains(key)) {
                            brute.addEdge(u, v);
                            tarjan.addEdge(u, v);
                            edgeSet.add(key);
                        }
                    }
                }
                long t1 = System.nanoTime();
                brute.findArticulationPoints();
                long t2 = System.nanoTime();
                tarjan.findArticulationPoints();
                long t3 = System.nanoTime();
                bruteTotal += (t2-t1);
                tarjanTotal += (t3-t2);
            }
            long bruteAvg = bruteTotal / 10;
            long tarjanAvg = tarjanTotal / 10;
            System.out.printf("V=%d, E=%d | Brute(avg): %d ns | Tarjan(avg): %d ns\n",
                    V, E, bruteAvg, tarjanAvg);
        }
    }

    // 暴力法
    class BruteForceArticulationPoints {
        private int V;
        private List<List<Integer>> adj;

        public BruteForceArticulationPoints(int V) {
            this.V = V;
            adj = new ArrayList<>();
            for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        }

        public void addEdge(int u, int v) {
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        private void dfs(int u, boolean[] visited, int skip) {
            if (u == skip) return;
            visited[u] = true;
            for (int v : adj.get(u)) {
                if (!visited[v] && v != skip) dfs(v, visited, skip);
            }
        }

        public Set<Integer> findArticulationPoints() {
            if (V <= 1) return new HashSet<>(); // 單一頂點無關節點
            Set<Integer> result = new HashSet<>();
            for (int i = 0; i < V; i++) {
                boolean[] visited = new boolean[V];
                int start = (i == 0) ? 1 : 0;
                dfs(start, visited, i);
                for (int j = 0; j < V; j++) {
                    if (j != i && !visited[j]) {
                        result.add(i);
                        break;
                    }
                }
            }
            return result;
        }
    }

    // Tarjan 演算法
    class TarjanArticulationPoints {
        private int V, time;
        private List<List<Integer>> adj;
        private boolean[] visited, isAP;
        private int[] disc, low, parent;

        public TarjanArticulationPoints(int V) {
            this.V = V;
            adj = new ArrayList<>();
            for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        }

        public void addEdge(int u, int v) {
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        public Set<Integer> findArticulationPoints() {
            time = 0;
            visited = new boolean[V];
            disc = new int[V];
            low = new int[V];
            parent = new int[V];
            isAP = new boolean[V];
            Arrays.fill(parent, -1);

            for (int i = 0; i < V; i++) {
                if (!visited[i]) dfs(i);
            }

            Set<Integer> result = new HashSet<>();
            for (int i = 0; i < V; i++) if (isAP[i]) result.add(i);
            return result;
        }

        private void dfs(int u) {
            visited[u] = true;
            disc[u] = low[u] = ++time;
            int children = 0;
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    children++;
                    parent[v] = u;
                    dfs(v);
                    low[u] = Math.min(low[u], low[v]);
                    if (parent[u] == -1 && children > 1) isAP[u] = true;
                    if (parent[u] != -1 && low[v] >= disc[u]) isAP[u] = true;
                } else if (v != parent[u]) {
                    low[u] = Math.min(low[u], disc[v]);
                }
            }
        }
    }