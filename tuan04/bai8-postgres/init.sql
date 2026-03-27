CREATE DATABASE myapp;

\c myapp;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO users (name, email) VALUES
  ('Alice', 'alice@example.com'),
  ('Bob', 'bob@example.com');

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    product_name VARCHAR(200),
    price DECIMAL(10, 2)
);

INSERT INTO products (product_name, price) VALUES
  ('Laptop', 999.99),
  ('Phone', 499.99);
