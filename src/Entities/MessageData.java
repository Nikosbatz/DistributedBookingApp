package Entities;

import java.io.Serializable;

public class MessageData implements Serializable {
    public String data ;
    public MessageData(String data){
        this.data = data;
    }
    public MessageData(){
        this.data = "";
    }
}