#include <stdio.h>
#include <stdbool.h>

// Fonction pour importer la fonction somme du fichier utilisateur
bool premier(int a);

int main() {
    int a,c;
    bool comp=true;
    c=0;
    scanf("%d", &a);
    bool res=premier(a);
    
    if(a<=0){
        comp=false;
    } else {
        for(int i=1;i<=a;i++){
            if(a%i==0){
                c += i;
            }
        }
        if(c!=a){
            comp=false;
        }
    }
    if (comp==res) {  
        printf("1\n");
    } else {
        printf("0\n");
        printf("%B %d \n",parfait(a));
        printf("%B %d \n",comp);
        printf("1\n");
    } 
    
    return 0;
}