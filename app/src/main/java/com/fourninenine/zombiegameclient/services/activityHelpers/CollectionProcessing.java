package com.fourninenine.zombiegameclient.services.activityHelpers;

import com.fourninenine.zombiegameclient.models.Zombie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by morganebridges on 7/10/16.
 */
public class CollectionProcessing {

    public static HashMap<Long, Zombie> zombieListToMap(ArrayList<Zombie> zombList){
        HashMap<Long, Zombie> map = null;
        Iterator<Zombie> zit = zombList.iterator();
        map = new HashMap<>();
        while(zit.hasNext()){
           Zombie tmp = zit.next();
            map.put(tmp.getId(), tmp);
        }
        return map;
    }

}
