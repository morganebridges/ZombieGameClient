package com.fourninenine.zombiegameclient.models.dto;

import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;

import java.util.HashMap;

/**
 * This class is a data transfer object that will update the client
 * Created by morganebridges on 7/9/16.
 */
public class ClientUpdateDto {

    long id;

    long targetId;
    HashMap<Long, Zombie> zombies;
    User user;
    UserActionDto.Action userAction;

    public ClientUpdateDto(){}
    public ClientUpdateDto(long targetId, HashMap<Long, Zombie> zombies, User user, UserActionDto.Action userAction){
        this.targetId = targetId;
        this. zombies = zombies;
        this.user = user;
        this.userAction = userAction;
    }
    public UserActionDto.Action getUserAction() {
        return userAction;
    }

    public void setUserAction(UserActionDto.Action userAction) {
        this.userAction = userAction;
    }


    public long getTargetId() {
        return targetId;
    }

    public void setAttackedZombie(Zombie attackedZombie) {
        this.targetId = targetId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HashMap<Long, Zombie> getZombies() {
        return zombies;
    }

    public void setZombies(HashMap<Long, Zombie> zombies) {
        this.zombies = zombies;
    }

    public enum UserAction {
        NOTHING (0),
        ATTACK (1),
        SALVAGE (2);

        private long id;

        int code;
        UserAction(int code) {
            this.code = code;
        }

        int getCode() {
            return this.code;
        }
    }
}
