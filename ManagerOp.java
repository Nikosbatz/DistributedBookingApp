package com.example;

public interface ManagerOp {
    void addListing(RentalListing listing);
    void setDate(String dateStr);
    void showInfo();
}

class RentalListing {
    private String roomName;
    private int noOfPersons;
    private String area;
    private int stars;
    private int noOfReviews;
    private String roomImage;

    // Constructor
    public RentalListing(String roomName, int noOfPersons, String area, int stars, int noOfReviews, String roomImage) {
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
    }

    // Getters and toString method for displaying information
    public String getRoomName() { return roomName; }
    public int getNoOfPersons() { return noOfPersons; }
    public String getArea() { return area; }
    public int getStars() { return stars; }
    public int getNoOfReviews() { return noOfReviews; }
    public String getRoomImage() { return roomImage; }

    @Override
    public String toString() {
        return "RentalListing{" +
                "roomName='" + roomName + '\'' +
                ", noOfPersons=" + noOfPersons +
                ", area='" + area + '\'' +
                ", stars=" + stars +
                ", noOfReviews=" + noOfReviews +
                ", roomImage='" + roomImage + '\'' +
                '}';
    }
}

