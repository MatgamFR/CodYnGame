from random import *
import sys
import string

def main():
    seed(sys.argv[1])
    for i in range(2):
        print(randint(1,100))
    


if __name__ == "__main__":
    main()