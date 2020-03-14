package controller;

import JDBC.User;
import JDBC.jdbc;
import com.Data;
import com.MyFTP;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/7 15:46
 */
@WebServlet(name = "Check")
public class Check extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getParameter("path");

        ArrayList<Data> files = MyFTP.getFiles(path);

        JSONArray data = JSONArray.fromObject(files);
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
