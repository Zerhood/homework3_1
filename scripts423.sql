SELECT s.name, s.age, f.name FROM student AS s
JOIN faculty f ON f.id = s.faculty_id;

SELECT s.* FROM student AS s
JOIN avatar AS a ON s.id = a.student_id
WHERE a.media_type IS NOT NULL;