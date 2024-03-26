package com.example;

public class MockDataLoader {
    public static void loadMockData(ManagerService managerService) {
        // Mock some rental listings
        managerService.addListing(new RentalListing("Beach House", 5, "Area1", 5, 100, "/path/to/image.png"));
        managerService.addListing(new RentalListing("Mountain Cabin", 4, "Area2", 4, 75, "/path/to/anotherImage.png"));
        managerService.showInfo();
        // More mock data can be added here
    }
}
