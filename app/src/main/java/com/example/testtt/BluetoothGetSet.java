package com.example.testtt;

public class BluetoothGetSet {

    private String from, message;

    public BluetoothGetSet(){

    }


    BluetoothGetSet(String from,String message){
        this.from=from;
        this.message=message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
