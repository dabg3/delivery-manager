-- TODO move schema generation instructions to its own script
alter table if exists OrderProductsDetail
    drop constraint if exists PK_Order;
drop table if exists OrderProductsDetail cascade;
drop sequence if exists PRODUCT_DETAIL_SEQ;
create sequence PRODUCT_DETAIL_SEQ start with 1 increment by 1;
create table OrderProductsDetail (
    id bigint not null,
    product_id varchar(255) not null,
    quantity integer not null,
    order_id bigint not null,
    primary key (id)
);

alter table if exists ShoppingOrder
    drop constraint if exists PK_Warehouse;
drop table if exists ShoppingOrder cascade;
drop sequence if exists ORDER_SEQ;
create sequence ORDER_SEQ start with 1 increment by 1;
create table ShoppingOrder (
    id bigint not null,
    latitude float(53) not null,
    longitude float(53) not null,
    state varchar(255) check (state in ('ACCEPTED','DELIVERY')),
    submission_date timestamp(6) not null,
    warehouse_id bigint not null,
    primary key (id)
);

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

alter table if exists OrderProductsDetail
    add constraint PK_Order
    foreign key (order_id)
    references ShoppingOrder;

alter table if exists ShoppingOrder
    add constraint PK_Warehouse
        foreign key (warehouse_id)
            references Warehouse;

-- https://stackoverflow.com/questions/10034636/postgresql-latitude-longitude-query
-- input is in degrees (e.g. 52.34273489, 6.23847) and output is in meters.
CREATE OR REPLACE FUNCTION distance(
    lat1 double precision,
    lon1 double precision,
    lat2 double precision,
    lon2 double precision)
    RETURNS double precision AS
$BODY$
DECLARE
    R integer = 6371e3; -- Meters
    rad double precision = 0.01745329252;

    φ1 double precision = lat1 * rad;
    φ2 double precision = lat2 * rad;
    Δφ double precision = (lat2-lat1) * rad;
    Δλ double precision = (lon2-lon1) * rad;

    a double precision = sin(Δφ/2) * sin(Δφ/2) + cos(φ1) * cos(φ2) * sin(Δλ/2) * sin(Δλ/2);
    c double precision = 2 * atan2(sqrt(a), sqrt(1-a));
BEGIN
    RETURN R * c;
END
$BODY$
    LANGUAGE plpgsql VOLATILE
    COST 100;


insert into Warehouse values
    (nextval('WAREHOUSE_SEQ'), 'pisa', 43.7228, 10.4018),
    (nextval('WAREHOUSE_SEQ'), 'pistoia', 43.9298, 10.9068),
    (nextval('WAREHOUSE_SEQ'), 'firenze', 43.7700, 11.2577);

insert into ShoppingOrder values
    (nextval('ORDER_SEQ'), '43.7189', '10.4131', 'ACCEPTED', '2024-10-01 12:05:06', 1), -- ~1km distance
    (nextval('ORDER_SEQ'), '43.7100', '10.4100', 'ACCEPTED', '2024-10-01 14:42:15', 1); -- ~1.5km distance

insert into OrderProductsDetail values
    (nextval('PRODUCT_DETAIL_SEQ'), 'shampoo', 1, 1),
    (nextval('PRODUCT_DETAIL_SEQ'), 'pizza', 3, 2);
