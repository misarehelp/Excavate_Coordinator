package ru.volganap.nikolay.haircut_schedule;


import com.google.gson.annotations.SerializedName;

public class RecordData {
   @SerializedName("i")
   private int client_id;
   @SerializedName("n")
   private String name;
   @SerializedName("ph")
   private String phone;
   @SerializedName("dt")
   private String date;
   @SerializedName("t")
   private String time;
   @SerializedName("du")
   private String duration;
   @SerializedName("j")
   private String job;
   @SerializedName("pr")
   private String price;
   @SerializedName("c")
   private String comment;
   @SerializedName("b")
   private byte bits_index;

   private transient String index;

   public RecordData() {
   }

   //getters
   String getIndex() {
      return index;
   }
   byte getBitsIndex() {
      return bits_index;
   }
   int getId() {return client_id;}
   String getName() {return name;}
   String getPhone() {return phone;}
   String getDate() {
      return date;
   }
   String getTime() {
      return time;
   }

   String getDuration() {
      return duration;
   }
   String getJob() {
      return job;
   }
   String getPrice() {
      return price;
   }
   String getComment() {
      return comment;
   }

   //setters
   void setIndex (String index) {
      this.index = index;
}
   void setBitsIndex (byte bits_index) {
      this.bits_index = bits_index;
   }
   void setId( int client_id) {this.client_id = client_id;}
   void setName( String name) {this.name = name;}
   void setPhone( String phone) {this.phone = phone;}
   void setDate( String  date) {this.date = date;}
   void setTime( String  time) {this.time = time;}

   void setDuration( String duration) {this.duration = duration;}
   void setJob( String job) {this.job = job;}
   void setPrice( String price) {this.price = price;}
   void setComment( String comment) {this.comment = comment;}

   public boolean getIndexBit (byte b_id, int pos) {
      int mask = 1 << pos; // equivalent of 2 to the nth power
      return (b_id & mask) != 0;
   }
}
