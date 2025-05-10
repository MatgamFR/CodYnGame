#!/bin/bash
# filepath: /home/cytech/CodYnGame/setup.sh

# Définir les couleurs pour une meilleure lisibilité
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Informations de connexion MySQL
MYSQL_USER="root"
MYSQL_PASS="cytech0001"
DB_NAME="codegame"

# Chemin vers le fichier SQL
SQL_PATH="src/main/resources/SQL.sql"

echo -e "${BLUE}Configuration de la base de données pour CodYnGame${NC}"

# Vérifier si le fichier SQL existe
if [ ! -f "$SQL_PATH" ]; then
    echo -e "${RED}Erreur: Le fichier SQL '$SQL_PATH' n'existe pas.${NC}"
    exit 1
fi

# Créer la base de données si elle n'existe pas et importer le fichier SQL
echo -e "${BLUE}Connexion à MySQL et configuration de la base de données...${NC}"

# Vérifier si MySQL est en cours d'exécution
if ! pgrep -x "mysqld" > /dev/null; then
    echo -e "${RED}Erreur: Le serveur MySQL n'est pas en cours d'exécution.${NC}"
    echo -e "Veuillez démarrer MySQL avec: ${BLUE}sudo service mysql start${NC}"
    exit 1
fi

# Créer la base de données et importer le fichier SQL
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" <<EOF
CREATE DATABASE IF NOT EXISTS $DB_NAME;
USE $DB_NAME;
SOURCE $SQL_PATH;
EOF

# Vérifier si la commande MySQL s'est exécutée avec succès
if [ $? -eq 0 ]; then
    echo -e "${GREEN}La base de données a été configurée avec succès!${NC}"
    
    # Vérification que la base contient bien des données
    TABLES=$(mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" -e "USE $DB_NAME; SHOW TABLES;" 2>/dev/null)
    
    if [[ $TABLES == *"Exercice"* ]]; then
        echo -e "${GREEN}Tables créées avec succès:${NC}"
        echo "$TABLES"
    else
        echo -e "${RED}Attention: Les tables semblent vides ou n'ont pas été créées correctement.${NC}"
    fi
else
    echo -e "${RED}Erreur lors de la configuration de la base de données.${NC}"
    exit 1
fi

echo -e "${BLUE}Configuration terminée. L'application CodYnGame est prête à être utilisée.${NC}"
echo -e "Pour lancer l'application: ${GREEN}mvn javafx:run${NC}"