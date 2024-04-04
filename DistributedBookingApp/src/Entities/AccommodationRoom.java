package Entities;
import org.json.simple.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AccommodationRoom {

    private String name;
    private  int capacity;
    private int price;
    private String area;
    private int stars;
    private int noOfReviews;
    private String imagePath;
    private int owner;

    private HashMap<String,String> availiableDates = new HashMap<String,String>(); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    // Create a new instance using a JSONObject object.
    public AccommodationRoom(JSONObject json){
        setArea((String)json.get("area"));
        setCapacity( (long) json.get("capacity"));
        setName((String) json.get("roomName"));
        setStars((long)json.get("stars"));
        setNoOfReviews((long)json.get("noOfReviews"));
        setPrice((long) json.get("price"));
        setImagePath((String)json.get("imagePath"));
    }



    public void addReview(int review){
        int sum = getNoOfReviews() * getStars();
        sum += review;
        setNoOfReviews(getNoOfReviews()+1);
        setStars(sum/getNoOfReviews());
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getPrice() {return price;}

    public void setPrice(long price) {this.price = (int)price;}

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public ArrayList<String> getDatesFirst(){           //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        ArrayList<String> datesFirst = new ArrayList<>();
        for (String date: availiableDates.keySet()){
            datesFirst.add(date);

        }
        return datesFirst;
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
    private Date availDateFirst;
    private Date availDateLast;
    public boolean isAvailiable(Date dateFirst, Date dateLast) throws ParseException {            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //return true;
        for (String date: availiableDates.keySet()){
            availDateFirst = simpleDateFormat.parse(date);
            availDateLast = simpleDateFormat.parse(availiableDates.get(date));

            return ( (dateFirst.after(availDateFirst) || dateFirst.compareTo(availDateLast) == 0)
                        && (dateLast.before(availDateLast) || dateLast.compareTo(availDateLast) == 0)
                    );
        }

        return false;
    }
}
