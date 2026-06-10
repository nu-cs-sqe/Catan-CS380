package board;

public class Tile {
  private final TileType tileType;
  private final int q;
  private final int r;
  private int numberToken;

  public Tile(TileType tileType, int q, int r) {
    this.tileType = tileType;
    this.q = q;
    this.r = r;
    this.numberToken = 0;
  }

  public Tile(Tile other) {
    this.tileType = other.tileType;
    this.q = other.q;
    this.r = other.r;
    this.numberToken = other.numberToken;
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
