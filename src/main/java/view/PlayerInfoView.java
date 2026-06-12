package view;

import domain.Player;
import domain.PlayerColor;
import domain.Resource;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

import java.util.EnumMap;
import java.util.Map;

public class PlayerInfoView extends VBox {

  private static final double COLOR_BOX_SIZE = 18.0;
  private static final double SPACING = 8.0;
  private static final double PADDING_VALUE = 12.0;
  private static final double PANEL_WIDTH = 260.0;

  private final Label nameLabel;
  private final Rectangle colorIndicator;
  private final Label vpLabel;
  private final Label settlementsLabel;
  private final Label citiesLabel;
  private final Label roadsLabel;
  private final Label knightsLabel;
  private final Map<Resource, Label> resourceLabels;

  public PlayerInfoView() {
    setSpacing(SPACING);
    setPadding(new Insets(PADDING_VALUE));
    setPrefWidth(PANEL_WIDTH);
    setStyle("-fx-border-color: -color-border-default;");
    nameLabel = new Label("—");
    nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    colorIndicator = new Rectangle(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
    vpLabel = new Label("VP: 0");
    settlementsLabel = new Label("Settlements: 5");
    citiesLabel = new Label("Cities: 4");
    roadsLabel = new Label("Roads: 15");
    knightsLabel = new Label("Knights: 0");
    resourceLabels = buildResourceLabels();
    buildLayout();
  }

  private Map<Resource, Label> buildResourceLabels() {
    Map<Resource, Label> map = new EnumMap<>(Resource.class);
    for (Resource res : Resource.values()) {
      if (res != Resource.GENERIC) {
        map.put(res, new Label(res.name() + ": 0"));
      }
    }
    return map;
  }

  private void buildLayout() {
    HBox nameRow = new HBox(SPACING, colorIndicator, nameLabel);
    Label resourceHeader = new Label("Resources:");
    resourceHeader.setStyle("-fx-font-weight: bold;");
    GridPane resourceGrid = buildResourceGrid();
    Label piecesHeader = new Label("Pieces:");
    piecesHeader.setStyle("-fx-font-weight: bold;");
    getChildren().addAll(
        nameRow, vpLabel, resourceHeader, resourceGrid,
        piecesHeader, settlementsLabel, citiesLabel, roadsLabel, knightsLabel);
  }

  private GridPane buildResourceGrid() {
    GridPane grid = new GridPane();
    grid.setHgap(SPACING);
    grid.setVgap(2.0);
    int row = 0;
    for (Map.Entry<Resource, Label> entry : resourceLabels.entrySet()) {
      grid.add(new Label(entry.getKey().name() + ":"), 0, row);
      grid.add(entry.getValue(), 1, row);
      row++;
    }
    return grid;
  }

  public void refresh(Player player, int vp) {
    nameLabel.setText(player.getName());
    colorIndicator.setFill(playerColorFx(player.getColor()));
    colorIndicator.setStroke(Color.BLACK);
    vpLabel.setText("VP: " + vp);
    for (Map.Entry<Resource, Label> entry : resourceLabels.entrySet()) {
      int count = player.getResourceCount(entry.getKey());
      entry.getValue().setText(String.valueOf(count));
    }
    settlementsLabel.setText("Settlements: " + player.getRemainingSettlements());
    citiesLabel.setText("Cities: " + player.getRemainingCities());
    roadsLabel.setText("Roads: " + player.getRemainingRoads());
    knightsLabel.setText("Knights: " + player.getKnightsPlayed());
  }

  private static Color playerColorFx(PlayerColor color) {
    return BoardView.playerColor(color);
  }
}
