drop table if exists beer;

drop table if exists customer;

create table beer
(
    id               varchar(36)    not null,
    name             varchar(50)    not null,
    beer_style       tinyint        not null,
    created_date     TIMESTAMP(6),
    price            decimal(38, 2) not null,
    quantity_on_hand integer,
    upc              varchar(255)   not null,
    updated_date     TIMESTAMP(6),
    version          integer,
    primary key (id)
);

create table customer
(
    id                 varchar(36) not null,
    created_date       TIMESTAMP(6),
    name               varchar(255),
    last_modified_date TIMESTAMP(6),
    version            integer,
    primary key (id)
);