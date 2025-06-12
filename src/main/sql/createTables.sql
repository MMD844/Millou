create database Millou_db;
use Millou_db;

create table users (
                             id int primary key auto_increment,
                             name nvarchar(100) not null ,
                             email nvarchar(100) not null unique ,
                             password varchar(255) not null ,
                             created_at timestamp not null default current_timestamp
);

create table emails (
                            id int primary key auto_increment ,
                            subject nvarchar(200) not null ,
                            sender_id integer not null ,
                            body text not null ,
                            created_at timestamp not null default current_timestamp,
                            foreign key (sender_id) references users(id)
);

create table email_recipients (
                                id int primary key auto_increment,
                                email_id integer not null ,
                                recipient_id integer not null ,
                                read_at timestamp default null,
                                foreign key (email_id) references emails(id) ,
                                foreign key (recipient_id) references users(id)
);

drop table users;
drop table emails;
drop table email_recipients;
select * from users;
select * from email_recipients;
select * from emails;
