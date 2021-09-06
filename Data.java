import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {
    public int m; // Max # of child nodes
    public Node root; // Root node of B+ Tree
    public ArrayList<Node> list; // Array for linked list

    public Data(int m, Node root, ArrayList<Node> list) {
        this.m = m;
        this.root = root;
        this.list = list;
    }

    public Data() { }
}
