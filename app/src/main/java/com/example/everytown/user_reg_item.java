package com.example.everytown;

public class user_reg_item {
    int no;
    String address;

    public user_reg_item(int no, String address){
        this.no = no;
        this.address = address;
    }

    public int getno(){
        return no;
    }

    public String getAddress(){
        return address;
    }

    public void setno(int no){
        this.no = no;
    }
    public void setaddress(String address){
        this.address = address;
    }
}
