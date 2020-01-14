DROP TABLE IF EXISTS Users;

CREATE TABLE Users(
    NAME varchar(100) NOT NULL,
    ID int(10) NOT NULL AUTO_INCREMENT,
    TOKEN varchar(100) NOT NULL,
    ES_INDEX_NAME varchar(100) NOT NULL,
    PRIMARY KEY ( ID ),
    UNIQUE KEY ( TOKEN , ES_INDEX_NAME )
    )