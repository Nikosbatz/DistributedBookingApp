package Entities;

import java.io.Serializable;

import org.json.simple.JSONObject;

public class MessageData implements Serializable {
    public String data ;
    public JSONObject json;

    public MessageData(String data){

        this.data = data;
    }

    public MessageData(){
        this.data = "";
    }

    public MessageData(JSONObject json){
        this.json = json;
    }
}