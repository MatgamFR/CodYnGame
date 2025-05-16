Drop table if exists LanguageCode;
Drop table if exists Exercice;

CREATE Table Exercice(
	Id INT AUTO_INCREMENT PRIMARY KEY,
	Titre VARCHAR(50),
	difficulty VARCHAR(20),
	Question VARCHAR(1000),
	Answer VARCHAR(1000),
	Try INT,
	Successfulltry INT
);

Create table LanguageCode(
	Exerciceid INT,
	NomLanguage VARCHAR(100),
	PRIMARY KEY (Exerciceid, NomLanguage),
	FOREIGN KEY (Exerciceid) REFERENCES Exercice(Id)
);

CREATE TABLE TypeExo(
	Exerciceid INT,	
	TypeExo VARCHAR(50),
	PRIMARY KEY (Exerciceid, TypeExo),
	FOREIGN KEY (Exerciceid) REFERENCES Exercice(Id)
);

Insert Into Exercice Values(1,"Hello World!","facile","écrivez un programme pour que le terminal envoie hello world", 'print("hello world")',0,0);
Insert Into Exercice Values(2,"Somme","facile","écrivez un programme pour que le terminal renvoie renvoie la somme de deux nombres", 'print("hello world")',0,0);
INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (1,'Python'),(1,'Java'),(1,'C'),(1,'JavaScript'),(1,'PHP');
INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (2,'Java');
INSERT INTO TypeExo Values (1,'STDIN/STDOUT'),(2,'INCLUDE')
