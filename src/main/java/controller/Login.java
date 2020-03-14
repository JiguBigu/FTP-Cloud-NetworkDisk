package controller;

import JDBC.User;
import JDBC.jdbc;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/7 15:46
 */
@WebServlet(name = "Login")
public class Login extends HttpServlet {
    /**
     * 登录成功标志
     */
    private boolean loginSuccess;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * 获取前端用户登录信息：账号、密码
         */
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));


        /**
         * 对用户进行数据库查询
         * 若用户存在且密码匹配，则登录成功
         * 若不匹配或用户不存在，则返回失败信息
         */
        try {
            jdbc dataBase = new jdbc();
            loginSuccess = dataBase.verifyUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /**
         * 向前端返回信息
         */
        if(loginSuccess){
            Cookie cookie = new Cookie("username", request.getParameter("username"));
            response.addCookie(cookie);
        }


        JSONArray data = JSONArray.fromObject(loginSuccess);
        PrintWriter out = response.getWriter();
        out.println(data);
        out.flush();
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

