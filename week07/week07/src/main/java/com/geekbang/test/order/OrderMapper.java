package com.geekbang.test.order;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderMapper {

    void insertOne(OrderEntity orderEntity);
    void insertMany(List<OrderEntity> orders);
    void delete(Long id);
    void update(OrderEntity orderEntity);
    List<Map<String, Object>> query(Map<String, Object> condition);
}
