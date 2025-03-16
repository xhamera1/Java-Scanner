package pl.own.projects;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MyScanner myScanner = null;
        try {
            myScanner = new MyScanner("src/main/resources/numbers.txt");
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