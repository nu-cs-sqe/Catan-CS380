package view;

import board.Board;
import board.Edge;
import board.Tile;
import board.TileType;
import board.Vertex;
import domain.Player;
import domain.PlayerColor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class BoardView extends Pane {

  public enum SelectionMode { NONE, VERTEX, EDGE }

  private static final double SCALE_X = 50.0;
  private static final double SCALE_Y = 30.0;
  private static final double CENTER_X = 290.0;
  private static final double CENTER_Y = 280.0;
  private static final double VERTEX_RADIUS = 8.0;
  private static final double VERTEX_ACTIVE_RADIUS = 13.0;
  private static final double ROAD_STROKE = 4.0;
  private static final double ROAD_ACTIVE_STROKE = 9.0;
  private static final double UNOWNED_STROKE = 1.0;
  private static final double PREF_WIDTH = 600.0;
  private static final double PREF_HEIGHT = 580.0;
  private static final double TOKEN_LABEL_OFFSET_X = 6.0;
  private static final double TOKEN_LABEL_OFFSET_Y = 5.0;
  private static final int HIGH_PROB_TOKEN_A = 6;
  private static final int HIGH_PROB_TOKEN_B = 8;
  private static final int HEX_Q_SCALE = 2;
  private static final int HEX_R_SCALE = -3;
  private static final int[][] CORNER_OFFSETS = {
    {1, 1}, {0, 2}, {-1, 1},
    {-1, -1}, {0, -2}, {1, -1}
  };

  private SelectionMode mode = SelectionMode.NONE;
  private Consumer<Vertex> vertexClickHandler;
  private Consumer<Edge> edgeClickHandler;

  public BoardView() {
    setPrefSize(PREF_WIDTH, PREF_HEIGHT);
    setStyle("-fx-background-color: #b0d4f1;");
  }

  public void setSelectionMode(SelectionMode newMode) {
    this.mode = newMode;
  }

  public void setOnVertexClick(Consumer<Vertex> handler) {
    this.vertexClickHandler = handler;
  }

  public void setOnEdgeClick(Consumer<Edge> handler) {
    this.edgeClickHandler = handler;
  }

  public void refresh(Board board) {
    getChildren().clear();
    drawTiles(board);
    drawEdges(board);
    drawVertices(board);
  }

  private void drawTiles(Board board) {
    for (Tile tile : board.getTiles()) {
      drawTile(tile);
    }
  }

  private void drawTile(Tile tile) {
    double cx = tileCenterX(tile);
    double cy = tileCenterY(tile);
    Polygon hex = buildHexPolygon(cx, cy);
    hex.setFill(tileColor(tile.getTileType()));
    hex.setStroke(Color.SADDLEBROWN);
    getChildren().add(hex);
    if (tile.getNumberToken() != 0) {
      addTokenLabel(tile.getNumberToken(), cx, cy);
    }
  }

  private Polygon buildHexPolygon(double cx, double cy) {
    Polygon hex = new Polygon();
    for (int[] offset : CORNER_OFFSETS) {
      hex.getPoints().add(cx + offset[0] * SCALE_X);
      hex.getPoints().add(cy + offset[1] * SCALE_Y);
    }
    return hex;
  }

  private void addTokenLabel(int token, double cx, double cy) {
    Text text = new Text(cx - TOKEN_LABEL_OFFSET_X, cy + TOKEN_LABEL_OFFSET_Y, String.valueOf(token));
    boolean highProbability = token == HIGH_PROB_TOKEN_A || token == HIGH_PROB_TOKEN_B;
    text.setFill(highProbability ? Color.RED : Color.BLACK);
    text.setStyle("-fx-font-weight: bold;");
    getChildren().add(text);
  }

  private void drawEdges(Board board) {
    for (Edge edge : board.getEdges()) {
      drawEdge(edge);
    }
  }

  private void drawEdge(Edge edge) {
    double[][] pts = edgePixelCoords(edge.getId());
    Line line = new Line(pts[0][0], pts[0][1], pts[1][0], pts[1][1]);
    Player owner = edge.getOwner();
    if (owner != null) {
      line.setStroke(playerColor(owner.getColor()));
      line.setStrokeWidth(ROAD_STROKE);
    } else {
      line.setStroke(Color.TRANSPARENT);
      line.setStrokeWidth(ROAD_ACTIVE_STROKE);
    }
    final Edge edgeRef = edge;
    line.setOnMouseClicked(e -> onEdgeClicked(edgeRef));
    getChildren().add(line);
  }

  private void drawVertices(Board board) {
    for (Vertex vertex : board.getVertices()) {
      drawVertex(vertex);
    }
  }

  private void drawVertex(Vertex vertex) {
    double[] px = vertexPixelCoords(vertex.getId());
    boolean active = mode == SelectionMode.VERTEX && vertex.getSettlement() == null;
    double radius = active ? VERTEX_ACTIVE_RADIUS : VERTEX_RADIUS;
    Circle circle = new Circle(px[0], px[1], radius);
    applyVertexStyle(circle, vertex, active);
    final Vertex vRef = vertex;
    circle.setOnMouseClicked(e -> onVertexClicked(vRef));
    getChildren().add(circle);
  }

  private void applyVertexStyle(Circle circle, Vertex vertex, boolean active) {
    Player owner = vertex.getOwner();
    if (owner != null) {
      circle.setFill(playerColor(owner.getColor()));
      circle.setStroke(Color.BLACK);
      circle.setStrokeWidth(UNOWNED_STROKE);
    } else if (active) {
      circle.setFill(Color.WHITE);
      circle.setStroke(Color.DARKGREEN);
      circle.setStrokeWidth(2.0);
    } else {
      circle.setFill(Color.TRANSPARENT);
      circle.setStroke(Color.TRANSPARENT);
    }
  }

  private void onVertexClicked(Vertex vertex) {
    if (mode == SelectionMode.VERTEX && vertexClickHandler != null) {
      vertexClickHandler.accept(vertex);
    }
  }

  private void onEdgeClicked(Edge edge) {
    if (mode == SelectionMode.EDGE && edgeClickHandler != null) {
      edgeClickHandler.accept(edge);
    }
  }

  private double tileCenterX(Tile tile) {
    return (HEX_Q_SCALE * tile.getQ() + tile.getR()) * SCALE_X + CENTER_X;
  }

  private double tileCenterY(Tile tile) {
    return (HEX_R_SCALE * tile.getR()) * SCALE_Y + CENTER_Y;
  }

  private double[] vertexPixelCoords(String vertexId) {
    String[] parts = vertexId.split(",");
    double vx = Double.parseDouble(parts[0]);
    double vy = Double.parseDouble(parts[1]);
    return new double[]{vx * SCALE_X + CENTER_X, vy * SCALE_Y + CENTER_Y};
  }

  private double[][] edgePixelCoords(String edgeId) {
    String[] endpoints = edgeId.split("\\|");
    double[] p1 = parsePoint(endpoints[0]);
    double[] p2 = parsePoint(endpoints[1]);
    return new double[][]{p1, p2};
  }

  private double[] parsePoint(String coord) {
    String[] parts = coord.split(",");
    double x = Double.parseDouble(parts[0]) * SCALE_X + CENTER_X;
    double y = Double.parseDouble(parts[1]) * SCALE_Y + CENTER_Y;
    return new double[]{x, y};
  }

  private static Color tileColor(TileType type) {
    switch (type) {
      case FOREST: return Color.web("#228B22");
      case PASTURE: return Color.web("#90EE90");
      case FIELDS: return Color.web("#FFD700");
      case HILLS: return Color.web("#CD853F");
      case MOUNTAINS: return Color.web("#808080");
      case DESERT: return Color.web("#F5DEB3");
      default: return Color.LIGHTGRAY;
    }
  }

  static Color playerColor(PlayerColor color) {
    switch (color) {
      case RED: return Color.CRIMSON;
      case BLUE: return Color.DODGERBLUE;
      case WHITE: return Color.LIGHTYELLOW;
      case ORANGE: return Color.DARKORANGE;
      default: return Color.GRAY;
    }
  }
}
