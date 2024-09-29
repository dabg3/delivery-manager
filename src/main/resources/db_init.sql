-- TODO move schema generation instructions to its own script
drop table if exists Warehouse cascade;
drop sequence if exists WAREHOUSE_SEQ;
create sequence WAREHOUSE_SEQ start with 1 increment by 1;
create table Warehouse
(
    id        bigint not null,
    name      varchar(255),
    latitude  float(53),
    longitude float(53),
    primary key (id)
);
insert into Warehouse (id, name, latitude, longitude)
values (nextval('WAREHOUSE_SEQ'), 'pisa', 43.7228, 10.4018);
