package domain;

import board.Board;
import board.Edge;
import board.Vertex;
import java.util.HashSet;
import java.util.Set;

public final class LongestRoadCalculator {

    private LongestRoadCalculator() {
    }

    public static int calculateForPlayer(Board board,
                                         Player player) {
        int longest = 0;
        for (Edge edge : board.getEdges()) {
            if (player.equals(edge.getOwner())) {
                Set<Edge> visited = new HashSet<>();
                visited.add(edge);
                String[] verts = edge.getId().split("\\|");
                int len1 = dfs(board, player, verts[0], visited);
                int len2 = dfs(board, player, verts[1], visited);
                longest = Math.max(longest, len1 + len2 + 1);
            }
        }
        return longest;
    }

    private static int dfs(Board board, Player player,
                           String vertexKey, Set<Edge> visited) {
        int max = 0;
        Vertex vertex = board.getVertex(vertexKey);
        if (vertex == null || isBlockedByOpponent(vertex, player)) {
            return 0;
        }
        for (Edge neighbor : getConnectedEdges(board, vertexKey)) {
            if (!visited.contains(neighbor)
                    && player.equals(neighbor.getOwner())) {
                visited.add(neighbor);
                String other = getOtherVertex(neighbor, vertexKey);
                int len = 1 + dfs(board, player, other, visited);
                max = Math.max(max, len);
                visited.remove(neighbor);
            }
        }
        return max;
    }

    private static boolean isBlockedByOpponent(Vertex vertex,
                                               Player player) {
        return vertex.getOwner() != null
                && !player.equals(vertex.getOwner());
    }

    private static Set<Edge> getConnectedEdges(Board board,
                                               String vertexKey) {
        Set<Edge> connected = new HashSet<>();
        for (Edge edge : board.getEdges()) {
            String[] verts = edge.getId().split("\\|");
            if (verts[0].equals(vertexKey)
                    || verts[1].equals(vertexKey)) {
                connected.add(edge);
            }
        }
        return connected;
    }

    private static String getOtherVertex(Edge edge,
                                         String vertexKey) {
        String[] verts = edge.getId().split("\\|");
        if (verts[0].equals(vertexKey)) {
            return verts[1];
        }
        return verts[0];
    }
}