import codyngame  # Importe le module utilisateur

a=int(input())

tab=[]
for i in range(50):
    tab.append(int(input()))
tab=tab.sort()
res=0
for i in range(len(tab)):
    if tab[i]==a:
        res=i
        break

if res==codyngame.index(a,tab):
    print(1)
else:
    print(0)
    print(codyngame.index(a,tab))
    print(res)
    print(1)

#DIRE QUE C4EST UN TABLEAU DE 50 VALEURS