Drop table Exercice;

CREATE Table Exercice(
	Id INT PRIMARY KEY,
	difficulty VARCHAR(20),
	Question VARCHAR(1000),
	Answer VARCHAR(1000),
	Try INT,
	Successfulltry INT,
	language VARCHAR(100)
);

Insert Into Exercice Values(1,"facile","écrivez un programme pour que le terminal renvoie 'hello world'", 'print("hello world")',0,0,"java");
