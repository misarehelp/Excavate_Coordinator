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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CalendarFragment extends Fragment implements Contract.MainActivityToCalendarFragment {
    private static final int START_POSITION = 1;
    private static final int GRID_SPACING = 2;
    GridView grid;
    ImageView left, right;
    TextView tv_month, tv_year;
    CalendarAdapter adapter2;
    ArrayList<String> weeks = new ArrayList<>();

    String[] months= {"ЯНВАРЬ","ФЕВРАЛЬ","МАРТ","АПРЕЛЬ","МАЙ","ИЮНЬ","ИЮЛЬ","АВГУСТ","СЕНТЯБРЬ","ОКТЯБРЬ", "НОЯБРЬ","ДЕКАБРЬ"};
    private int month, year;
    Calendar calendar = Calendar.getInstance();

    private Contract.CalendarFragmentToMainActivity callbackToActivity;
    private HashMap<String, Integer> cal_hashmap;

    private Context context;

    public CalendarFragment() {
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
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

        weeks.add("ПН");
        weeks.add("ВТ");
        weeks.add("СР");
        weeks.add("ЧТ");
        weeks.add("ПТ");
        weeks.add("СБ");
        weeks.add("ВС");

        grid = v.findViewById(R.id.grid);
        left = v.findViewById(R.id.left);
        right = v.findViewById(R.id.right);
        tv_month = v.findViewById(R.id.mon);
        tv_year = v.findViewById(R.id.yr);

        grid.setVerticalSpacing(GRID_SPACING);
        grid.setHorizontalSpacing(GRID_SPACING);

        tv_month.setText(months[month]);
        tv_year.setText("" + year);

        grid.setOnItemClickListener((parent, view, position, id) -> {

            Object obj = view.getTag();
            if (null != obj) {
                int day = (Integer) obj;
                calendar.set(Calendar.DAY_OF_MONTH, day);

                int page = getOrdinalDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));

                setDayForAdapter(day, page);
            }
        });

        left.setOnClickListener(v1 -> {
            if (month == 0) {
                month = 11;
                year--;
            } else month--;

            drawCalendarOnArrowsClick ();
        });

        right.setOnClickListener(v12 -> {
            if (month == 11) {
                month = 0;
                year++;
            } else month++;

            drawCalendarOnArrowsClick ();
        });

        return v;
    }

    @Override
    public void setCalendarHashMap (HashMap<String, Integer> cal_hashmap, Calendar calendar_backup) {

        this.cal_hashmap = cal_hashmap;
        calendar.setTime(calendar_backup.getTime());

        int day_backup = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int first_dayOfTheWeek = getOrdinalDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.DAY_OF_MONTH, day_backup);

        adapter2 = new CalendarAdapter(calendar, first_dayOfTheWeek, cal_hashmap);
        grid.setAdapter(adapter2);

        int ordinal_dayOfTheWeek = getOrdinalDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        setDayForAdapter(calendar.get(Calendar.DAY_OF_MONTH), ordinal_dayOfTheWeek);

    }

    private void drawCalendarOnArrowsClick () {

        calendar.set(year, month, START_POSITION);
        tv_month.setText(months[month]);

        tv_year.setText("" + year);
        int ordinal_dayOfTheWeek = getOrdinalDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));

        adapter2 = new CalendarAdapter(calendar, ordinal_dayOfTheWeek, cal_hashmap);
        grid.setAdapter(adapter2);
        setDayForAdapter(calendar.get(Calendar.DAY_OF_MONTH), ordinal_dayOfTheWeek);
    }

    private int getOrdinalDayOfWeek(int dow) {
        switch ( dow ) {
            case 1:
                return 6;
            case 2:
                return 7;
            default:
                return dow - 2;
        }
    }

    @Override
    public void syncCalendarDayToPage(int day) {
        adapter2.setDay(day);
        adapter2.notifyDataSetChanged();
    }

    private void setDayForAdapter (int day, int page) {

        String data_str = adapter2.getDateString(day);

        if (page == 7) callbackToActivity.onDateSet( calendar, 0, data_str );
            else callbackToActivity.onDateSet( calendar, page, data_str );

        syncCalendarDayToPage(day);
    }
}