package ru.volganap.nikolay.haircut_schedule;

class ClientData {
    private String id;
    private String name;
    private String phone;
    private String comment;
    private boolean pic_wish;

    public ClientData() {
    }

    //getters
    String getId() {return id;}
    String getName() {return name;}
    String getPhone() {return phone;}
    String getComment() {return comment;}
    boolean getPicWish() {return pic_wish;}

    //setters
    void setId (String id) { this.id = id; };
    void setName (String name) { this.name = name; };
    void setPhone (String phone) { this.phone = phone; };
    void getComment (String comment) { this.comment = comment; };
    void setPicWish (boolean pic_wish) { this.pic_wish = pic_wish; };

}
