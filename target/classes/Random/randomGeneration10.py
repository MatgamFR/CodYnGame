from random import *
import sys
import string

def main():
    seed(sys.argv[1])
    a=randint(1,100)
    print(a)
    print(a)
    for i in range(49):
        print(randint(1,100))
    


if __name__ == "__main__":
    main()