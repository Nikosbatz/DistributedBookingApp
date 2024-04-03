package Worker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import Entities.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Scanner;
import Entities.MessageData;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WorkerFunctions {
    private static String rootJSON = "C:\\Users\\Sotir\\Downloads\\DistributedBookingApp-v-Batz\\DistributedBookingApp-v-Batz\\src\\assets\\room.json";

    public static void insert() throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter room name:");
        String roomName = scanner.nextLine();

        System.out.println("Enter number of persons:");
        int noOfPersons = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter area:");
        String area = scanner.nextLine();

        System.out.println("Enter stars:");
        int stars = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter number of reviews:");
        int noOfReviews = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter room image URL/path:");
        String roomImage = scanner.nextLine();

        JSONObject newRoom = new JSONObject();
        newRoom.put("roomName", roomName);
        newRoom.put("noOfPersons", noOfPersons);
        newRoom.put("area", area);
        newRoom.put("stars", stars);
        newRoom.put("noOfReviews", noOfReviews);
        newRoom.put("roomImage", roomImage);

        // Load the existing rooms JSON array from the file
        File file = new File(rootJSON);
        JSONObject rootObject;
        JSONArray roomsArray;
        if (file.length() == 0) {
            roomsArray = new JSONArray();
            rootObject = new JSONObject();
        } else {
            String content = new String(Files.readAllBytes(Paths.get(rootJSON)));
            rootObject = (JSONObject) new JSONParser().parse(content);
            if (rootObject.containsKey("rooms")) {
                roomsArray = (JSONArray) rootObject.get("rooms");
            } else {
                roomsArray = new JSONArray();
            }
        }
        roomsArray.add(newRoom);
        rootObject.put("rooms", roomsArray);

        // Write the updated rooms array back to the file
        try (FileWriter writer = new FileWriter(rootJSON)) {
            writer.write(rootObject.toJSONString());
            System.out.println("Room added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to add room.");
        }
    }
}