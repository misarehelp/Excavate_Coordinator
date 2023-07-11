package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment implements KM_Constants {

    private MainFragmentInterface mCallback;
    private ArrayList<Map<String, String>> data;

    public MainFragment (ArrayList<Map<String, String>> data) {
        this.data = data;
    }
    // TODO: Rename and change types and number of parameters
    /* public static MainFragment newInstance(int page) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE, page);
        fragment.setArguments(args);
        return fragment;
    } */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (MainFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //page = getArguments().getInt(PAGE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_holder, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        int[] to = { R.id.li_rec_time, R.id.li_rec_job, R.id.li_rec_name, R.id.li_rec_comment };

        SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.main_list_item_1, FROM, to);

        ListView lv_records_list = view.findViewById(R.id.lv_records_list);
        lv_records_list.setAdapter(adapter);

        lv_records_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int rec_pos = 0;
                //Log.d(LOG_TAG, "Main_Fragment, list item position: " + position );
                String s = data.get(position).get(FROM[4]);
                try {
                    rec_pos = Integer.parseInt(s);
                } catch (NullPointerException npe) {
                    Log.d(LOG_TAG, "Exception: " + npe );
                }
                //Log.d(LOG_TAG, "Main_Fragment, data item position: " + s );

                mCallback.getDatafromMainFragment(rec_pos);
            }
        });
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }
}