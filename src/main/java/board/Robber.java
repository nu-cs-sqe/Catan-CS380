package board;

import domain.Player;

public class Robber {
  private Tile tile;
  private Player player;

  public Tile getTile() {
    return (tile != null) ? new Tile(tile) : null;
  }

  public void setTile(Tile tile) {
    this.tile = (tile != null) ? new Tile(tile) : null;
  }

  public Player getPlayer() {
    return (player != null) ? new Player(player) : null;
  }

  public void setPlayer(Player player) {
    this.player = (player != null) ? new Player(player) : null;
  }
}
