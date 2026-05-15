CREATE DATABASE IF NOT EXISTS sba_db;
USE sba_db;

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE,
    image VARCHAR(255),
    description TEXT
);
