import java.io.Serializable;

public class Pair<L, R> implements Serializable {
    // Node class의 p 배열에 담길 Pair Class

    public L first;
    public R second;

    public Pair(L first, R second) {
        this.first = first;
        this.second = second;
    }
}