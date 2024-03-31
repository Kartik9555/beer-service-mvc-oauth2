drop table if exists beer_order_line;
drop table if exists beer_order;

CREATE TABLE beer_order
(
    id
                       varchar(36) NOT NULL,
    created_date       TIMESTAMP(6),
    customer_ref       varchar(255) DEFAULT NULL,
    last_modified_date TIMESTAMP(6),
    version            bigint       DEFAULT NULL,
    customer_id        varchar(36)  DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT beer_order_fk FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE beer_order_line
(
    id                 varchar(36) NOT NULL,
    beer_id            varchar(36) DEFAULT NULL,
    created_date       TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    order_quantity     int         DEFAULT NULL,
    quantity_allocated int         DEFAULT NULL,
    version            bigint      DEFAULT NULL,
    beer_order_id      varchar(36) DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT beer_order_line_fk1 FOREIGN KEY (beer_order_id) REFERENCES beer_order (id),
    CONSTRAINT beer_order_line_fk2 FOREIGN KEY (beer_id) REFERENCES beer (id)
);