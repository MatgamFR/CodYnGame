import codyngame

a = int(input())
tab = []
x,y=0,1
if a >= 0:
    while x<= a:
        tab.append(x)
        x,y = y, x+y
      
if tab==codyngame.fibonacci(a):
    print(1)
else:
    print(0)
    print(codyngame.fibonacci(a))
    print(tab)
    print(1)












def fibonacci_jusqua_n(n):
    if n < 0:
        return []  
    
    a, b = 0, 1
    suite = []
    
    while a <= n:
        suite.append(a)
        a, b = b, a + b
    
    return suite
