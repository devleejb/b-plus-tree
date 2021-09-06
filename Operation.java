import java.io.*;
import java.util.*;

public class Operation {
    public static int m; // Max # of child nodes
    public static int UNDERFLOW; // Underflow�� ����

    static public void create(String index_file, String m) {
        // Command Line Argument�� ���� �Է¹��� data�� ����� ���ο� index file�� ������.

        FileOutputStream fos; // ��ü�� ���Ͽ� ��� ���� ��Ʈ�� ����
        ObjectOutputStream oos;
        Data data = new Data(Integer.parseInt(m), new Node(), new ArrayList<>()); // ���Ͽ� ��� Data Class ��ü

        try {
            fos = new FileOutputStream(index_file);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(data);

            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void insert(String index_file, String data_file) {
        // Command Line Argument�� ���� �Է¹��� data�� �̿��� B+ Tree�� data�� insertion �޼ҵ带 �̿��Ͽ� �����ϰ�, index_file�� ������.

        Data data; // index file���� �о�� ��ü�� �����ϱ� ���� ���� ����
        Node root; // B+ Tree�� Root Node
        Scanner sc = null; // data file���� data�� �о���� ���� Scanner ���� ����
        StringTokenizer st; // data file���� key�� value�� �����ϱ� ���� StringTokenizer ���� ����

        try {
            sc = new Scanner(new File(data_file));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        data = buildTree(index_file);

        m = data.m; // Data ��ü �ȿ� �ִ� data�� ������ ������.
        root = data.root;

        while (sc.hasNextLine()) {
            // data file���� data�� �Է¹޾� B+ Tree�� insert��.

            st = new StringTokenizer(sc.nextLine(), ",");

            insertion(root, Integer.parseInt(st.nextToken().trim()), Integer.parseInt(st.nextToken().trim()));

            if (root.m == m) {
                // Root Node���� Overflow�� �߻��� ��� rootSplit �޼ҵ带 �̿���.

                root = rootSplit(root);
            }
        }

        saveTree(index_file, m, root);
    }

    static public void insertion(Node node, int key, int value) {
        // ������ B+ Tree�� key�� ���ԵǴ� ������ ��������� ������.

        if (node.isLeaf) {
            // ���ڷ� ���� node�� Leaf Node�� ���, �ش� Node�� key�� value�� ������.

            Pair<Integer, Object> pair = new Pair<>(key, value); // Leaf Node�� �����ϱ� ���� Pair Class ��ü ����

            node.p.add(pair);
            Collections.sort(node.p, new Ascending()); // Leaf Node�� key�� �������� ������.
            node.m++;
        } else {
            // ���ڷ� ���� node�� Leaf Node�� �ƴ� ���

            for (int i = 0; i < node.p.size(); ++i) {
                if(key < node.p.get(i).first) {
                    // key�� value�� ���ԵǱ� ������ Child Node�� ã�� insertion �޼ҵ带 ȣ����.

                    insertion((Node)node.p.get(i).second, key, value);

                    if(((Node)node.p.get(i).second).m == m) {
                        // key�� value�� insert �� Child Node���� Overflow�� �߻��� ���

                        node.p.add(i, new Pair<>(((Node)node.p.get(i).second).p.get(m / 2).first, node.p.get(i).second)); // Child Node���� key�� ���� �ø���, Child Node�� �����ϰ� �̾���.
                        node.m++;
                        node.p.get(i + 1).second = new Node(); // Overflow�� �߻��� ��尡 Split�Ǿ� ��������� ���ο� ��� ��ü ����
                        ((Node)node.p.get(i + 1).second).isLeaf = ((Node)node.p.get(i).second).isLeaf; // �ٲ�

                        for (int j = m / 2; j < m; ++j) {
                            // Overflow�� �߻��� Child Node�� ���� ���ο� ���� ���� �����ϰ� �ű�(Split).

                            if (j == m / 2 && !((Node) node.p.get(i).second).isLeaf) {
                                // non-leaf node�� key�� �θ� ��忡 �ߺ� ������ �ʿ䰡 ������ ������.

                                ((Node) node.p.get(i + 1).second).r = ((Node) node.p.get(i).second).r;
                                ((Node) node.p.get(i).second).r = (Node) ((Node) node.p.get(i).second).p.get(m / 2).second;
                                ((Node) node.p.get(i).second).p.remove(m / 2);
                                ((Node) node.p.get(i).second).m--;
                            } else {
                                ((Node) node.p.get(i + 1).second).p.add(((Node) node.p.get(i).second).p.get(m / 2));
                                ((Node) node.p.get(i).second).p.remove(m / 2);
                                ((Node) node.p.get(i + 1).second).m++;
                                ((Node) node.p.get(i).second).m--;
                            }
                        }

                        if(node.r.isLeaf) {
                            // Leaf Mode�� Linked List�� ���� ����

                            ((Node) node.p.get(i + 1).second).r = ((Node) node.p.get(i).second).r;
                            ((Node) node.p.get(i).second).r = ((Node) node.p.get(i + 1).second);
                        }
                    }
                    break;
                }
            }

            if(node.p.get(node.p.size() - 1).first <= key) {
                // key�� value�� ���ԵǱ⿡ Node�� Rightmost Child Node�� ������ ���

                insertion(node.r, key, value);

                if(node.r.m == m) {
                    // key�� value�� insert �� Child Node���� Overflow�� �߻��� ���

                    node.p.add(new Pair<>(node.r.p.get(m / 2).first, node.r)); // Child Node���� key�� ���� �ø���, Child Node�� �����ϰ� �̾���.
                    node.m++;
                    node.r = new Node(); // Split�Ǿ� ��������� ���ο� ���
                    node.r.isLeaf = ((Node)node.p.get(node.p.size() - 1).second).isLeaf;

                    for (int j = m / 2; j < m; ++j) {
                        // Overflow�� �߻��� Child Node�� ���� ���ο� ���� ���� �����ϰ� �ű�(Split).

                        if (j == m / 2 && !node.r.isLeaf) {
                            // non-leaf node�� key�� �θ� ��忡 �ߺ� ������ �ʿ䰡 ������ ������.

                            node.r.r = ((Node) node.p.get(node.p.size() - 1).second).r;
                            ((Node) node.p.get(node.p.size() - 1).second).r = ((Node) ((Node) node.p.get(node.p.size() - 1).second).p.get(m / 2).second);
                            ((Node) node.p.get(node.p.size() - 1).second).p.remove(m / 2);
                            ((Node) node.p.get(node.p.size() - 1).second).m--;
                        } else {
                            node.r.p.add(((Node)node.p.get(node.p.size() - 1).second).p.get(m / 2));
                            ((Node)node.p.get(node.p.size() - 1).second).p.remove(m / 2);
                            node.r.m++;
                            ((Node) node.p.get(node.p.size() - 1).second).m--;
                        }
                    }

                    if(node.r.isLeaf) {
                        // Leaf Mode�� Linked List�� ���� ����

                        node.r.r = ((Node)node.p.get(node.p.size() - 1).second).r;
                        ((Node)node.p.get(node.p.size() - 1).second).r = node.r;
                    }
                }
            }
        }
    }

    static public Node rootSplit(Node root) {
        // Root Node���� Overflow�� �߻��� ��� Root Node�� �� ���� Node�� ������.

        Node newRoot = new Node(); // �� root�� �� Node ��ü ����

        newRoot.r = new Node(); // �� Node�� Rightmost Child�� �����ϰ� ������.
        newRoot.r.r = root.r;
        newRoot.p.add(new Pair<>(root.p.get(m / 2).first, root)); // ���� Root Node���� key�� ���� �ø���, Child Node�� �����ϰ� �̾���.
        newRoot.m++;
        newRoot.isLeaf = false;
        newRoot.r.isLeaf = root.isLeaf;

        for (int i = m / 2; i < m; ++i) {
            // Overflow�� �߻��� ���� Root Node�� ���� ���ο� ���� ���� �����ϰ� �ű�(Split).

            if (i == m / 2 && !newRoot.r.isLeaf) {
                // non-leaf node�� key�� �θ� ��忡 �ߺ� ������ �ʿ䰡 ������ ������.

                root.r = (Node)root.p.get(i).second;
                root.p.remove(i);
                root.m--;
            } else {
                newRoot.r.p.add(root.p.get(m / 2));
                root.p.remove(m / 2);
                newRoot.r.m++;
                root.m--;
            }
        }

        if(root.isLeaf) {
            // Leaf Mode�� Linked List�� ���� ����

            newRoot.r.r =  ((Node) newRoot.p.get(0).second).r;
            ((Node) newRoot.p.get(0).second).r = newRoot.r;
        }

        return newRoot;
    }

    static public void delete(String index_file, String data_file) {
        // index file�� ����ִ� B+ Tree���� ���ڷ� ���� data_file�� �����ϴ� key�� �����ϵ��� ��ü�� �о���� ������ �޼ҵ带 ȣ����.

        Data data;
        Node root;
        Scanner sc = null;

        data = buildTree(index_file);

        m = data.m;
        UNDERFLOW = (int) Math.floor(((double)m - 1) / 2);
        root = data.root;

        try {
            sc = new Scanner(new File(data_file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (sc.hasNextLine()) {
            deletion(root, Integer.parseInt(sc.nextLine()));

            if (root.m == 0) {
                // root Node���� Underflow�� �߻���.

                root = root.r;
            }
        }

        saveTree(index_file, m, root);
    }

    static public void singleSearch(String index_file, int key) {
        // B+ Tree���� Ư�� key�� value�� ã�� ����ϸ�, value�� ã�ư��� ��ο� �����ϴ� Node�� ��� key�� �����.

        Data data;
        Node node;
        int idx;
        String keys = "";

        data = buildTree(index_file);

        node = data.root;

        while (!node.isLeaf) {
            // ã������ key�� �����ִ� Node�� ã��.

            for(int i = 0; i < node.p.size(); ++i) {
                // ���İ��� Node�� ��� key�� �� �ٿ� �����.

                keys += node.p.get(i).first;

                if(i != node.p.size() - 1) {
                    keys += ", ";
                }
            }

            keys += "\n";

            for (int i = 0; i < node.p.size(); ++i) {
                // ������ ��� Ž��

                if (node.p.get(i).first > key) {
                    node = (Node) node.p.get(i).second;

                    break;
                } else if (i == node.p.size() - 1) {
                    node = node.r;

                    break;
                }
            }
        }

        for(idx = 0; idx < node.p.size(); ++idx) {
            // ã������ key�� index�� ã�� value�� �����.

            if (node.p.get(idx).first == key) {
                System.out.println(keys + node.p.get(idx).second.toString());

                break;
            }
        }

        if (idx == node.p.size()) {
            // ã������ key�� ã�� ���� ��� Not Found�� �����.

            System.out.println(keys + "Not Found");
        }
    }

    static public void rangedSearch(String index_file, int start_key, int end_key) {
        // B+ Tree���� start_key�� index_key ���̿� �����ϴ� ��� key�� value�� �����.

        Data data; // index file���� �о�� ��ü�� ���� ���� ����
        int idx = 0; // ������ key�� value�� ����� ���� ���� index�� ��� ���� ���� ����
        Node start_node; // ����� ����� �� ù��° Node
        Node end_node; // ����� ����� �� ������ Node

        data = buildTree(index_file);

        start_node = end_node = data.root;

        while (!start_node.isLeaf) {
            // B+ Tree���� start_key���� ũ�ų� ���� key �� ���� ���� key�� ����ִ� Leaf Node�� ã��.

            for (int i = 0; i < start_node.p.size(); ++i) {
                if (start_node.p.get(i).first > start_key) {
                    start_node = (Node) start_node.p.get(i).second;

                    break;
                } else if (i == start_node.p.size() - 1 && start_node.p.get(i).first <= start_key) {
                    start_node = start_node.r;

                    break;
                }
            }
        }

        while (!end_node.isLeaf) {
            // B+ Tree���� end_key���� �۰ų� ���� key �� ���� ū key�� ����ִ� Leaf Node�� ã��.

            for (int i = 0; i < end_node.p.size(); ++i) {
                if (end_node.p.get(i).first > end_key) {
                    end_node = (Node) end_node.p.get(i).second;

                    break;
                } else if (i == end_node.p.size() - 1 && end_node.p.get(i).first <= end_key) {
                    end_node = end_node.r;

                    break;
                }
            }
        }

        if (start_node.p.size() != 0) {
            for (; start_node.p.get(idx).first < start_key; ++idx) {
                // leaf node���� star_key���� ū �� �� ���� ���� ���� ã��.

                if (start_node.p.get(idx).first > end_key) {
                    // �ش� Node���� end_key���� ū ���� �߰ߵȴٸ� �ش� ������ �����ϴ� key�� value�� ���� ���̹Ƿ� ���ܸ� �߻���Ű�� ���α׷��� ������.

                    try {
                        throw new Exception("No keys and values in that condition!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        while (true) {
            if(start_node != end_node) {
                // start_node�� end_node�� �ٸ��ٸ� Node�ȿ� �����ϴ� key�� value�� ��� �����.(start_node�� ��� �ش�Ǵ� key�� value�� �����.)

                for (; idx < start_node.p.size(); ++idx) {
                    System.out.println(start_node.p.get(idx).first + ", " + start_node.p.get(idx).second);
                }

                idx = 0;
                start_node = start_node.r;
            } else {
                // start_node�� end_node�� ���ٸ� ��� ������ �ش�Ǵ� key�� value �� �����.)

                for (; idx < start_node.p.size(); ++idx) {
                    if (start_node.p.get(idx).first <= end_key) {
                        System.out.println(start_node.p.get(idx).first + ", " + start_node.p.get(idx).second);
                    }
                }

                break;
            }
        }
    }

    static public void deletion(Node node, int key) {
        if (node.isLeaf) {
            // Leaf Node���� key�� ã�� ������.

            for (int i = 0; i < node.p.size(); ++i) {
                if (node.p.get(i).first == key) {
                    // ������ index�� ã�� key�� ����.

                    node.p.remove(i);
                    node.m--;

                    return;
                }
            }

            System.out.println("There is no key, value pair to delete key " + key + ".");
        } else {
            // Non Leaf Node���� key�� ����ִ� Leaf Node�� ã��.

            for (int i = 0; i < node.p.size(); ++i) {
                if (key < node.p.get(i).first) {
                    deletion((Node) node.p.get(i).second, key);

                    int underflow_idx = childUnderflowChecker(node);

                    if (underflow_idx != -1) {
                        // Underflow�� �Ͼ.

                        if (node.r.isLeaf) {
                            // Leaf Node���� Underflow�� �Ͼ.

                            if (!leafBorrow(node, underflow_idx)) { // Sibling Node���� Key Borrow�� �õ���.
                                leafMerge(node, underflow_idx); // Borrow�� ������ ���, Merge��.
                            }

                        } else {
                            // Non Leaf Node���� Underflow�� �Ͼ.

                            if (!nonLeafBorrow(node, underflow_idx)) { // Sibling Node���� Key Borrow�� �õ���.
                                nonLeafMerge(node, underflow_idx); // Borrow�� ������ ���, Merge��.

                                for (int j = 0; j < node.p.size(); ++j) {
                                    // node�� �ִ� delete�� key�� child non leaf node�� �������� ��츦 ������.

                                    deleteKeyInNonLeaf(((Node) node.p.get(j).second), key);
                                }
                                deleteKeyInNonLeaf(node.r, key);
                            }
                        }
                    }
                    deleteKeyInNonLeaf(node, key);

                    return;
                }
            }

            if(key >= node.p.get(node.p.size() - 1).first) {
                // key�� value�� �����Ǳ⿡ Node�� Rightmost Child Node�� ������ ���

                deletion(node.r, key);

                int underflow_idx = childUnderflowChecker(node);

                if (underflow_idx != -1) {
                    // Underflow�� �Ͼ.

                    if(node.r.isLeaf) {
                        // Leaf Node���� Underflow�� �Ͼ.

                        if (!leafBorrow(node, underflow_idx)) { // Sibling Node���� Key Borrow�� �õ���.
                            leafMerge(node, underflow_idx); // Borrow�� ������ ���, Merge��.
                        }
                    } else {
                        // Non Leaf Node���� Underflow�� �Ͼ.

                        if (!nonLeafBorrow(node, underflow_idx)) {
                            nonLeafMerge(node, underflow_idx);

                            for (int j = 0; j < node.p.size(); ++j) {
                                // node�� �ִ� delete�� key�� child non leaf node�� �������� ��츦 ������.

                                deleteKeyInNonLeaf(((Node) node.p.get(j).second), key);
                            }
                            deleteKeyInNonLeaf(node.r, key);

                        }
                    }
                }
                deleteKeyInNonLeaf(node, key);

            }
        }
    }

    public static int childUnderflowChecker(Node node) {
        // ���ڷ� �Է¹��� Node�� Child�� Underflow�� �Ͼ�ٸ� �ش� Child Node�� �ε����� ��ȯ�ϰ�(RightMost Child�� ��� -2), �׷��� �ʴٸ� -1�� ��ȯ��.

        int pSize = node.p.size();

        for (int i = 0; i < pSize; ++i) {
            if (((Node) node.p.get(i).second).m < UNDERFLOW) {
                // Underflow�� �߻��� Child Node�� �߰���.

                return i;
            }
        }

        if (node.r.m < UNDERFLOW) {
            // Node�� Rightmost Child���� Underflow�� �Ͼ.

            return -2;
        } else {
            // Underflow�� �Ͼ�� ����.

            return -1;
        }
    }

    public static boolean leafBorrow(Node node, int idx) {
        // Leaf Node Child ���� Underflow�� �Ͼ ��� Sibling Node���� Borrow�� �õ��Ͽ� �����ϸ� true, �׷��� �ʴٸ� false�� ��ȯ��.

        if (idx != -2) {
            if (idx != node.p.size() - 1 && ((Node) node.p.get(idx + 1).second).m > UNDERFLOW) {
                // Right Sibling���� key�� ������.

                node.p.set(idx, new Pair(((Node) node.p.get(idx + 1).second).p.get(1).first, node.p.get(idx).second));
                ((Node) node.p.get(idx).second).p.add(((Node) node.p.get(idx + 1).second).p.get(0));
                ((Node) node.p.get(idx + 1).second).p.remove(0);
                ((Node) node.p.get(idx + 1).second).m--;
                ((Node) node.p.get(idx).second).m++;

                return true;
            } else if (idx == node.p.size() - 1 && node.r.m > UNDERFLOW) {
                // Right Sibling(Rightmost Child)���� key�� ������.

                node.p.set(idx, new Pair(node.r.p.get(1).first, node.p.get(idx).second));
                ((Node) node.p.get(idx).second).p.add(node.r.p.get(0));
                node.r.p.remove(0);
                node.r.m--;
                ((Node) node.p.get(idx).second).m++;

                return true;
            } else if (idx != 0 && ((Node) node.p.get(idx - 1).second).m > UNDERFLOW) {
                // Left Sibling���� key�� ������.

                node.p.set(idx - 1, new Pair(((Node) node.p.get(idx - 1).second).p.get(((Node) node.p.get(idx - 1).second).p.size() - 1).first, node.p.get(idx - 1).second));

                if (((Node)node.p.get(idx).second).p.size() == 0) {
                    ((Node) node.p.get(idx).second).p.add(((Node) node.p.get(idx - 1).second).p.get(((Node) node.p.get(idx - 1).second).p.size() - 1));
                } else {
                    ((Node) node.p.get(idx).second).p.add(0, ((Node) node.p.get(idx - 1).second).p.get(((Node) node.p.get(idx - 1).second).p.size() - 1));
                }

                ((Node) node.p.get(idx - 1).second).p.remove(((Node) node.p.get(idx - 1).second).p.size() - 1);
                ((Node) node.p.get(idx - 1).second).m--;
                ((Node) node.p.get(idx).second).m++;

                return true;
            }
        } else if(((Node) node.p.get(node.p.size() - 1).second).m > UNDERFLOW){
            //Rightmost Child�� Left Sibling���� key�� ������.

            Pair borrowPair = ((Node) node.p.get(node.p.size() - 1).second).p.get(((Node) node.p.get(node.p.size() - 1).second).p.size() - 1);

            ((Node) node.p.get(node.p.size() - 1).second).p.remove(((Node) node.p.get(node.p.size() - 1).second).p.size() - 1);
            node.p.set(node.p.size() - 1, new Pair(borrowPair.first, node.p.get(node.p.size() - 1).second ));
            node.r.p.add(0, borrowPair);
            ((Node) node.p.get(node.p.size() - 1).second).m--;
            node.r.m++;

            return true;
        }

        return false; // Leaf Borrow�� �Ұ�����.
    }

    public static void leafMerge(Node node, int idx) {
        // Child Leaf Node�� Underflow�� �߻��� ��� Sibling Node�� Merge�� �õ���.

        if (idx != -2) {
            if (idx != node.p.size() - 1) {
                // Right Sibling�� Merge��.

                for (int j = 0; j < ((Node) node.p.get(idx + 1).second).p.size(); ++j) {
                    ((Node) node.p.get(idx).second).p.add(((Node) node.p.get(idx + 1).second).p.get(j));
                    ((Node) node.p.get(idx).second).m++;
                }

                ((Node) node.p.get(idx).second).r = ((Node) node.p.get(idx + 1).second).r;
                node.p.set(idx + 1, new Pair(node.p.get(idx + 1).first, ((Node) node.p.get(idx).second)));
                node.p.remove(idx);
                node.m--;
            } else if (idx == node.p.size() - 1) {
                // Right Sibling(Rightmost Child)�� Merge��.

                for (int j = 0; j < node.r.m; ++j) {
                    ((Node) node.p.get(idx).second).p.add(node.r.p.get(j));
                    ((Node) node.p.get(idx).second).m++;
                }

                ((Node) node.p.get(idx).second).r = node.r.r;
                node.r = ((Node) node.p.get(idx).second);

                node.p.remove(idx);
                node.m--;
            }
        } else {
            // node�� Rightmost Child�� �� ���� Child�� Merge��.

            for (int i = 0; i < node.r.m; ++i) {
                ((Node) node.p.get(node.p.size() - 1).second).p.add(node.r.p.get(i));
                ((Node) node.p.get(node.p.size() - 1).second).m++;
            }

            ((Node) node.p.get(node.p.size() - 1).second).r = node.r.r;
            node.r = ((Node) node.p.get(node.p.size() - 1).second);

            node.p.remove(node.p.size() - 1);
            node.m--;
        }
    }

    public static void deleteKeyInNonLeaf(Node node, int key) {
        // NonLeafNode�� �ڽ� ��忡�� ������ key�� ������ ���, �� key�� ������ Child Node�� �������� ���� ���ʿ� �ִ� Leaf Node���� ���� ���� key�� ��ü�Ѵ�.

        Node tmp;
        int i;
        int p_size = node.p.size();

        for (i = 0; i < p_size; ++i) {
            if (node.p.get(i).first == key) {

                // �߰ߵ� Ű�� ������ Child
                if (i == p_size - 1) {
                    tmp = node.r;
                } else {
                    tmp = ((Node) node.p.get(i + 1).second);
                }

                while (!tmp.isLeaf) {
                    tmp = ((Node) tmp.p.get(0).second);
                }

                node.p.set(i, new Pair<>(tmp.p.get(0).first, node.p.get(i).second));

                break;
            }
        }
    }

    public static boolean nonLeafBorrow(Node node, int idx) {
        // Non Leaf Node Child���� Underflow�� �Ͼ ��� Sibling Node���� Borrow�� �õ��Ͽ� �����ϸ� true, �׷��� ������ false�� ��ȯ��.
                
        if (idx != -2) {
            if (idx != node.p.size() - 1 && ((Node) node.p.get(idx + 1).second).m > UNDERFLOW) {
                // Right Sibling���� Key�� ������.

                Node left = ((Node) node.p.get(idx).second);
                Node right = ((Node) node.p.get(idx + 1).second);

                left.p.add(new Pair<>(node.p.get(idx).first, left.r));
                left.r = (Node) right.p.get(0).second;
                node.p.set(idx, new Pair<>(right.p.get(0).first, node.p.get(idx).second));
                right.p.remove(0);

                left.m++;
                right.m--;

                return true;
            } else if (idx == node.p.size() - 1 && node.r.m > UNDERFLOW) { // checked
                // Right Sibling(Rightmost Child)���� Key�� ������.

                Node left = (Node) node.p.get(idx).second;
                Node right = node.r;

                left.p.add(new Pair<>(node.p.get(idx).first, left.r));
                left.r = (Node) right.p.get(0).second;
                node.p.set(idx, new Pair<>(right.p.get(0).first, left));
                right.p.remove(0);

                left.m++;
                right.m--;

                return true;
            } else if (idx != 0 && ((Node) node.p.get(idx - 1).second).m > UNDERFLOW) {
                // Left Sibling���� key�� ������.

                Node left = ((Node) node.p.get(idx - 1).second); // �̻�����.
                Node right = ((Node) node.p.get(idx).second);

                right.p.add(0, new Pair<>(node.p.get(idx - 1).first, left.r));
                node.p.set(idx - 1, new Pair<>(left.p.get(left.p.size() - 1).first, node.p.get(idx - 1).second));
                left.r = (Node) left.p.get(left.p.size() - 1).second;
                left.p.remove(left.p.size() - 1);

                left.m--;
                right.m++;

                return true;
            }
        } else if (((Node) node.p.get(node.p.size() - 1).second).m > UNDERFLOW) {
            // Rightmost Child�� Left Sibling���� key�� ������.

            int p_size = node.p.size();
            Node left = (Node) node.p.get(p_size - 1).second;
            Node right = node.r;

            right.p.add(0, new Pair<>(node.p.get(p_size - 1).first, left.r));
            node.p.set(p_size - 1, new Pair<>(left.p.get(left.p.size() - 1).first, node.p.get(p_size - 1).second));
            left.r = (Node) left.p.get(left.p.size() - 1).second;
            left.p.remove(left.p.size() - 1);

            left.m--;
            right.m++;

            return true;
        }

        return false;
    }

    public static void nonLeafMerge(Node node, int idx) {
        // Child Non Leaf Node�� Underflow�� �߻��� ��� Sibling Node�� Merge�� �õ��Ѵ�.

        if (idx != -2) {
            // Rightmost Child���� Underflow�� �Ͼ�� �ƴ� ���

            if (idx != node.p.size() - 1) {
                // Rightmost Child�� ���� Sibling Child���� Underflow�� �Ͼ ���� �ƴ� ��� idx + 1��° Child�� Merge�� ��.

                Node left = (Node) node.p.get(idx).second;
                Node right = (Node) node.p.get(idx + 1).second;
                int r_p_size = right.p.size();

                left.p.add(new Pair<>(node.p.get(idx).first, left.r));
                left.m++;

                for(int i = 0; i < r_p_size; ++i) {
                    left.p.add(right.p.get(i));
                    left.m++;
                }

                left.r = right.r;
                node.p.set(idx + 1, new Pair<>(node.p.get(idx + 1).first, left));
                node.p.remove(idx);
                node.m--;

            } else {
                // Rightmost Child�� ���� Sibling Child���� Underflow�� �Ͼ ��� Rightmost Child�� Merge�� ��.

                Node left = (Node) node.p.get(idx).second;
                Node right = node.r;
                int r_p_size = right.p.size();

                left.p.add(new Pair<>(node.p.get(idx).first, left.r));
                left.m++;

                for (int i = 0; i < r_p_size; ++i) {
                    left.p.add(right.p.get(i));
                    left.m++;
                }

                left.r = right.r;
                node.r = left;
                node.p.remove(idx);
                node.m--;
            }
        } else {
            // Rightmost Child���� Underflow�� �Ͼ ��� Rightmost Child�� Left Sibling�� Merge��.

            Node left = (Node) node.p.get(node.p.size() - 1).second;
            Node right = node.r;
            int r_p_size = right.p.size();

            left.p.add(new Pair<>(node.p.get(node.p.size() - 1).first, left.r));
            left.m++;

            for (int i = 0; i < r_p_size; ++i) {
                left.p.add(right.p.get(i));
                left.m++;
            }

            left.r = right.r;
            node.r = left;
            node.p.remove(node.p.size() - 1);
            node.m--;
        }
    }

    public static Data buildTree(String index_file) {
        // index_file���� ��ü�� �ҷ���, ������ B+ Tree��(Linked List ����) �����Ͽ� ��ȯ��.

        FileInputStream fis; // ���Ͽ��� ��ü�� �ҷ����� ���� ��Ʈ�� ����
        ObjectInputStream ois;
        Data data = null;
        ArrayList<Node> list;
        Node node;
        int size;

        try {
            fis = new FileInputStream(index_file);
            ois = new ObjectInputStream(fis);

            data = (Data) ois.readObject();

            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        node = data.root;

        if (node == null)
            node = data.root = new Node();

        list = data.list;
        size = list.size();

        while (!node.isLeaf) {
            // ���� ���� Leaf Node�� ã��.

            node = (Node) node.p.get(0).second;
        }

        for (int i = 0; i < size; ++i) {
            // Data.list�� ���� ���� Node�� Linked List�� ������.

            node.r = list.get(i);
            node = node.r;
        }

        data.list = null; // ���̻� �� ���� ���� ����Ʈ�̹Ƿ� null�� �Ҵ��ϰ� ��ȯ��.

        return data;
    }

    public static ArrayList<Node> makeList(Node root) {
        // B+ Tree �ȿ� �ִ� ��� Linked List ������ ����, �̸� ArrayList�� ��ȯ��.

        ArrayList<Node> list = new ArrayList<>();
        Node tmp;

        if (root != null) {
            while (!root.isLeaf) {
                root = (Node) root.p.get(0).second;
            }

            while (root != null) {
                tmp = root.r;

                list.add(root.r); // �ش� Node�� ���� Node�� ArrayList�� ����.

                root.r = null; // ��� Linked List ������ ����.

                root = tmp;
            }
        }

        return list;
    }

    public static void saveTree(String index_file, int m, Node root) {
        // B+ Tree���� Linked List ������ ����, �� ����Ʈ�� ArrayList�� ��� ���Ͽ� ��.

        FileOutputStream fos;
        ObjectOutputStream oos;
        ArrayList<Node> list;
        Data data = new Data();

        list = makeList(root); // B+ Tree ���� Linked List�� ��� ����, ���� List�� ArrayList�� ����.

        try {
            fos = new FileOutputStream(index_file);
            oos = new ObjectOutputStream(fos);

            data.list = list;
            data.root = root;
            data.m = m;

            oos.writeObject(data);

            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
