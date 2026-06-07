error id: file://<WORKSPACE>/src/main/java/domain/Player.java:java/lang/Integer#
file://<WORKSPACE>/src/main/java/domain/Player.java
empty definition using pc, found symbol in pc: java/lang/Integer#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1551
uri: file://<WORKSPACE>/src/main/java/domain/Player.java
text:
```scala
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
        this.name = requireNonBlank(name);
        this.color = Objects.requireNonNull(color, "color");
        this.resources = emptyResourceHand();
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
        Objects.requireNonNull(resource, "resource");
        return resources.get(resource);
    }

    public void addResource(Resource resource, int amount) {
        Objects.requireNonNull(resource, "resource");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        resources.merge(resource, amount, Inte@@ger::sum);
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/Integer#