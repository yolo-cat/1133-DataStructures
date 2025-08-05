import java.util.Objects;

public class Edge {
    public String from, to;
    public Edge(String f, String t) { from = f; to = t; }
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

