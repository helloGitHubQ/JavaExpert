package com.geekbang.week0802.mapper;

import com.geekbang.week0802.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Q
 */
@Mapper
public interface OrderMapper {

    void insertOne(Order order);

    List<Map<String, Object>> query(Map<String, Object> condition);

    void delete(long orderId);

    void update(Order order);
}
