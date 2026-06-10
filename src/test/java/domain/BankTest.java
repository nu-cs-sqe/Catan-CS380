package domain;

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
    assertEquals(INITIAL_STOCK, bank.getStock(Resource.WOOD));
    assertEquals(INITIAL_STOCK, bank.getStock(Resource.BRICK));
    assertEquals(INITIAL_STOCK, bank.getStock(Resource.SHEEP));
    assertEquals(INITIAL_STOCK, bank.getStock(Resource.WHEAT));
    assertEquals(INITIAL_STOCK, bank.getStock(Resource.ORE));
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
    bank.distributeResource(Resource.WOOD, 1);
    assertEquals(INITIAL_STOCK - 1, bank.getStock(Resource.WOOD));
  }

  // TC7 - Distributing exactly all stock reduces stock to zero (boundary)
  @Test
  public void distributingAllStockReducesStockToZero() {
    Bank bank = new Bank(list -> {});
    bank.distributeResource(Resource.WOOD, INITIAL_STOCK);
    assertEquals(0, bank.getStock(Resource.WOOD));
  }

  // TC8 - Distributing one more than stock throws IllegalStateException (boundary)
  @Test
  public void distributingMoreThanStockThrowsIllegalStateException() {
    Bank bank = new Bank(list -> {});
    assertThrows(
        IllegalStateException.class,
        () -> bank.distributeResource(Resource.WOOD, INITIAL_STOCK + 1));
  }

  // TC9 - Zero amount in distributeResource throws IllegalArgumentException
  @Test
  public void zeroAmountInDistributeThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class, () -> bank.distributeResource(Resource.WOOD, 0));
  }

  // TC10 - Negative amount in distributeResource throws IllegalArgumentException
  @Test
  public void negativeAmountInDistributeThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class, () -> bank.distributeResource(Resource.WOOD, -1));
  }

  // TC11 - Null resource in distributeResource throws NullPointerException
  @Test
  public void nullResourceInDistributeThrowsNullPointerException() {
    Bank bank = new Bank(list -> {});
    assertThrows(NullPointerException.class, () -> bank.distributeResource(null, 1));
  }

  // TC12 - canDistribute returns true when stock is sufficient
  @Test
  public void canDistributeReturnsTrueWhenStockIsSufficient() {
    Bank bank = new Bank(list -> {});
    assertTrue(bank.canDistribute(Resource.WOOD, 1));
  }

  // TC13 - canDistribute returns true when amount equals stock exactly (boundary)
  @Test
  public void canDistributeReturnsTrueWhenAmountEqualsStock() {
    Bank bank = new Bank(list -> {});
    assertTrue(bank.canDistribute(Resource.WOOD, INITIAL_STOCK));
  }

  // TC14 - canDistribute returns false when amount exceeds stock by 1 (boundary)
  @Test
  public void canDistributeReturnsFalseWhenAmountExceedsStock() {
    Bank bank = new Bank(list -> {});
    assertFalse(bank.canDistribute(Resource.WOOD, INITIAL_STOCK + 1));
  }

  // TC15 - Zero amount in canDistribute throws IllegalArgumentException
  @Test
  public void zeroAmountInCanDistributeThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class, () -> bank.canDistribute(Resource.WOOD, 0));
  }

  // TC16 - Returning 1 increases stock by 1
  @Test
  public void returningOneIncreasesStockByOne() {
    Bank bank = new Bank(list -> {});
    bank.distributeResource(Resource.WOOD, 1);
    bank.returnResource(Resource.WOOD, 1);
    assertEquals(INITIAL_STOCK, bank.getStock(Resource.WOOD));
  }

  // TC17 - Zero amount in returnResource throws IllegalArgumentException
  @Test
  public void zeroAmountInReturnResourceThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class, () -> bank.returnResource(Resource.WOOD, 0));
  }

  // TC18 - Negative amount in returnResource throws IllegalArgumentException
  @Test
  public void negativeAmountInReturnResourceThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(IllegalArgumentException.class, () -> bank.returnResource(Resource.WOOD, -1));
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

  // TC21 - Drawing from empty deck throws IllegalStateException (boundary)
  @Test
  public void drawingFromEmptyDeckThrowsIllegalStateException() {
    Bank bank = new Bank(list -> {});
    for (int i = 0; i < INITIAL_DEV_CARD_COUNT; i++) {
      bank.drawDevelopmentCard();
    }
    assertThrows(IllegalStateException.class, bank::drawDevelopmentCard);
  }

  // TC22 - Returning a KNIGHT card increases deck count
  @Test
  public void returningKnightCardIncreasesDeckCount() {
    Bank bank = new Bank(list -> {});
    int before = bank.getDevCardCount();
    bank.returnDevelopmentCard(new DevelopmentCard(DevelopmentCardType.KNIGHT));
    assertEquals(before + 1, bank.getDevCardCount());
  }

  // TC23 - Returning a ROAD_BUILDING card increases deck count
  @Test
  public void returningRoadBuildingCardIncreasesDeckCount() {
    Bank bank = new Bank(list -> {});
    int before = bank.getDevCardCount();
    bank.returnDevelopmentCard(new DevelopmentCard(DevelopmentCardType.ROAD_BUILDING));
    assertEquals(before + 1, bank.getDevCardCount());
  }

  // TC24 - Returning a YEAR_OF_PLENTY card increases deck count
  @Test
  public void returningYearOfPlentyCardIncreasesDeckCount() {
    Bank bank = new Bank(list -> {});
    int before = bank.getDevCardCount();
    bank.returnDevelopmentCard(new DevelopmentCard(DevelopmentCardType.YEAR_OF_PLENTY));
    assertEquals(before + 1, bank.getDevCardCount());
  }

  // TC25 - Returning a MONOPOLY card increases deck count
  @Test
  public void returningMonopolyCardIncreasesDeckCount() {
    Bank bank = new Bank(list -> {});
    int before = bank.getDevCardCount();
    bank.returnDevelopmentCard(new DevelopmentCard(DevelopmentCardType.MONOPOLY));
    assertEquals(before + 1, bank.getDevCardCount());
  }

  // TC26 - Returning a VICTORY_POINT card throws IllegalArgumentException
  @Test
  public void returningVictoryPointCardThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(
        IllegalArgumentException.class,
        () -> bank.returnDevelopmentCard(new DevelopmentCard(DevelopmentCardType.VICTORY_POINT)));
  }

  // TC27 - Null card in returnDevelopmentCard throws NullPointerException
  @Test
  public void nullCardInReturnDevelopmentCardThrowsNullPointerException() {
    Bank bank = new Bank(list -> {});
    assertThrows(NullPointerException.class, () -> bank.returnDevelopmentCard(null));
  }

  // TC28 - Rate 1 throws IllegalArgumentException (boundary below valid)
  @Test
  public void maritimeTradeRateOneBelowMinThrowsIllegalArgumentException() {
    Bank bank = new Bank(list -> {});
    assertThrows(
        IllegalArgumentException.class,
        () -> bank.maritimeTrade(Resource.WOOD, 1, Resource.BRICK));
  }

  // TC29 - Rate 2 succeeds (boundary — minimum valid)
  @Test
  public void maritimeTradeRateTwoSucceeds() {
    Bank bank = new Bank(list -> {});
    int giveBefore = bank.getStock(Resource.WOOD);
    int receiveBefore = bank.getStock(Resource.BRICK);
    bank.maritimeTrade(Resource.WOOD, 2, Resource.BRICK);
    assertEquals(giveBefore + 2, bank.getStock(Resource.WOOD));
    assertEquals(receiveBefore - 1, bank.getStock(Resource.BRICK));
  }
}
