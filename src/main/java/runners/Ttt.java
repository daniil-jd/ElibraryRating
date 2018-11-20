package runners;

public class Ttt {
    public static void main(String[] args) {
        int FIRST_LED = 11;
        int LAST_LED = 13;

        for (int i = LAST_LED; i >= FIRST_LED; i--) {
            System.out.println(i + " HIGH");
        }
        System.out.println("----");

        for (int i = FIRST_LED; i <= LAST_LED; i++) {
            System.out.println(i + " LOW");
        }
    }

}
