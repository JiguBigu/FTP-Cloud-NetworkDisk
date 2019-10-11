package controller;

import net.sf.json.JSONArray;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/7/10 16:02
 */
public class BarThread extends Thread{

    private HttpServletResponse response;
    private ArrayList<Long> bar = new ArrayList<Long>();
    private int flag = 0;
    private boolean listen = true;

    public BarThread(HttpServletResponse response, ArrayList<Long> bar) {
        this.response = response;
        this.bar = bar;
    }

    @Override
    public void run() {
        while (listen){
            if(bar.size() > flag && bar.get(flag) <= 100){
                Long pro = bar.get(flag);
                JSONArray result = JSONArray.fromObject(pro);
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.println(result);
                    out.flush();
                    flag++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
