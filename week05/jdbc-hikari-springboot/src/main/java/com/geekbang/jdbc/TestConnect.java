package com.geekbang.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 连接测试
 *
 * @author Q
 * @date 2021/6/2
 */
public class TestConnect {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/mysql45?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "root", "zsq123");
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from person");
        while (resultSet.next()) {
            System.out.println("查询结果： " + resultSet.getString("name"));
        }
    }
}
