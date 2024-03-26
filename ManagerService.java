package com.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ManagerService implements ManagerOp {
    private List<RentalListing> listings = new ArrayList<>();
    //Remove ManagerService args and constructor content for mock
    public ManagerService(String jsonFilePath) {
        parseJson(jsonFilePath);
    }

    private void parseJson(String filePath) {
        try {
            JSONTokener tokener = new JSONTokener(new FileInputStream(filePath));
            JSONObject jsonObject = new JSONObject(tokener);

            RentalListing listing = new RentalListing(
                    jsonObject.getString("roomName"),
                    jsonObject.getInt("noOfPersons"),
                    jsonObject.getString("area"),
                    jsonObject.getInt("stars"),
                    jsonObject.getInt("noOfReviews"),
                    jsonObject.getString("roomImage")
            );

            addListing(listing);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        }
    }

    @Override
    public void addListing(RentalListing listing) {
        listings.add(listing);
        System.out.println("Listing added!");
    }

    @Override
    public void setDate(String dateStr) {
        // Method implementation remains the same
    }

    @Override
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
}


