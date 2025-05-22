import java.util.Scanner;

public class Exercice4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int a = scanner.nextInt();
        int c=0;
        boolean comp = true;

        if (a<=0){
            comp=false;
        }
        else {
            for (int i = 1; i < a; i++) {
                if(a%i==0){
                    c+=i;
                }
            }
            if(c!=a){
                comp=false;
            }
        }

        if (Codyngame.parfait(a) == comp) {
            System.out.println(1);
        } else {
            System.out.println(0);
            System.out.println(Codyngame.parfait(a));
            System.out.println(comp);
            System.out.println(1);
        }

        scanner.close();
    }
}