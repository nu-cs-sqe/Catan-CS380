package view;

import domain.PlayerColor;
import i18n.Messages;
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
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SetupView {

  private static final int PLAYER_MIN = 3;
  private static final int PLAYER_MAX = 4;
  private static final double SCENE_WIDTH = 480.0;
  private static final double SCENE_HEIGHT = 580.0;
  private static final double ROOT_SPACING = 14.0;
  private static final double ROOT_PADDING = 24.0;
  private static final double ROW_SPACING = 8.0;
  private static final Locale TURKISH = new Locale("tr");

  private final Scene scene;
  private final Spinner<Integer> playerCountSpinner;
  private final VBox playerRowsBox;
  private final List<TextField> nameFields;
  private final List<ChoiceBox<PlayerColor>> colorBoxes;
  private final List<Label> rowLabels;
  private final Label titleLabel;
  private final Label languageLabel;
  private final Label countLabel;
  private final ChoiceBox<Locale> languageBox;
  private final Label errorLabel;
  private final Button startButton;

  public SetupView() {
    Messages.setLocale(Locale.ENGLISH);
    playerCountSpinner = new Spinner<>(PLAYER_MIN, PLAYER_MAX, PLAYER_MIN);
    playerRowsBox = new VBox(ROW_SPACING);
    nameFields = new ArrayList<>();
    colorBoxes = new ArrayList<>();
    rowLabels = new ArrayList<>();
    titleLabel = new Label();
    titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
    languageLabel = new Label();
    countLabel = new Label();
    languageBox = buildLanguageBox();
    errorLabel = buildErrorLabel();
    startButton = new Button();
    VBox root = buildRoot();
    scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    rebuildPlayerRows(PLAYER_MIN);
    applyTexts();
    playerCountSpinner.valueProperty().addListener(
        (obs, oldVal, newVal) -> rebuildPlayerRows(newVal));
  }

  private ChoiceBox<Locale> buildLanguageBox() {
    ChoiceBox<Locale> box = new ChoiceBox<>();
    box.getItems().addAll(Locale.ENGLISH, TURKISH);
    box.setValue(Locale.ENGLISH);
    box.setConverter(new StringConverter<Locale>() {
      @Override
      public String toString(Locale locale) {
        return locale == null ? "" : locale.getDisplayLanguage(locale);
      }

      @Override
      public Locale fromString(String value) {
        return Locale.ENGLISH;
      }
    });
    box.valueProperty().addListener((obs, oldVal, newVal) -> onLanguageChanged(newVal));
    return box;
  }

  private void onLanguageChanged(Locale locale) {
    if (locale == null) {
      return;
    }
    Messages.setLocale(locale);
    applyTexts();
  }

  private Label buildErrorLabel() {
    Label label = new Label();
    label.setTextFill(Color.RED);
    label.setVisible(false);
    return label;
  }

  private VBox buildRoot() {
    HBox languageRow = new HBox(ROW_SPACING, languageLabel, languageBox);
    VBox root = new VBox(ROOT_SPACING);
    root.setPadding(new Insets(ROOT_PADDING));
    root.getChildren().addAll(
        titleLabel, languageRow, countLabel, playerCountSpinner,
        playerRowsBox, errorLabel, startButton);
    return root;
  }

  private void applyTexts() {
    titleLabel.setText(Messages.get("setup.welcome"));
    languageLabel.setText(Messages.get("setup.language"));
    countLabel.setText(Messages.get("setup.players"));
    startButton.setText(Messages.get("setup.start"));
    for (int i = 0; i < rowLabels.size(); i++) {
      rowLabels.get(i).setText(Messages.get("setup.player.row", i + 1));
    }
  }

  private void rebuildPlayerRows(int count) {
    playerRowsBox.getChildren().clear();
    nameFields.clear();
    colorBoxes.clear();
    rowLabels.clear();
    for (int i = 0; i < count; i++) {
      addPlayerRow(i);
    }
  }

  private void addPlayerRow(int index) {
    Label label = new Label(Messages.get("setup.player.row", index + 1));
    TextField nameField = new TextField(Messages.get("setup.player.name", index + 1));
    ChoiceBox<PlayerColor> colorBox = new ChoiceBox<>();
    colorBox.getItems().addAll(PlayerColor.values());
    colorBox.setValue(PlayerColor.values()[index]);
    nameFields.add(nameField);
    colorBoxes.add(colorBox);
    rowLabels.add(label);
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
