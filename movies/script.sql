CREATE TABLE genres(
    id int NOT NULL AUTO_INCREMENT,
    name varchar(32) NOT NULL,
    PRIMARY KEY(id)
    );

CREATE TABLE movies(
    id varchar(10) NOT NULL,
    title varchar(100) NOT NULL,
    year int NOT NULL,
    director varchar(100) NOT NULL,
    backdrop_path varchar(256),
    budget int DEFAULT 0,
    overview varchar(8192),
    poster_path varchar(256),
    revenue int DEFAULT 0,
    hidden int DEFAULT 0,
    PRIMARY KEY(id)
    );

CREATE TABLE genres_in_movies(
    genreId int NOT NULL,
    movieId varchar(10) NOT NULL,
    PRIMARY KEY(genreId, movieId),
    FOREIGN KEY (genreId) REFERENCES genres(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON UPDATE CASCADE ON DELETE CASCADE
    );

CREATE INDEX movieId ON genres_in_movies(movieId);

CREATE TABLE ratings(
    movieId varchar(10) NOT NULL,
    rating float NOT NULL,
    numVotes int NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON UPDATE CASCADE ON DELETE CASCADE
    );

CREATE TABLE stars(
    id varchar(10) NOT NULL,
    name varchar(100) NOT NULL,
    birthYear int,
    PRIMARY KEY(id)
    );

CREATE TABLE stars_in_movies(
   starId varchar(10) NOT NULL,
   movieId varchar(10) NOT NULL,
   PRIMARY KEY(starId, movieId),
   FOREIGN KEY (starId) REFERENCES stars(id) ON UPDATE CASCADE ON DELETE CASCADE,
   FOREIGN KEY (movieId) REFERENCES movies(id) ON UPDATE CASCADE ON DELETE CASCADE
   );

CREATE INDEX movieId ON stars_in_movies(movieId);
