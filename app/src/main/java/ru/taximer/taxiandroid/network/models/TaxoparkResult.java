package ru.taximer.taxiandroid.network.models;

import java.util.Date;
import java.util.List;

public class TaxoparkResult {
    int id;
    String name;
    String name_en;
    String country_id;
    int city_id;
    int region_id;
    String about;
    String email;
    String phone_number;
    String img_url;
    int rating;
    int status;
    int price_rating;
    int make_up;
    boolean has_taxi;
    boolean has_carsharing;
    boolean has_child;
    boolean pay_cash;
    boolean pay_card;
    boolean pay_card_driver;
    int average_wait_time;
    Date last_use_datetime;
    Date created_at;
    Date updated_at;
    String code;
    boolean federal;
    List<Classes> classes;
    List<Volumes> volumes;

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

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getPrice_rating() {
        return price_rating;
    }

    public void setPrice_rating(int price_rating) {
        this.price_rating = price_rating;
    }

    public int getMake_up() {
        return make_up;
    }

    public void setMake_up(int make_up) {
        this.make_up = make_up;
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

    public boolean isPay_cash() {
        return pay_cash;
    }

    public void setPay_cash(boolean pay_cash) {
        this.pay_cash = pay_cash;
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

    public Date getLast_use_datetime() {
        return last_use_datetime;
    }

    public void setLast_use_datetime(Date last_use_datetime) {
        this.last_use_datetime = last_use_datetime;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isFederal() {
        return federal;
    }

    public void setFederal(boolean federal) {
        this.federal = federal;
    }

    public List<Classes> getClasses() {
        return classes;
    }

    public void setClasses(List<Classes> classes) {
        this.classes = classes;
    }

    public List<Volumes> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volumes> volumes) {
        this.volumes = volumes;
    }

}
