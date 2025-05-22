#include <stdio.h>
#include <stdbool.h>

// Fonction pour importer la fonction somme du fichier utilisateur
bool premier(int a);

int main() {
    int a,c;
    bool comp=true;

    scanf("%d", &a);
    bool res=premier(a);
    
    if(a<=1){
        comp=false;
    } else {
        for(int i=2;i<=a;i++){
            c = a%i;
            if(c==0){
                comp=false;
                break;
            }
        }
    }
    if (comp==res) {  
        printf("1\n");
    } else {
        printf("0\n");
        printf("%B %d \n",premier(a),a);
        printf("%B %d \n",comp,a);
        printf("1\n");
    } 
    
    return 0;
}

/*
  int c
  bool comp=true
  bool res=premier(a)
  if(a<=1){
  return false
  } else {
  } 



*/