package controller;

import com.MyFTP;
import net.sf.json.JSONArray;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/7 15:46
 */
@WebServlet(name = "Upload")
public class Upload extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ParamDto dto = RequestUtil.parseParam(request);

        //获取服务器路径
        String value = dto.getParamMap().get("serverPath");
        //获取传输设置
        String option = dto.getParamMap().get("option");
        //获取文件item
        FileItem item = dto.getFileMap().get("file");
        //获取文件名
        String fileName = item.getName();

        //FTPClient传入参数
        String serverPath = value + fileName;
        InputStream in = item.getInputStream();
        long size = item.getSize();

        //调用
        MyFTP myFTP = new MyFTP();
        myFTP.connect("127.0.0.1", 27564, "user1", "123456");
        myFTP.setFTPMode(myFTP, option);
        MyFTP.UploadStatus uploadStatus =  myFTP.upload(serverPath, size, in);


        //判断传输结果
        String status = uploadStatus.toString();
        boolean success;
        if(uploadStatus.equals(MyFTP.UploadStatus.Upload_New_File_Success)
         || uploadStatus.equals(MyFTP.UploadStatus.File_Exits)){
             success = true;
        }else{
            success = false;
        }
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


    public String init(String userPath){
        StringBuffer str = new StringBuffer();
        for(int i = 0; i < userPath.length(); i++){
            if(userPath.charAt(i) == '\\'){
                str.append("/");
            }else {
                str.append(userPath.charAt(i));
            }
        }
        return str.toString();
    }


}
