# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table address (
  id                        bigint not null,
  street                    varchar(255),
  number                    varchar(255),
  postal_code               varchar(255),
  city                      varchar(255),
  country                   varchar(255),
  constraint pk_address primary key (id))
;

create table masters_student (
  id                        bigint not null,
  ean                       varchar(255),
  name                      varchar(255),
  address                   varchar(255),
  phonenumber               varchar(255),
  faculty                   varchar(255),
  course                    varchar(255),
  email                     varchar(255),
  birthday                  timestamp,
  picture                   bytea,
  constraint pk_masters_student primary key (id))
;

create table stock_item (
  id                        bigint not null,
  warehouse_id              bigint,
  masters_student_id        bigint,
  quantity                  bigint,
  constraint pk_stock_item primary key (id))
;

create table tag (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_tag primary key (id))
;

create table user_account (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  permission                varchar(255),
  constraint pk_user_account primary key (id))
;

create table warehouse (
  id                        bigint not null,
  name                      varchar(255),
  address_id                bigint,
  constraint pk_warehouse primary key (id))
;


create table masters_student_tag (
  masters_student_id             bigint not null,
  tag_id                         bigint not null,
  constraint pk_masters_student_tag primary key (masters_student_id, tag_id))
;
create sequence address_seq;

create sequence masters_student_seq;

create sequence stock_item_seq;

create sequence tag_seq;

create sequence user_account_seq;

create sequence warehouse_seq;

alter table stock_item add constraint fk_stock_item_warehouse_1 foreign key (warehouse_id) references warehouse (id);
create index ix_stock_item_warehouse_1 on stock_item (warehouse_id);
alter table stock_item add constraint fk_stock_item_mastersStudent_2 foreign key (masters_student_id) references masters_student (id);
create index ix_stock_item_mastersStudent_2 on stock_item (masters_student_id);
alter table warehouse add constraint fk_warehouse_address_3 foreign key (address_id) references address (id);
create index ix_warehouse_address_3 on warehouse (address_id);



alter table masters_student_tag add constraint fk_masters_student_tag_master_01 foreign key (masters_student_id) references masters_student (id);

alter table masters_student_tag add constraint fk_masters_student_tag_tag_02 foreign key (tag_id) references tag (id);

# --- !Downs

drop table if exists address cascade;

drop table if exists masters_student cascade;

drop table if exists masters_student_tag cascade;

drop table if exists stock_item cascade;

drop table if exists tag cascade;

drop table if exists user_account cascade;

drop table if exists warehouse cascade;

drop sequence if exists address_seq;

drop sequence if exists masters_student_seq;

drop sequence if exists stock_item_seq;

drop sequence if exists tag_seq;

drop sequence if exists user_account_seq;

drop sequence if exists warehouse_seq;

