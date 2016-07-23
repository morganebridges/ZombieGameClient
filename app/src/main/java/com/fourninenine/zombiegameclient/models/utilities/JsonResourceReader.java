package com.fourninenine.zombiegameclient.models.utilities;

import android.util.Log;

import com.fourninenine.zombiegameclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by morganebridges on 7/20/16.
 */
public class JsonResourceReader {
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = ApplicationContextProvider.getAppContext().getAssets().open(".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public String createFormData() {
        try {
            //JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = new JSONArray(ApplicationContextProvider.getAppContext().getAssets().open("midnight_commander_theme.JSON"));

            ArrayList<HashMap<String, String>> stylerList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("featureType-->", jo_inside.getString("featureType"));
                String featureType_value = jo_inside.getString("featureType");
                String url_value = jo_inside.getString("url");

                //Add your values in your `ArrayList` as below:
                m_li = new HashMap<String, String>();
                m_li.put("formule", featureType_value);
                m_li.put("url", url_value);

                //.add(m_li);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("");
        }
        return " ";
    }

}
