package controller;

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

import static com.MyFTP.DownloadStatus.*;
/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/7 15:46
 */
@WebServlet(name = "Download")
public class Download extends HttpServlet {

    private String success;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * 获取服务端路径，客户端路径，传输设置
         */
        String serverPath = request.getParameter("serverPath");
        String userPath = request.getParameter("userPath");
        String option = request.getParameter("option");
        System.out.println("download-serverpath:" + serverPath);
        System.out.println("download-userpath:" + userPath);
        System.out.println("download-option:" + option);

        /**
         * 调用
         */
        MyFTP myFTP = new MyFTP();
        myFTP.connect("127.0.0.1", 27564, "user1", "123456");
        myFTP.setFTPMode(myFTP, option);
        MyFTP.DownloadStatus downloadStatus =  myFTP.download(serverPath, userPath);

        /**
         * 判断传输结果
         */
        String status = downloadStatus.toString();
        System.out.println("status:  " + status);
        boolean success;
        if(status.equals(Download_From_Break_Success) || status.equals(Download_New_Success)){
            success = true;
        } else {
            success = false;
        }

        /**
         * 返回结果给前端
         */
        JSONArray result = JSONArray.fromObject(success);
        PrintWriter out = response.getWriter();
        out.println(result);
        out.flush();
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }




}
