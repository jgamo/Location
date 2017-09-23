package com.example.t410.foursquare;

/**
 * Created by T410 on 22/09/2017.
 */

public class FQ {
    private String id;
    private String name;
    private String urlPict;
    private String address;
    private int distance;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public FQ(String url) {
        this.urlPict = url;
    }

    public FQ(String id, String name, int distance, String add) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.address = add;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getUrlPict() {
        return urlPict;
    }

    public void setUrlPict(String urlPict) {
        this.urlPict = urlPict;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}