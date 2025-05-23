import codyngame

a = int(input())
som=""

if a > 0:
    for c in range(1,11):
        som+=str(a*c)+" "

if som==codyngame.multiplication(a):
    print(1)
else:
    print(0)
    print(codyngame.multiplication(a),)
    print(som)
    print(1)

#DIRE QU'IL FAUT RENVOYER UN TABLEAU DE STRING AVEC DES ESPACE ENTRE CHAQUE VALEUR