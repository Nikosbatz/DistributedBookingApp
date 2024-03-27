package Manager;

public class RentalListing {
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

    public String toString() {
        return String.format("Room: %s, Persons: %d, Area: %s, Stars: %d, Reviews: %d, Image: %s",
                roomName, noOfPersons, area, stars, noOfReviews, roomImage);
    }
}
