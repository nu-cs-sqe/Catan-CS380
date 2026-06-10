package domain;

import board.ResourceType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Bank {
  private static final int INITIAL_STOCK = 19;
  private static final int INITIAL_DEV_CARD_COUNT = 25;
  private final Map<ResourceType, Integer> stock = new EnumMap<>(ResourceType.class);
  private int devCardCount = INITIAL_DEV_CARD_COUNT;

  public Bank(Consumer<List<ResourceType>> notifier) {
    for (ResourceType type : ResourceType.values()) {
      if (type != ResourceType.GENERIC) {
        stock.put(type, INITIAL_STOCK);
      }
    }
  }

  public int getStock(ResourceType type) {
    return stock.getOrDefault(type, 0);
  }

  public int getDevCardCount() {
    return devCardCount;
  }
}
