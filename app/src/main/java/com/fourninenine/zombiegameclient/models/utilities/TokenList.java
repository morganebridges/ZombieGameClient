package com.fourninenine.zombiegameclient.models.utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by morganebridges on 6/18/16.
 */
public class TokenList extends ArrayList<String>{
    private static TokenList instance;
    private ArrayList<TokenItem> tokenMap;
    private TokenList(){
        this.tokenMap = new ArrayList<>();
    }
    public static TokenList Instance(){
        if(instance != null)
            instance = new TokenList();
        return instance;
    }

    public ArrayList<TokenItem> getMap() {
        return tokenMap;
    }
}
