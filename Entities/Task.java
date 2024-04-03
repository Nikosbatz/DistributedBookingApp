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
    private int starsFilter ;
    private int priceFilter;
    private int capacityFilter;
    private String areaFilter;
    private int threadId;
    private JSONObject json;
    private int managerID;
    private String roomName;

    private int noOfPersons;
    private String area;
    private int stars;
    private int noOfReviews;
    private String roomImage;


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

    public String getRoomName() {
        return roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public int getNoOfPersons(){
        return noOfPersons;
    }
    public void setNoOfPersons(int noOfPersons){
        this.noOfPersons = noOfPersons;
    }

    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }

    public int getStars(){
        return stars;
    }
    public void setStars(int stars){
        this.stars = stars;
    }

    public int getNoOfReviews(){
        return noOfReviews;
    }
    public void setNoOfReviews(int noOfReviews){
        this.noOfReviews = noOfReviews;
    }

    public String getRoomImage() {
        return roomImage;
    }
    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }
}