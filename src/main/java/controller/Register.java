package controller;

import JDBC.User;
import JDBC.jdbc;
import net.sf.json.JSONArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Base64;
/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/7 15:46
 */
@WebServlet(name = "Register")
public class Register extends HttpServlet {
    private boolean registerSuccess;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /**
         * 获取前端用户的注册信息：账号、密码,并对密码进行base64加密
         */
        User user = new User();
        String password = Base64.getEncoder().encodeToString(request.getParameter("password").getBytes("utf-8"));
        user.setUsername(request.getParameter("username"));
        user.setPassword(password);


        /**
         * 对用户名查询数据库
         * 若用户名已存在则返回注册失败信息
         * 若用户没未存在，则将用户插入数据库，并返回成功信息
         */
        try {
            jdbc dataBase = new jdbc();
            registerSuccess = !dataBase.hasUser(user);
            if(registerSuccess){
                dataBase.insertUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("regis information:" + registerSuccess);
        /**
         * 数据库和FTP服务器同步
         */

        /**
         * 向前端返回信息
         */
        JSONArray data = JSONArray.fromObject(registerSuccess);
        System.out.println(data.toString());
        PrintWriter out = response.getWriter();
        out.println(data);
        out.flush();
        out.close();


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request, response);
    }
}
