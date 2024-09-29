drop table if exists Warehouse cascade;
drop sequence if exists WAREHOUSE_SEQ;
create sequence WAREHOUSE_SEQ start with 1 increment by 1;
create table Warehouse (
    latitude float(53),
    longitude float(53),
    id bigint not null,
    name varchar(255),
    primary key (id)
);
