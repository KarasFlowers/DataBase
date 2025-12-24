-- Create Database
CREATE DATABASE IF NOT EXISTS campus_delivery;
USE campus_delivery;

-- 1. 用户表 (User Table) - 独立表
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    role ENUM('admin', 'user', 'merchant', 'rider') NOT NULL DEFAULT 'user',
    entity_id INT NULL COMMENT 'Corresponds to merchant_id or rider_id based on role',
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX(username)
);

-- 2. 商家表 (Merchant Table) - 独立表
CREATE TABLE merchants (
    merchant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    rating DECIMAL(3, 2) DEFAULT 0.00,
    sales_count INT NOT NULL DEFAULT 0,
    average_order_price DECIMAL(10, 2) DEFAULT 0.00,
    open_time TIME NULL,
    close_time TIME NULL,
    is_manually_closed BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX(name)
);

-- 3. 配送员表 (Rider Table) - 独立表
CREATE TABLE riders (
    rider_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    status ENUM('available', 'delivering', 'offline') DEFAULT 'offline'
);

-- 4. 收货地址表 (Address Table) - 依赖 users
CREATE TABLE addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    address_details VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 5. 菜品分类表 (Dish Category Table) - 依赖 merchants
-- 注意：必须在 dishes 表之前创建，因为 dishes 引用了它
CREATE TABLE dish_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id) ON DELETE CASCADE,
    UNIQUE (merchant_id, category_name)
);

-- 6. 菜品表 (Dish Table) - 依赖 merchants 和 dish_categories
CREATE TABLE dishes (
    dish_id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES dish_categories(category_id) ON DELETE SET NULL,
    INDEX(name)
);

-- 7. 订单表 (Order Table) - 依赖 users, merchants, addresses
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    merchant_id INT NOT NULL,
    address_id INT NOT NULL,
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('unpaid', 'preparing', 'ready_for_pickup', 'delivering', 'completed', 'cancelled') DEFAULT 'unpaid',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id),
    FOREIGN KEY (address_id) REFERENCES addresses(address_id)
);

-- 8. 订单明细表 (Order Detail Table) - 依赖 orders, dishes
CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    dish_id INT NOT NULL,
    quantity INT NOT NULL,
    price_per_item DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(dish_id)
);

-- 9. 配送记录表 (Delivery Record Table) - 依赖 orders, riders
CREATE TABLE delivery_records (
    delivery_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    rider_id INT,
    status ENUM('assigned', 'picked_up', 'delivered') NOT NULL,
    pickup_time DATETIME,
    delivery_time DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (rider_id) REFERENCES riders(rider_id)
);

-- 10. 评价表 (Review Table) - 依赖 orders, users
CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_seen_by_merchant BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


-- =============================================
-- Stored Procedures & Triggers
-- =============================================
DELIMITER //

-- ----------------------------------------------------------------
-- Stored Procedure to update all merchant statistics
-- ----------------------------------------------------------------
CREATE PROCEDURE update_merchant_statistics(IN p_merchant_id INT)
BEGIN
    DECLARE avg_price DECIMAL(10, 2);
    DECLARE total_sales INT;

    -- Calculate average order price from completed orders
    SELECT COALESCE(AVG(total_price), 0.00) INTO avg_price
    FROM orders
    WHERE merchant_id = p_merchant_id AND status = 'completed';

    -- Calculate total sales count from completed orders
    SELECT COUNT(*) INTO total_sales
    FROM orders
    WHERE merchant_id = p_merchant_id AND status = 'completed';

    -- Update the merchants table with the new statistics
    UPDATE merchants
    SET average_order_price = avg_price,
        sales_count = total_sales
    WHERE merchant_id = p_merchant_id;
END;
//

-- ----------------------------------------------------------------
-- Triggers on REVIEWS table
-- ----------------------------------------------------------------

