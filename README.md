# CodYnGame
## Description

CodYnGame est une application JavaFX qui permet aux utilisateurs de résoudre des exercices de programmation dans différents langages (Python, Java, C, JavaScript, PHP). Elle offre une interface graphique interactive pour ajouter, filtrer, et exécuter des exercices tout en suivant les statistiques des essais.

## Prérequis
Avant de lancer l'application, assurez-vous que votre environnement est correctement configuré.

### Logiciels nécessaires
- Java Development Kit (JDK) : Version 17 ou supérieure.
- Apache Maven : Pour gérer les dépendances et exécuter l'application.
- GCC : Pour compiler les exercices en C.
- Node.js : Pour exécuter les exercices en JavaScript.
- PHP : Pour exécuter les exercices en PHP.
- Python 3 : Pour exécuter les exercices en Python.
- MySQL : Pour la base de données.

## Configuration
### Fichier configue.txt
Avant de lancer l'application, vous devez configurer les informations de connexion à la base de données dans le fichier configue.txt. Ce fichier doit être placé à la racine du projet.
- db_url : URL de connexion à la base de données MySQL.
- db_user : Nom d'utilisateur de la base de données.
- db_password : Mot de passe de la base de données.

## Installation
### Script d'installation
Un script Bash setup.sh est fourni pour configurer l'environnement et préparer le projet. Rentrer dans le terminal la commande ```bash setup.sh```

## Lancer l'application
### Commande Maven
Pour exécuter l'application, utilisez la commande suivante : ```mvn javafx:run```
