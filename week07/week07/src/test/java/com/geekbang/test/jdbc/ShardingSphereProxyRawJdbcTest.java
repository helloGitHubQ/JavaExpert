package com.geekbang.test.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ShardingSphere Proxy Raw JDBC测试
 */
public class ShardingSphereProxyRawJdbcTest {

    @Test @Transactional
    public void test() throws SQLException {
        System.out.println("Start test");
        DataSource SSDataSource = createShardingSphereProxyDataSource();
        DataSource slaveDataSource = createSlaveDataSource();

        // 测试通过SSP是否数据修改和查询正常
        Connection ssCon = SSDataSource.getConnection();
        Statement ssState = ssCon.createStatement();
        ssState.execute("UPDATE `users` SET `money` = '200' WHERE (`id` = '1');");

        ResultSet ret = ssState.executeQuery("select * from test.users where id=1;");
        while (ret.next()) {
            System.out.println(ret.getLong("money"));
            assert ret.getLong("money") == 200;
        }

        // 测试从库是否数据同步更改
        Connection slaveCon = slaveDataSource.getConnection();
        Statement slaveState = slaveCon.createStatement();
        ResultSet ret2 = slaveState.executeQuery("select * from test.users where id=1;");
        while (ret2.next()) {
            System.out.println(ret2.getLong("money"));
            assert ret2.getLong("money") == 200;
        }

        ret2.close();
        ret.close();
        ssState.close();
        slaveState.close();
        ssCon.close();
        slaveCon.close();
    }

    private DataSource createSlaveDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3309/test?serverTimezone=UTC&useUnicode=true&characterEncoding" +
                "=utf-8&useSSL=false&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    private DataSource createShardingSphereProxyDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:13306/test?serverTimezone=UTC&useUnicode=true&characterEncoding" +
                "=utf-8&useSSL=false&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }
}
