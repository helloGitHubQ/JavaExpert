package com.geekbang.jdbc.base;

import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * 使用事务(注解)操作数据库
 *
 * @author Q
 * @date 2021/6/2
 */
public class JDBCTX {


    @Transactional(rollbackFor = Exception.class)
    public void transactionPerson() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/mysql45?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "root", "zsq123");

        String sql = "insert into person(id,name,age,address,notes) values(null,?,?,?,?)";

        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, "Z");
        statement.setInt(2, 22);
        statement.setString(3, "SH");
        statement.setString(4, "无");

//        int a = 1 / 0;

        int i = statement.executeUpdate();

        System.out.println("插入结果,成功插入 " + i + "条数据");
    }

    public static void main(String[] args) throws Exception {
        JDBCTX jdbctx = new JDBCTX();
        jdbctx.transactionPerson();
    }
}
