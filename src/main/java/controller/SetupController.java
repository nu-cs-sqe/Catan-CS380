package controller;

import board.Board;
import domain.Bank;
import domain.Game;
import domain.Player;
import domain.PlayerColor;
import i18n.Messages;
import javafx.stage.Stage;
import view.GameView;
import view.SetupView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetupController {

  private final SetupView setupView;
  private final Stage primaryStage;

  public SetupController(SetupView setupView, Stage primaryStage) {
    this.setupView = setupView;
    this.primaryStage = primaryStage;
    setupView.setOnStartGame(this::onStartGame);
  }

  private void onStartGame() {
    setupView.clearError();
    int count = setupView.getPlayerCount();
    List<Player> players = buildPlayers(count);
    if (players == null) {
      return;
    }
    Board board = new Board(Collections::shuffle);
    board.create();
    Bank bank = new Bank(Collections::shuffle);
    Game game = new Game(players);
    GameView gameView = new GameView();
    new GameController(gameView, game, board, bank);
    primaryStage.setScene(gameView.getScene());
  }

  private List<Player> buildPlayers(int count) {
    List<Player> players = new ArrayList<>();
    Set<PlayerColor> usedColors = new HashSet<>();
    for (int i = 0; i < count; i++) {
      String name = setupView.getPlayerName(i).trim();
      PlayerColor color = setupView.getPlayerColor(i);
      String error = validatePlayer(i, name, color, usedColors);
      if (error != null) {
        setupView.showError(error);
        return null;
      }
      usedColors.add(color);
      players.add(new Player(name, color));
    }
    return players;
  }

  private String validatePlayer(int index, String name,
      PlayerColor color, Set<PlayerColor> usedColors) {
    if (name.isEmpty()) {
      return Messages.get("error.name.empty", index + 1);
    }
    if (usedColors.contains(color)) {
      return Messages.get("error.color.taken", index + 1);
    }
    return null;
  }
}
