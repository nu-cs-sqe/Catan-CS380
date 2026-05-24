package domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Player {

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
        this.remainingSettlements = 1;
        this.remainingCities = 1;
        this.remainingRoads =  1;
    }

    public String getName() {
        return name;
    }

    public PlayerColor getColor() {
        return PlayerColor.BLUE;
    }

    public int getVictoryPoints() {
        return -1;
    }

    public int getResourceCount(Resource resource) {
        return -1;
    }

    public int getRemainingSettlements() {
        return -1;
    }

    public int getRemainingCities() {
        return -1;
    }

    public int getRemainingRoads() {
        return -1;
    }

}
