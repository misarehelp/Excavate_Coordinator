package ru.volganap.nikolay.haircut_schedule;

import com.google.gson.annotations.SerializedName;

class ClientData {
    @SerializedName("i")
    private int id;
    @SerializedName("n")
    private String name;
    @SerializedName("ph")
    private String phone;
    @SerializedName("c")
    private String comment;

    public ClientData() {
    }
    public ClientData(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    //getters
    int getId() {return id;}
    String getName() {return name;}
    String getPhone() {return phone;}
    String getComment() {return comment;}

    //setters
    void setId (int id) { this.id = id; }
    void setName (String name) { this.name = name; }
    void setPhone (String phone) { this.phone = phone; }
    void setComment (String comment) { this.comment = comment; }

}
