package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameView {

  private static final double SCENE_WIDTH = 1100.0;
  private static final double SCENE_HEIGHT = 720.0;
  private static final double TOP_PADDING = 10.0;
  private static final double BUTTON_SPACING = 8.0;
  private static final double BOTTOM_PADDING = 10.0;
  private static final double LOG_HEIGHT = 80.0;
  private static final int LOG_MAX_LINES = 10;

  private final Scene scene;
  private final BoardView boardView;
  private final PlayerInfoView playerInfoView;
  private final Label statusLabel;
  private final TextArea logArea;
  private final Button rollButton;
  private final Button endTurnButton;
  private final Button buildSettlementButton;
  private final Button buildRoadButton;
  private final Button buildCityButton;
  private final Button buyDevCardButton;
  private final Button playDevCardButton;
  private final Button tradeButton;

  public GameView() {
    boardView = new BoardView();
    playerInfoView = new PlayerInfoView();
    statusLabel = new Label("Initialising...");
    statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    logArea = buildLogArea();
    rollButton = new Button("Roll Dice");
    endTurnButton = new Button("End Turn");
    buildSettlementButton = new Button("Build Settlement");
    buildRoadButton = new Button("Build Road");
    buildCityButton = new Button("Build City");
    buyDevCardButton = new Button("Buy Dev Card");
    playDevCardButton = new Button("Play Dev Card");
    tradeButton = new Button("Trade");
    BorderPane root = buildLayout();
    scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
  }

  private TextArea buildLogArea() {
    TextArea area = new TextArea();
    area.setEditable(false);
    area.setPrefHeight(LOG_HEIGHT);
    area.setWrapText(true);
    return area;
  }

  private BorderPane buildLayout() {
    BorderPane root = new BorderPane();
    root.setCenter(boardView);
    root.setRight(buildRightPanel());
    root.setBottom(buildBottomPanel());
    root.setTop(buildTopBar());
    return root;
  }

  private VBox buildTopBar() {
    VBox top = new VBox(2.0, statusLabel);
    top.setPadding(new Insets(TOP_PADDING));
    return top;
  }

  private VBox buildRightPanel() {
    VBox panel = new VBox(BUTTON_SPACING, playerInfoView, logArea);
    panel.setPadding(new Insets(BOTTOM_PADDING));
    return panel;
  }

  private HBox buildBottomPanel() {
    HBox bar = new HBox(BUTTON_SPACING,
        rollButton, buildSettlementButton, buildRoadButton,
        buildCityButton, buyDevCardButton, playDevCardButton,
        tradeButton, endTurnButton);
    bar.setPadding(new Insets(BOTTOM_PADDING));
    return bar;
  }

  public Scene getScene() {
    return scene;
  }

  public BoardView getBoardView() {
    return boardView;
  }

  public PlayerInfoView getPlayerInfoView() {
    return playerInfoView;
  }

  public void setStatusMessage(String message) {
    statusLabel.setText(message);
  }

  public void logMessage(String message) {
    logArea.appendText(message + "\n");
    trimLog();
    logArea.positionCaret(logArea.getLength());
    logArea.setScrollTop(Double.MAX_VALUE);
  }

  private void trimLog() {
    String[] lines = logArea.getText().split("\n");
    if (lines.length > LOG_MAX_LINES) {
      StringBuilder sb = new StringBuilder();
      for (int i = lines.length - LOG_MAX_LINES; i < lines.length; i++) {
        sb.append(lines[i]).append("\n");
      }
      logArea.setText(sb.toString());
    }
  }

  public void setRollEnabled(boolean enabled) {
    rollButton.setDisable(!enabled);
  }

  public void setEndTurnEnabled(boolean enabled) {
    endTurnButton.setDisable(!enabled);
  }

  public void setBuildActionsEnabled(boolean enabled) {
    buildSettlementButton.setDisable(!enabled);
    buildRoadButton.setDisable(!enabled);
    buildCityButton.setDisable(!enabled);
    buyDevCardButton.setDisable(!enabled);
    playDevCardButton.setDisable(!enabled);
    tradeButton.setDisable(!enabled);
  }

  public void setOnRollDice(Runnable handler) {
    rollButton.setOnAction(e -> handler.run());
  }

  public void setOnEndTurn(Runnable handler) {
    endTurnButton.setOnAction(e -> handler.run());
  }

  public void setOnBuildSettlement(Runnable handler) {
    buildSettlementButton.setOnAction(e -> handler.run());
  }

  public void setOnBuildRoad(Runnable handler) {
    buildRoadButton.setOnAction(e -> handler.run());
  }

  public void setOnBuildCity(Runnable handler) {
    buildCityButton.setOnAction(e -> handler.run());
  }

  public void setOnBuyDevCard(Runnable handler) {
    buyDevCardButton.setOnAction(e -> handler.run());
  }

  public void setOnPlayDevCard(Runnable handler) {
    playDevCardButton.setOnAction(e -> handler.run());
  }

  public void setOnTrade(Runnable handler) {
    tradeButton.setOnAction(e -> handler.run());
  }
}
