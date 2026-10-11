CREATE TABLE students;
CREATE TABLE professors;
CREATE TABLE courses;
CREATE TABLE student_courses;
CREATE TABLE professor_courses;
CREATE TABLE grades;

ALTER TABLE grades
    ADD CONSTRAINT fk_grades_enrollment
    FOREIGN KEY (student_am, course_id)
    REFERENCES student_courses (student_am, course_id)
    ON DELETE RESTRICT;