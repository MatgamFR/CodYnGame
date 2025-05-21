import java.util.Scanner;

public class Exercice2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int a = scanner.nextInt();
        int b = scanner.nextInt();

        if (Codyngame.somme(a, b) == a + b) {
            System.out.println(1);
        } else {
            System.out.println(0);
            System.out.println(Codyngame.somme(a, b));
            System.out.println(a + b);
            System.out.println(1);
        }

        scanner.close();
    }
}