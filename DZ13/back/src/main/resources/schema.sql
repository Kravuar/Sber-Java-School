CREATE TABLE account
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    username       VARCHAR2(255) NOT NULL UNIQUE,
    email          VARCHAR2(255) NOT NULL UNIQUE,
    email_verified BOOLEAN      NOT NULL DEFAULT FALSE
);
