package com.example.test;

public class Car {
    //data we get from JSON
    private String id;
    private String plateNumber;
    private Location location;
    private Model model;
    private String batteryPercentage;
    private int batteryEstimatedDistance;
    private Boolean isCharging;
    private String distance;

    //nested location JSON
    public static class Location{
        private String id;
        private Double latitude;
        private Double longitude;
        private String address;

        public String getAddress() {
            return address;
        }

        public String getId() {
            return id;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }
    }

    //nested model JSON
    public static class Model{
        private String id;
        private String title;
        private String photoUrl;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }
    }

    //constructor
    public Car(String id, String plateNumber, Location location, Model model, String batteryPercentage,
               int batteryEstimatedDistance, Boolean isCharging){
        this.id = id;
        this.plateNumber = plateNumber;
        this.location = location;
        this.model = model;
        this.batteryPercentage = batteryPercentage;
        this.batteryEstimatedDistance = batteryEstimatedDistance;
        this.isCharging = isCharging;
    }

    //setters and getters, should delete unused ones

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(String batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public int getBatteryEstimatedDistance() {
        return batteryEstimatedDistance;
    }

    public void setBatteryEstimatedDistance(int batteryEstimatedDistance) {
        this.batteryEstimatedDistance = batteryEstimatedDistance;
    }

    public Boolean getIsCharging() {
        return isCharging;
    }

    public void setIsCharging(Boolean isCharging) {
        this.isCharging = isCharging;
    }

    public Location getLocation(){
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
