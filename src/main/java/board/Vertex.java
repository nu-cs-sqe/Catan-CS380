package board;

import domain.Player;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
  private final String id;
  private final List<Tile> adjacentTiles;
  private Settlement settlement;
  private Player owner;
  private Harbor harbor;

  public Vertex(String id) {
    this.id = id;
    this.adjacentTiles = new ArrayList<>(0);
    this.settlement = null;
    this.owner = null;
  }

  public void addTile(Tile tile) {
    this.adjacentTiles.add(new Tile(tile));
  }

  public String getId() {
    return id;
  }

  public List<Tile> getAdjacentTiles() {
    return new ArrayList<>(adjacentTiles);
  }

  public Player getOwner() {
    return (owner != null) ? new Player(owner) : null;
  }

  public void setOwner(Player owner) {
    this.owner = (owner != null) ? new Player(owner) : null;
  }

  public Settlement getSettlement() {
    return settlement;
  }

  public void setSettlement(Settlement settlement) {
    this.settlement = settlement;
  }

  public Harbor getHarbor() {
    return harbor;
  }

  public void setHarbor(Harbor harbor) {
    this.harbor = harbor;
  }
}