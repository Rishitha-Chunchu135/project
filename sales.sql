CREATE DATABASE online_retail_db;
USE online_retail_db;


CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50)
);


CREATE TABLE products (
    product_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date DATE NOT NULL,

    FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id)
);


CREATE TABLE order_items (
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,

    PRIMARY KEY (order_id, product_id),

    FOREIGN KEY (order_id)
        REFERENCES orders(order_id),

    FOREIGN KEY (product_id)
        REFERENCES products(product_id)
);



INSERT INTO customers (customer_id, name, city)
VALUES
(1, 'Rahul', 'Hyderabad'),
(2, 'Priya', 'Chennai'),
(3, 'Arjun', 'Bangalore'),
(4, 'Sneha', 'Mumbai'),
(5, 'Kiran', 'Hyderabad'),
(6, 'Anjali', 'Delhi'),
(7, 'Ravi', 'Pune'),
(8, 'Neha', 'Chennai'),
(9, 'Vikram', 'Kolkata'),
(10, 'Divya', 'Bangalore');


INSERT INTO products (product_id, name, category, price)
VALUES
(101, 'Laptop', 'Electronics', 55000.00),
(102, 'Smartphone', 'Electronics', 25000.00),
(103, 'Headphones', 'Electronics', 2000.00),
(104, 'Keyboard', 'Accessories', 1500.00),
(105, 'Mouse', 'Accessories', 800.00),
(106, 'Backpack', 'Fashion', 1800.00),
(107, 'Shoes', 'Fashion', 3000.00),
(108, 'T-Shirt', 'Fashion', 1200.00),
(109, 'Watch', 'Accessories', 4500.00),
(110, 'Tablet', 'Electronics', 18000.00);



INSERT INTO orders (order_id, customer_id, order_date)
VALUES
(1001, 1, '2026-01-05'),
(1002, 2, '2026-01-10'),
(1003, 3, '2026-01-15'),
(1004, 1, '2026-02-02'),
(1005, 4, '2026-02-12'),
(1006, 5, '2026-02-20'),
(1007, 2, '2026-03-03'),
(1008, 6, '2026-03-10'),
(1009, 3, '2026-03-18'),
(1010, 7, '2026-04-01'),
(1011, 8, '2026-04-12'),
(1012, 1, '2026-04-25'),
(1013, 4, '2026-05-05'),
(1014, 5, '2026-05-15'),
(1015, 2, '2026-05-25'),
(1016, 9, '2026-06-05'),
(1017, 10, '2026-06-15'),
(1018, 3, '2026-06-25'),
(1019, 1, '2026-07-05'),
(1020, 6, '2026-07-20');



INSERT INTO order_items (order_id, product_id, quantity)
VALUES
(1001, 101, 1),
(1001, 103, 2),

(1002, 102, 1),
(1002, 105, 2),

(1003, 104, 1),
(1003, 106, 2),

(1004, 101, 1),
(1004, 105, 1),

(1005, 107, 2),
(1005, 108, 3),

(1006, 110, 1),
(1006, 103, 2),

(1007, 102, 2),
(1007, 109, 1),

(1008, 106, 1),
(1008, 108, 2),

(1009, 101, 1),
(1009, 104, 2),

(1010, 107, 1),
(1010, 105, 3),

(1011, 102, 1),
(1011, 103, 1),

(1012, 110, 2),
(1012, 105, 1),

(1013, 109, 2),
(1013, 108, 2),

(1014, 101, 1),
(1014, 103, 2),

(1015, 102, 1),
(1015, 107, 1),

(1016, 106, 3),
(1016, 108, 2),

(1017, 110, 1),
(1017, 104, 2),

(1018, 101, 1),
(1018, 102, 1),

(1019, 103, 3),
(1019, 105, 2),

(1020, 107, 2),
(1020, 109, 1);



CREATE INDEX idx_orders_customer
ON orders(customer_id);

CREATE INDEX idx_orders_date
ON orders(order_date);

CREATE INDEX idx_order_items_product
ON order_items(product_id);




#top selling products

SELECT
    p.product_id,
    p.name AS product_name,
    p.category,
    SUM(oi.quantity) AS total_quantity_sold
FROM products p
JOIN order_items oi
    ON p.product_id = oi.product_id
GROUP BY
    p.product_id,
    p.name,
    p.category
ORDER BY total_quantity_sold DESC;



#Identify Most Valuable Customers

SELECT
    c.customer_id,
    c.name AS customer_name,
    c.city,
    SUM(oi.quantity * p.price) AS total_spent
FROM customers c
JOIN orders o
    ON c.customer_id = o.customer_id
JOIN order_items oi
    ON o.order_id = oi.order_id
JOIN products p
    ON oi.product_id = p.product_id
GROUP BY
    c.customer_id,
    c.name,
    c.city
ORDER BY total_spent DESC;


#Top 3 Customers by Spending


SELECT
    c.customer_id,
    c.name AS customer_name,
    SUM(oi.quantity * p.price) AS total_spent
FROM customers c
JOIN orders o
    ON c.customer_id = o.customer_id
JOIN order_items oi
    ON o.order_id = oi.order_id
JOIN products p
    ON oi.product_id = p.product_id
GROUP BY
    c.customer_id,
    c.name
ORDER BY total_spent DESC
LIMIT 3;


#Monthly Revenue With Month Name

SELECT
    YEAR(o.order_date) AS year,
    MONTHNAME(o.order_date) AS month,
    SUM(oi.quantity * p.price) AS monthly_revenue
FROM orders o
JOIN order_items oi
    ON o.order_id = oi.order_id
JOIN products p
    ON oi.product_id = p.product_id
GROUP BY
    YEAR(o.order_date),
    MONTH(o.order_date),
    MONTHNAME(o.order_date)
ORDER BY
    YEAR(o.order_date),
    MONTH(o.order_date);
    
#Category-Wise Product Details

SELECT
    p.category,
    p.name AS product_name,
    SUM(oi.quantity) AS units_sold,
    SUM(oi.quantity * p.price) AS revenue
FROM products p
JOIN order_items oi
    ON p.product_id = oi.product_id
GROUP BY
    p.category,
    p.product_id,
    p.name
ORDER BY
    p.category,
    revenue DESC;
    
    #Detect Customers Who Have Not Purchased Recently
    
    SELECT
    c.customer_id,
    c.name,
    c.city,
    MAX(o.order_date) AS last_order_date
FROM customers c
LEFT JOIN orders o
    ON c.customer_id = o.customer_id
GROUP BY
    c.customer_id,
    c.name,
    c.city
HAVING
    MAX(o.order_date) < DATE_SUB('2026-07-20', INTERVAL 60 DAY)
    OR MAX(o.order_date) IS NULL;
    
#total revenue
SELECT
    SUM(oi.quantity * p.price) AS total_revenue
FROM order_items oi
JOIN products p
    ON oi.product_id = p.product_id;
    
    
#sales analysis

CREATE VIEW sales_details AS
SELECT
    o.order_id,
    o.order_date,
    c.customer_id,
    c.name AS customer_name,
    c.city,
    p.product_id,
    p.name AS product_name,
    p.category,
    p.price,
    oi.quantity,
    oi.quantity * p.price AS total_amount
FROM orders o
JOIN customers c
    ON o.customer_id = c.customer_id
JOIN order_items oi
    ON o.order_id = oi.order_id
JOIN products p
    ON oi.product_id = p.product_id; 