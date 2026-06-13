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
import i18n.Messages;
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
    gameView.setOnTrade(this::onTrade);
  }

  private void enterSetupSettlement() {
    gameView.setRollEnabled(false);
    gameView.setEndTurnEnabled(false);
    gameView.setBuildActionsEnabled(false);
    gameView.getBoardView().setVertexValidator(
        v -> turnFlow.canBuildSetupSettlement(v, board));
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.VERTEX);
    Player current = game.getCurrentSetupPlayer();
    gameView.setStatusMessage(Messages.get("status.setup.settlement", current.getName()));
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
      gameView.logMessage(Messages.get("log.occupied"));
      return;
    }
    pendingSetupVertex = vertex;
    Player current = game.getCurrentSetupPlayer();
    phase = GamePhase.SETUP_ROAD;
    gameView.getBoardView().setEdgeValidator(
        e -> e.getOwner() == null && edgeTouches(e, pendingSetupVertex));
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.EDGE);
    gameView.setStatusMessage(Messages.get("status.setup.road", current.getName()));
    refreshBoard();
  }

  private void handleMainVertexClick(Vertex vertex) {
    Player current = getMainPlayer();
    if (buildMode == BuildMode.SETTLEMENT) {
      try {
        turnFlow.buildSettlement(current, vertex, board);
        gameView.logMessage(Messages.get("log.built.settlement", current.getName()));
      } catch (RuntimeException e) {
        gameView.logMessage(Messages.get("log.build.retry.spot", e.getMessage()));
        return;
      }
    } else if (buildMode == BuildMode.CITY) {
      try {
        turnFlow.buildCity(current, vertex);
        gameView.logMessage(Messages.get("log.upgraded.city", current.getName()));
      } catch (RuntimeException e) {
        gameView.logMessage(Messages.get("log.build.retry.spot", e.getMessage()));
        return;
      }
    } else {
      return;
    }
    buildMode = BuildMode.NONE;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.NONE);
    gameView.setStatusMessage(Messages.get("status.takeActions", current.getName()));
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
      gameView.logMessage(Messages.get("log.edge.occupied"));
      return;
    }
    if (!edgeTouches(edge, pendingSetupVertex)) {
      gameView.logMessage(Messages.get("log.road.mustconnect"));
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
    gameView.logMessage(Messages.get("log.placed.settlementroad", current.getName()));
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
      gameView.logMessage(Messages.get("log.built.road", current.getName()));
    } catch (RuntimeException e) {
      gameView.logMessage(Messages.get("log.build.retry.edge", e.getMessage()));
      return;
    }
    buildMode = BuildMode.NONE;
    gameView.getBoardView().setSelectionMode(BoardView.SelectionMode.NONE);
    gameView.setStatusMessage(Messages.get("status.takeActions", current.getName()));
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
    gameView.logMessage(Messages.get("log.setup.complete"));
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
    gameView.logMessage(Messages.get("log.rolled", current.getName(), roll));
    if (roll == ROBBER_ROLL) {
      handleRobberRoll();
    } else {
      turnFlow.resolveRoll(board, robber, roll);
    }
    phase = GamePhase.MAIN_POST_ROLL;
    gameView.setRollEnabled(false);
    gameView.setEndTurnEnabled(true);
    gameView.setBuildActionsEnabled(true);
    gameView.setStatusMessage(Messages.get("status.takeActions", current.getName()));
    refreshViews();
  }

  private void handleRobberRoll() {
    gameView.logMessage(Messages.get("log.robber.activated"));
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
        gameView.logMessage(Messages.get("log.discarded", player.getName(), count));
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
      gameView.logMessage(Messages.get("log.steal.none"));
      return;
    }
    Player victim = chooseVictim(candidates);
    if (victim != null) {
      turnFlow.stealResource(current, victim, robber, board);
      gameView.logMessage(
          Messages.get("log.stole", current.getName(), victim.getName()));
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
    dialog.setTitle(Messages.get("dialog.robber.title"));
    dialog.setHeaderText(Messages.get("dialog.robber.header"));
    dialog.setContentText(Messages.get("dialog.robber.content"));
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
    dialog.setTitle(Messages.get("dialog.steal.title"));
    dialog.setHeaderText(Messages.get("dialog.steal.header"));
    dialog.setContentText(Messages.get("dialog.steal.content"));
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
    gameView.setStatusMessage(
        Messages.get("status.click.settlement", getMainPlayer().getName()));
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
    gameView.setStatusMessage(
        Messages.get("status.click.road", getMainPlayer().getName()));
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
    gameView.setStatusMessage(
        Messages.get("status.click.city", getMainPlayer().getName()));
    refreshBoard();
  }

  private void onBuyDevCard() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    Player current = getMainPlayer();
    try {
      turnFlow.buyDevelopmentCard(current);
      gameView.logMessage(Messages.get("log.bought.devcard", current.getName()));
      refreshViews();
    } catch (Exception e) {
      gameView.logMessage(e.getMessage());
    }
  }

  private void onTrade() {
    if (phase != GamePhase.MAIN_POST_ROLL) {
      return;
    }
    Player current = getMainPlayer();
    Resource give = chooseResource(Messages.get("trade.give"));
    int rate = turnFlow.bestTradeRate(current, give, board);
    if (current.getResourceCount(give) < rate) {
      gameView.logMessage(Messages.get("log.trade.insufficient", rate,
          resourceName(give), current.getResourceCount(give)));
      return;
    }
    Resource receive = chooseResource(Messages.get("trade.receive"));
    try {
      turnFlow.maritimeTrade(give, rate, receive, board);
      gameView.logMessage(Messages.get("log.traded", current.getName(), rate,
          resourceName(give), resourceName(receive)));
      refreshViews();
    } catch (RuntimeException e) {
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
      gameView.logMessage(Messages.get("log.devcard.none"));
      return;
    }
    DevelopmentCard card = chooseChoice(Messages.get("dialog.playdev.title"),
        Messages.get("dialog.playdev.header"), playable);
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
        Resource monopolised = chooseResource(Messages.get("devcard.monopoly.prompt"));
        turnFlow.playMonopolyCard(current, monopolised);
        gameView.logMessage(Messages.get("log.devcard.monopoly",
            current.getName(), resourceName(monopolised)));
        break;
      case YEAR_OF_PLENTY:
        Resource first = chooseResource(Messages.get("devcard.yop.first"));
        Resource second = chooseResource(Messages.get("devcard.yop.second"));
        turnFlow.playYearOfPlentyCard(current, first, second);
        gameView.logMessage(Messages.get("log.devcard.yop", current.getName()));
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
      gameView.logMessage(Messages.get("log.devcard.knight", current.getName()));
      return;
    }
    Player victim = chooseVictim(candidates);
    turnFlow.stealResource(current, victim, robber, board);
    gameView.logMessage(Messages.get("log.devcard.knight.steal",
        current.getName(), victim.getName()));
  }

  private void playRoadBuilding(Player current) {
    List<Edge> legal = new ArrayList<>();
    for (Edge edge : board.getEdges()) {
      if (turnFlow.canBuildRoad(current, edge, board)) {
        legal.add(edge);
      }
    }
    if (legal.size() < 2) {
      gameView.logMessage(Messages.get("log.devcard.roadbuilding.none"));
      return;
    }
    Edge first = chooseEdge(legal);
    legal.remove(first);
    Edge second = chooseEdge(legal);
    turnFlow.playRoadBuildingCard(current, first, second, board);
    gameView.logMessage(Messages.get("log.devcard.roadbuilding", current.getName()));
  }

  private Resource chooseResource(String header) {
    List<Resource> options = new ArrayList<>();
    for (Resource resource : Resource.values()) {
      if (resource != Resource.GENERIC) {
        options.add(resource);
      }
    }
    return chooseChoice(Messages.get("dialog.devcard.title"), header, options);
  }

  private Edge chooseEdge(List<Edge> legal) {
    Map<String, Edge> options = new LinkedHashMap<>();
    for (Edge edge : legal) {
      options.put(edge.getId(), edge);
    }
    String first = options.keySet().iterator().next();
    ChoiceDialog<String> dialog = new ChoiceDialog<>(first, options.keySet());
    dialog.setTitle(Messages.get("dialog.roadbuilding.title"));
    dialog.setHeaderText(Messages.get("dialog.roadbuilding.header"));
    dialog.setContentText(Messages.get("dialog.roadbuilding.content"));
    return options.get(dialog.showAndWait().orElse(first));
  }

  private <T> T chooseChoice(String title, String header, List<T> options) {
    T first = options.get(0);
    ChoiceDialog<T> dialog = new ChoiceDialog<>(first, options);
    dialog.setTitle(title);
    dialog.setHeaderText(header);
    dialog.setContentText(Messages.get("dialog.choice.content"));
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
    alert.setTitle(Messages.get("win.title"));
    alert.setHeaderText(Messages.get("win.header", player.getName()));
    alert.setContentText(Messages.get("win.content", player.getName(), vp));
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
      gameView.getPlayerInfoView().refresh(current, turnFlow.getVictoryPoints(idx),
          turnFlow.getLongestRoadHolder() == idx,
          turnFlow.getLargestArmyHolder() == idx);
    }
    List<Player> players = game.getPlayers();
    int[] vps = new int[players.size()];
    for (int i = 0; i < players.size(); i++) {
      vps[i] = turnFlow.getVictoryPoints(i);
    }
    gameView.getPlayerInfoView().refreshSummary(players, vps,
        turnFlow.getLongestRoadHolder(), turnFlow.getLargestArmyHolder());
  }

  private void updateMainGameStatus() {
    Player current = getMainPlayer();
    gameView.setStatusMessage(Messages.get("status.turn.roll", current.getName()));
  }

  private Player getMainPlayer() {
    return game.getPlayers().get(game.getCurrentPlayerIndex());
  }

  private static String resourceName(Resource resource) {
    return Messages.get("resource." + resource.name());
  }
}
