package board;

import java.util.List;

public interface Shuffler {
    <T> void shuffle(List<T> list);
}