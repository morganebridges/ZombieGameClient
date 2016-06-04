package com.fourninenine.zombiegameclient.models;

import com.orm.SugarRecord;

/**
 * Created by morganebridges on 5/28/16.
 */
public class User extends SugarRecord{

    private long id;
    private String name;

    public User(String name){
        this.name = name;
    }
    public User(){}


    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String gamerTag) {
        this.name = gamerTag;
    }

    public String toString(){
        return "\nGamerTag: " + name
                +"\nId: " + id;
    }


}
