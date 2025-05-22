import codyngame

comp=True
som=0
a = int(input())
if a <= 0:
    comp=False
else:
    for c in range(1,a):
        if a%c==0:
            som+=c
    if(som!=a):
        comp=False
    

        
if comp==codyngame.parfait(a):
    print(1)
else:
    print(0)
    print(codyngame.parfait(a),)
    print(comp,)
    print(1)

