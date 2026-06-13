package controller;

import board.Board;
import board.Edge;
import board.Robber;
import board.Tile;
import board.Vertex;
import domain.Bank;
import domain.DevelopmentCard;
import domain.Game;
import domain.Player;
import domain.RandomDiceRoller;
import domain.Resource;
import domain.TurnFlow;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
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
    gameView.setOnPlayDevCard(this::onPlayDevCard);
  }

  private void enterSetupSettlement() {
    gameView.setRollEnabled(false);
    gameView.setEndTurnEnabled(false);
    gameView.setBuildActionsEnabled(false);
    gameView.getBoardView().setVertexValidator(
        v -> turnFlow.canBuildSetupSettlement(v, board));
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
    gameView.getBoardView().setEdgeValidator(
        e -> e.getOwner() == null && edgeTouches(e, pendingSetupVertex));
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
      } catch (RuntimeException e) {
        gameView.logMessage(e.getMessage() + " — pick another spot.");
        return;
      }
    } else if (buildMode == BuildMode.CITY) {
      try {
        turnFlow.buildCity(current, vertex);
        gameView.logMessage(current.getName() + " upgraded to a city.");
      } catch (RuntimeException e) {
        gameView.logMessage(e.getMessage() + " — pick another spot.");
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
    } catch (RuntimeException e) {
      gameView.logMessage(e.getMessage() + " — pick another edge.");
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
    turnFlow.setCurrentPlayer(game.getCurrentPlayerIndex());
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
    gameView.logMessage("Rolled a 7 — robber activated.");
    performDiscards();
    moveRobberInteractive();
  }

  private void performDiscards() {
    List<Player> players = game.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      int count = turnFlow.getDiscardCount(i);
      if (count > 0) {
        Player player = players.get(i);
        turnFlow.discard(player, chooseDiscards(player, count));
        gameView.logMessage(player.getName() + " discarded " + count + " cards.");
      }
    }
  }

  private Map<Resource, Integer> chooseDiscards(Player player, int count) {
    Map<Resource, Integer> chosen = new EnumMap<>(Resource.class);
    int remaining = count;
    for (Resource resource : Resource.values()) {
      if (resource == Resource.GENERIC || remaining == 0) {
        continue;
      }
      int take = Math.min(player.getResourceCount(resource), remaining);
      if (take > 0) {
        chosen.put(resource, take);
        remaining -= take;
      }
    }
    return chosen;
  }

  private void moveRobberInteractive() {
    Tile target = chooseRobberTile();
    if (target == null) {
      return;
    }
    turnFlow.moveRobber(robber, target, board);
    refreshBoard();
    Player current = getMainPlayer();
    List<Player> candidates = turnFlow.stealCandidates(robber, board);
    candidates.remove(current);
    if (candidates.isEmpty()) {
      gameView.logMessage("No players to steal from.");
      return;
    }
    Player victim = chooseVictim(candidates);
    if (victim != null) {
      turnFlow.stealResource(current, victim, robber, board);
      gameView.logMessage(current.getName() + " stole from " + victim.getName() + ".");
      refreshViews();
    }
  }

  private Tile chooseRobberTile() {
    Map<String, Tile> options = new LinkedHashMap<>();
    Tile currentTile = robber.getTile();
    for (Tile tile : board.getTiles()) {
      if (currentTile != null && tile.getQ() == currentTile.getQ()
          && tile.getR() == currentTile.getR()) {
        continue;
      }
      options.put(tile.getTileType() + " (" + tile.getQ() + "," + tile.getR() + ")", tile);
    }
    if (options.isEmpty()) {
      return null;
    }
    String first = options.keySet().iterator().next();
    ChoiceDialog<String> dialog = new ChoiceDialog<>(first, options.keySet());
    dialog.setTitle("Move Robber");
    dialog.setHeaderText("Choose a tile to move the robber to");
    dialog.setContentText("Tile:");
    Optional<String> result = dialog.showAndWait();
    return options.get(result.orElse(first));
  }

  private Player chooseVictim(List<Player> candidates) {
    Map<String, Player> options = new LinkedHashMap<>();
    for (Player player : candidates) {
      options.put(player.getName(), player);
    }
    String first = options.keySet().iterator().next();
    ChoiceDialog<String> dialog = new ChoiceDialog<>(first, options.keySet());
    dialog.setTitle("Steal");
    dialog.setHeaderText("Choose a player to steal from");
    dialog.setContentText("Player:");
    Optional<String> result = dialog.showAndWait();
    return options.get(result.orElse(first));
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
    turnFlow.setCurrentPlayer(game.getCurrentPlayerIndex());
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
    gameView.getBoardView().setVertexValidator(
        v -> turnFlow.canBuildSettlement(getMainPlayer(), v, board));
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.VERTEX);
    gameView.setStatusMessage(getMainPlayer().getName() + ": Click a vertex to place settlement");
    refreshBoard();
  }

  public void onBuildRoad() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    buildMode = BuildMode.ROAD;
    gameView.getBoardView().setEdgeValidator(
        e -> turnFlow.canBuildRoad(getMainPlayer(), e, board));
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.EDGE);
    gameView.setStatusMessage(getMainPlayer().getName() + ": Click an edge to place road");
    refreshBoard();
  }

  private void onBuildCity() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    buildMode = BuildMode.CITY;
    gameView.getBoardView().setVertexValidator(
        v -> turnFlow.canBuildCity(getMainPlayer(), v));
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

  private void onPlayDevCard() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    Player current = getMainPlayer();
    List<DevelopmentCard> playable = new ArrayList<>();
    for (DevelopmentCard card : current.getDevelopmentCards()) {
      if (card != DevelopmentCard.VICTORY_POINT && !playable.contains(card)) {
        playable.add(card);
      }
    }
    if (playable.isEmpty()) {
      gameView.logMessage("No playable development cards.");
      return;
    }
    DevelopmentCard card = chooseChoice("Play Development Card",
        "Choose a card to play", playable);
    try {
      playDevCard(current, card);
      refreshViews();
    } catch (RuntimeException e) {
      gameView.logMessage(e.getMessage());
    }
  }

  private void playDevCard(Player current, DevelopmentCard card) {
    switch (card) {
      case KNIGHT:
        playKnight(current);
        break;
      case MONOPOLY:
        Resource monopolised = chooseResource("Choose a resource to monopolise");
        turnFlow.playMonopolyCard(current, monopolised);
        gameView.logMessage(current.getName() + " played Monopoly on " + monopolised + ".");
        break;
      case YEAR_OF_PLENTY:
        Resource first = chooseResource("Choose the first resource");
        Resource second = chooseResource("Choose the second resource");
        turnFlow.playYearOfPlentyCard(current, first, second);
        gameView.logMessage(current.getName() + " played Year of Plenty.");
        break;
      case ROAD_BUILDING:
        playRoadBuilding(current);
        break;
      default:
        break;
    }
  }

  private void playKnight(Player current) {
    turnFlow.playDevelopmentCard(current, DevelopmentCard.KNIGHT);
    Tile target = chooseRobberTile();
    if (target != null) {
      turnFlow.moveRobber(robber, target, board);
    }
    current.playKnight();
    turnFlow.updateLargestArmy();
    refreshBoard();
    List<Player> candidates = turnFlow.stealCandidates(robber, board);
    candidates.remove(current);
    if (candidates.isEmpty()) {
      gameView.logMessage(current.getName() + " played a Knight.");
      return;
    }
    Player victim = chooseVictim(candidates);
    turnFlow.stealResource(current, victim, robber, board);
    gameView.logMessage(current.getName() + " played a Knight and stole from "
        + victim.getName() + ".");
  }

  private void playRoadBuilding(Player current) {
    List<Edge> legal = new ArrayList<>();
    for (Edge edge : board.getEdges()) {
      if (turnFlow.canBuildRoad(current, edge, board)) {
        legal.add(edge);
      }
    }
    if (legal.size() < 2) {
      gameView.logMessage("Not enough legal road spots for Road Building.");
      return;
    }
    Edge first = chooseEdge(legal);
    legal.remove(first);
    Edge second = chooseEdge(legal);
    turnFlow.playRoadBuildingCard(current, first, second, board);
    gameView.logMessage(current.getName() + " played Road Building.");
  }

  private Resource chooseResource(String header) {
    List<Resource> options = new ArrayList<>();
    for (Resource resource : Resource.values()) {
      if (resource != Resource.GENERIC) {
        options.add(resource);
      }
    }
    return chooseChoice("Development Card", header, options);
  }

  private Edge chooseEdge(List<Edge> legal) {
    Map<String, Edge> options = new LinkedHashMap<>();
    for (Edge edge : legal) {
      options.put(edge.getId(), edge);
    }
    String first = options.keySet().iterator().next();
    ChoiceDialog<String> dialog = new ChoiceDialog<>(first, options.keySet());
    dialog.setTitle("Road Building");
    dialog.setHeaderText("Choose a road to place");
    dialog.setContentText("Edge:");
    return options.get(dialog.showAndWait().orElse(first));
  }

  private <T> T chooseChoice(String title, String header, List<T> options) {
    T first = options.get(0);
    ChoiceDialog<T> dialog = new ChoiceDialog<>(first, options);
    dialog.setTitle(title);
    dialog.setHeaderText(header);
    dialog.setContentText("Choice:");
    return dialog.showAndWait().orElse(first);
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
    gameView.getBoardView().refresh(board, robber);
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
