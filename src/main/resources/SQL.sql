Drop table LanguageCode;
Drop table Exercice;

CREATE Table Exercice(
	Id INT PRIMARY KEY,
	Titre VARCHAR(50),
	difficulty VARCHAR(20),
	Question VARCHAR(1000),
	Answer VARCHAR(1000),
	Try INT,
	Successfulltry INT
);

Create table LanguageCode(
	Id INT AUTO_INCREMENT PRIMARY KEY,
	Exerciceid INT,
	NomLanguage VARCHAR(100),
	FOREIGN KEY (Exerciceid) REFERENCES Exercice(Id)
);

Insert Into Exercice Values(1,"Hello World!","facile","écrivez un programme pour que le terminal renvoie rien", 'print("hello world")',0,0);
Insert Into Exercice Values(2,"Hello World!(sans python)","facile","écrivez un programme pour que le terminal renvoie hello world", 'print("hello world")',0,0);
INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (1,'Python'),(1,'Java');
INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (2,'Java');
