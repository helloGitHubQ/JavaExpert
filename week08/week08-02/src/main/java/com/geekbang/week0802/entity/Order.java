package com.geekbang.week0802.entity;

import lombok.Data;

/**
 * 订单实体类
 *
 * @author Q
 */
@Data
public class Order {
    private long orderId;
    private long userId;

    public Order(long orderId, long userId) {
        this.orderId = orderId;
        this.userId = userId;
    }
}
