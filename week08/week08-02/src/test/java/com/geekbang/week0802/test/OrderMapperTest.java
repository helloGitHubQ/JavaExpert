package com.geekbang.week0802.test;

import com.geekbang.week0802.entity.Order;
import com.geekbang.week0802.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@MapperScan("com.geekbang.week0802.mapper")
public class OrderMapperTest {

    @Resource
    private OrderMapper orderMapper;

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void test() {
        // 通过sharding插入数据，通过sharding自己的日志输出看出插入不同的数据库和表
        orderMapper.insertOne(new Order(1L, 1L));
        orderMapper.insertOne(new Order(2L, 2L));

        // 只传user_id，看到单库进行了所有表的查询
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("user_id", 1L);

        List<Map<String, Object>> orderQuery = orderMapper.query(condition);
        assert orderQuery.size() == 1;
        for (Map item : orderQuery) {
            System.out.println(item.toString());
        }

        // 只传order_id，看到进行了多库单表的查询
        condition = new HashMap<>(1);
        condition.put("order_id", 1L);
        orderQuery = orderMapper.query(condition);
        assert orderQuery.size() == 1;
        for (Map item : orderQuery) {
            System.out.println(item.toString());
        }

        // 传入order_id和user_id，看到进行单库单表的查询
        condition = new HashMap<>(2);
        condition.put("order_id", 2L);
        condition.put("user_id", 2L);
        orderQuery = orderMapper.query(condition);
        assert orderQuery.size() == 1;
        for (Map item : orderQuery) {
            System.out.println(item.toString());
        }
    }
}
