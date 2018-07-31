package ru.taximer.taxiandroid.network.models;

import java.util.Date;

public class Classes {
    int id;
    String class_name;
    String class_name_en;
    Date created_at;
    Date updated_at;
    Pivot pivot;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_name_en() {
        return class_name_en;
    }

    public void setClass_name_en(String class_name_en) {
        this.class_name_en = class_name_en;
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