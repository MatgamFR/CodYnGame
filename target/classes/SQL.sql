Drop table if exists LanguageCode;
Drop table if exists Exercice;

CREATE Table Exercice(
	Id INT AUTO_INCREMENT PRIMARY KEY,
	Titre VARCHAR(50),
	difficulty VARCHAR(20),
	Question VARCHAR(1000),
	Answer VARCHAR(1000),
	Try INT,
	Successfulltry INT,
	TypeExo VARCHAR(50)
);

Create table LanguageCode(
	Exerciceid INT,
	NomLanguage VARCHAR(100),
	PRIMARY KEY (Exerciceid, NomLanguage),
	FOREIGN KEY (Exerciceid) REFERENCES Exercice(Id)
);

Insert Into Exercice Values(1,"Hello World!","facile","écrivez un programme pour que le terminal envoie hello world", 'print("hello world")',0,0,'STDIN/STDOUT');
Insert Into Exercice Values(2,"Somme","facile","écrivez un programme pour que le terminal renvoie renvoie la somme de deux nombres", 'print("hello world")',0,0,'INCLUDE');
Insert into Exercice Values(3,"nombre premier", "facile","écrivez un programme pour que le terminal renvoie vrai si c'est in nombre premier et faux sinon", '',0,0,'INCLUDE');
Insert Into Exercice Values(4,"nombre parfait", "moyen","écrivez un programme pour que le terminal renvoie vrai si c'est in nombre parfait et faux sinon", '',0,0,'INCLUDE');
Insert Into Exercice Values(5,"Suite de Fibonacci", "difficile","écrivez un programme pour que le terminal renvoie la suite de Fibonacci jusqu'à n", '',0,0,'INCLUDE');
Insert Into Exercice Values(6,"Factorielle", "difficile","écrivez un programme pour que le terminal renvoie la factorielle de n", '',0,0,'INCLUDE');
Insert Into Exercice Values(7,"Palindrome", "difficile","écrivez un programme pour que le terminal renvoie vrai si c'est in palindrome et faux sinon", '',0,0,'INCLUDE');
Insert Into Exercice Values(8,"Table de multiplication", "difficile","écrivez un programme pour que le terminal renvoie la table de multiplication de n", '',0,0,'INCLUDE');
Insert Into Exercice Values(9,"Tri ", "difficile","écrivez un programme pour que le terminal renvoie un tableau trié", '',0,0,'INCLUDE');
Insert Into Exercice Values(10,"Recherche binaire", "difficile","écrivez un programme pour que le terminal renvoie l'index d'un nombre dans un tableau trié", '',0,0,'INCLUDE');
Insert Into Exercice Values(11,"Paire", "facile","print un nombre paire", '',0,0,'STDIN/STDOUT');
Insert Into Exercice Values(12,"Impair", "facile","print un nombre impair", '',0,0,'STDIN/STDOUT');
Insert Into Exercice Values(13,"Addition", "facile","Addition de 2 nombre", '',0,0,'STDIN/STDOUT');
Insert Into Exercice Values(14,"Soustraction", "facile","Soustraction de 2 nombre", '',0,0,'STDIN/STDOUT');
Insert Into Exercice Values(15,"Multiplication", "facile","Multiplication de 2 nombre", '',0,0,'STDIN/STDOUT');
Insert Into Exercice Values(16,"Division", "facile","Division de 2 nombre", '',0,0,'STDIN/STDOUT');

INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (1,'Python'),(1,'Java'),(1,'C'),(1,'JavaScript'),(1,'PHP');
INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (2,'Python'),(2,'Java'), (2,'C'),(2,'JavaScript'),(2,'PHP');
INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (3,'Python'),(3,'Java'),(3,'C'),(3,'JavaScript'),(3,'PHP');
INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (4,'Java'),(4,'Python'),(4,'Javascript'),(4,'PHP'),(4,'C');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (5,'Python'),(5,'Java'),(5,'C'),(5,'JavaScript'),(5,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (6,'Python'),(6,'Java'),(6,'C'),(6,'JavaScript'),(6,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (7,'Python'),(7,'Java'),(7,'C'),(7,'JavaScript'),(7,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (8,'Python'),(8,'Java'),(8,'C'),(8,'JavaScript'),(8,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (9,'Python'),(9,'Java'),(9,'C'),(9,'JavaScript'),(9,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (10,'Python'),(10,'Java'),(10,'C'),(10,'JavaScript'),(10,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (11,'Python'),(11,'Java'),(11,'C'),(11,'JavaScript'),(11,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (12,'Python'),(12,'Java'),(12,'C'),(12,'JavaScript'),(12,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (13,'Python'),(13,'Java'),(13,'C'),(13,'JavaScript'),(13,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (14,'Python'),(14,'Java'),(14,'C'),(14,'JavaScript'),(14,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (15,'Python'),(15,'Java'),(15,'C'),(15,'JavaScript'),(15,'PHP');
Insert Into LanguageCode (Exerciceid, NomLanguage) VALUES (16,'Python'),(16,'Java'),(16,'C'),(16,'JavaScript'),(16,'PHP');

