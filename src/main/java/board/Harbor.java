package board;

public class Harbor {
  private final ResourceType resourceType;
  private final int exchangeRate;
  private final String vertex1Id;
  private final String vertex2Id;

  public Harbor(ResourceType resourceType, int exchangeRate, String vertex1Id, String vertex2Id) {
    this.resourceType = resourceType;
    this.exchangeRate = exchangeRate;
    this.vertex1Id = vertex1Id;
    this.vertex2Id = vertex2Id;
  }

  public ResourceType getHarborType() {
    return resourceType;
  }

  public int getExchangeRate() {
    return exchangeRate;
  }

  public String getVertex1Id() {
    return vertex1Id;
  }

  public String getVertex2Id() {
    return vertex2Id;
  }
}
