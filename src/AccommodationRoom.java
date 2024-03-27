package Entities;
import java.util.ArrayList;

public class AccommodationRoom {

    private String name;
    private  int capacity;
    private String area;
    private int stars;
    private int noOfReviews;
    private String imagePath;
    private double price;
    private ArrayList<String> availiableDates = new ArrayList<>();




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

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<String> getDates() {
        return availiableDates;
    }

    public void setPrice(ArrayList<String> date) {
        this.availiableDates = date;
    }

}
