import java.util.Scanner;

public class Exercice3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int a = scanner.nextInt();
        int c;
        boolean comp = true;

        if (a<=1){
            comp=false;
        }
        else {
            for (int i = 2; i < a; i++) {
                c=a%i;
                if(c==0){
                    comp=false;
                }
            }
        }

        if (Codyngame.premier(a) == comp) {
            System.out.println(1);
        } else {
            System.out.println(0);
            System.out.println(Codyngame.premier(a));
            System.out.println(comp);
            System.out.println(1);
        }

        scanner.close();
    }
}