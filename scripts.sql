SELECT s.* FROM Student AS s WHERE s.age > 10 AND s.age < 20;
SELECT s.name FROM Student AS s;
SELECT * FROM Student AS s WHERE s.name LIKE '%o%';
SELECT * FROM Student AS s WHERE s.age < s.id;
SELECT * FROM Student AS s ORDER BY s.age;