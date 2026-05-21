package board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vertex {
    private final String key;
    private final List<Tile> adjacentTiles;

    public Vertex(List<Tile> adjacentTiles) {
        this.adjacentTiles = adjacentTiles;
        this.key = buildKey(adjacentTiles);
    }

    public String getKey() {
        return key;
    }

    public List<Tile> getAdjacentTiles() {
        return adjacentTiles;
    }

    private static String buildKey(List<Tile> tiles) {
        List<String> coords = new ArrayList<>();
        for (Tile t : tiles) {
            coords.add(t.getQ() + "," + t.getR());
        }
        Collections.sort(coords);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coords.size(); i++) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append(coords.get(i));
        }
        return sb.toString();
    }
}