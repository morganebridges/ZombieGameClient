package com.fourninenine.zombiegameclient.models;

import com.orm.SugarRecord;

/**
 * Created by morganebridges on 6/4/16.
 */
public class Zombie extends SugarRecord {
    String name;
    long key;
    public Zombie(String name, long key) {this.key=key; this.name=name;}

}
