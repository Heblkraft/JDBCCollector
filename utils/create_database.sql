drop database jdbc_test;
create database jdbc_test;
use jdbc_test;
drop table test_table_id;
create table test_table_id(
	name varchar(64),
	username varchar(32),
	email varchar(64)
);

ALTER TABLE test_table_id ADD id int NOT NULL IDENTITY (1,1) PRIMARY KEY

drop table test_table_timestamp;
create table test_table_timestamp(
	title varchar(64),
	completed varchar(12),
	timestampcolumn datetime default GETDATE()
);

drop table test_table_photos;
create table test_table_photos(
	albumId int,
	title varchar(128),
	url varchar(128),
	thumbnailUrl varchar(128)
);

ALTER TABLE test_table_photos ADD id int NOT NULL IDENTITY (1,1) PRIMARY KEY
