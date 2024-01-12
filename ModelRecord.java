package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class ModelRecord implements Contract.ModelRecord, Constants {

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

   private void convertAndSendJsonData(Contract.ModelRecord.OnPresenterRecordCallback listener, String command, String dateID, RecordData  rec_data ) {

      if (isInputFieldsCorrect( rec_data.getDate(), rec_data.getTime(), rec_data.getDuration() )) {

         String name, phone;
         name = rec_data.getName();
         phone = rec_data.getPhone();

         if ( rec_data.getId() > NOT_IN_CLIENT_BASE) {
            rec_data.setName("");
            rec_data.setPhone("");
         }

         sendRecordDataToServer( command, dateID, new Gson().toJson(rec_data) );

         if ( rec_data.getId() > NOT_IN_CLIENT_BASE) {
            rec_data.setName(name);
            rec_data.setPhone(phone);
         }

         rd = rec_data;

      } else {
         listener.onShowToast(context.getResources().getString(R.string.incorrect_duration));
      }
   }

   @Override
   public void changeRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, RecordData  rec_data, String command_code  ) {
      //
      code = command_code;

      switch (code) {
         // Record Data was changed on the server
         case SERVER_ADD_RECORD:
         case SERVER_MARK_HOLIDAY:
            convertAndSendJsonData(listener, SERVER_ADD_RECORD, "",  rec_data);
            break;

         case SERVER_CHANGE_RECORD:
            convertAndSendJsonData(listener, code, id_date,  rec_data);
            break;

         case SERVER_DELETE_RECORD:
            sendRecordDataToServer( code, id_date, "");
            break;

         case SERVER_UNMARK_HOLIDAY:
            id_date = rec_data.getDate() + DELIMITER + rec_data.getTime();
            sendRecordDataToServer( SERVER_DELETE_RECORD, id_date, "");
            break;

         default:
            break;
      }
   }

   void sendRecordDataToServer ( String command, String dateID, String value) {
      new OkHttpRequest().serverGetback( context, command, dateID, value );
   }

   public boolean isInputFieldsCorrect (String s_new_date, String s_new_time, String s_new_duration) {

      ArrayList<RecordData> rda_date = new ArrayList<>();
      for (RecordData rda_base: dataParameters.getRecordDataArray()) {
         if (rda_base.getDate().equals(s_new_date)) {
            rda_date.add(rda_base);
         }
      }

      String s_position = Integer.toString(dataParameters.getRecordPosition());

      Calendar calendar_new_start = Calendar.getInstance();
      Calendar calendar_new_end = Calendar.getInstance();
      Calendar calendar_next_start = Calendar.getInstance();
      Calendar calendar_next_end = Calendar.getInstance();
      SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

      boolean fit = true;
      try {
         Date new_time = time_formatter.parse(s_new_time);
         int duration_int = Integer.parseInt(s_new_duration);

         for (RecordData rda_item: rda_date) {
            Date time_next = time_formatter.parse(rda_item.getTime());
            int duration_next = Integer.parseInt(rda_item.getDuration());

            // check if the position of a new record equals itself
            if ( !s_position.equals(rda_item.getIndex())) {

               calendar_new_start.setTime(new_time);
               calendar_new_end.setTime(new_time);
               calendar_new_end.add(Calendar.MINUTE, duration_int);

               calendar_next_start.setTime(time_next);
               calendar_next_end.setTime(time_next);
               calendar_next_end.add(Calendar.MINUTE, duration_next);

               // check if the start/end of new record equals the current one
               if (calendar_new_start.equals(calendar_next_start) || calendar_new_end.equals(calendar_next_end)) {
                  return false;
               }
               // check if the a new record intersects the current one
               if (calendar_new_start.after(calendar_next_start) && calendar_new_start.before(calendar_next_end)) {
                  return false;
               }

               if (calendar_new_end.after(calendar_next_start) && calendar_new_end.before(calendar_next_end)) {
                  return false;
               }

               if (calendar_new_start.before(calendar_next_start) && calendar_new_end.after(calendar_next_end)) {
                  return false;
               }
            }
         }

      } catch (ParseException e) {
         Log.d(LOG_TAG, "ModelRecord - Exception is: " + e);
      }

      return fit;
   }

   @Override
   public RecordData getSelectedRecordData () {
      rd = dataParameters.getRecordDataArray().get(dataParameters.getRecordPosition());
      id_date = rd.getDate() + DELIMITER + rd.getTime();

      return rd;
   }

   @Override
   public void sendSMS(OnPresenterRecordCallback listener, RecordData rec_data, String message) {

      Uri uri = Uri.parse("smsto:" + rec_data.getPhone());
      Intent smsSIntent = new Intent(Intent.ACTION_SENDTO, uri);
      smsSIntent.putExtra("sms_body", message);

      try {
         context.startActivity(smsSIntent);

      } catch (Exception ex) {
         listener.onShowToast("Ошибка отправки напоминания через СМС");
         ex.printStackTrace();
      }
   }

   @Override
   public void getFromModelBroadcastReceiver( Contract.ModelRecord.OnPresenterRecordCallback act_listener, Intent intent ) {
      new Handler().postDelayed(() -> {

         String status = intent.getStringExtra(SENDER);
         ArrayList<RecordData> rec_data_array = dataParameters.getRecordDataArray();

         if (status.equals(DATA_WAS_SAVED) || status.equals(DATA_WAS_DELETED) ) {
            switch (code) {
               // Record Data was changed on the server
               case SERVER_ADD_RECORD:
               case SERVER_MARK_HOLIDAY:
                  rec_data_array.add(rd);
                  break;

               case SERVER_DELETE_RECORD:
                  rec_data_array.remove(dataParameters.getRecordPosition());
                  break;

               case SERVER_UNMARK_HOLIDAY:
                  for (RecordData item: rec_data_array) {
                     if (id_date.equals(item.getDate() + DELIMITER + item.getTime())) {
                        rec_data_array.remove(item);
                     }
                  }
                  rec_data_array.remove(dataParameters.getRecordPosition());
                  break;

               case SERVER_CHANGE_RECORD:
                  int pos = dataParameters.getRecordPosition();
                  rec_data_array.set( pos, rd );
                  break;
               //Config Data has got or confirmation of saving Depline Data to Server
               default:
                  break;
            }

            dataParameters.setRecordDataArray(rec_data_array);
            act_listener.onShowToast(status);
            act_listener.onCloseRecordAction();

         } else {
            act_listener.onShowToast(DATA_WAS_NOT_SAVED);
         }

         dataParameters.setStateCode(status);
      }, 0);
   }

   @Override
   public void getLastIncomingCall ( Contract.ModelRecord.OnPresenterRecordCallback listener ) {

      //this help you to get recent call
      Uri contacts = CallLog.Calls.CONTENT_URI;
      Cursor managedCursor = context.getContentResolver().query(contacts, null, null,
              null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
      int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
      int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
      /* int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
      int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
      StringBuffer sb = new StringBuffer(); */
      managedCursor.moveToNext();
      String phone_number = managedCursor.getString(number);
      String callType = managedCursor.getString(type);
      /* String callDate = managedCursor.getString(date);
      String callDayTime = new Date(Long.valueOf(callDate)).toString();
      int callDuration = managedCursor.getInt(duration); */
      managedCursor.close();
      /*int dircode = Integer.parseInt(callType);
      if (dircode == CallLog.Calls.INCOMING_TYPE) {
         //Incoming calls
      } */
      listener.onFinishedGetLastCall( phone_number );
   }
}
