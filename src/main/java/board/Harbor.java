package board;

import domain.Resource;

public class Harbor {
  private final Resource rsrc;
  private final int exchangeRate;
  private final String vertex1Id;
  private final String vertex2Id;

  public Harbor(Resource rsrc, int exchangeRate, String vertex1Id, String vertex2Id) {
    this.rsrc = rsrc;
    this.exchangeRate = exchangeRate;
    this.vertex1Id = vertex1Id;
    this.vertex2Id = vertex2Id;
  }

  public Resource getHarborType() {
    return rsrc;
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
