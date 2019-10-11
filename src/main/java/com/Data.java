package com;

/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/7 15:46
 */
public class Data {
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件大小
     */
    private long fileSize;
    /**
     * 文件类型 dir：目录 file：文件
     */
    private String type;
    /**
     * 最后修改时间
     */
    private String time;

    /**
     * 文件数据的构造方法
     * @param path 文件路径
     * @param name 文件名
     * @param fileSize 文件大小
     * @param type 文件类型
     * @param time 最后修改时间
     */
    public Data(String path, String name, long fileSize, String type, String time) {
        this.path = path;
        this.name = name;
        this.fileSize = fileSize;
        this.type = type;
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
