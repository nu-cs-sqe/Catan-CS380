package view;

import domain.DevelopmentCard;
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
import java.util.List;
import java.util.Map;

public class PlayerInfoView extends VBox {

  private static final double COLOR_BOX_SIZE = 18.0;
  private static final double SPACING = 8.0;
  private static final double PADDING_VALUE = 12.0;
  private static final double PANEL_WIDTH = 260.0;

  private final Label nameLabel;
  private final Rectangle colorIndicator;
  private final Label vpLabel;
  private final Label specialLabel;
  private final Label settlementsLabel;
  private final Label citiesLabel;
  private final Label roadsLabel;
  private final Label knightsLabel;
  private final Label devCardsLabel;
  private final VBox summaryBox;
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
    specialLabel = new Label("");
    specialLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8a6d00;");
    settlementsLabel = new Label("Settlements: 5");
    citiesLabel = new Label("Cities: 4");
    roadsLabel = new Label("Roads: 15");
    knightsLabel = new Label("Knights: 0");
    devCardsLabel = new Label("Dev Cards: none");
    devCardsLabel.setWrapText(true);
    summaryBox = new VBox(2.0);
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
    Label summaryHeader = new Label("All Players:");
    summaryHeader.setStyle("-fx-font-weight: bold;");
    getChildren().addAll(
        nameRow, vpLabel, specialLabel, resourceHeader, resourceGrid,
        piecesHeader, settlementsLabel, citiesLabel, roadsLabel, knightsLabel,
        devCardsLabel, summaryHeader, summaryBox);
  }

  public void refreshSummary(List<Player> players, int[] victoryPoints,
      int longestRoadHolder, int largestArmyHolder) {
    summaryBox.getChildren().clear();
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      StringBuilder row = new StringBuilder();
      row.append(player.getName())
          .append(" — VP ").append(victoryPoints[i])
          .append(", cards ").append(totalResourceCards(player))
          .append(", dev ").append(player.getDevelopmentCards().size());
      if (i == longestRoadHolder) {
        row.append(" [LR]");
      }
      if (i == largestArmyHolder) {
        row.append(" [LA]");
      }
      Label label = new Label(row.toString());
      label.setTextFill(playerColorFx(player.getColor()));
      summaryBox.getChildren().add(label);
    }
  }

  private static int totalResourceCards(Player player) {
    int total = 0;
    for (Resource resource : Resource.values()) {
      if (resource != Resource.GENERIC) {
        total += player.getResourceCount(resource);
      }
    }
    return total;
  }

  private static String devCardsText(Player player) {
    Map<DevelopmentCard, Integer> counts = new EnumMap<>(DevelopmentCard.class);
    for (DevelopmentCard card : player.getDevelopmentCards()) {
      counts.merge(card, 1, Integer::sum);
    }
    if (counts.isEmpty()) {
      return "Dev Cards: none";
    }
    StringBuilder text = new StringBuilder("Dev Cards: ");
    boolean firstEntry = true;
    for (Map.Entry<DevelopmentCard, Integer> entry : counts.entrySet()) {
      if (!firstEntry) {
        text.append(", ");
      }
      text.append(entry.getKey().name()).append(" x").append(entry.getValue());
      firstEntry = false;
    }
    return text.toString();
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

  public void refresh(Player player, int vp,
      boolean hasLongestRoad, boolean hasLargestArmy) {
    nameLabel.setText(player.getName());
    colorIndicator.setFill(playerColorFx(player.getColor()));
    colorIndicator.setStroke(Color.BLACK);
    vpLabel.setText("VP: " + vp);
    specialLabel.setText(specialText(hasLongestRoad, hasLargestArmy));
    for (Map.Entry<Resource, Label> entry : resourceLabels.entrySet()) {
      int count = player.getResourceCount(entry.getKey());
      entry.getValue().setText(String.valueOf(count));
    }
    settlementsLabel.setText("Settlements: " + player.getRemainingSettlements());
    citiesLabel.setText("Cities: " + player.getRemainingCities());
    roadsLabel.setText("Roads: " + player.getRemainingRoads());
    knightsLabel.setText("Knights: " + player.getKnightsPlayed());
    devCardsLabel.setText(devCardsText(player));
  }

  private static String specialText(boolean hasLongestRoad, boolean hasLargestArmy) {
    StringBuilder text = new StringBuilder();
    if (hasLongestRoad) {
      text.append("★ Longest Road");
    }
    if (hasLargestArmy) {
      if (text.length() > 0) {
        text.append("   ");
      }
      text.append("★ Largest Army");
    }
    return text.toString();
  }

  private static Color playerColorFx(PlayerColor color) {
    return BoardView.playerColor(color);
  }
}
