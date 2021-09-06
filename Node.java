import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {
    // B+ Tree¿« Node Class

    public boolean isLeaf; // If this node is Leaf, then true
    public int m; // # of Keys
    public Node r; // Reference variable to the rightmost child node
    public ArrayList<Pair<Integer, Object>> p; // An array of <key, left_child_node> pairs

    public Node() {
        isLeaf = true;
        m = 0;
        r = null;
        p = new ArrayList<>();
    }
}
