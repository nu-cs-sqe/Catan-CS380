package controller;

import board.Board;
import board.Edge;
import board.Robber;
import board.Vertex;
import domain.Bank;
import domain.Game;
import domain.Player;
import domain.RandomDiceRoller;
import domain.TurnFlow;
import javafx.scene.control.Alert;
import view.BoardView;
import view.GameView;

public class GameController {

  private static final int ROBBER_ROLL = 7;
  private static final int WIN_THRESHOLD = 10;

  private enum GamePhase { SETUP_SETTLEMENT, SETUP_ROAD, MAIN_PRE_ROLL, MAIN_POST_ROLL }
  private enum BuildMode { NONE, SETTLEMENT, ROAD, CITY }

  private final GameView gameView;
  private final Game game;
  private final Board board;
  private final Bank bank;
  private final RandomDiceRoller diceRoller;
  private final TurnFlow turnFlow;
  private final Robber robber;

  private GamePhase phase;
  private BuildMode buildMode;
  private Vertex pendingSetupVertex;

  public GameController(GameView gameView, Game game, Board board, Bank bank) {
    this.gameView = gameView;
    this.game = game;
    this.board = board;
    this.bank = bank;
    this.diceRoller = new RandomDiceRoller();
    this.turnFlow = new TurnFlow(game.getPlayers(), bank);
    this.robber = board.createRobber();
    this.phase = GamePhase.SETUP_SETTLEMENT;
    this.buildMode = BuildMode.NONE;
    wireActions();
    enterSetupSettlement();
  }

  private void wireActions() {
    BoardView boardView = gameView.getBoardView();
    boardView.setOnVertexClick(this::handleVertexClick);
    boardView.setOnEdgeClick(this::handleEdgeClick);
    gameView.setOnRollDice(this::onRollDice);
    gameView.setOnEndTurn(this::onEndTurn);
    gameView.setOnBuildSettlement(this::onBuildSettlement);
    gameView.setOnBuildRoad(this::onBuildRoad);
    gameView.setOnBuildCity(this::onBuildCity);
    gameView.setOnBuyDevCard(this::onBuyDevCard);
  }

