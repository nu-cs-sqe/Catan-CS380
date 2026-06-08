package domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Player {

    private static final int STARTING_SETTLEMENTS = 5;
    private static final int STARTING_CITIES = 4;
    private static final int STARTING_ROADS = 15;
    private static final int DISCARD_THRESHOLD = 7;
    private static final int WIN_THRESHOLD = 10;
    private static final int SETTLEMENT_VP = 1;
    private static final int CITY_VP = 2;
    private static final int LONGEST_ROAD_VP = 2;
    private static final int LARGEST_ARMY_VP = 2;

    private final String name;
    private final PlayerColor color;
    private final Map<Resource, Integer> resources;
    private int remainingSettlements;
    private int remainingCities;
    private int remainingRoads;
    private int settlementsPlaced;
    private int citiesPlaced;
    private int victoryPointDevCards;
    private boolean hasLongestRoad;
    private boolean hasLargestArmy;

    public Player(String name, PlayerColor color) {
        this.name = requireNonBlank(name);
        this.color = Objects.requireNonNull(color, "color");
        this.resources = emptyResourceHand();
        this.remainingSettlements = STARTING_SETTLEMENTS;
        this.remainingCities = STARTING_CITIES;
        this.remainingRoads = STARTING_ROADS;
    }

    public String getName() {
        return name;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getVictoryPoints() {
        int vp = settlementsPlaced * SETTLEMENT_VP
                + citiesPlaced * CITY_VP
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

    public void placeSettlement() {
        if (remainingSettlements == 0) {
            throw new IllegalStateException();
        }
        settlementsPlaced++;
        remainingSettlements--;
    }

    public void placeCity() {
        if (remainingCities == 0) {
            throw new IllegalStateException();
        }
        citiesPlaced++;
        remainingCities--;
    }

    public void placeRoad() {
        if (remainingRoads == 0) {
            throw new IllegalStateException();
        }
        remainingRoads--;
    }

    public void upgradeSettlementToCity() {
        if (settlementsPlaced == 0) {
            throw new IllegalStateException();
        }
        settlementsPlaced--;
        remainingSettlements++;
        citiesPlaced++;
        remainingCities--;
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

    public int discardOnSevenCount() {
        int hand = getHandSize();
        if (hand <= DISCARD_THRESHOLD) {
            return 0;
        }
        return hand / 2;
    }

    private int getHandSize() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    private static void requireNonNegative(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
    }

    public int getRemainingSettlements() {
        return remainingSettlements;
    }

    public int getRemainingCities() {
        return remainingCities;
    }

    public int getRemainingRoads() {
        return remainingRoads;
    }

    private static String requireNonBlank(String name) {
        Objects.requireNonNull(name, "name");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return name;
    }

    private static Map<Resource, Integer> emptyResourceHand() {
        Map<Resource, Integer> hand = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            hand.put(resource, 0);
        }
        return hand;
    }

}
