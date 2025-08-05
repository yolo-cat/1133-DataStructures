import java.util.Objects;

public class Edge {
    public String from, to;
    public int cost;
    public Edge(String f, String t, int c) { from = f; to = t; cost = 1; } // cost 強制為 1
    public Edge(String f, String t) { this(f, t, 1); } // 默認 cost=1，兼容舊用法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
    }
    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
