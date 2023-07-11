package ru.volganap.nikolay.haircut_schedule;

import java.util.ArrayList;
import java.util.Map;

class ListViewData {

   private ArrayList<ArrayList<Map<String, String>>> output_array;
   private ArrayList<String> days_interval;
   private ArrayList<String> days_of_week;

   public ListViewData(ArrayList<ArrayList<Map<String, String>>> output_array, ArrayList<String> days_interval, ArrayList<String> days_of_week) {
     this.output_array = output_array;
     this.days_interval = days_interval;
     this.days_of_week = days_of_week;
   }

   ArrayList<ArrayList<Map<String, String>>> getOutputArray() {
      return output_array;
   }

   ArrayList<String> getDaysInterval() {
      return days_interval;
   }

   ArrayList<String> getDaysOfWeek() {
      return days_of_week;
   }

}
