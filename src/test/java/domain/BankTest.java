package domain;

import board.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BankTest {

  private static final int INITIAL_STOCK = 19;
  private static final int INITIAL_DEV_CARD_COUNT = 25;
  private static final int KNIGHT_COUNT = 14;
  private static final int VICTORY_POINT_COUNT = 5;
  private static final int ACTION_CARD_COUNT = 2;

  // TC1 - Initial resource stock is 19 per resource
  @Test
  public void initialStockIs19ForEachResource() {
    Bank bank = new Bank(list -> {});
    assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.WOOD));
    assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.BRICK));
    assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.SHEEP));
    assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.WHEAT));
    assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.ORE));
  }

  // TC2 - Initial development card count is 25
  @Test
  public void initialDevCardCountIs25() {
    Bank bank = new Bank(list -> {});
    assertEquals(INITIAL_DEV_CARD_COUNT, bank.getDevCardCount());
  }

  // TC3 - Initial deck composition is correct
  @Test
  public void initialDeckCompositionIsCorrect() {
    Bank bank = new Bank(list -> {});
    Map<DevelopmentCardType, Integer> counts = new EnumMap<>(DevelopmentCardType.class);
    for (int i = 0; i < INITIAL_DEV_CARD_COUNT; i++) {
      DevelopmentCardType type = bank.drawDevelopmentCard().getType();
      counts.merge(type, 1, Integer::sum);
    }
    assertEquals(KNIGHT_COUNT, counts.get(DevelopmentCardType.KNIGHT));
    assertEquals(VICTORY_POINT_COUNT, counts.get(DevelopmentCardType.VICTORY_POINT));
    assertEquals(ACTION_CARD_COUNT, counts.get(DevelopmentCardType.ROAD_BUILDING));
    assertEquals(ACTION_CARD_COUNT, counts.get(DevelopmentCardType.YEAR_OF_PLENTY));
    assertEquals(ACTION_CARD_COUNT, counts.get(DevelopmentCardType.MONOPOLY));
  }

  // TC4 - Null shuffler throws NullPointerException
  @Test
  public void nullShufflerThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new Bank(null));
  }

  // TC5 - Null resource in getStock throws NullPointerException
  @Test
  public void nullResourceInGetStockThrowsNullPointerException() {
    Bank bank = new Bank(list -> {});
    assertThrows(NullPointerException.class, () -> bank.getStock(null));
  }

  // TC6 - Distributing 1 reduces stock by 1
  @Test
  public void distributingOneReducesStockByOne() {
    Bank bank = new Bank(list -> {});
    bank.distributeResource(ResourceType.WOOD, 1);
    assertEquals(INITIAL_STOCK - 1, bank.getStock(ResourceType.WOOD));
  }

  // TC7 - Distributing exactly all stock reduces stock to zero (boundary)
  @Test
  public void distributingAllStockReducesStockToZero() {
    Bank bank = new Bank(list -> {});
    bank.distributeResource(ResourceType.WOOD, INITIAL_STOCK);
    assertEquals(0, bank.getStock(ResourceType.WOOD));
  }

  // TC8 - Distributing one more than stock throws IllegalStateException (boundary)
  @Test
  public void distributingMoreThanStockThrowsIllegalStateException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalStateException.class,
        () -> bank.distributeResource(ResourceType.WOOD, INITIAL_STOCK + 1));
  }

  // TC9 - Zero amount in distributeResource throws IllegalArgumentException
  @Test
  public void zeroAmountInDistributeThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class,
        () -> bank.distributeResource(ResourceType.WOOD, 0));
  }

  // TC10 - Negative amount in distributeResource throws IllegalArgumentException
  @Test
  public void negativeAmountInDistributeThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class,
        () -> bank.distributeResource(ResourceType.WOOD, -1));
  }

  // TC11 - Null resource in distributeResource throws NullPointerException
  @Test
  public void nullResourceInDistributeThrowsNullPointerException() {
    Bank bank = new Bank(list -> {});
    assertThrows(NullPointerException.class,
        () -> bank.distributeResource(null, 1));
  }

  // TC12 - canDistribute returns true when stock is sufficient
  @Test
  public void canDistributeReturnsTrueWhenStockIsSufficient() {
    Bank bank = new Bank(list -> {});
    assertTrue(bank.canDistribute(ResourceType.WOOD, 1));
  }

  // TC13 - canDistribute returns true when amount equals stock exactly (boundary)
  @Test
  public void canDistributeReturnsTrueWhenAmountEqualsStock() {
    Bank bank = new Bank(list -> {});
    assertTrue(bank.canDistribute(ResourceType.WOOD, INITIAL_STOCK));
  }

  // TC14 - canDistribute returns false when amount exceeds stock by 1 (boundary)
  @Test
  public void canDistributeReturnsFalseWhenAmountExceedsStock() {
    Bank bank = new Bank(list -> {});
    assertFalse(bank.canDistribute(ResourceType.WOOD, INITIAL_STOCK + 1));
  }

  // TC15 - Zero amount in canDistribute throws IllegalArgumentException
  @Test
  public void zeroAmountInCanDistributeThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class,
        () -> bank.canDistribute(ResourceType.WOOD, 0));
  }

  // TC16 - Returning 1 increases stock by 1
  @Test
  public void returningOneIncreasesStockByOne() {
    Bank bank = new Bank(list -> {});
    bank.distributeResource(ResourceType.WOOD, 1);
    bank.returnResource(ResourceType.WOOD, 1);
    assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.WOOD));
  }

  // TC17 - Zero amount in returnResource throws IllegalArgumentException
  @Test
  public void zeroAmountInReturnResourceThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class,
        () -> bank.returnResource(ResourceType.WOOD, 0));
  }

  // TC18 - Negative amount in returnResource throws IllegalArgumentException
  @Test
  public void negativeAmountInReturnResourceThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class,
        () -> bank.returnResource(ResourceType.WOOD, -1));
  }

  // TC19 - Drawing a card reduces deck count by 1
  @Test
  public void drawingCardReducesDeckCountByOne() {
    Bank bank = new Bank(list -> {});
    bank.drawDevelopmentCard();
    assertEquals(INITIAL_DEV_CARD_COUNT - 1, bank.getDevCardCount());
  }

  // TC20 - Drawing last card succeeds and deck count becomes 0 (boundary)
  @Test
  public void drawingLastCardSucceedsAndDeckBecomesEmpty() {
    Bank bank = new Bank(list -> {});
    for (int i = 0; i < INITIAL_DEV_CARD_COUNT - 1; i++) {
      bank.drawDevelopmentCard();
    }
    bank.drawDevelopmentCard();
    assertEquals(0, bank.getDevCardCount());
  }
}
