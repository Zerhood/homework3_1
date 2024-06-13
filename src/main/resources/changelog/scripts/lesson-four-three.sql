-- liquibase formatted sql

-- changeset savelyev:1
CREATE INDEX name_student ON student (name);
CREATE INDEX name_color_faculty ON faculty (name, color);