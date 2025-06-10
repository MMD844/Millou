create database Millou_db;
use Millou_db;

create table users (
                             id int primary key,
                             name varchar(100) not null ,
                             email varchar(100) not null unique ,
                             password varchar(255) not null ,
                             created_at timestamp not null default current_timestamp
);

create table emails (
                            id int primary key auto_increment ,
                            code varchar(6) unique ,
                            sender_id integer not null ,
                            subject varchar(200) not null ,
                            body text not null ,
                            created_at timestamp not null default current_timestamp,
                            foreign key (sender_id) references users(id)
);

create table email_recipients (
                                id int primary key ,
                                email_id int not null ,
                                recipient_id integer not null ,
                                is_read boolean not null default false,
                                read_at timestamp,
                                foreign key (email_id) references emails(id) ,
                                foreign key (recipient_id) references users(id)
);