  private void enterSetupSettlement() {
    gameView.setRollEnabled(false);
    gameView.setEndTurnEnabled(false);
    gameView.setBuildActionsEnabled(false);
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.VERTEX);
    Player current = game.getCurrentSetupPlayer();
    gameView.setStatusMessage("Setup — " + current.getName() + ": Place a settlement");
    refreshBoard();
  }

  private void handleVertexClick(Vertex vertex) {
    if (phase == GamePhase.SETUP_SETTLEMENT) {
      handleSetupVertexClick(vertex);
    } else if (phase == GamePhase.MAIN_POST_ROLL) {
      handleMainVertexClick(vertex);
    }
  }

  private void handleSetupVertexClick(Vertex vertex) {
    if (vertex.getSettlement() != null) {
      gameView.logMessage("That spot is already occupied.");
      return;
    }
    pendingSetupVertex = vertex;
    Player current = game.getCurrentSetupPlayer();
    phase = GamePhase.SETUP_ROAD;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.EDGE);
    gameView.setStatusMessage("Setup — " + current.getName()
        + ": Place a road touching that settlement");
    refreshBoard();
  }

  private void handleMainVertexClick(Vertex vertex) {
    Player current = getMainPlayer();
    if (buildMode == BuildMode.SETTLEMENT) {
      try {
        turnFlow.buildSettlement(current, vertex, board);
        gameView.logMessage(current.getName() + " built a settlement.");
      } catch (IllegalStateException e) {
        gameView.logMessage(e.getMessage());
        return;
      }
    } else if (buildMode == BuildMode.CITY) {
      try {
        turnFlow.buildCity(current, vertex);
        gameView.logMessage(current.getName() + " upgraded to a city.");
      } catch (IllegalStateException e) {
        gameView.logMessage(e.getMessage());
        return;
      }
    } else {
      return;
    }
    buildMode = BuildMode.NONE;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.NONE);
    gameView.setStatusMessage(current.getName() + " — Take your actions");
    refreshViews();
  }

  private void handleEdgeClick(Edge edge) {
    if (phase == GamePhase.SETUP_ROAD) {
      handleSetupEdgeClick(edge);
    } else if (phase == GamePhase.MAIN_POST_ROLL) {
      handleMainEdgeClick(edge);
    }
  }

  private void handleSetupEdgeClick(Edge edge) {
    if (edge.getOwner() != null) {
      gameView.logMessage("That edge already has a road.");
      return;
    }
    if (!edgeTouches(edge, pendingSetupVertex)) {
      gameView.logMessage("The road must connect to your new settlement.");
      return;
    }
    Player current = game.getCurrentSetupPlayer();
    try {
      game.placeSetupSettlement(pendingSetupVertex, edge, board, bank);
    } catch (RuntimeException e) {
      gameView.logMessage(e.getMessage());
      pendingSetupVertex = null;
      phase = GamePhase.SETUP_SETTLEMENT;
      enterSetupSettlement();
      return;
    }
    gameView.logMessage(current.getName() + " placed a settlement and road.");
    pendingSetupVertex = null;
    advanceSetup();
  }

  private boolean edgeTouches(Edge edge, Vertex vertex) {
    if (vertex == null) {
      return false;
    }
    for (String endpoint : edge.getId().split("\\|")) {
      if (endpoint.equals(vertex.getId())) {
        return true;
      }
    }
    return false;
  }

  private void handleMainEdgeClick(Edge edge) {
    if (buildMode != BuildMode.ROAD) {
      return;
    }
    Player current = getMainPlayer();
    try {
      turnFlow.buildRoad(current, edge, board);
      gameView.logMessage(current.getName() + " built a road.");
    } catch (IllegalStateException e) {
      gameView.logMessage(e.getMessage());
      return;
    }
    buildMode = BuildMode.NONE;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.NONE);
    gameView.setStatusMessage(current.getName() + " — Take your actions");
    refreshViews();
  }

  private void advanceSetup() {
    if (game.isSetupComplete()) {
      startMainGame();
    } else {
      phase = GamePhase.SETUP_SETTLEMENT;
      enterSetupSettlement();
    }
  }

  private void startMainGame() {
    phase = GamePhase.MAIN_PRE_ROLL;
    gameView.logMessage("Setup complete! Game begins.");
    gameView.setRollEnabled(true);
    gameView.setEndTurnEnabled(false);
    gameView.setBuildActionsEnabled(false);
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.NONE);
    updateMainGameStatus();
    refreshViews();
  }

  public void onRollDice() {
    if (phase != GamePhase.MAIN_PRE_ROLL) {
      return;
    }
    int roll = diceRoller.roll();
    Player current = getMainPlayer();
    gameView.logMessage(current.getName() + " rolled a " + roll + ".");
    if (roll == ROBBER_ROLL) {
      handleRobberRoll();
    } else {
      turnFlow.resolveRoll(board, robber, roll);
    }
    phase = GamePhase.MAIN_POST_ROLL;
    gameView.setRollEnabled(false);
    gameView.setEndTurnEnabled(true);
    gameView.setBuildActionsEnabled(true);
    gameView.setStatusMessage(current.getName() + " — Take your actions");
    refreshViews();
  }

  private void handleRobberRoll() {
    // The full robber resolution (discard for 8+ cards, move the robber via
    // turnFlow.moveRobberAndSteal, choose a victim from stealCandidates) is
    // added with the robber UI task; production is correctly skipped on a 7.
    gameView.logMessage("Rolled a 7 — robber handling coming soon.");
  }

  public void onEndTurn() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    if (checkWinCondition()) {
      gameView.setRollEnabled(false);
      gameView.setEndTurnEnabled(false);
      gameView.setBuildActionsEnabled(false);
      gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.NONE);
      return;
    }
    turnFlow.endTurn(getMainPlayer());
    game.endTurn();
    phase = GamePhase.MAIN_PRE_ROLL;
    buildMode = BuildMode.NONE;
    gameView.setRollEnabled(true);
    gameView.setEndTurnEnabled(false);
    gameView.setBuildActionsEnabled(false);
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.NONE);
    updateMainGameStatus();
    refreshViews();
  }

  public void onBuildSettlement() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    buildMode = BuildMode.SETTLEMENT;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.VERTEX);
    gameView.setStatusMessage(getMainPlayer().getName() + ": Click a vertex to place settlement");
    refreshBoard();
  }

  public void onBuildRoad() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    buildMode = BuildMode.ROAD;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.EDGE);
    gameView.setStatusMessage(getMainPlayer().getName() + ": Click an edge to place road");
    refreshBoard();
  }

  private void onBuildCity() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    buildMode = BuildMode.CITY;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.VERTEX);
    gameView.setStatusMessage(getMainPlayer().getName() + ": Click your settlement to upgrade to city");
    refreshBoard();
  }

  private void onBuyDevCard() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    Player current = getMainPlayer();
    try {
      turnFlow.buyDevelopmentCard(current);
      gameView.logMessage(current.getName() + " bought a development card.");
      refreshViews();
    } catch (Exception e) {
      gameView.logMessage(e.getMessage());
    }
  }

  private boolean checkWinCondition() {
    turnFlow.updateLongestRoad(board);
    turnFlow.updateLargestArmy();
    int idx = game.getCurrentPlayerIndex();
    int vp = turnFlow.getVictoryPoints(idx);
    if (vp >= WIN_THRESHOLD) {
      showWinner(game.getPlayers().get(idx), vp);
      return true;
    }
    return false;
  }

  private void showWinner(Player player, int vp) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText(player.getName() + " wins!");
    alert.setContentText(player.getName() + " has " + vp + " victory points.");
    alert.showAndWait();
  }

  private void refreshBoard() {
    gameView.getBoardView().refresh(board);
  }

  private void refreshViews() {
    refreshBoard();
    Player current = (phase == GamePhase.SETUP_SETTLEMENT
        || phase == GamePhase.SETUP_ROAD) ? game.getCurrentSetupPlayer() : getMainPlayer();
    int idx = game.getPlayers().indexOf(current);
    if (idx >= 0) {
      gameView.getPlayerInfoView().refresh(current, turnFlow.getVictoryPoints(idx));
    }
  }

  private void updateMainGameStatus() {
    Player current = getMainPlayer();
    gameView.setStatusMessage(current.getName() + "'s turn — Roll the dice");
  }

  private Player getMainPlayer() {
    return game.getPlayers().get(game.getCurrentPlayerIndex());
  }
}
