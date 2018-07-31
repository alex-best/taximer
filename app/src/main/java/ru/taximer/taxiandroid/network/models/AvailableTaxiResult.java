package ru.taximer.taxiandroid.network.models;

public class AvailableTaxiResult {
    int id;
    String name;
    String name_en;
    String about;
    String phone_number;
    String img_url;
    int rating;
    int status;
    boolean has_taxi;
    boolean has_carsharing;
    boolean has_child;
    boolean pay_card;
    boolean pay_card_driver;
    int average_wait_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isHas_taxi() {
        return has_taxi;
    }

    public void setHas_taxi(boolean has_taxi) {
        this.has_taxi = has_taxi;
    }

    public boolean isHas_carsharing() {
        return has_carsharing;
    }

    public void setHas_carsharing(boolean has_carsharing) {
        this.has_carsharing = has_carsharing;
    }

    public boolean isHas_child() {
        return has_child;
    }

    public void setHas_child(boolean has_child) {
        this.has_child = has_child;
    }

    public boolean isPay_card() {
        return pay_card;
    }

    public void setPay_card(boolean pay_card) {
        this.pay_card = pay_card;
    }

    public boolean isPay_card_driver() {
        return pay_card_driver;
    }

    public void setPay_card_driver(boolean pay_card_driver) {
        this.pay_card_driver = pay_card_driver;
    }

    public int getAverage_wait_time() {
        return average_wait_time;
    }

    public void setAverage_wait_time(int average_wait_time) {
        this.average_wait_time = average_wait_time;
    }
}
