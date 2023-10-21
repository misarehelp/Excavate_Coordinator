package ru.volganap.nikolay.haircut_schedule;

import java.util.ArrayList;

class ListViewData {

   private ArrayList<ArrayList<MainScreenData>> output_array;
   private ArrayList<String> days_interval;
   private ArrayList<String> days_of_week;
   private int sum_of_rec[];

   public ListViewData(ArrayList<ArrayList<MainScreenData>> output_array, ArrayList<String> days_interval, ArrayList<String> days_of_week, int[] sum_of_rec) {

     this.output_array = output_array;
     this.days_interval = days_interval;
     this.days_of_week = days_of_week;
     this.sum_of_rec = sum_of_rec;
   }

   ArrayList<ArrayList<MainScreenData>> getOutputArray() {
      return output_array;
   }

   ArrayList<String> getDaysInterval() {
      return days_interval;
   }

   ArrayList<String> getDaysOfWeek() {
      return days_of_week;
   }

   int[] getSumOfRec () {
      return sum_of_rec;
   }
}
