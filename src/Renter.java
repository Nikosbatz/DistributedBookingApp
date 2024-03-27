import java.util.Date;

import Entities.AccommodationRoom;

import java.util.ArrayList;

public class Renter {
    private ArrayList<AccommodationRoom> bookings;

    public Renter(String name) {
        this.name = name;
        this.bookings = new ArrayList<>();
    }

    //ArrayList<AccommodationRoom> accommodations

    public ArrayList<AccommodationRoom> filterAccommodations(String location, String datesStart, String dateEnd, int numPeople, double price, int stars) {
        ArrayList<AccommodationRoom> filteredAccommodations = new ArrayList<>();
        for (AccommodationRoom accommodation : accommodations) {
            if (
                (location == null || accommodation.getArea().equals(location)) 
                && (dateStart == null || accommodation.getDates().contains(dateStart))
                && (dateEnd == null || accommodation.getDates().contains(dateEnd))
                && (numPeople == 0 || accommodation.getCapacity() >= numPeople)
                && (price == 0 || accommodation.getPrice() <= price)
                && (stars == 0 || accommodation.getStars() >= stars)
                ){
                    filteredAccommodations.add(accommodation);
                }
        }
        return filteredAccommodations;
    }

    public void bookAccommodation(String roomName) {
        for (AccommodationRoom accommodation : accommodations) {
            if (roomName == accommodation.getName()){
                bookings.add();
            }
        }
    }

    public void rateAccommodation(String roomName, int stars) {
        for (AccommodationRoom accommodation : accommodations) {
            if (roomName == accommodation.getName()){
                int numberOfReviews = accommodation.getNoOfReviews() + 1;
                accommodation.setNoOfReviews(numberOfReviews);
                int newRating = ((numberOfReviews-1) * accommodation.getStars() + stars)/ numberOfReviews ;
                accommodation.setStars(newRating);
            }
        }
    }
}
