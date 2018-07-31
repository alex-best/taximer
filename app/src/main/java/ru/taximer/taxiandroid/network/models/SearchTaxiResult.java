package ru.taximer.taxiandroid.network.models;

public class SearchTaxiResult {
    int taxopark_id;
    int locality_id;
    int status;
    int taxopark_price;
    int move_distanse;
    int move_time;
    int order;
    Point destination_points;
    Point source_points;


    public class Point {
        long lon;
        long lat;
    }
}
