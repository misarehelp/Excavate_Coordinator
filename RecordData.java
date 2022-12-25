package ru.volganap.nikolay.haircut_schedule;

import java.util.Date;

public class RecordData {
   private String id, name, phone, date, time, duration, job, price, comment;
   private boolean pic_bef, pic_aft, pic_wish, remind;

   public RecordData() {
   }

   //getters
   String getId() {return id;}
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

   boolean getPicBefore() {
      return pic_bef;
   }
   boolean getPicAfter() {
      return pic_aft;
   }
   boolean getPicWish() {
      return pic_wish;
   }
   boolean getRemind() {
      return remind;
   }

   //setters
   void setId( String id) {this.id = id;}
   void setName( String name) {this.name = name;}
   void setPhone( String phone) {this.phone = phone;}
   void setDate( String  date) {this.date = date;}
   void setTime( String  time) {this.time = time;}

   void setDuration( String duration) {this.duration = duration;}
   void setJob( String job) {this.job = job;}
   void setPrice( String price) {this.price = price;}
   void setComment( String comment) {this.comment = comment;}

   void setPicBefore( boolean pic_bef) {this.pic_bef = pic_bef;}
   void setPicAfter( boolean pic_aft) {this.pic_aft = pic_aft;}
   void setPicWish( boolean pic_wish) {this.pic_wish = pic_wish;}
   void setRemind( boolean remind) {this.remind = remind;}
}
