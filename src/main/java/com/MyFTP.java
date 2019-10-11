package com;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class MyFTP {

    /**
     * 主被动模式选择
     * true为主动模式，false为被动模式
     */
    private boolean usePort = true;
    /**
     * 传输模式指定
     * true为二进制模式，false为ASCII模式
     */
    private boolean useBinary = true;
    /**
     * 服务器存放文件默认路径
     */
    private final String ftpPath = "/home/ftp/";
    private FTPClient ftpClient = new FTPClient();

    private String directoryPath;
    /**
     * 上传操作状态
     */
    public enum UploadStatus {
        /**
         * 服务器创建目录成功
         */
        Create_Directory_Fail,
        /**
         * 服务器创建目录失败
         */
        Create_Directory_Success,
        /**
         * 上传新文件成功
         */
        Upload_New_File_Success,
        /**
         * 上传新文件失败
         */
        Upload_New_File_Failed,
        /**
         * 文件已存在
         */
        File_Exits,
        /**
         * 服务器文件大小大于本地文件大小
         */
        Remote_Bigger_Local,
        /**
         * 断点续传成功
         */
        Upload_From_Break_Success,
        /**
         * 断点续传失败
         */
        Upload_From_Break_Failed,
        /**
         * 删除远程文件失败
         */
        Delete_Remote_Faild;
    }

    /**
     * 枚举类DownloadStatus代码
     */
    public enum DownloadStatus {
        /**
         * 远程文件不存在
         */
        Remote_File_Noexist,
        /**
         * 本地文件大于远程文件
         */
        Local_Bigger_Remote,
        /**
         * 断点下载文件成功
         */
        Download_From_Break_Success,
        /**
         * 断点下载文件失败
         */
        Download_From_Break_Failed,
        /**
         * 全新下载文件成功
         */
        Download_New_Success,
        /**
         * 全新下载文件失败
         */
        Download_New_Failed;
    }


    /**
     * 主被动模式的切换
     * @param mode: true -> port/active mode, false -> passive mode
     */
    public  void setMode(boolean mode){
        if(mode){
            usePort = true;
            ftpClient.enterLocalActiveMode();
        }
        else{
            usePort = false;
            ftpClient.enterLocalPassiveMode();
        }
    }

    /**
     *
     * @param type: true -> ascii, false -> binary
     */
    public void transmitType(boolean type){
        try{
            //ASCII方式
            if(type){
                useBinary = true;
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            }
            //二进制方式
            else{
                useBinary = false;
                ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println("设置文件传输方式失败");
        }
    }


    public boolean connect(String hostname, int port, String username,
                           String password) throws IOException {

        // 连接到FTP服务器
        ftpClient.connect(hostname, port);
        // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {

            if (ftpClient.login(username, password)) {
                return true;
            }
        }
        disconnect();
        return false;

    }

    public void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }


    /**
     * 下载文件，每次进行一个文件的下载
     * @param remotePath
     * @param localPath
     * @return download
     */
    public DownloadStatus download(String remotePath, String localPath) throws IOException{
        DownloadStatus status;
        transmitType(useBinary); //以Binary方式传文件

        //文件的输入输出流
        InputStream in = null;
        OutputStream out = null;

        File local = new File(localPath);  //本地文件
        FTPFile[] remote =ftpClient.listFiles(remotePath);  //ftp服务器中的文件

        long remoteSize = remote[0].getSize();
        long localSize = local.length();

        if(remoteSize == 0){
            System.out.println("ftp服务器中不存在此文件");
            return DownloadStatus.Remote_File_Noexist;
        }

        //若本地文件已存在，则进行断点续传，不存在则全新下载
        if(local.exists()){
            //判断ftp文件是否小于本地文件
            if(remoteSize < localSize){
                return DownloadStatus.Local_Bigger_Remote;
            }

            //设置文件的断点
            ftpClient.setRestartOffset(localSize);
            in = ftpClient.retrieveFileStream(remotePath);

            out = new FileOutputStream(local);

            byte[] bytes = new byte[1024];
            long step = remoteSize / 100;
            long process = localSize / step;
            long nowProcess;

            int c = 0;
            while((c = in.read(bytes)) != -1){
                out.write(bytes, 0, c);
                localSize += c;
                if(localSize / step != process){
                    //由于long除法精度的问题，可能会出现大于100的情况，但此时文件正确传输
                    if((process = localSize / step) >= 100){
                        process = 100;
                    }
                    System.out.println("下载进度:" + process + "%");
                }
            }
            in.close();
            out.close();
            boolean isDo = ftpClient.completePendingCommand();
            if (isDo) {
                status = DownloadStatus.Download_From_Break_Success;
            } else {
                status = DownloadStatus.Download_From_Break_Failed;
            }
        }
        //本地不存在此文件，进行下载
        else{
            out = new FileOutputStream(local);
            in = ftpClient.retrieveFileStream(remotePath);

            localSize = 0L;
            byte[] bytes = new byte[1024];
            long step = remoteSize / 100;
            long process = 0;

            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    if(nowProcess > 100){
                        process = 100;
                    }else{
                        process = nowProcess;
                    }

                    System.out.println("下载进度：" + process + "%");
                }
            }
            in.close();
            out.close();
            boolean upNewStatus = ftpClient.completePendingCommand();
            if (upNewStatus) {
                status = DownloadStatus.Download_New_Success;
            } else {
                status = DownloadStatus.Download_New_Failed;
            }
        }
        return status;
    }

    public UploadStatus upload(String remote,long local, InputStream in) throws IOException{
        //选择主被动模式
        setMode(usePort);

        //选择传输方式：Binary or ASCII
        transmitType(useBinary);

        UploadStatus result;
        //对远程目录的处理
        String remoteFileName = remote;
        if(remote.contains("/")){
            remoteFileName = remote.substring(remote.lastIndexOf("/")+1);
            //创建服务器远程目录结构，创建失败直接返回
            if(CreateDirecroty(remote, ftpClient)==UploadStatus.Create_Directory_Fail){
                return UploadStatus.Create_Directory_Fail;
            }
        }

        //检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));
        if(files.length == 1){
            long remoteSize = files[0].getSize();
            long localSize = local;
            if(remoteSize==localSize){
                return UploadStatus.File_Exits;
            }else if(remoteSize > localSize){
                return UploadStatus.Remote_Bigger_Local;
            }

            //尝试移动文件内读取指针,实现断点续传
            result = uploadFile(remoteFileName, localSize, remoteSize, in);

            //如果断点续传没有成功，则删除服务器上文件，重新上传
            if(result == UploadStatus.Upload_From_Break_Failed){
                if(!ftpClient.deleteFile(remoteFileName)){
                    return UploadStatus.Delete_Remote_Faild;
                }
                result = uploadFile(remoteFileName, localSize, 0, in);
            }
        }else {
            result = uploadFile(remoteFileName, local, 0, in);
        }
        return result;
    }

    public UploadStatus CreateDirecroty(String remote,FTPClient ftpClient) throws IOException{
        UploadStatus status = UploadStatus.Create_Directory_Success;
        String directory = remote.substring(0,remote.lastIndexOf("/")+1);
        if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"iso-8859-1"))){
            //如果远程目录不存在，则递归创建远程服务器目录
            int start=0;
            int end = 0;
            if(directory.startsWith("/")){
                start = 1;
            }else{
                start = 0;
            }
            end = directory.indexOf("/",start);
            while(true){
                String subDirectory = new String(remote.substring(start,end).getBytes("GBK"),"iso-8859-1");
                if(!ftpClient.changeWorkingDirectory(subDirectory)){
                    if(ftpClient.makeDirectory(subDirectory)){
                        ftpClient.changeWorkingDirectory(subDirectory);
                    }else {
                        System.out.println("创建目录失败");
                        return UploadStatus.Create_Directory_Fail;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/",start);

                //检查所有目录是否创建完毕
                if(end <= start){
                    break;
                }
            }
        }
        return status;
    }

    /**
     * 上传文件
     * @param remoteFile
     * @param localFile
     * @param remoteSize
     * @return
     * @throws IOException
     */
    public UploadStatus uploadFile(String remoteFile,long localFile,long remoteSize, InputStream in) throws IOException{
        UploadStatus status;
        //显示进度的上传
        long step = localFile / 100;
        long process = 0;
        long localreadbytes = 0L;
        InputStream raf = in;
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"),"iso-8859-1"));
        //断点续传
        if(remoteSize>0){
            ftpClient.setRestartOffset(remoteSize);
            process = remoteSize /step;
//            raf.seek(remoteSize);
            byte[] seek = new byte[(int) remoteSize];
            in.read(seek);
            localreadbytes = remoteSize;
        }
        byte[] bytes = new byte[1024];
        int c;
        while((c = raf.read(bytes)) != -1){
            out.write(bytes,0,c);
            localreadbytes += c;
            if(localreadbytes / step != process){
                //由于long除法精度的问题，可能会出现大于100的情况，但此时文件正确传输
                if((process = localreadbytes / step) >= 100){
                    process = 100;
                }
                System.out.println("上传进度:" + process + "%");
            }
        }
        out.flush();
        raf.close();
        out.close();
        boolean result =ftpClient.completePendingCommand();
        if(remoteSize > 0){
            status = result?UploadStatus.Upload_From_Break_Success:UploadStatus.Upload_From_Break_Failed;
        }else {
            status = result?UploadStatus.Upload_New_File_Success:UploadStatus.Upload_New_File_Failed;
        }
        return status;
    }

    /**
     * 遍历路径下的文件和目录
     * @param directoryPath 目录路径
     * @return Data类型ArrayList
     */
    public static ArrayList<Data> getFiles(String directoryPath){
        ArrayList<Data> fileData = new ArrayList<Data>();
        File file = new File(directoryPath);
        File[] files = file.listFiles();

        if(files == null){
            System.out.println("该目录下没有文件和目录");
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        for(File f: files){
            /**
             * 对文件的路径进行更改，使用ftp server的相对路径保存
             */
            String filePath = f.getAbsolutePath().replaceAll("\\\\","/");
            for(int i = 0; i < 4; i++){
                filePath = filePath.substring(filePath.indexOf("/")+1 );
            }
            filePath = "/" + filePath;

            if(f.isDirectory()){
                fileData.add(new
                        Data( filePath + "/", f.getName(), f.length(),"dir", sdf.format(new Date(f.lastModified())))
                );
            } else{
                fileData.add(new
                        Data(filePath, f.getName(), f.length(),"file", sdf.format(new Date(f.lastModified())))
                );
            }
        }

        return fileData;
    }




    public DownloadStatus downloads(String remote, String local, String mode)
            throws IOException {

        // 设置ftp传输方式
        if (mode.equalsIgnoreCase("P")) {
            // PassiveMode传输
            ftpClient.enterLocalPassiveMode();
        } else {
            // ActiveMode传输
            ftpClient.enterLocalActiveMode();
        }

        // 设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        // 下载状态
        DownloadStatus result;

        // 本地文件列表
        File f = new File(local);

        // 检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(new String(
                remote.getBytes("GBK"), "iso-8859-1"));

//        if (files.length != 1) {
//            System.out.println("远程文件不存在");
//            return DownloadStatus.Remote_File_Noexist;
//        }

        // 获得远端文件大小
        long lRemoteSize = files[0].getSize();

        // 构建输出对象
        OutputStream out = null;

        // 本地存在文件，进行断点下载 ；不存在则新下载
        if (f.exists()) {

            // 构建输出对象
            out = new FileOutputStream(f, true);

            // 本地文件大小
            long localSize = f.length();

            System.out.println("本地文件大小为:" + localSize);

            // 判定本地文件大小是否大于远程文件大小
            if (localSize >= lRemoteSize) {
                System.out.println("本地文件大于远程文件，下载中止");
                return DownloadStatus.Local_Bigger_Remote;
            }

            // 否则进行断点续传，并记录状态
            ftpClient.setRestartOffset(localSize); // 该方法尝试把指针移动到远端文件的指定字节位置

            InputStream in = ftpClient.retrieveFileStream(new String(remote
                    .getBytes("GBK"), "iso-8859-1"));

            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;

            // 存放下载进度
            long process = localSize / step;
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 2 == 0)
                        System.out.println("下载进度：" + process);
                    // TODO更新文件下载进度,值存放在process变量中
                }
            }
            // 下载完成关闭输入输出流对象
            in.close();
            out.close();
            boolean isDo = ftpClient.completePendingCommand();
            if (isDo) {
                result = DownloadStatus.Download_From_Break_Success;
            } else {
                result = DownloadStatus.Download_From_Break_Failed;
            }

        } else {
            out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(remote
                    .getBytes("GBK"), "iso-8859-1"));
            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            long process = 0;
            long localSize = 0L;
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 10 == 0)
                        System.out.println("下载进度：" + process);
                    // TODO更新文件下载进度,值存放在process变量中
                }
            }
            in.close();
            out.close();
            boolean upNewStatus = ftpClient.completePendingCommand();
            if (upNewStatus) {
                result = DownloadStatus.Download_New_Success;
            } else {
                result = DownloadStatus.Download_New_Failed;
            }
        }
        return result;
    }

    /**
     * 设置传输模式
     * @param myFTP FTPClient
     * @param mode 模式字符
     */
    public void setFTPMode(MyFTP myFTP, String mode) {
        if (mode.equals("主动模式-ASCII传输")) {
            myFTP.setMode(true);
            myFTP.transmitType(true);
        } else if (mode.equals("主动模式-Binary传输")) {
            myFTP.setMode(true);
            myFTP.transmitType(false);
        } else if (mode.equals("被动模式-ASCII传输")) {
            myFTP.setMode(false);
            myFTP.setMode(true);
        } else if (mode.equals("被动模式-Binary传输")) {
            myFTP.setMode(false);
            myFTP.setMode(false);
        }
    }

}
