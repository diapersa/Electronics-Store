package com.example.a4diapersa.domain;

import java.io.Serial;
import java.io.Serializable;

public abstract class Entity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int ID;

    Entity(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "ID=" + getID();
    }
}
