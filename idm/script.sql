CREATE TABLE privilege_levels(
    plevel int NOT NULL,
    pname varchar(20) NOT NULL,
    PRIMARY KEY(plevel)
    );

CREATE TABLE session_status(
    statusid int NOT NULL,
    status varchar(20) NOT NULL,
    PRIMARY KEY(statusid)
    );

CREATE TABLE user_status(
    statusid int NOT NULL,
    status varchar(20) NOT NULL,
    PRIMARY KEY(statusid)
    );

CREATE TABLE users(
    id int NOT NULL AUTO_INCREMENT,
    email varchar(50) NOT NULL UNIQUE,
    status int NOT NULL,
    plevel int NOT NULL,
    salt varchar(8) NOT NULL,
    pword varchar(128) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (plevel) REFERENCES privilege_levels(plevel) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (status) REFERENCES user_status(statusid) ON UPDATE CASCADE ON DELETE CASCADE
    );

CREATE TABLE sessions(
    sessionID varchar(128) NOT NULL,
    email varchar(50) NOT NULL,
    status int NOT NULL,
    timeCreated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastUsed timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exprTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(sessionID),
    FOREIGN KEY (status) REFERENCES session_status(statusid) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (email) REFERENCES users(email) ON UPDATE CASCADE ON DELETE CASCADE
    );


