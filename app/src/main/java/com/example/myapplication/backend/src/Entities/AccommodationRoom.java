package com.example.myapplication.backend.src.Entities;
import org.json.simple.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class AccommodationRoom implements Serializable {

    private String name;
    private  int capacity;
    private int price;
    private String area;
    private int stars;
    private int noOfReviews;
    private int sumOfReviews;
    private byte[] imageData;
    private String imageName;
    private int owner;
    private int noOfBookingsInRange = 0;

    private HashMap<LocalDate,LocalDate> availableDates = new HashMap<LocalDate,LocalDate>();
    private HashMap<LocalDate,LocalDate> bookedDates = new HashMap<LocalDate,LocalDate>();

    // Create a new instance using a JSONObject object.
    public AccommodationRoom(JSONObject json, byte[]imageData){
        setArea((String)json.get("area"));
        setCapacity( (long) json.get("capacity"));
        setName((String) json.get("roomName"));
        setStars((long)json.get("stars"));
        setNoOfReviews((long)json.get("noOfReviews"));
        setSumOfReviews(getNoOfReviews() * getStars());
        setPrice((long) json.get("price"));
        setImageName((String)json.get("imageName"));
        setAvailableDates(json);
        setImageData(imageData);
    }



    public boolean addReview(int review){

        setSumOfReviews(getSumOfReviews() + review);
        setNoOfReviews(getNoOfReviews()+1);
        setStars(getSumOfReviews()/getNoOfReviews());
        return true;
    }


    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String str = "\nName: " + getName() +"\nArea: " + getArea() + "\nCapacity: " + getCapacity() +
                "\nStars: " + getStars() + "\nPrice: " + getPrice() + "\nAvailable Dates: ";

        for (LocalDate tempDate: availableDates.keySet()){
            String date1 = tempDate.format(formatter);
            String date2 = availableDates.get(tempDate).format(formatter);
            str += "\n" + date1 + " -> " + date2 ;
        }

        str += "\n";

        return (str);
    }

    // Just Getters and Setters below
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public void setNoOfReviews(long noOfReviews) {this.noOfReviews = (int) noOfReviews;}

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = (int) capacity;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(long stars) {
        this.stars = (int)stars;
    }
    public int getSumOfReviews() {return sumOfReviews;}

    public void setSumOfReviews(int sumOfReviews) {this.sumOfReviews = sumOfReviews;}

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getPrice() {return price;}

    public void setPrice(long price) {this.price = (int)price;}

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public HashMap<LocalDate, LocalDate> getAvailableDates() {       //!!!!!!!!!!
        return availableDates;
    }

    public void setAvailableDates(JSONObject json) {
        JSONArray availableDatesArray = (JSONArray) json.get("availableDates");
        for (Object date : availableDatesArray) {
            JSONObject availDate = (JSONObject) date;
            String dateFirst = (String) availDate.get("dateFirst");
            String dateLast = (String)  availDate.get("dateLast");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate dateStart = LocalDate.parse(dateFirst, formatter);
            LocalDate dateEnd = LocalDate.parse(dateLast, formatter);

            synchronized (availableDates) {
                availableDates.put(dateStart, dateEnd);
            }
        }
    }

    // Override setAvailableDates()
    public Boolean setAvailableDates(LocalDate dateStart, LocalDate dateEnd){
        HashMap<LocalDate, LocalDate> tempMap = new HashMap<>();
        
        synchronized (availableDates){
            if (!availableDates.containsKey(dateStart)){
                availableDates.put(dateStart, dateEnd);
                return true;
            }
        }
        return false;
    }



    public HashMap<LocalDate, LocalDate> getBookedDates() {
        return bookedDates;
    }





    public boolean isAvailable(LocalDate dateFirst, LocalDate dateLast) {
        LocalDate availDateFirst;
        LocalDate availDateLast;

        for (LocalDate date: availableDates.keySet()){
            availDateFirst = date;
            availDateLast = availableDates.get(date);

            if ( (dateFirst == null) && (dateLast == null) )
            {
                return true;
            } else if ( (dateLast == null)
                    && (dateFirst.isAfter(availDateFirst) || dateFirst.isEqual(availDateFirst)) ){
                return true;
            }else if ( (dateFirst == null)
                    && (dateLast.isBefore(availDateLast) || dateLast.isEqual(availDateLast)) ){
                return true;
            } else if ( (dateFirst != null) && (dateLast != null) && (dateFirst.isAfter(availDateFirst) || dateFirst.isEqual(availDateFirst))
                    && (dateLast.isBefore(availDateLast) || dateLast.isEqual(availDateLast)) ){
                return true;
            }
        }

        return false;
    }


    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public int getNoOfBookingsInRange() {
        return noOfBookingsInRange;
    }

    public void setNoOfBookingsInRange(int noOfBookingsInRange) {
        this.noOfBookingsInRange = noOfBookingsInRange;
    }
}
