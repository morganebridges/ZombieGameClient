package com.fourninenine.zombiegameclient.models.entityMediators;

import android.location.Location;

import com.fourninenine.zombiegameclient.MainMapActivity;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by morganebridges on 7/21/16.
 */
public class EntityTargetMediator {

    EntityTargetMediator.Entities selection;
    long eid;
    String markerId;
    Marker marker;
    Location userLocation;


    public Location getUserLocation() {
        return userLocation;
    }

    public Entities getSelection() {
        return selection;
    }

    public long getEid() {
        return eid;
    }
    public String getMarkerId(){return markerId;}
    public Marker getMarker(){return marker;};

    public void setMarker(Marker marker) {
        this.marker = marker;
    }


    public enum Entities {

            UENT (0),
            ZENT (1),
            NARRATIVE_NPC(2),
            RANDOM_NPC(3);

            private int enTypeSel;
            Entities(int selected) {
                this.enTypeSel = selected;
            }

            int getSelected() {
                return this.enTypeSel;
            }

    }
    /**
     *
     * @param selection - Which type of entity from Entities
     * @param eid - the unique identifier of the target \
     * @param userLocation -
     * @return
     */
    public EntityTargetMediator(Entities selection, long eid, Location userLocation, Marker marker) {
        this.selection = selection;
        this.eid = eid;
        this.marker = marker;
        this.markerId = marker.getId();
        //this.entityMarker = entityMarker;

    }



}
