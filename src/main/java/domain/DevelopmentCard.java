package domain;

public class DevelopmentCard {

  private final DevelopmentCardType type;

  public DevelopmentCard(DevelopmentCardType type) {
    this.type = type;
  }

  public DevelopmentCardType getType() {
    return type;
  }
}