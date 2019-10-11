package com;

import org.apache.ftpserver.*;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.*;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyFTPServer{

    /**
     * 设置ftp服务器的根目录
     */
//    private static final String ftpPath = "/home/ftp/";
    private static final String ftpPath = "G:/ftp/";

    /**
     * 保存用户的配置文件路径
     */
//    private static final String  propertiesPath = "/home/ftp/users.properties";
    private static final String  propertiesPath = "G:/ftp/users.properties";

    /**
     * log 路径
     */
//    private static final String log4jConfPath = "E:\\repository\\ftp\\log4j.properties";

    /**
     * server port
     */
    private static final int port = 27564;


    /**
     * 检测文件或路径是否存在，若不存在则创建
     * @param path 文件或路径的创建
     */
    public static void checkPath(String path){
        File file = new File(path);
        if(file.exists()){
            System.out.println("路径存在");
        }
        else {
            String suffix = path.substring(path.lastIndexOf("/")+1);
            System.out.println(suffix);
            if(suffix.equals("")){
                System.out.println("需要创建目录");
                if(file.mkdirs()){
                    System.out.println("目录创建成功");
                }
            }
            else{
                System.out.println("需要创建文件");
                try{
                    if(file.createNewFile()){
                        System.out.println("文件创建成功");
                    }
                }
                catch (IOException ioe){
                    System.out.println("文件创建失败");
                }
            }
        }
    }

    /**
     * 向ftp server中添加一个用户
     * @param name
     * @param password
     * @param writePermission: 为true时给用户添加写权限
     * @return 返回用户信息
     */
    public static BaseUser addUser(String name, String password, boolean writePermission){

        /**
         * 设置用户名与密码与主目录
         */
        BaseUser user = new BaseUser();
        user.setName(name);
        user.setPassword(password);

        checkPath(ftpPath + name + "/");
        user.setHomeDirectory(ftpPath + name + "/");

        /**
         * 增加用户的权限
         */
        List<Authority> authorities = new ArrayList<Authority>();
        //若writePermission == true, 那么给用户添加写权限
        if(writePermission){
            authorities.add(new WritePermission());
        }
        user.setAuthorities(authorities);

        return user;
    }

    public static void main(String[] args) {
        FtpServerFactory serverFactory = new FtpServerFactory();

        //设置监听的端口
        ListenerFactory listerner = new ListenerFactory();
        listerner.setPort(port);

        //替换默认监听
        serverFactory.addListener("default", listerner.createListener());

        //添加用户
        BaseUser user1 = addUser("user1", "123456", true);

        /**
         * 使用配置文件管理用户
         */
        BasicConfigurator.configure();
//        PropertyConfigurator.configure(log4jConfPath);
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        checkPath(propertiesPath);      //检查propertiesPath是否存在，不存在则创建
        userManagerFactory.setFile(new File(propertiesPath));
        UserManager um = userManagerFactory.createUserManager();

        /**
         * 将用户添加进user manager中
         */
        try {
            um.save(user1);
        }
        catch (FtpException ftpe){
            ftpe.printStackTrace();
            System.out.println("将用户添加进usermanager失败");
        }

        /**
         * 设置server的 user manager
         */
        serverFactory.setUserManager(um);

        /**
         * 根据所有的用户及配置文件创建一个ftp server
         */
        FtpServer server = serverFactory.createServer();

        /**
         * 启动 ftp server
         */
        try{
            server.start();
        }
        catch (FtpException ftpe){
            ftpe.printStackTrace();
            System.out.println("启动 ftp server 失败");
        }
    }
}
