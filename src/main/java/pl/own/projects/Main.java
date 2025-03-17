package pl.own.projects;

public class Main {
    public static void main(String[] args) {
        MyScanner myScanner = null;
        try {
            myScanner = new MyScanner("src/main/java/pl/own/projects/Main.java");
        }
        catch (MyScannerException e) {
            System.out.println(e.getMessage());
        }

        if (myScanner != null) {
            while (myScanner.hasNextToken()) {
                Token token;
                try {
                    token = myScanner.nextToken();
                    System.out.println(token.getType().toString() + " " + token.getValue());
                } catch (MyScannerException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }
}