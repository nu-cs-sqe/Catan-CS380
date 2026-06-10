package board;

import domain.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Edge {
  private final String id;
  private final List<Tile> adjacentTiles;
  private Player owner;

  public Edge(String id) {
    this.id = id;
    this.adjacentTiles = new ArrayList<Tile>();
    this.owner = null;
  }

  public void addTile(Tile tile) {
    this.adjacentTiles.add(tile);
  }

  public String getId() {
    return id;
  }

  public List<Tile> getAdjacentTiles() {
    return Collections.unmodifiableList(adjacentTiles);
  }

  public Player getOwner() {
    return owner;
  }

  public void setOwner(Player owner) {
    this.owner = owner;
  }
}
