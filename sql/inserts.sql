-- create database iamok;

-- use iamok;

-- create user iamok identified by 'youareok';

-- GRANT ALL ON iamok.* TO 'iamok'@'%';


use iamok;

SET SQL_SAFE_UPDATES = 0; 
-- drop table if exists result;
-- drop table if exists assignment_history;
-- drop table if exists assignment;
-- drop table if exists challenge;

drop table if exists user_role;

create table user_role (
	user_id int(11),
	role_id int (11),
	primary key (user_id , role_id)
);

delete from user;
delete from role;

insert into user (id, username, password) values (1, 'gepi', 'BEsQhfKl4Ifu8TDUNPDdig=='); -- pepi
insert into user (id, username, password) values (2, 'gama', 'UQLs09R/ZWHecJeQF7h6gA=='); -- mia
insert into user (id, username, password) values (3, 'lele', 'B89Pj12LdigpFzIHFd2irQ=='); -- male


insert into role (id, name, description) values (1, 'Administrator', 'Administrator Role');
insert into role (id, name, description) values (2, 'general', 'General Role');
insert into role (id, name, description) values (3, 'Moderator', 'Moderator Role');


insert into user_role values (1, 1);
insert into user_role values (1, 2);
insert into user_role values (1, 3);

insert into user_role values (2, 2);
insert into user_role values (3, 2);

commit;