-- On Review Insert: Recalculate merchant rating
CREATE TRIGGER reviews_after_insert
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN
    DECLARE merchant_id_val INT;
    SELECT o.merchant_id INTO merchant_id_val FROM orders o WHERE o.order_id = NEW.order_id;
    UPDATE merchants m
    SET m.rating = (
        SELECT COALESCE(AVG(r.rating), 0.00)
        FROM reviews r JOIN orders o2 ON r.order_id = o2.order_id
        WHERE o2.merchant_id = merchant_id_val
    )
    WHERE m.merchant_id = merchant_id_val;
END;
//

-- On Review Update: Recalculate merchant rating
CREATE TRIGGER reviews_after_update
AFTER UPDATE ON reviews
FOR EACH ROW
BEGIN
    DECLARE merchant_id_val INT;
    SELECT o.merchant_id INTO merchant_id_val FROM orders o WHERE o.order_id = NEW.order_id;
    UPDATE merchants m
    SET m.rating = (
        SELECT COALESCE(AVG(r.rating), 0.00)
        FROM reviews r JOIN orders o2 ON r.order_id = o2.order_id
        WHERE o2.merchant_id = merchant_id_val
    )
    WHERE m.merchant_id = merchant_id_val;
END;
//

-- On Review Delete: Recalculate merchant rating
CREATE TRIGGER reviews_after_delete
AFTER DELETE ON reviews
FOR EACH ROW
BEGIN
    DECLARE merchant_id_val INT;
    SELECT o.merchant_id INTO merchant_id_val FROM orders o WHERE o.order_id = OLD.order_id;
    IF merchant_id_val IS NOT NULL THEN
        UPDATE merchants m
        SET m.rating = (
            SELECT COALESCE(AVG(r.rating), 0.00)
            FROM reviews r JOIN orders o2 ON r.order_id = o2.order_id
            WHERE o2.merchant_id = merchant_id_val
        )
        WHERE m.merchant_id = merchant_id_val;
    END IF;
END;
//

-- ----------------------------------------------------------------
-- Triggers on ORDERS table
-- ----------------------------------------------------------------

-- On Order Insert: Update statistics if an order is created as 'completed' (unlikely but safe)
CREATE TRIGGER orders_after_insert
AFTER INSERT ON orders
FOR EACH ROW
BEGIN
    IF NEW.status = 'completed' THEN
        CALL update_merchant_statistics(NEW.merchant_id);
    END IF;
END;
//

-- On Order Update: Update statistics if status changes to/from 'completed'
CREATE TRIGGER orders_after_update
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF OLD.status <> 'completed' AND NEW.status = 'completed' THEN
        CALL update_merchant_statistics(NEW.merchant_id);
    ELSEIF OLD.status = 'completed' AND NEW.status <> 'completed' THEN
        CALL update_merchant_statistics(NEW.merchant_id);
    END IF;
END;
//

-- On Order Delete: Clean up associated data and update statistics
CREATE TRIGGER orders_after_delete
AFTER DELETE ON orders
FOR EACH ROW
BEGIN
    -- Update statistics for the merchant
    CALL update_merchant_statistics(OLD.merchant_id);
    
    -- Delete the associated review, which will then fire the reviews_after_delete trigger
    DELETE FROM reviews WHERE order_id = OLD.order_id;
END;
//

-- Stored Procedure to get user orders by date range
CREATE PROCEDURE get_user_orders_by_date_range(
    IN p_user_id INT,
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT
        o.order_id,
        o.user_id,
        o.merchant_id,
        o.address_id,
        o.order_time,
        o.total_price,
        o.status,
        m.name AS merchant_name,
        a.address_details AS delivery_address
    FROM
        orders o
    JOIN
        merchants m ON o.merchant_id = m.merchant_id
    JOIN
        addresses a ON o.address_id = a.address_id
    WHERE
        o.user_id = p_user_id
        AND o.order_time BETWEEN p_start_date AND p_end_date
    ORDER BY
        o.order_time DESC;
END;
//

DELIMITER ;
