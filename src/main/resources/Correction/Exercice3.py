import codyngame

comp=False
a = int(input())
if a <= 1:
    comp=False
else:
    for c in range(2, int(a**0.5)+1):
        res = a%c
        if res==0:
            comp=False
            break
        comp=True
        
if comp==codyngame.premier(a):
    print(1)
else:
    print(0)
    print(codyngame.premier(a),a)
    print(comp,a)
    print(1)

