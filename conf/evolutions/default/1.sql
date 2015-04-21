# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table faculty (
  id                        bigint not null,
  code                      varchar(255),
  name                      varchar(255),
  degree                    varchar(255),
  address                   varchar(255),
  email                     varchar(255),
  phone_number              varchar(255),
  constraint pk_faculty primary key (id))
;

create table masters_student (
  id                        bigint not null,
  code                      varchar(255),
  name                      varchar(255),
  birthday                  timestamp,
  address                   varchar(255),
  phone_number              varchar(255),
  faculty                   varchar(255),
  course                    varchar(255),
  email                     varchar(255),
  masters_thesis_id         bigint,
  constraint pk_masters_student primary key (id))
;

create table masters_thesis (
  id                        bigint not null,
  code                      varchar(255),
  name                      varchar(255),
  faculty_name              varchar(255),
  description               varchar(255),
  constraint pk_masters_thesis primary key (id))
;

create table user_account (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  permission                varchar(255),
  constraint pk_user_account primary key (id))
;

create sequence faculty_seq;

create sequence masters_student_seq;

create sequence masters_thesis_seq;

create sequence user_account_seq;

alter table masters_student add constraint fk_masters_student_mastersThes_1 foreign key (masters_thesis_id) references masters_thesis (id);
create index ix_masters_student_mastersThes_1 on masters_student (masters_thesis_id);



# --- !Downs

drop table if exists faculty cascade;

drop table if exists masters_student cascade;

drop table if exists masters_thesis cascade;

drop table if exists user_account cascade;

drop sequence if exists faculty_seq;

drop sequence if exists masters_student_seq;

drop sequence if exists masters_thesis_seq;

drop sequence if exists user_account_seq;

