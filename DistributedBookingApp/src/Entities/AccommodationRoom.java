package Entities;
import org.json.simple.*;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AccommodationRoom implements Serializable {

    private String name;
    private  int capacity;
    private int price;
    private String area;
    private int stars;
    private int noOfReviews;
    private String imagePath;
    private int owner;

    private HashMap<String,String> availiableDates = new HashMap<String,String>();
    private HashMap<LocalDate,LocalDate> bookedDates = new HashMap<LocalDate,LocalDate>();

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


    public HashMap<String, String> getAvailiableDates() {       //!!!!!!!!!!
        return availiableDates;
    }

    public HashMap<LocalDate, LocalDate> getBookedDates() {
        return bookedDates;
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



//    public ArrayList<String> getDatesFirst(){           //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        ArrayList<String> datesFirst = new ArrayList<>();
//        for (String date: availiableDates.keySet()){
//            datesFirst.add(date);
//
//        }
//        return datesFirst;
//    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private LocalDate availDateFirst;
    private LocalDate availDateLast;
    public boolean isAvailiable(LocalDate dateFirst, LocalDate dateLast) throws ParseException {            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //return true;
        for (String date: availiableDates.keySet()){
            availDateFirst = LocalDate.parse(date,formatter);
            availDateLast = LocalDate.parse(availiableDates.get(date),formatter);

            if ( (dateFirst.isAfter(availDateFirst) || dateFirst.isEqual(availDateLast))
                    && (dateLast.isBefore(availDateLast) || dateLast.isEqual(availDateLast))
            ) {
                return true;
            }
        }

        return false;
    }


//    public boolean bookWholePeriod(Date dateFirst, Date dateLast) throws ParseException{
//        for (String date: availiableDates.keySet()) {
//            availDateFirst = simpleDateFormat.parse(date);
//            availDateLast = simpleDateFormat.parse(availiableDates.get(date));
//
//            if (availDateFirst.compareTo(dateFirst) == 0 && availDateLast.compareTo(dateLast) == 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    public boolean bookBeginPeriod(Date dateFirst, Date dateLast) throws ParseException{
//        for (String date: availiableDates.keySet()) {
//            availDateFirst = simpleDateFormat.parse(date);
//            availDateLast = simpleDateFormat.parse(availiableDates.get(date));
//
//            if (availDateFirst.compareTo(dateFirst) == 0 && dateLast.before(availDateLast)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean bookLastPeriod(Date dateFirst, Date dateLast) throws ParseException{
//        for (String date: availiableDates.keySet()) {
//            availDateFirst = simpleDateFormat.parse(date);
//            availDateLast = simpleDateFormat.parse(availiableDates.get(date));
//
//            if (dateFirst.after(availDateFirst) && availDateLast.compareTo(dateLast) == 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean bookMidPeriod(Date dateFirst, Date dateLast) throws ParseException{
//        for (String date: availiableDates.keySet()) {
//            availDateFirst = simpleDateFormat.parse(date);
//            availDateLast = simpleDateFormat.parse(availiableDates.get(date));
//
//            if (dateFirst.after(availDateFirst) && dateFirst.before(availDateLast)) {
//                return true;
//            }
//        }
//        return false;
//    }

}
