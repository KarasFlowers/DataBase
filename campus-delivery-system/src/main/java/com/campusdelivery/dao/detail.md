# 数据操作实现和存储过程
## 商家
1. 商家注册
**涉及的基本表：** merchants
**过程描述：** 新增商家
**代码实现：**
```java
public void addMerchant(Merchant merchant) {
        String sql = "INSERT INTO merchants (name, address, phone_number, rating) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, merchant.getName());
            pstmt.setString(2, merchant.getAddress());
            pstmt.setString(3, merchant.getPhoneNumber());
            pstmt.setBigDecimal(4, merchant.getRating());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added merchant " + merchant.getName());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add merchant " + merchant.getName());
            e.printStackTrace();
        }
    }
```

2. 更新信息
**涉及的基本表：** merchants
**过程描述：** 更新商家信息
**代码实现：**
```java
public void updateMerchant(Merchant merchant) {
        String sql = "UPDATE merchants SET name = ?, address = ?, phone_number = ?, rating = ? WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, merchant.getName());
            pstmt.setString(2, merchant.getAddress());
            pstmt.setString(3, merchant.getPhoneNumber());
            pstmt.setBigDecimal(4, merchant.getRating());
            pstmt.setInt(5, merchant.getMerchantId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated merchant " + merchant.getName());
            } else {
                System.out.println("DAO: No merchant found with ID " + merchant.getMerchantId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update merchant " + merchant.getName());
            e.printStackTrace();
        }
    }
```
3. 删除
**涉及的基本表：** merchants
**过程描述：** 删除商家
**代码实现：**
```java
public void deleteMerchant(int merchantId) {
        String sql = "DELETE FROM merchants WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted merchant with ID " + merchantId);
            } else {
                System.out.println("DAO: No merchant found with ID " + merchantId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete merchant with ID " + merchantId);
            e.printStackTrace();
        }
    }
```

## 用户
1. 用户注册
**涉及的基本表：** users
**过程描述：** 新增用户
**代码实现：**
```java
public void addUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, phone_number) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added user " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add user " + user.getUsername());
            e.printStackTrace();
        }
    }
```
2. 更新用户信息
**涉及的基本表：** users
**过程描述：** 更新用户信息
**代码实现：**
```java
public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, phone_number = ? WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setInt(4, user.getUserId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated user " + user.getUsername());
            } else {
                System.out.println("DAO: No user found with ID " + user.getUserId() + " to update.");
            }

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update user " + user.getUsername());
            e.printStackTrace();
        }
    }
```
3. 删除用户
**涉及的基本表：** users
**过程描述：** 删除用户
**代码实现：**
```java
public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted user with ID " + userId);
            } else {
                System.out.println("DAO: No user found with ID " + userId + " to delete.");
            }

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete user with ID " + userId);
            e.printStackTrace();
        }
    }
```

## 配送员
1. 配送员注册
**涉及的基本表：** riders
**过程描述：** 新增配送员
**代码实现：**
```java
public void addRider(Rider rider) {
        String sql = "INSERT INTO riders (name, phone_number, status) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rider.getName());
            pstmt.setString(2, rider.getPhoneNumber());
            pstmt.setString(3, rider.getStatus());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added rider " + rider.getName());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add rider " + rider.getName());
            e.printStackTrace();
        }
    }
```
2. 更新配送员信息
**涉及的基本表：** riders
**过程描述：** 更新配送员信息
**代码实现：**
```java
public void updateRider(Rider rider) {
        String sql = "UPDATE riders SET name = ?, phone_number = ?, status = ? WHERE rider_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rider.getName());
            pstmt.setString(2, rider.getPhoneNumber());
            pstmt.setString(3, rider.getStatus());
            pstmt.setInt(4, rider.getRiderId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated rider " + rider.getName());
            } else {
                System.out.println("DAO: No rider found with ID " + rider.getRiderId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update rider " + rider.getName());
            e.printStackTrace();
        }
    }
```
3. 删除配送员
**涉及的基本表：** riders
**过程描述：** 删除配送员
**代码实现：**
```java
public void deleteRider(int riderId) {
        String sql = "DELETE FROM riders WHERE rider_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riderId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted rider with ID " + riderId);
            } else {
                System.out.println("DAO: No rider found with ID " + riderId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete rider with ID " + riderId);
            e.printStackTrace();
        }
    }
```

