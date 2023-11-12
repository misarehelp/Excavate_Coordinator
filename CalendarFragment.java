package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CalendarFragment extends Fragment {
    GridView grid;
    ImageView left,right;
    TextView mon,yr;
    CalendarAdapter adapter2;
    ArrayList<String> weeks = new ArrayList<>();

    String[] months= {"ЯНВАРЬ","ФЕВРАЛЬ","МАРТ","АПРЕЛЬ","МАЙ","ИЮНЬ","ИЮЛЬ","АВГУСТ","СЕНТЯБРЬ","ОКТЯБРЬ", "НОЯБРЬ","ДЕКАБРЬ"};
    private int month, year;
    Date d;
    Calendar cal;
    private String dayOfTheWeek;

    private Contract.CalendarFragmentToMainActivity callbackToActivity;
    private final HashMap<String, Integer> cal_hashmap;

    private Context context;

    public CalendarFragment( HashMap<String, Integer> cal_hashmap, int year_backup, int month_backup ) {
        this.cal_hashmap = cal_hashmap;
        cal = Calendar.getInstance();

        if (year_backup != 0) {
            cal.set(year_backup, month_backup, 10);
        }

        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            callbackToActivity = (Contract.CalendarFragmentToMainActivity) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Contract.CalendarFragmentToMainActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        weeks.add("MON");
        weeks.add("TUE");
        weeks.add("WED");
        weeks.add("THU");
        weeks.add("FRI");
        weeks.add("SAT");
        weeks.add("SUN");

        grid = v.findViewById(R.id.grid);
        left = v.findViewById(R.id.left);
        right = v.findViewById(R.id.right);
        mon = v.findViewById(R.id.mon);
        yr = v.findViewById(R.id.yr);

        final SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        d = new Date();
        d.setDate(1);
        dayOfTheWeek = sdf.format(d).toUpperCase();
        int ordinal_dayOfTheWeek = weeks.indexOf(dayOfTheWeek);

        adapter2 = new CalendarAdapter(cal, ordinal_dayOfTheWeek, cal_hashmap);
        grid.setAdapter(adapter2);
        mon.setText(months[month]);
        yr.setText("" + year);

        grid.setOnItemClickListener((parent, view, position, id) -> {

            Object obj = view.getTag();
            if (null != obj) {
                int pos = (Integer) obj;
                callbackToActivity.onDateSet(year, month, pos);
                //callbackToActivity.onDateSet(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), pos);
                adapter2.setDay(pos);
                adapter2.notifyDataSetChanged();

            }
        });

        left.setOnClickListener(v1 -> {
            if (month == 0) {
                month = 11;
                year--;
            } else month--;

            drawCalendarOnArrowsClick (sdf);
        });

        right.setOnClickListener(v12 -> {

            if (month == 11) {
                month = 0;
                year++;
            } else month++;

            drawCalendarOnArrowsClick (sdf);
        });

        return v;
    }

    private void drawCalendarOnArrowsClick (SimpleDateFormat sdf) {
        d.setMonth(month);
        d.setYear(year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        mon.setText(months[month]);
        yr.setText("" + year);
        dayOfTheWeek = sdf.format(d).toUpperCase();
        int w = weeks.indexOf(dayOfTheWeek);

        if(w == 0) {
            adapter2 = new CalendarAdapter(cal,6, cal_hashmap);
        } else {
            adapter2 = new CalendarAdapter(cal,w - 1, cal_hashmap);
        }

        grid.setAdapter(adapter2);
    }

}