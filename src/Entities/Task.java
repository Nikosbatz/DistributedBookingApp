package Entities;

import java.io.Serializable;
import java.security.PublicKey;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Task implements Serializable {
    public static long nextTaskID = 0;
    private long taskID;
    private String method ;
    private int WorkerID;
    private String roomName;
    private Boolean isManager;
    private int starsFilter ;
    private int priceFilter;
    private int capacityFilter;
    private String areaFilter;
    private int threadId;
    private JSONObject json;
    private int managerID;


    // Default constructor setting the unique ID of the task
    public Task(){
        setTaskID();
    }




    // Just Getters and Setters Below ...
    public String getAreaFilter() {
        return areaFilter;
    }

    public void setAreaFilter(String areaFilter) {
        this.areaFilter = areaFilter;
    }

    public int getCapacityFilter() {
        return capacityFilter;
    }

    public void setCapacityFilter(int capacityFilter) {
        this.capacityFilter = capacityFilter;
    }

    public int getStarsFilter() {
        return starsFilter;
    }

    public void setStarsFilter(int starsFilter) {
        this.starsFilter = starsFilter;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public int getManagerID() {
        return managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public long getTaskID() {
        return taskID;
    }

    public void setTaskID() {
        this.taskID = nextTaskID++;
    }

    public int getPriceFilter() {
        return priceFilter;
    }

    public void setPriceFilter(int price) {
        this.priceFilter = price;
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean isManager) {
        this.isManager = isManager;
    }

    public int getWorkerID() {
        return WorkerID;
    }

    public void setWorkerID(int workerID) {
        WorkerID = workerID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
