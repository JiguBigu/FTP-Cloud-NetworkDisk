package controller;

import org.apache.commons.fileupload.FileItem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/10 22:23
 */
public class ParamDto {

    private Map<String,String> paramMap;
    private Map<String,FileItem> fileMap;

    public ParamDto() {
        paramMap = new HashMap<String,String>();
        fileMap = new HashMap<String, FileItem>();
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public Map<String, FileItem> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, FileItem> fileMap) {
        this.fileMap = fileMap;
    }
}