package Manager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ManagerService {
    //public static void main(String[] args) {
       // ManagerService service = new ManagerService("C:\\Users\\Sotir\\Downloads\\DistributedBookingApp-v-Batz\\DistributedBookingApp-v-Batz\\src\\assets\\room.json");
        //service.addListing(new RentalListing("Sunset Suite",2,"35 sqm",5,150,"url"));
       // service.showInfo();
    //}
    
    private List<RentalListing> listings = new ArrayList<>();
    private String jsonFilePath;
    
    public ManagerService(String jsonFilePath) {
        System.out.println("Initializing ManagerService with file path: " + jsonFilePath);
        this.jsonFilePath = jsonFilePath;
        readJson();
    }

    

    
    public void addListing(RentalListing listing) {
        listings.add(listing);
        saveToJsonFile(); // Save the updated listings to JSON file
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
    


    //here
    public void saveToJsonFile() {
        JSONArray listingsArray = new JSONArray();
        for (RentalListing listing : listings) {
            JSONObject listingObject = new JSONObject();
            listingObject.put("roomName", listing.getRoomName());
            listingObject.put("noOfPersons", listing.getNoOfPersons());
            listingObject.put("area", listing.getArea());
            listingObject.put("stars", listing.getStars());
            listingObject.put("noOfReviews", listing.getNoOfReviews());
            listingObject.put("roomImage", listing.getRoomImage());
            listingsArray.add(listingObject);
        }

        try (FileWriter file = new FileWriter(jsonFilePath)) {
            file.write(listingsArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readJson() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFilePath)) {
            Object obj = parser.parse(reader);
            // Ensure the root object is a JSONObject
            if (obj instanceof JSONObject) {
                JSONObject rootObj = (JSONObject) obj;
                // Check if the "listings" key exists and is an array
                if (rootObj.containsKey("listings") && rootObj.get("listings") instanceof JSONArray) {
                    JSONArray listingsArray = (JSONArray) rootObj.get("listings");
    
                    for (Object listingObj : listingsArray) {
                        JSONObject listingJson = (JSONObject) listingObj;
                        String roomName = (String) listingJson.get("roomName");
                        int noOfPersons = ((Long) listingJson.get("noOfPersons")).intValue();
                        String area = (String) listingJson.get("area");
                        int stars = ((Long) listingJson.get("stars")).intValue();
                        int noOfReviews = ((Long) listingJson.get("noOfReviews")).intValue();
                        String roomImage = (String) listingJson.get("roomImage");
    
                        RentalListing listing = new RentalListing(roomName, noOfPersons, area, stars, noOfReviews, roomImage);
                        listings.add(listing);
                    }
                } else {
                    System.err.println("The 'listings' key is missing or is not an array in the JSON file.");
                }
            } else {
                System.err.println("The root element in the JSON file is not a JSONObject.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("The JSON file was not found: " + jsonFilePath);
        } catch (IOException e) {
            System.err.println("IOException while reading the JSON file: " + jsonFilePath);
        } catch (ParseException e) {
            System.err.println("ParseException while parsing the JSON file: " + jsonFilePath);
        }
    }
    
}