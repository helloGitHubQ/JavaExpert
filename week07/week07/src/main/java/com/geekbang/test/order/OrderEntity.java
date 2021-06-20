package com.geekbang.test.order;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * 订单
 */
@Entity
@Data
public class OrderEntity {

    @Id @GeneratedValue
    private Long id;
    private Long user_id;
    private String commodities;
    private int status;
    private String deliver_status;
    private Long total_price;
    private Long create_time;
    private Long update_time;

    OrderEntity(long userId, String commodities, long totalPrice) {
        this.user_id = userId;
        this.commodities = commodities;
        this.status = 0;
        this.deliver_status = "{}";
        this.total_price = totalPrice;
        this.create_time = Long.valueOf(String.valueOf(System.currentTimeMillis() / 1000));
        this.update_time = create_time;
    }
}
