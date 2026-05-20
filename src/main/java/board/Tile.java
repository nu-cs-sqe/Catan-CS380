package board;

public class Tile {
    private final TileType tileType;
    private int numberToken;
    private boolean hasRobber;

    public Tile(TileType tileType) {
        this.tileType = tileType;
        this.numberToken = 0;
        this.hasRobber = false;
    }

    public TileType getTileType() {
        return tileType;
    }

    public int getNumberToken() {
        return numberToken;
    }

    public void setNumberToken(int numberToken) {
        this.numberToken = numberToken;
    }

    public boolean hasRobber() {
        return hasRobber;
    }

    public void setHasRobber(boolean hasRobber) {
        this.hasRobber = hasRobber;
    }
}