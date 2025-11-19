-- Database: campus_delivery

-- 1. 用户表 (User Table)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX(username)
);

-- 2. 收货地址表 (Address Table)
CREATE TABLE addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    address_details VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. 商家表 (Merchant Table)
CREATE TABLE merchants (
    merchant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    rating DECIMAL(3, 2) DEFAULT 0.00,
    INDEX(name)
);

-- 4. 菜品表 (Dish Table)
CREATE TABLE dishes (
    dish_id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id) ON DELETE CASCADE,
    INDEX(name)
);

-- 5. 订单表 (Order Table)
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    merchant_id INT NOT NULL,
    address_id INT NOT NULL,
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('pending', 'preparing', 'delivering', 'completed', 'cancelled') DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id),
    FOREIGN KEY (address_id) REFERENCES addresses(address_id)
);

-- 6. 订单明细表 (Order Detail Table)
CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    dish_id INT NOT NULL,
    quantity INT NOT NULL,
    price_per_item DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(dish_id)
);

-- 7. 配送员表 (Rider Table)
CREATE TABLE riders (
    rider_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    status ENUM('available', 'delivering', 'offline') DEFAULT 'offline'
);

-- 8. 配送记录表 (Delivery Record Table)
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

-- 9. 评价表 (Review Table)
CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