## 订单
1. 创建订单
**涉及的基本表：** orders
**过程描述：** 创建一个新订单
**代码实现：**
```java
public int createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, merchant_id, address_id, total_price, status) VALUES (?, ?, ?, ?, ?)";
        int orderId = 0;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getMerchantId());
            pstmt.setInt(3, order.getAddressId());
            pstmt.setBigDecimal(4, order.getTotalPrice());
            pstmt.setString(5, order.getStatus());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    }
                }
                System.out.println("DAO: Successfully created order with ID " + orderId);
            } else {
                System.out.println("DAO: Failed to create order for user ID " + order.getUserId());
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to create order for user ID " + order.getUserId());
            e.printStackTrace();
        }
        return orderId;
    }
```
2. 更新订单
**涉及的基本表：** orders
**过程描述：** 更新订单信息
**代码实现：**
```java
public void updateOrder(Order order) {
        String sql = "UPDATE orders SET user_id = ?, merchant_id = ?, address_id = ?, total_price = ?, status = ? WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getMerchantId());
            pstmt.setInt(3, order.getAddressId());
            pstmt.setBigDecimal(4, order.getTotalPrice());
            pstmt.setString(5, order.getStatus());
            pstmt.setInt(6, order.getOrderId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated order with ID " + order.getOrderId());
            } else {
                System.out.println("DAO: No order found with ID " + order.getOrderId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update order with ID " + order.getOrderId());
            e.printStackTrace();
        }
    }
```
3. 删除订单
**涉及的基本表：** orders
**过程描述：** 删除一个订单
**代码实现：**
```java
public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted order with ID " + orderId);
            } else {
                System.out.println("DAO: No order found with ID " + orderId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete order with ID " + orderId);
            e.printStackTrace();
        }
    }
```
4. 更新订单状态
**涉及的基本表：** orders
**过程描述：** 更新一个订单的状态
**代码实现：**
```java
public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated status for order ID " + orderId + " to " + status);
            } else {
                System.out.println("DAO: No order found with ID " + orderId + " to update status.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update status for order ID " + orderId);
            e.printStackTrace();
        }
    }
```
## 地址
1. 新增地址
**涉及的基本表：** addresses
**过程描述：** 新增地址
**代码实现：**
```java
public void addAddress(Address address) {
        String sql = "INSERT INTO addresses (user_id, address_details, is_default) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, address.getUserId());
            pstmt.setString(2, address.getAddressDetails());
            pstmt.setBoolean(3, address.isDefault());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added address for user " + address.getUserId());

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add address for user " + address.getUserId());
            e.printStackTrace();
        }
    }
```
1. 更新地址
**涉及的基本表：** addresses
**过程描述：** 更新地址
**代码实现：**
```java
public void updateAddress(Address address) {
        String sql = "UPDATE addresses SET user_id = ?, address_details = ?, is_default = ? WHERE address_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, address.getUserId());
            pstmt.setString(2, address.getAddressDetails());
            pstmt.setBoolean(3, address.isDefault());
            pstmt.setInt(4, address.getAddressId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
```
1. 删除地址
**涉及的基本表：** addresses
**过程描述：** 删除地址
**代码实现：**
```java
public void deleteAddress(int addressId) {
        String sql = "DELETE FROM addresses WHERE address_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addressId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
```
1. 根据ID获取地址
**涉及的基本表：** addresses
**过程描述：** 根据ID获取地址
**代码实现：**
```java
public Address getAddressById(int addressId) {
        String sql = "SELECT * FROM addresses WHERE address_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addressId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getInt("address_id"));
                address.setUserId(rs.getInt("user_id"));
                address.setAddressDetails(rs.getString("address_details"));
                address.setDefault(rs.getBoolean("is_default"));
                return address;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
```
1. 根据用户ID获取地址
**涉及的基本表：** addresses
**过程描述：** 根据用户ID获取地址
**代码实现：**
```java
public List<Address> getAddressesByUserId(int userId) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM addresses WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getInt("address_id"));
                address.setUserId(rs.getInt("user_id"));
                address.setAddressDetails(rs.getString("address_details"));
                address.setDefault(rs.getBoolean("is_default"));
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }
```
## 配送记录
1. 新增配送记录
**涉及的基本表：** delivery_records
**过程描述：** 新增配送记录
**代码实现：**
```java
public void addDeliveryRecord(DeliveryRecord record) {
        String sql = "INSERT INTO delivery_records (order_id, rider_id, status, pickup_time, delivery_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, record.getOrderId());
            // rider_id can be null
            if (record.getRiderId() != 0) {
                pstmt.setInt(2, record.getRiderId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, record.getStatus());
            pstmt.setTimestamp(4, record.getPickupTime() != null ? new Timestamp(record.getPickupTime().getTime()) : null);
            pstmt.setTimestamp(5, record.getDeliveryTime() != null ? new Timestamp(record.getDeliveryTime().getTime()) : null);
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added delivery record for order ID " + record.getOrderId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add delivery record for order ID " + record.getOrderId());
            e.printStackTrace();
        }
    }
```
2. 更新配送记录
**涉及的基本表：** delivery_records
**过程描述：** 更新配送记录
**代码实现：**
```java
public void updateDeliveryRecord(DeliveryRecord record) {
        String sql = "UPDATE delivery_records SET rider_id = ?, status = ?, pickup_time = ?, delivery_time = ? WHERE delivery_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // rider_id can be null
            if (record.getRiderId() != 0) {
                pstmt.setInt(1, record.getRiderId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, record.getStatus());
            pstmt.setTimestamp(3, record.getPickupTime() != null ? new Timestamp(record.getPickupTime().getTime()) : null);
            pstmt.setTimestamp(4, record.getDeliveryTime() != null ? new Timestamp(record.getDeliveryTime().getTime()) : null);
            pstmt.setInt(5, record.getDeliveryId());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully updated delivery record for ID " + record.getDeliveryId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update delivery record for ID " + record.getDeliveryId());
            e.printStackTrace();
        }
    }
```
3. 删除配送记录
**涉及的基本表：** delivery_records
**过程描述：** 删除配送记录
**代码实现：**
```java
public void deleteDeliveryRecord(int deliveryId) {
        String sql = "DELETE FROM delivery_records WHERE delivery_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted delivery record with ID " + deliveryId);
            } else {
                System.out.println("DAO: No delivery record found with ID " + deliveryId + " to delete.");
            }

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete delivery record with ID " + deliveryId);
            e.printStackTrace();
        }
    }
```

