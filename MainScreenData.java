package ru.volganap.nikolay.haircut_schedule;

class MainScreenData {
   private String name, time, job, index, type;
   private int resource,  color;
   public MainScreenData() {
   }

   //getters
   String getName() {return name;}
   String getTime() {
      return time;
   }
   String getJob() {
      return job;
   }
   String getIndex() {
      return index;
   }
   String getType() {
      return type;
   }
   int getResource() {
      return resource;
   }
   int  getColor() {
      return color;
   }

   //setters
   void setTime( String time) {this.time = time;}
   void setName( String name) {this.name = name;}
   void setJob( String job) {this.job = job;}
   void setIndex (String index) {
      this.index = index;
   }
   void setType( String type) { this.type = type; }
   void setResource( int resource) {this.resource = resource;}
   void setColor( int color) {this.color = color;}
}