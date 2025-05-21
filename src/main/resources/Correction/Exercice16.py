word = input().replace('\\n', '\n').split('\n')

a=input()
b=input()
if int(word[0]) == int(a)//int(b):
    print(1)
else:
    print(0)
    print(word[0])
    print(int(a)//int(b))
    print(1)