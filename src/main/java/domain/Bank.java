package domain;

import board.ResourceType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Bank {

  private static final int INITIAL_STOCK = 19;
  private static final int KNIGHT_COUNT = 14;
  private static final int VICTORY_POINT_COUNT = 5;
  private static final int ACTION_CARD_COUNT = 2;

  private final Map<ResourceType, Integer> stock = new EnumMap<>(ResourceType.class);
  private final List<DevelopmentCard> deck = new ArrayList<>();

  public Bank(Consumer<List<DevelopmentCard>> shuffler) {
    Objects.requireNonNull(shuffler);
    for (ResourceType type : ResourceType.values()) {
      if (type != ResourceType.GENERIC) {
        stock.put(type, INITIAL_STOCK);
      }
    }
    initDeck(shuffler);
  }

  private void initDeck(Consumer<List<DevelopmentCard>> shuffler) {
    addCards(DevelopmentCardType.KNIGHT, KNIGHT_COUNT);
    addCards(DevelopmentCardType.VICTORY_POINT, VICTORY_POINT_COUNT);
    addCards(DevelopmentCardType.ROAD_BUILDING, ACTION_CARD_COUNT);
    addCards(DevelopmentCardType.YEAR_OF_PLENTY, ACTION_CARD_COUNT);
    addCards(DevelopmentCardType.MONOPOLY, ACTION_CARD_COUNT);
    shuffler.accept(deck);
  }

  private void addCards(DevelopmentCardType type, int count) {
    for (int i = 0; i < count; i++) {
      deck.add(new DevelopmentCard(type));
    }
  }

  public int getStock(ResourceType type) {
    Objects.requireNonNull(type);
    return stock.getOrDefault(type, 0);
  }

  public void distributeResource(ResourceType type, int amount) {
    Objects.requireNonNull(type);
    if (amount <= 0) {
      throw new IllegalArgumentException();
    }
    if (amount > stock.get(type)) {
      throw new IllegalStateException();
    }
    stock.put(type, stock.get(type) - amount);
  }

  public void returnResource(ResourceType type, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException();
    }
    stock.put(type, stock.get(type) + amount);
  }

  public boolean canDistribute(ResourceType type, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException();
    }
    return amount <= stock.get(type);
  }

  public int getDevCardCount() {
    return deck.size();
  }

  public void returnDevelopmentCard(DevelopmentCard card) {
    deck.add(card);
  }

  public DevelopmentCard drawDevelopmentCard() {
    if (deck.isEmpty()) {
      throw new IllegalStateException();
    }
    return deck.remove(deck.size() - 1);
  }
}