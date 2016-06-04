package com.fourninenine.zombiegameclient.models;

import com.orm.SugarRecord;

/**
 * Created by morganebridges on 5/28/16.
 */
    public class User extends SugarRecord{
    private long key;
    private String name;

    public User(String name, long key){
        this.name = name;
        this.key = key;
    }
    public User(){}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setId(long uid){
    }

    public void setKey(long key){
        this.key = key;
    }

    public long getKey() {
        return key;
    }
}
