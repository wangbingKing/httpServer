#CREATE SCHEMA `gamedata` DEFAULT CHARACTER SET utf8mb4 ;
USE gamedata;
CREATE TABLE score_game (
    uuid VARCHAR(28) PRIMARY KEY,
    ename VARCHAR(28),
    sex INT(1),
    headurl VARCHAR(100),
    score INT(8)
);