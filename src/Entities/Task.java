package Entities;

import java.io.Serializable;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Task implements Serializable {
    private String method;
    private int threadId;
    private JSONObject json;
    private int managerID;

    public Task(String method, int threadId, int managerID, JSONObject json){
        this.method = method;
        this.threadId = threadId;
        this.json = json;
        this.managerID = managerID;
    }

    public Task(String method, int threadId, int managerID){
        this.method = method;
        this.threadId = threadId;
        this.managerID = managerID;
    }

    public Task(String method, int threadId){
        this.method = method;
        this.threadId = threadId;
    }
}
