package com.fourninenine.zombiegameclient.models;

import com.orm.SugarRecord;

/**
 * Created by morganebridges on 5/28/16.
 */
    public class User extends SugarRecord{
    private long clientKey;
    private String name;

    public User(String name, long clientKey){
        this.name = name;
        this.clientKey = clientKey;
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

    public void setClientKey(long clientKey){
        this.clientKey = clientKey;
    }

    public long getClientKey() {
        return clientKey;
    }
}
