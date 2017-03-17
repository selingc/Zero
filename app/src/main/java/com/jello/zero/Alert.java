package com.jello.zero;

/**
 * Created by hoangphat1908 on 3/5/2017.
 */

public class Alert {
    public String name;
    public String category;
    public String location;
    public double latitude;
    public double longitude;
    public Alert(){};
    public Alert(String name, String category, String location, double latitude, double longitude){
        this.name = name;
        this.category = category;
        this.location = location;
        if(latitude != -100 && longitude != -200) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
