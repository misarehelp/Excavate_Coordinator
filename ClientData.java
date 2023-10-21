package ru.volganap.nikolay.haircut_schedule;

class ClientData {
    private String name;
    private String phone;
    private String comment;
    private boolean pic_wish;

    public ClientData() {
    }

    //getters
    String getName() {return name;}
    String getPhone() {return phone;}
    String getComment() {return comment;}

    //setters
    void setName (String name) { this.name = name; };
    void setPhone (String phone) { this.phone = phone; };
    void setComment (String comment) { this.comment = comment; };

}
