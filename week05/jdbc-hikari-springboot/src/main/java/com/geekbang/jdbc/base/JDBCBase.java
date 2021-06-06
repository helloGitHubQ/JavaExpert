package com.geekbang.jdbc.base;

import java.sql.*;

/**
 * 使用JDBC原生接口操作数据库,实现增删改查
 *
 * @author Q
 * @date 2021/6/2
 */
public class JDBCBase {

    /**
     * 查询
     *
     * @throws Exception
     */
    public void queryPerson() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/mysql45?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "root", "zsq123");

        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from person");
        while (resultSet.next()) {
            System.out.println("查询结果： " + resultSet.getInt("id")+"," + resultSet.getString("name")+","+resultSet.getInt("age")+","+resultSet.getString("address")+","+resultSet.getString("notes"));
        }
    }

    /**
     * 插入
     *
     * @throws Exception
     */
    public void insertPerson() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/mysql45?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "root", "zsq123");

        String sql = "insert into person(id,name,age,address,notes) values(?,?,?,?,?)";

        PreparedStatement statement = con.prepareStatement(sql);
        statement.setInt(1, 1);
        statement.setString(2, "Q");
        statement.setInt(3, 25);
        statement.setString(4, "HZ");
        statement.setString(5, "无");
        int i = statement.executeUpdate();

        System.out.println("插入结果,成功插入 " + i + "条数据");
    }


    /**
     * 更新
     *
     * @throws Exception
     */
    public void updatePerson() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/mysql45?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "root", "zsq123");

        String sql = "update person set age=? where id = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setInt(1, 24);
        statement.setInt(2, 1);
        int i = statement.executeUpdate();

        System.out.println("更新结果,成功更新 " + i + "条数据");
    }

    /**
     * 删除
     *
     * @throws Exception
     */
    public void deletePerson() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/mysql45?characterEncoding=utf8&useSSL=false&serverTimezone=GMT";
        Connection con = DriverManager.getConnection(url, "root", "zsq123");

        String sql = "delete from person where id = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setInt(1, 1);
        int i = statement.executeUpdate();

        System.out.println("删除结果,成功删除 " + i + "条数据");
    }

    public static void main(String[] args) throws Exception {
        JDBCBase jdbcBase = new JDBCBase();
        jdbcBase.insertPerson();
        jdbcBase.queryPerson();
        jdbcBase.updatePerson();
        jdbcBase.deletePerson();
    }
}
