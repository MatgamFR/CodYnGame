word = input().replace("\\n", "\n").split("\n")
a = int(input())
b = int(input())

if word[0] == str(a+b):
    print(1)
else:
    print(0)
    print(a+b)
    print(1)