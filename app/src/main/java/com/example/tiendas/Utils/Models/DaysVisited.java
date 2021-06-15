package com.example.tiendas.Utils.Models;

public class DaysVisited {

    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    public DaysVisited(){
        this.monday=false;
        this.tuesday=false;
        this.wednesday=false;
        this.thursday=false;
        this.friday=false;
        this.saturday=false;
        this.sunday=false;
    }

    public void assingDays(boolean[] daysSelected){
        if(daysSelected[0]==true){
            this.monday=true;
        }
        if(daysSelected[1]==true){
            this.tuesday=true;
        }
        if(daysSelected[2]==true){
            this.wednesday=true;
        }
        if(daysSelected[3]==true){
            this.thursday=true;
        }
        if(daysSelected[4]==true){
            this.friday=true;
        }
        if (daysSelected[5] == true) {
            this.saturday =true;
        }
        if(daysSelected[6]==true){
            this.sunday =true;
        }
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }
}
