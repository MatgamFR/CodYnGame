#include <stdio.h>

// Fonction pour importer la fonction somme du fichier utilisateur
int somme(int a, int b);

int main() {
    int a, b;
    
    // Lecture des deux nombres depuis l'entrée standard
    scanf("%d", &a);
    scanf("%d", &b);
    
    // Vérification du résultat - correction de la condition
    if (somme(a, b) == a + b) {  // Condition corrigée
        printf("1\n");
    } else {
        printf("0\n");
        printf("%d\n", somme(a, b));  // Résultat reçu
        printf("%d\n", a + b);        // Résultat attendu
        printf("1\n");                // Valeur de comparaison
    }
    
    return 0;
}