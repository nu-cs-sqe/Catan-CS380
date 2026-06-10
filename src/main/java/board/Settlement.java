package board;

public class Settlement {

    private boolean city = false;

    public void upgrade() {
        if (city) {
            throw new IllegalStateException("already a city");
        }
        city = true;
    }

    public boolean isCity() {
        return city;
    }

    public int getVictoryPoints() {
        return 1;
    }
}