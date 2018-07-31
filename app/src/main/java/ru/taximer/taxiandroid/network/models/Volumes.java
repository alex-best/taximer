package ru.taximer.taxiandroid.network.models;

import java.util.Date;

public class Volumes {
    int id;
    int volume_value;
    String volume_name;
    String volume_name_en;
    Date created_at;
    Date updated_at;
    Pivot pivot;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVolume_value() {
        return volume_value;
    }

    public void setVolume_value(int volume_value) {
        this.volume_value = volume_value;
    }

    public String getVolume_name() {
        return volume_name;
    }

    public void setVolume_name(String volume_name) {
        this.volume_name = volume_name;
    }

    public String getVolume_name_en() {
        return volume_name_en;
    }

    public void setVolume_name_en(String volume_name_en) {
        this.volume_name_en = volume_name_en;
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

    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }
}