drop table user_roles;
drop table roles;
drop table users;
drop table answers;
drop table questions;
drop table surveys;

delete from user_roles;
delete from roles;
delete from users;
delete from answers;
delete from questions;
delete from surveys;


INSERT INTO roles(name) VALUES('Admin');
INSERT INTO roles(name) VALUES('Report');
INSERT INTO roles(name) VALUES('User');


select * from users;
select * from roles;
select * from user_roles;

select * from surveys;
select * from questions;
select * from answers;
