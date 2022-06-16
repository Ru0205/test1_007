package com.example.finalproject1;

import com.google.firebase.firestore.GeoPoint;

public class Member {

    public  Member(String id, String name, String snippet,
                   String ItemID, String ann, String title, GeoPoint geo){

        this.id = id;
        this.name = name;
        this.snippet = snippet;
        this.ItemID = ItemID;
        this.ann = ann;
        this.title = title;
        this.geo = geo;
    }

    public Member(){}

    public String id;
    public String name;
    public String snippet;
    public String ItemID;
    public String ann;
    public String title;
    public GeoPoint geo;

}
