package Manager;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ManagerService {
    private List<RentalListing> listings = new ArrayList<>();
    //Remove ManagerService args and constructor content for mock
    public ManagerService(String jsonFilePath) {
        parseJson(jsonFilePath);
    }

    private void parseJson(String filePath) {
        JSONParser parser = new JSONParser();
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            JSONObject jsonObject = (JSONObject) parser.parse(new InputStreamReader(fileInputStream));

            RentalListing listing = new RentalListing(
                (String) jsonObject.get("roomName"),
                ((Long) jsonObject.get("noOfPersons")).intValue(),
                (String) jsonObject.get("area"),
                ((Long) jsonObject.get("stars")).intValue(),
                ((Long) jsonObject.get("noOfReviews")).intValue(),
                (String) jsonObject.get("roomImage")
        );

        addListing(listing);
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + filePath);
    } catch (IOException e) {
        System.err.println("IO error processing file: " + filePath);
    } catch (ParseException e) {
        System.err.println("Error parsing JSON in file: " + filePath);
    }
}

    
    public void addListing(RentalListing listing) {
        listings.add(listing);
        System.out.println("Listing added!");
    }

    public void setDate(String dateStr) {
        //impl
    }

    public void showInfo() {
        if (listings.isEmpty()) {
            System.out.println("No listings are made yet!");
        } else {
            System.out.println("Listings: ");
            for (RentalListing listing : listings) {
                System.out.println(listing);
            }
        }
    }
    public List<RentalListing> getListings() {
        return new ArrayList<>(this.listings); // Return a copy of the listings
    }

    
}
