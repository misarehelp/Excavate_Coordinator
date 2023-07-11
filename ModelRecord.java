package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Boolean.getBoolean;

class ModelRecord implements Contract.ModelRecord, KM_Constants  {

   //private final ArrayList<RecordData> rec_data_array  = new ArrayList<>();
   final String DELIMITER = "*";
   private DataParameters dataParameters;
   private Context context;

   private String code;
   private RecordData rd;
   private String id_date;

   // initiating the objects of Model
   public ModelRecord( Context context, DataParameters dataParameters ) {
      this.context = context;
      this.dataParameters = dataParameters;

   }

   // Convert Record into Json
   private String getFromRecordDataToJson(RecordData record_data) {
      return new Gson().toJson(record_data);
   }

   @Override
   public void addRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, ArrayList<String> rec_data ) {
      //
      //Date date_time = convertStringtoDateTime(rec_data.get(2), rec_data.get(3));
      rd = new RecordData();

      rd.setName(rec_data.get(0));
      rd.setPhone(rec_data.get(1));
      rd.setDate(rec_data.get(2));
      rd.setTime(rec_data.get(3));
      rd.setDuration(rec_data.get(4));
      rd.setJob(rec_data.get(5));
      rd.setPrice(rec_data.get(6));
      rd.setComment(rec_data.get(7));
      rd.setRemind(getBoolean(rec_data.get(8)));
      // add chosen record to list of records in the Repository

      //dataParameters.setStateCode(ADD_CODE);
      code = ADD_CODE;
      new OkHttpRequest().serverGetback( context, SERVER_ADD_RECORD, "", getFromRecordDataToJson(rd) );

      //listener.onCloseRecordAction();
   }

   @Override
   public void changeRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, ArrayList<String> rec_data  ) {
      //
      rd = new RecordData();

      rd.setName(rec_data.get(0));
      rd.setPhone(rec_data.get(1));
      rd.setDate(rec_data.get(2));
      rd.setTime(rec_data.get(3));
      rd.setDuration(rec_data.get(4));
      rd.setJob(rec_data.get(5));
      rd.setPrice(rec_data.get(6));
      rd.setComment(rec_data.get(7));
      rd.setRemind(getBoolean(rec_data.get(8)));

      //String date_time = rd.getDate() + DELIMITER + rd.getTime();
      //dataParameters.setStateCode(ADD_CODE);
      code = CHANGE_CODE;
      new OkHttpRequest().serverGetback( context, SERVER_CHANGE_RECORD, id_date, getFromRecordDataToJson(rd) );

   }

   @Override
   public void deleteRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener ) {
      //
      //String date_time = rd.getDate() + "*" + rd.getTime();
      //dataParameters.setStateCode(ADD_CODE);
      code = DELETE_CODE;
      new OkHttpRequest().serverGetback( context, SERVER_DELETE_RECORD, id_date, "" );

   }

   @Override
   public ArrayList<String> getSelectedRecordData () {

      rd = dataParameters.getRecordDataArray().get(dataParameters.getPosition());
      id_date = rd.getDate() + DELIMITER + rd.getTime();
      ArrayList<String> rd_str  = new ArrayList<>();

      rd_str.add(rd.getName());
      rd_str.add(rd.getPhone());
      rd_str.add(rd.getDate());
      rd_str.add(rd.getTime());
      rd_str.add(rd.getDuration());
      rd_str.add(rd.getJob());
      rd_str.add(rd.getPrice());
      rd_str.add(rd.getComment());
      rd_str.add(Boolean.toString(rd.getRemind()));

      return rd_str;
   }

   @Override
   public void getFromModelBroadcastReceiver( Contract.ModelRecord.OnPresenterRecordCallback act_listener, Contract.ViewMainLayout view_listener, Intent intent ) {
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {

            String message = intent.getStringExtra(MESSAGE);
            String status = intent.getStringExtra(SENDER);
            ArrayList<RecordData> rec_data_array = dataParameters.getRecordDataArray();

            if (status.equals(DATA_WAS_SAVED)) {
               switch (code) {
                  // Record Data was changed on the server
                  case ADD_CODE:

                     rec_data_array.add(rd);

                     dataParameters.setRecordDataArray(rec_data_array);
                     break;

                  // Config answer was got from the server
                  case DELETE_CODE:

                     rec_data_array.remove(dataParameters.getPosition());

                     dataParameters.setRecordDataArray(rec_data_array);
                     break;

                  // Got some Record Data
                  case CHANGE_CODE:
                     // Getting Record Data after Command GET_BY_DATE or GET_ALL
                     int pos = dataParameters.getPosition();
                     rec_data_array.set( pos, rd );

                     dataParameters.setRecordDataArray(rec_data_array);
                     break;
                  //Config Data has got or confirmation of saving Depline Data to Server
                  default:
                     status = message;
                     break;
               }
               act_listener.onCloseRecordAction();
            } else {
               act_listener.onShowToast(DATA_WAS_NOT_SAVED);
            }
            //view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( status );

         }
      }, 0);
   }

   /* @Override
   public void sendModelDataToServer ( Contract.ViewMainLayout view_listener, String command, String dateID, String value) {
      //
   } */

}