## 菜品
1. 新增菜品
**涉及的基本表：** dishes
**过程描述：** 新增菜品
**代码实现：**
```java
public void addDish(Dish dish) {
        String sql = "INSERT INTO dishes (merchant_id, name, description, price, is_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dish.getMerchantId());
            pstmt.setString(2, dish.getName());
            pstmt.setString(3, dish.getDescription());
            pstmt.setBigDecimal(4, dish.getPrice());
            pstmt.setBoolean(5, dish.isAvailable());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added dish " + dish.getName() + " for merchant ID " + dish.getMerchantId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add dish " + dish.getName());
            e.printStackTrace();
        }
    }
```
2. 更新菜品
**涉及的基本表：** dishes
**过程描述：** 更新菜品
**代码实现：**
```java
public void updateDish(Dish dish) {
        String sql = "UPDATE dishes SET name = ?, description = ?, price = ?, is_available = ? WHERE dish_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dish.getName());
            pstmt.setString(2, dish.getDescription());
            pstmt.setBigDecimal(3, dish.getPrice());
            pstmt.setBoolean(4, dish.isAvailable());
            pstmt.setInt(5, dish.getDishId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated dish " + dish.getName());
            } else {
                System.out.println("DAO: No dish found with ID " + dish.getDishId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update dish " + dish.getName());
            e.printStackTrace();
        }
    }
```
3. 删除菜品
**涉及的基本表：** dishes
**过程描述：** 删除菜品
**代码实现：**
```java
public void deleteDish(int dishId) {
        String sql = "DELETE FROM dishes WHERE dish_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dishId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted dish with ID " + dishId);
            } else {
                System.out.println("DAO: No dish found with ID " + dishId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete dish with ID " + dishId);
            e.printStackTrace();
        }
    }
```

