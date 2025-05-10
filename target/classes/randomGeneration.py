from random import *
import sys

def main():
    seed(sys.argv[1])
    match sys.argv[2]:
        case _:    
            print(randint(0, 100))

    


if __name__ == "__main__":
    main()