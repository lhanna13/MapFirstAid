package com.example.lhila.mapfirstaid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
* Created by Lauren Hiland on 6/06/2016.
* Class to parse JSON file
 */
public class JSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String,String>> parseJsonObj(JSONObject jObject){

        JSONArray jsonLocations = null;
        try {
            /* Retrieves all the elements in the jsonLocations JSON array */
            jsonLocations = jObject.getJSONArray("results");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        /* Invoking getPlaces with the array of json object where each json object represent a location.
         */
        return getPlacesArray(jsonLocations);
    }

    private List<HashMap<String, String>> getPlacesArray(JSONArray jPlaces){
        int locationCount = jPlaces.length();
        List<HashMap<String, String>> listOfLocations = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> place;

        /** Taking each place, parses and adds to list object */
        for(int j = 0; j < locationCount; j++){
            try {
                /* Call getPlace with place JSON object to parse the place */
                place = getPlace((JSONObject) jPlaces.get(j));
                listOfLocations.add(place);

            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }

        return listOfLocations;
    }

    /* Parsing the Place JSON object */
    private HashMap<String, String> getPlace(JSONObject jPlace){

        HashMap<String, String> hmPlace = new HashMap<String, String>();
        String locationName = "-Location Name Not Available-";
        String vicinity="-Vicinity Not Available-";

        String latitude;
        String longitude;

        try {
            // Retrieving the Place name, if it is not null
            if(!jPlace.isNull("name")){
                locationName = jPlace.getString("name");
            }

            //  Retrieving the Place vicinity, if it is not null
            if(!jPlace.isNull("vicinity")){
                vicinity = jPlace.getString("vicinity");
            }

            //Getting the latitude and longitude of the Place
            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");

            hmPlace.put("place_name", locationName);
            hmPlace.put("vicinity", vicinity);
            hmPlace.put("lat", latitude);
            hmPlace.put("lng", longitude);

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return hmPlace;
    }
}