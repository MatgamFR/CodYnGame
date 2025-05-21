import codyngame

a = int(input())
b = int(input())

if codyngame.somme(a,b) == a+b:
    print(1)
else:
    print(0)
    print(codyngame.somme(a,b))
    print(a+b)
    print(1)