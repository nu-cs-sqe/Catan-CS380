package board;

import java.util.List;

public class Board {
    private final Shuffler shuffler;
    private List<Tile> tiles;

    public Board(Shuffler shuffler) {
        this.shuffler = shuffler;
    }

    public void create() {
        // TODO: implement random board creation
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}