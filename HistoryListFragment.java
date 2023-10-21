package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryListFragment extends Fragment implements Constants, Enums, Contract.RecordActivityToFragmentBroadcast,
         Contract.RecordActivityToSomeFragment {
   private Context context;
   private RecyclerView recyclerView;
   private HistoryRecycleAdapter adapter;
   private Button bt_back;
   private TextView tv_client_name, tv_client_phone;
   private Contract.SomeFragmentToRecordActivity callbackToRecordFragment;

   public HistoryListFragment () {
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      this.context = context;
      try {
         callbackToRecordFragment = (Contract.SomeFragmentToRecordActivity) context;

      } catch (ClassCastException e) {
         throw new ClassCastException(context + " must implement Contract.RecordFragmentToRecordActivity");
      }
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.history_holder, container, false);
      return view;
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {

      bt_back = view.findViewById(R.id.bt_back);
      tv_client_name = view.findViewById(R.id.tv_client_name);
      tv_client_phone = view.findViewById(R.id.tv_client_phone);
      // go back
      bt_back.setOnClickListener(v -> {
         callbackToRecordFragment.backToRecordFragment(RECORD_HOST);
      });

      recyclerView = view.findViewById(R.id.rv_history);
      adapter = new HistoryRecycleAdapter(context);

      DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
      Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.layout_devider);
      horizontalDecoration.setDrawable(horizontalDivider);

      recyclerView.addItemDecoration(horizontalDecoration);
      recyclerView.setAdapter(adapter);
   }

   // Convert Json  into Record Data
   private RecordData getFromJsonToRecordData(String data) {
      return new Gson().fromJson(data, RecordData.class);
   }

   @Override
   public void onBroadcastReceive(Intent intent) {

      ArrayList<RecordData> rec_data_array = new ArrayList<>();
      String message = intent.getStringExtra(MESSAGE);
      String status = intent.getStringExtra(SENDER);
      // Getting History Client Data after Command SERVER_GET_CLIENTS
      switch ( status ) {

         case DATA_IS_READY:

            ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
            for (String item : array_level_json) {
               RecordData rd = getFromJsonToRecordData(item); // get original (LATIN) Record Data
               if (!(null == rd)) {
                  rec_data_array.add(rd);
               }
            }

            adapter.swap(rec_data_array);
            //this.data = data;
            adapter.notifyDataSetChanged();
            break;
         // No data was got from the server
         default:
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            break;
      }
   }

   @Override
   public void onGetClientDataToFragment(String name, String phone) {
      tv_client_name.setText(name);
      tv_client_phone.setText(phone);
   }

   @Override
   public void onDetach() {
      callbackToRecordFragment = null;
      super.onDetach();
   }
}

