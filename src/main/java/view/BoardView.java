package view;

import board.Board;
import board.Edge;
import board.Harbor;
import board.Tile;
import board.TileType;
import board.Vertex;
import domain.Player;
import domain.PlayerColor;
import domain.Resource;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class BoardView extends Pane {

  public enum SelectionMode {
    NONE,
    VERTEX,
    EDGE
  }

  private static final double SCALE_X = 50.0;
  private static final double SCALE_Y = 30.0;
  private static final double CENTER_X = 270.0;
  private static final double CENTER_Y = 280.0;
  private static final double VERTEX_RADIUS = 8.0;
  private static final double VERTEX_ACTIVE_RADIUS = 13.0;
  private static final double ROAD_STROKE = 4.0;
  private static final double ROAD_ACTIVE_STROKE = 9.0;
  private static final double UNOWNED_STROKE = 1.0;
  private static final double PREF_WIDTH = 860.0;
  private static final double PREF_HEIGHT = 700.0;
  private static final double TOKEN_LABEL_OFFSET_X = 6.0;
  private static final double TOKEN_LABEL_OFFSET_Y = 5.0;
  private static final int HIGH_PROB_TOKEN_A = 6;
  private static final int HIGH_PROB_TOKEN_B = 8;
  private static final double VERTEX_ACTIVE_STROKE_WIDTH = 2.0;
  private static final double VERTEX_HOVER_STROKE_WIDTH = 3.0;
  private static final double HARBOR_STROKE = 5.0;
  private static final double HARBOR_BOAT_OFFSET = 45.0;
  private static final double HARBOR_BOAT_RADIUS = 6.0;
  private static final double HARBOR_TEXT_HALF_W = 8.0;
  private static final double HARBOR_TEXT_HALF_H = 4.0;
  private static final double HEX_IMG_WIDTH = 2 * SCALE_X;
  private static final double HEX_IMG_HEIGHT = 4 * SCALE_Y;
  private static final double TOKEN_CIRCLE_RADIUS = 12.0;
  private static final double ROTATE_CCW_90 = -90.0;
  private static final int HEX_Q_SCALE = 2;
  private static final int HEX_R_SCALE = -3;
  private static final int[][] CORNER_OFFSETS = {
    {1, 1}, {0, 2}, {-1, 1},
    {-1, -1}, {0, -2}, {1, -1}
  };

  private SelectionMode mode = SelectionMode.NONE;
  private Consumer<Vertex> vertexClickHandler;
  private Consumer<Edge> edgeClickHandler;
  private final Map<TileType, Image> tileImages = new EnumMap<>(TileType.class);

  public BoardView() {
    setPrefSize(PREF_WIDTH, PREF_HEIGHT);
    setMaxSize(PREF_WIDTH, PREF_HEIGHT);
    initBackground();
    loadTileImages();
  }

  private void initBackground() {
    URL url = getClass().getResource("/images/WATER.png");
    if (url != null) {
      Image img = new Image(url.toExternalForm());
      BackgroundSize size = new BackgroundSize(1.0, 1.0, true, true, false, false);
      BackgroundImage bgImg = new BackgroundImage(img,
          BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.DEFAULT, size);
      setBackground(new Background(bgImg));
    } else {
      setStyle("-fx-background-color: #b0d4f1;");
    }
  }

  private void loadTileImages() {
    for (TileType type : TileType.values()) {
      URL url = getClass().getResource("/images/" + type.name() + ".png");
      if (url != null) {
        tileImages.put(type, new Image(url.toExternalForm()));
      }
    }
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
    drawHarbors(board);
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
    hex.setFill(tileFill(tile.getTileType(), cx, cy));
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
    Circle bg = new Circle(cx, cy, TOKEN_CIRCLE_RADIUS);
    bg.setFill(Color.IVORY);
    bg.setStroke(Color.SADDLEBROWN);
    bg.setStrokeWidth(UNOWNED_STROKE);
    getChildren().add(bg);
    Text text =
        new Text(cx - TOKEN_LABEL_OFFSET_X, cy + TOKEN_LABEL_OFFSET_Y, String.valueOf(token));
    boolean highProbability = token == HIGH_PROB_TOKEN_A || token == HIGH_PROB_TOKEN_B;
    text.setFill(highProbability ? Color.RED : Color.BLACK);
    text.setStyle("-fx-font-weight: bold;");
    getChildren().add(text);
  }

  private static Image rotateCcw90(Image src) {
    double sw = src.getWidth();
    double sh = src.getHeight();
    Canvas canvas = new Canvas(sh, sw);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.translate(0, sw);
    gc.rotate(ROTATE_CCW_90);
    gc.drawImage(src, 0, 0);
    WritableImage out = new WritableImage((int) sh, (int) sw);
    canvas.snapshot(null, out);
    return out;
  }

  private void drawHarbors(Board board) {
    for (Harbor harbor : board.getHarbors()) {
      drawHarbor(harbor);
    }
  }

  private void drawHarbor(Harbor harbor) {
    double[] p1 = vertexPixelCoords(harbor.getVertex1Id());
    double[] p2 = vertexPixelCoords(harbor.getVertex2Id());
    double[] boat = harborBoatPoint(p1, p2);
    Line dock1 = new Line(p1[0], p1[1], boat[0], boat[1]);
    dock1.setStroke(Color.SADDLEBROWN);
    dock1.setStrokeWidth(HARBOR_STROKE);
    dock1.setStrokeLineCap(StrokeLineCap.ROUND);
    getChildren().add(dock1);
    Line dock2 = new Line(p2[0], p2[1], boat[0], boat[1]);
    dock2.setStroke(Color.SADDLEBROWN);
    dock2.setStrokeWidth(HARBOR_STROKE);
    dock2.setStrokeLineCap(StrokeLineCap.ROUND);
    getChildren().add(dock2);
    Circle boatDot = new Circle(boat[0], boat[1], HARBOR_BOAT_RADIUS);
    boatDot.setFill(harborColor(harbor.getHarborType()));
    boatDot.setStroke(Color.BLACK);
    boatDot.setStrokeWidth(UNOWNED_STROKE);
    getChildren().add(boatDot);
    Text label =
        new Text(
            boat[0] - HARBOR_TEXT_HALF_W,
            boat[1] + HARBOR_BOAT_RADIUS + HARBOR_TEXT_HALF_H,
            harborLabel(harbor));
    label.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;");
    getChildren().add(label);
  }

  private double[] harborBoatPoint(double[] p1, double[] p2) {
    double mx = (p1[0] + p2[0]) / 2;
    double my = (p1[1] + p2[1]) / 2;
    double dx = p2[0] - p1[0];
    double dy = p2[1] - p1[1];
    double len = Math.hypot(dx, dy);
    double perpX = -dy / len;
    double perpY = dx / len;
    if (perpX * (mx - CENTER_X) + perpY * (my - CENTER_Y) < 0) {
      perpX = -perpX;
      perpY = -perpY;
    }
    return new double[] {mx + perpX * HARBOR_BOAT_OFFSET, my + perpY * HARBOR_BOAT_OFFSET};
  }

  private static String harborLabel(Harbor harbor) {
    if (harbor.getHarborType() == Resource.GENERIC) {
      return "3:1";
    }
    return "2:1\n" + harbor.getHarborType().name();
  }

  private static Color harborColor(Resource resource) {
    switch (resource) {
      case WOOD:
        return Color.web("#228B22");
      case BRICK:
        return Color.FIREBRICK;
      case SHEEP:
        return Color.LAWNGREEN;
      case WHEAT:
        return Color.GOLDENROD;
      case ORE:
        return Color.SLATEGRAY;
      default:
        return Color.WHITE;
    }
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
    boolean active = mode == SelectionMode.EDGE && owner == null;
    if (owner != null) {
      line.setStroke(playerColor(owner.getColor()));
      line.setStrokeWidth(ROAD_STROKE);
    } else if (active) {
      line.setStroke(Color.WHITE);
      line.setStrokeWidth(ROAD_ACTIVE_STROKE);
      line.setStrokeLineCap(StrokeLineCap.SQUARE);
      line.setCursor(Cursor.HAND);
      line.setOnMouseEntered(e -> line.setStroke(Color.GOLD));
      line.setOnMouseExited(e -> line.setStroke(Color.WHITE));
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
      circle.setStrokeWidth(VERTEX_ACTIVE_STROKE_WIDTH);
      circle.setCursor(Cursor.HAND);
      circle.setOnMouseEntered(
          e -> {
            circle.setFill(Color.LIGHTGREEN);
            circle.setStrokeWidth(VERTEX_HOVER_STROKE_WIDTH);
          });
      circle.setOnMouseExited(
          e -> {
            circle.setFill(Color.WHITE);
            circle.setStrokeWidth(VERTEX_ACTIVE_STROKE_WIDTH);
          });
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
    return new double[] {vx * SCALE_X + CENTER_X, vy * SCALE_Y + CENTER_Y};
  }

  private double[][] edgePixelCoords(String edgeId) {
    String[] endpoints = edgeId.split("\\|");
    double[] p1 = parsePoint(endpoints[0]);
    double[] p2 = parsePoint(endpoints[1]);
    return new double[][] {p1, p2};
  }

  private double[] parsePoint(String coord) {
    String[] parts = coord.split(",");
    double x = Double.parseDouble(parts[0]) * SCALE_X + CENTER_X;
    double y = Double.parseDouble(parts[1]) * SCALE_Y + CENTER_Y;
    return new double[] {x, y};
  }

  private Paint tileFill(TileType type, double cx, double cy) {
    Image img = tileImages.get(type);
    if (img != null) {
      return new ImagePattern(img, cx - SCALE_X, cy - 2 * SCALE_Y, HEX_IMG_WIDTH, HEX_IMG_HEIGHT, false);
    }
    return tileColor(type);
  }

  private static Color tileColor(TileType type) {
    switch (type) {
      case FOREST:
        return Color.web("#228B22");
      case PASTURE:
        return Color.web("#90EE90");
      case FIELDS:
        return Color.web("#FFD700");
      case HILLS:
        return Color.web("#CD853F");
      case MOUNTAINS:
        return Color.web("#808080");
      case DESERT:
        return Color.web("#F5DEB3");
      default:
        return Color.LIGHTGRAY;
    }
  }

  static Color playerColor(PlayerColor color) {
    switch (color) {
      case RED:
        return Color.CRIMSON;
      case BLUE:
        return Color.DODGERBLUE;
      case WHITE:
        return Color.LIGHTYELLOW;
      case ORANGE:
        return Color.DARKORANGE;
      default:
        return Color.GRAY;
    }
  }
}
