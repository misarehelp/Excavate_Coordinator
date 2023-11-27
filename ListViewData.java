package ru.volganap.nikolay.haircut_schedule;

import java.util.ArrayList;

class ListViewData {

   private final ArrayList<ArrayList<MainScreenData>> output_array;
   private final ArrayList<String> days_interval;

   public ListViewData(ArrayList<ArrayList<MainScreenData>> output_array, ArrayList<String> days_interval ) {

     this.output_array = output_array;
     this.days_interval = days_interval;
   }

   ArrayList<ArrayList<MainScreenData>> getOutputArray() {
      return output_array;
   }

   ArrayList<String> getDaysInterval() {
      return days_interval;
   }
}
