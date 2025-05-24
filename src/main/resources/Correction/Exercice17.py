word = input().replace("\\n", "\n").split("\n") # liste des sorties de l'utilisateur 

if word[0] == "hello world": # Si c'est vrai
    print(1)
else: # Si c'est faux
    print(0)
    print(word[0]) # valeur de l'utilisateur
    print("hello world") # valeur attendu
    print(1) # ligne de l'erreur