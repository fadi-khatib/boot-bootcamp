DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts(
    name varchar(100) NOT NULL,
    id int(10) NOT NULL AUTO_INCREMENT,
    token varchar(100) NOT NULL,
    es_index_name varchar(100) NOT NULL,
    PRIMARY KEY ( id ),
    UNIQUE KEY ( token , es_index_name )
    )