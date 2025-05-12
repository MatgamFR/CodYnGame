import sys

def main():
    match int(sys.argv[1]):
        case 1:    
            # exercice corrigé
            print("hello world")
        case 2:    
            # exercice corrigé
            print(int(input())+int(input()))
        case 3:
            # exercice corrigé
            n = int(input())
            
            print(int(n*(n+1)/2))


if __name__ == "__main__":
    main()
