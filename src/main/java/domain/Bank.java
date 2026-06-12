package domain;

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
  private static final int MIN_TRADE_RATE = 2;
  private static final int MAX_TRADE_RATE = 4;

  private final Map<Resource, Integer> stock;
  private final List<DevelopmentCard> deck;

  public Bank(Consumer<List<DevelopmentCard>> shuffler) {
    this.stock = new EnumMap<>(Resource.class);
    this.deck = new ArrayList<>();

    Objects.requireNonNull(shuffler);
    for (Resource type : Resource.values()) {
      if (type != Resource.GENERIC) {
        stock.put(type, INITIAL_STOCK);
      }
    }
    initDeck(shuffler);
  }

  private void initDeck(Consumer<List<DevelopmentCard>> shuffler) {
    addCards(DevelopmentCard.KNIGHT, KNIGHT_COUNT);
    addCards(DevelopmentCard.VICTORY_POINT, VICTORY_POINT_COUNT);
    addCards(DevelopmentCard.ROAD_BUILDING, ACTION_CARD_COUNT);
    addCards(DevelopmentCard.YEAR_OF_PLENTY, ACTION_CARD_COUNT);
    addCards(DevelopmentCard.MONOPOLY, ACTION_CARD_COUNT);
    shuffler.accept(deck);
  }

  private void addCards(DevelopmentCard type, int count) {
    for (int i = 0; i < count; i++) {
      deck.add(type);
    }
  }

  public int getStock(Resource type) {
    Objects.requireNonNull(type);
    return stock.getOrDefault(type, 0);
  }

  public void distributeResource(Resource type, int amount) {
    Objects.requireNonNull(type);
    if (amount <= 0) {
      throw new IllegalArgumentException();
    }
    if (amount > stock.get(type)) {
      throw new IllegalStateException();
    }
    stock.put(type, stock.get(type) - amount);
  }

  public void returnResource(Resource type, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException();
    }
    stock.put(type, stock.get(type) + amount);
  }

  public boolean canDistribute(Resource type, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException();
    }
    return amount <= stock.get(type);
  }

  public int getDevCardCount() {
    return deck.size();
  }

  public void returnDevelopmentCard(DevelopmentCard card) {
    Objects.requireNonNull(card);
    if (card == DevelopmentCard.VICTORY_POINT) {
      throw new IllegalArgumentException();
    }
    deck.add(card);
  }

  public DevelopmentCard drawDevelopmentCard() {
    if (deck.isEmpty()) {
      throw new IllegalStateException();
    }
    return deck.remove(deck.size() - 1);
  }

  public void maritimeTrade(Resource give, int giveCount, Resource receive) {
    Objects.requireNonNull(give);
    Objects.requireNonNull(receive);
    if (giveCount < MIN_TRADE_RATE || giveCount > MAX_TRADE_RATE) {
      throw new IllegalArgumentException();
    }
    if (give == receive) {
      throw new IllegalArgumentException();
    }
    if (!canDistribute(receive, 1)) {
      throw new IllegalStateException();
    }
    stock.put(give, stock.get(give) + giveCount);
    stock.put(receive, stock.get(receive) - 1);
  }
}
