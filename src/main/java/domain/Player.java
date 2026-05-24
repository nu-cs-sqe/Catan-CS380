package domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Player {

    private static final int STARTING_SETTLEMENTS = 5;
    private static final int STARTING_CITIES = 4;
    private static final int STARTING_ROADS = 15;

    private final String name;
    private final PlayerColor color;
    private final Map<Resource, Integer> resources;
    private int victoryPoints;
    private int remainingSettlements;
    private int remainingCities;
    private int remainingRoads;

    public Player(String name, PlayerColor color) {
        this.name = name;
        this.color = Objects.requireNonNull(color, "color");
        this.resources = Map.of();
        this.victoryPoints = 0;
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
        return victoryPoints;
    }

    public int getResourceCount(Resource resource) {
        return resources.get(resource);
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

}
