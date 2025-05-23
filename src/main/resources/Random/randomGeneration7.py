from random import *
import sys
import string

def main():
    seed(sys.argv[1])
    a = ""
    for i in range(randint(2,20)):
        a+=choice(string.ascii_letters)
    
    print(a)


if __name__ == "__main__":
    main()