## 订单详情
1. 新增订单详情
**涉及的基本表：** order_details
**过程描述：** 新增订单详情
**代码实现：**
```java
public void addOrderDetail(OrderDetail detail) {
        String sql = "INSERT INTO order_details (order_id, dish_id, quantity, price_per_item) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detail.getOrderId());
            pstmt.setInt(2, detail.getDishId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setBigDecimal(4, detail.getPricePerItem());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added order detail for order ID " + detail.getOrderId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add order detail for order ID " + detail.getOrderId());
            e.printStackTrace();
        }
    }
```
2. 更新订单详情
**涉及的基本表：** order_details
**过程描述：** 更新订单详情
**代码实现：**
```java
public void updateOrderDetail(OrderDetail detail) {
        String sql = "UPDATE order_details SET order_id = ?, dish_id = ?, quantity = ?, price_per_item = ? WHERE order_detail_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detail.getOrderId());
            pstmt.setInt(2, detail.getDishId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setBigDecimal(4, detail.getPricePerItem());
            pstmt.setInt(5, detail.getOrderDetailId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated order detail with ID " + detail.getOrderDetailId());
            } else {
                System.out.println("DAO: No order detail found with ID " + detail.getOrderDetailId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update order detail with ID " + detail.getOrderDetailId());
            e.printStackTrace();
        }
    }
```
3. 删除订单详情
**涉及的基本表：** order_details
**过程描述：** 删除订单详情
**代码实现：**
```java
public void deleteOrderDetail(int orderDetailId) {
        String sql = "DELETE FROM order_details WHERE order_detail_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderDetailId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted order detail with ID " + orderDetailId);
            } else {
                System.out.println("DAO: No order detail found with ID " + orderDetailId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete order detail with ID " + orderDetailId);
            e.printStackTrace();
        }
    }
```
## 评价
1. 新增评价
**涉及的基本表：** reviews
**过程描述：** 新增评价
**代码实现：**
```java
public void addReview(Review review) {
        String sql = "INSERT INTO reviews (order_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getOrderId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added review for order ID " + review.getOrderId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add review for order ID " + review.getOrderId());
            e.printStackTrace();
        }
    }
```
2. 更新评价
**涉及的基本表：** reviews
**过程描述：** 更新评价
**代码实现：**
```java
public void updateReview(Review review) {
        String sql = "UPDATE reviews SET order_id = ?, user_id = ?, rating = ?, comment = ? WHERE review_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getOrderId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.setInt(5, review.getReviewId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated review with ID " + review.getReviewId());
            } else {
                System.out.println("DAO: No review found with ID " + review.getReviewId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update review with ID " + review.getReviewId());
            e.printStackTrace();
        }
    }
```
3. 删除评价
**涉及的基本表：** reviews
**过程描述：** 删除评价
**代码实现：**
```java
public void deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted review with ID " + reviewId);
            } else {
                System.out.println("DAO: No review found with ID " + reviewId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete review with ID " + reviewId);
            e.printStackTrace();
        }
    }
```

