import codyngame  # Importe le module utilisateur

bol = True
tab = input().strip()
res = ''.join(c for c in tab.lower() if c.isalnum())

if(res!=res[::-1]):
    bol = False

if bol==codyngame.palindrome(tab):
    print(1)
else:
    print(0)
    print(codyngame.palindrome(tab),tab)
    print(bol,tab)
    print(1)

