package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Calendar;
import java.util.HashMap;

public class CalendarAdapter extends BaseAdapter {
   Calendar c;
   int ct, day, year, month, current_day, current_year, current_month;

   private HashMap<String, Integer> cal_hashmap;

   public CalendarAdapter(Calendar c, int ct, HashMap<String, Integer> cal_hashmap ) {
      this.c = c;
      this.ct = ct;
      this.cal_hashmap = cal_hashmap;

      Calendar current_cal = Calendar.getInstance();

      current_year = current_cal.get(Calendar.YEAR);
      current_month = current_cal.get(Calendar.MONTH);
      current_day = current_cal.get(Calendar.DAY_OF_MONTH);

      year = c.get(Calendar.YEAR);
      month = c.get(Calendar.MONTH);
   }

   public void setDay (int day) {
      this.day = day;
   }

   @Override
   public int getCount() {
      return getMonthDays(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)) + ct;
   }

   @Override
   public Object getItem(int position) {
      return c;
   }

   @Override
   public long getItemId(int position) {
      return position;
   }

   @Override
   public View getView(int p, View convertView, ViewGroup parent) {
      LayoutInflater mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View v = mLayoutInflater.inflate(R.layout.calendar_item, parent, false);

      TextView tv_day = v.findViewById(R.id.tv_day);
      TextView tv_event = v.findViewById(R.id.tv_event);

      int position = p + 1 - ct;
      if (position > 0) {
         v.setTag(position);
         tv_day.setText("" + position);

         if (position == day) {
            tv_day.setTextColor(Color.WHITE);
            tv_day.setBackgroundColor(Color.BLUE);
         }

         if (position == current_day && current_month == month && current_year == year ) {
            tv_day.setTextColor(Color.MAGENTA);
            tv_day.setBackgroundColor(Color.YELLOW);
         }

         String date = getDateString(position);
         if (cal_hashmap.containsKey(date)) {
            tv_event.setText("(" + cal_hashmap.get(date).toString() + ")");
            tv_event.setBackgroundColor(Color.LTGRAY);
         }
      }
      return v;
   }

   public String getDateString (int day) {
      String day_s = "" + day;
      if (day < 10) day_s = "0" + day_s;

      String month_s = "" + (month + 1);
      if (month < 9) month_s = "0" + month_s;

      return day_s + "." + month_s + "." + year;
   }

   public static int getMonthDays(int month, int year) {
      int daysInMonth ;
      if (month == 4 || month == 6 || month == 9 || month == 11) daysInMonth = 30;
         else if (month == 2) daysInMonth = (year % 4 == 0) ? 29 : 28;
                  else daysInMonth = 31;

      return daysInMonth;
   }

}
