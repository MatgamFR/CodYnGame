#include <stdio.h>

// Fonction pour importer la fonction somme du fichier utilisateur
int factorielle(int a);

int main() {
    int a,f;
    scanf("%d", &a);
    int res=factorielle(a);
    if(a<0){
        f=0;
    } else {
        f=1;
        for(int i=1;i<=a;i++){
            f *= i;
        }
    }
        
    if (f==res) {  
        printf("1\n");
    } else {
        printf("0\n");
        printf("%d \n",factorielle(a));
        printf("%d \n",f);
        printf("1\n");
    } 
    
    return 0;
}