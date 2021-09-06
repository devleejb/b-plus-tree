public class Main {
    public static void main(String[] args) {
        if(args[0].equals("-c")) {
            // Create

            Operation.create(args[1], args[2]);
        } else if(args[0].equals("-i")) {
            // Insert

            Operation.insert(args[1], args[2]);
        } else if(args[0].equals("-d")) {
            // Delete

            Operation.delete(args[1], args[2]);
        } else if(args[0].equals("-s")) {
            // SingleSearch

            Operation.singleSearch(args[1], Integer.parseInt(args[2]));
        } else if(args[0].equals("-r")) {
            // Ranged Search

            Operation.rangedSearch(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        }
    }
}
