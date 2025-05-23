import codyngame  # Importe le module utilisateur

tab=[]
for i in range(50):
    tab.append(int(input()))



if tab.sort()==codyngame.tri(tab):
    print(1)
else:
    print(0)
    print(codyngame.tri(tab))
    print(tab.sort())
    print(1)

#DIRE QUE C4EST UN TABLEAU DE 50 VALEURS