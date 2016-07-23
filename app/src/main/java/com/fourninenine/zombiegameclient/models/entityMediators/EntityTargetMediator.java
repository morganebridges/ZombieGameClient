package com.fourninenine.zombiegameclient.models.entityMediators;

import android.location.Location;

/**
 * Created by morganebridges on 7/21/16.
 */
public class EntityTargetMediator {

    EntityTargetMediator.Entities selection;
    long eid;
    String markerId;
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
    public EntityTargetMediator(Entities selection, long eid, Location userLocation, String markerId) {
        this.selection = selection;
        this.eid = eid;
        this.markerId = markerId;
        this.userLocation = userLocation;
    }


}
