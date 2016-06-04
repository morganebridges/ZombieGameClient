package com.fourninenine.zombiegameclient.models;

import com.orm.SugarRecord;

/**
 * Created by morganebridges on 5/28/16.
 */
public class User extends SugarRecord{
    private String name;

    public User(String name){
        this.name = name;
    }
    public User(){}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
