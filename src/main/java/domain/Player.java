package domain;

import board.Edge;
import board.Settlement;
import board.Vertex;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Player {

  private static final int STARTING_SETTLEMENTS = 5;
  private static final int STARTING_CITIES = 4;
  private static final int STARTING_ROADS = 15;
  private static final int DISCARD_THRESHOLD = 7;
  private static final int WIN_THRESHOLD = 10;
  private static final int LONGEST_ROAD_VP = 2;
  private static final int LARGEST_ARMY_VP = 2;

  private final String name;
  private final PlayerColor color;
  private final Map<Resource, Integer> resources;
  private final List<Settlement> settlements;
  private final List<Edge> roads;
  private int victoryPointDevCards;
  private int knightsPlayed;
  private boolean hasLongestRoad;
  private boolean hasLargestArmy;

  public Player(String name, PlayerColor color) {
    this.name = requireNonBlank(name);
    this.color = Objects.requireNonNull(color, "color");
    this.resources = emptyResourceHand();
    this.settlements = new ArrayList<>();
    this.roads = new ArrayList<>();
  }

  public Player(Player other) {
    this.name = other.name;
    this.color = other.color;
    this.resources = new EnumMap<>(other.resources);
    this.settlements = new ArrayList<>(other.settlements);
    this.roads = new ArrayList<>(other.roads);
    this.victoryPointDevCards = other.victoryPointDevCards;
    this.knightsPlayed = other.knightsPlayed;
    this.hasLongestRoad = other.hasLongestRoad;
    this.hasLargestArmy = other.hasLargestArmy;
  }

  public String getName() {
    return name;
  }

  public PlayerColor getColor() {
    return color;
  }

  public int getVictoryPoints() {
    int vp = settlements.stream().mapToInt(Settlement::getVictoryPoints).sum()
        + victoryPointDevCards;
    if (hasLongestRoad) {
      vp += LONGEST_ROAD_VP;
    }
    if (hasLargestArmy) {
      vp += LARGEST_ARMY_VP;
    }
    return vp;
  }

  public int getResourceCount(Resource resource) {
    Objects.requireNonNull(resource, "resource");
    return resources.get(resource);
  }

  public void addResource(Resource resource, int amount) {
    Objects.requireNonNull(resource, "resource");
    requireNonNegative(amount);
    resources.merge(resource, amount, Integer::sum);
  }

  public void removeResource(Resource resource, int amount) {
    requireNonNegative(amount);
    if (resources.get(resource) < amount) {
      throw new IllegalStateException("insufficient " + resource);
    }
    resources.merge(resource, -amount, Integer::sum);
  }

  public boolean hasResources(Map<Resource, Integer> cost) {
    return cost.entrySet().stream()
        .allMatch(entry -> resources.get(entry.getKey()) >= entry.getValue());
  }

  public boolean hasWon() {
    return getVictoryPoints() >= WIN_THRESHOLD;
  }

  public void placeSettlement(Vertex v) {
    Objects.requireNonNull(v, "vertex");
    if (v.getSettlement() != null) {
      throw new IllegalStateException("vertex already occupied");
    }
    if (getRemainingSettlements() == 0) {
      throw new IllegalStateException("no settlement pieces remaining");
    }
    Settlement s = new Settlement();
    settlements.add(s);
    v.setSettlement(s);
  }

  public void upgradeSettlementToCity(Vertex v) {
    Objects.requireNonNull(v, "vertex");
    Settlement s = v.getSettlement();
    if (s == null || !settlements.contains(s)) {
      throw new IllegalStateException("no player settlement at vertex");
    }
    if (getRemainingCities() == 0) {
      throw new IllegalStateException("no city pieces remaining");
    }
    s.upgrade();
  }

  public void placeRoad(Edge e) {
    if (getRemainingRoads() == 0) {
      throw new IllegalStateException("no road pieces remaining");
    }
    roads.add(e);
    e.setOwner(this);
  }

  public void awardLongestRoad() {
    hasLongestRoad = true;
  }

  public void revokeLongestRoad() {
    hasLongestRoad = false;
  }

  public void awardLargestArmy() {
    hasLargestArmy = true;
  }

  public void addVictoryPointDevCard() {
    victoryPointDevCards++;
  }

  public void playKnight() {
    knightsPlayed++;
  }

  public int getKnightsPlayed() {
    return knightsPlayed;
  }

  public int discardOnSevenCount() {
    int hand = getHandSize();
    if (hand <= DISCARD_THRESHOLD) {
      return 0;
    }
    return hand / 2;
  }

  public int getRemainingSettlements() {
    long placed = settlements.stream().filter(s -> !s.isCity()).count();
    return STARTING_SETTLEMENTS - (int) placed;
  }

  public int getRemainingCities() {
    long placed = settlements.stream().filter(Settlement::isCity).count();
    return STARTING_CITIES - (int) placed;
  }

  public int getRemainingRoads() {
    return STARTING_ROADS - roads.size();
  }

  private int getHandSize() {
    return resources.values().stream().mapToInt(Integer::intValue).sum();
  }

  private static void requireNonNegative(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("amount must be non-negative");
    }
  }

  private static String requireNonBlank(String name) {
    Objects.requireNonNull(name, "name");
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player)) {
      return false;
    }
    Player other = (Player) o;
    return name.equals(other.name) && color == other.color;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, color);
  }

  private static Map<Resource, Integer> emptyResourceHand() {
    Map<Resource, Integer> hand = new EnumMap<>(Resource.class);
    for (Resource resource : Resource.values()) {
      hand.put(resource, 0);
    }
    return hand;
  }
}
