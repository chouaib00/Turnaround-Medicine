package com.example.user.dipl1.entity;

import java.io.Serializable;
import java.sql.Blob;

public class UserEntity implements Serializable {

    private int user_id;

    private String user_name;

    private String user_surname;

    private String user_fathername;

    private String user_email;

    private String user_adress;

    private String user_phone_number;

    private String user_work;

    private String user_status;

    private byte[] user_icon;

    private String user_password;

    private String google_id;

    private String facebook_id;

    private byte[] sertificate;

    public UserEntity() {
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public String getUser_fathername() {
        return user_fathername;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_adress() {
        return user_adress;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public String getUser_work() {
        return user_work;
    }

    public String getUser_status() {
        return user_status;
    }

    public byte[] getUser_icon() {
        return user_icon;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }

    public void setUser_fathername(String user_fathername) {
        this.user_fathername = user_fathername;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_adress(String user_adress) {
        this.user_adress = user_adress;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public void setUser_work(String user_work) {
        this.user_work = user_work;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public void setUser_icon(byte[] user_icon) {
        this.user_icon = user_icon;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public byte[] getSertificate() {
        return sertificate;
    }

    public void setSertificate(byte[] sertificate) {
        this.sertificate = sertificate;
    }
}
