package board;

public class Tile {
  private final TileType tileType;
  private final int q;
  private final int r;
  private int numberToken;
  private boolean hasRobber;

  public Tile(TileType tileType, int q, int r) {
    this.tileType = tileType;
    this.q = q;
    this.r = r;
    this.numberToken = 0;
  }

  public TileType getTileType() {
    return tileType;
  }

  public int getQ() {
    return q;
  }

  public int getR() {
    return r;
  }

  public int getNumberToken() {
    return numberToken;
  }

  public void setNumberToken(int numberToken) {
    this.numberToken = numberToken;
  }
}
