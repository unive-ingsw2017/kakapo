package com.heliohost.kakapo.datitalia;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simonescaboro on 04/12/17.
 */
public class User implements Serializable{
    private String ID;
    private String email;
    private String username;
    private boolean isBot;
    private int points;
    private String provincia;

    public User(){}

    /*
    public User(String ID, String email, String name,String provincia) {
        this.ID = ID;
        this.email = email;
        this.username = name;
        this.provincia = provincia;
        isBot = false;
    }
    */

    public User(String ID, String email, String username, String provincia, boolean isBot, int points) {
        this.ID = ID;
        this.email = email;
        this.username = username;
        this.isBot = isBot;
        this.points = points;
        this.provincia = provincia;
    }

    public User(String username, int points, String provincia) {
        this.username = username;
        this.points = points;
        this.provincia = provincia;
    }

    public User(String ID, String email, String provincia) {
        this.ID = ID;
        this.email = email;
        this.provincia = provincia;

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }


    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("ID",ID);
        result.put("email",email);
        result.put("username",username);
        result.put("provincia",provincia);
        result.put("points",points);
        result.put("isBot",isBot);
        return result;
    }
}
