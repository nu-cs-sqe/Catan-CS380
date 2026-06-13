package view;

import domain.PlayerColor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SetupView {

  private static final int PLAYER_MIN = 3;
  private static final int PLAYER_MAX = 4;
  private static final double SCENE_WIDTH = 480.0;
  private static final double SCENE_HEIGHT = 540.0;
  private static final double ROOT_SPACING = 14.0;
  private static final double ROOT_PADDING = 24.0;
  private static final double ROW_SPACING = 8.0;

  private final Scene scene;
  private final Spinner<Integer> playerCountSpinner;
  private final VBox playerRowsBox;
  private final List<TextField> nameFields;
  private final List<ChoiceBox<PlayerColor>> colorBoxes;
  private final Label errorLabel;
  private final Button startButton;

  public SetupView() {
    playerCountSpinner = new Spinner<>(PLAYER_MIN, PLAYER_MAX, PLAYER_MIN);
    playerRowsBox = new VBox(ROW_SPACING);
    nameFields = new ArrayList<>();
    colorBoxes = new ArrayList<>();
    errorLabel = buildErrorLabel();
    startButton = new Button("Start Game");
    VBox root = buildRoot();
    scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    rebuildPlayerRows(PLAYER_MIN);
    playerCountSpinner.valueProperty().addListener(
        (obs, oldVal, newVal) -> rebuildPlayerRows(newVal));
  }

  private Label buildErrorLabel() {
    Label label = new Label();
    label.setTextFill(Color.RED);
    label.setVisible(false);
    return label;
  }

  private VBox buildRoot() {
    Label title = new Label("Welcome to Catan");
    title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
    Label countLabel = new Label("Number of Players:");
    VBox root = new VBox(ROOT_SPACING);
    root.setPadding(new Insets(ROOT_PADDING));
    root.getChildren().addAll(
        title, countLabel, playerCountSpinner,
        playerRowsBox, errorLabel, startButton);
    return root;
  }

  private void rebuildPlayerRows(int count) {
    playerRowsBox.getChildren().clear();
    nameFields.clear();
    colorBoxes.clear();
    for (int i = 0; i < count; i++) {
      addPlayerRow(i);
    }
  }

  private void addPlayerRow(int index) {
    Label label = new Label("Player " + (index + 1) + ":");
    TextField nameField = new TextField("Player " + (index + 1));
    ChoiceBox<PlayerColor> colorBox = new ChoiceBox<>();
    colorBox.getItems().addAll(PlayerColor.values());
    colorBox.setValue(PlayerColor.values()[index]);
    nameFields.add(nameField);
    colorBoxes.add(colorBox);
    HBox row = new HBox(ROW_SPACING, label, nameField, colorBox);
    playerRowsBox.getChildren().add(row);
  }

  public Scene getScene() {
    return scene;
  }

  public int getPlayerCount() {
    return playerCountSpinner.getValue();
  }

  public String getPlayerName(int index) {
    return nameFields.get(index).getText();
  }

  public PlayerColor getPlayerColor(int index) {
    return colorBoxes.get(index).getValue();
  }

  public void setOnStartGame(Runnable handler) {
    startButton.setOnAction(e -> handler.run());
  }

  public void showError(String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
  }

  public void clearError() {
    errorLabel.setVisible(false);
  }
}
