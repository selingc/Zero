package com.jello.zero;

/**
 * Created by hoangphat1908 on 3/5/2017.
 */

public class Alert {
    public String name;
    public String category;
    public String location;
    public double latitude = -1;
    public double longitude = -1;
    private String distance;
    private String key;
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
    public void setKey(String key)
    {
        this.key = key;
    }
    public String getKey()
    {
        return key;
    }
    public void setDistance(String distance)
    {
        this.distance = distance;
    }
    public String getLoc()
    {
        return distance;
    }
    @Override
    public String toString()
    {
        return name + "\n" + category + "\n" + location+"\n" + "Coordinates: " + latitude + ", " + longitude + "\n" + distance;
    }
}
