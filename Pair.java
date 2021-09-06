import java.io.Serializable;

public class Pair<L, R> implements Serializable {
    // Node class�� p �迭�� ��� Pair Class

    public L first;
    public R second;

    public Pair(L first, R second) {
        this.first = first;
        this.second = second;
    }
}