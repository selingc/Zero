package com.jello.zero;

import java.io.Serializable;

/**
 * Created by hoangphat1908 on 3/5/2017.
 */
@SuppressWarnings("serial")
public class Alert implements Serializable{
    public String name;
    public String category;
    public String location;
    public double latitude = -1;
    public double longitude = -1;
    public String key;
    public int confirmed = 0;
    public String distance = "";

    public Alert(){};

    public Alert(String name, String category, String location, double latitude, double longitude, String key, int confirmed){
        this.name = name;
        this.category = category;
        this.location = location;
        if(latitude != -100 && longitude != -200) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        this.key = key;
        this.confirmed = confirmed;
    }
    public Alert(String name, String category, String location, String latitude, String longitude, String key, int confirmed){
        double lat = 0, longi = 0;
        if(latitude != null && !latitude.equals("n/a"))
            lat = Double.valueOf(latitude);
        else lat = 0;

        if(longitude != null &&  !longitude.equals("n/a") )
            longi = Double.valueOf(longitude);
        else
            longi = 0;
        this.name = name;
        this.category = category;
        this.location = location;
        this.latitude = lat;
        this.longitude = longi;
        this.key = key;
        this.confirmed = confirmed;
    }

    public void incConfirm(){confirmed++;}
    public void decConfirm(){confirmed--;}



    //getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public void setDistance(String distance){ this.distance = distance;}


    @Override
    public String toString()
    {
        return name + "\n" + category + "\nLocation: " + location + "\nCoordinates: " + latitude + ", " + longitude + "\n" + distance;
    }
}
