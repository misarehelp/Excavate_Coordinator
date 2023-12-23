package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

class PresenterRecord implements Contract.PresenterRecord, Constants, Enums, Contract.ModelRecord.OnPresenterRecordCallback {

   private Contract.ViewRecord recordView;
   private Contract.ModelRecord modelRecord;
   private Context context;
   private String state_code;
   private DataParameters dataParameters;

   // initiating the objects of View and Model Interface
   public PresenterRecord( Context context, Contract.ViewRecord recordView ) {

      this.recordView = recordView;
      this.context = context;

      dataParameters = DataParameters.getInstance();
      state_code = dataParameters.getStateCode();
      modelRecord = new ModelRecord( context, dataParameters );

      recordView.setRecordButtonsVisibility(state_code);

      if (!(state_code.equals(SERVER_ADD_RECORD))) {
         recordView.fillRecordInfoFields(modelRecord.getSelectedRecordData());
      }
   }
   // ************* start methods passed from RecordActivity to ModelRecord *******************
   @Override
   // operations to be performed - BroadcastReceiver trigger
   public void onBroadcastReceive(Intent intent) {
      modelRecord.getFromModelBroadcastReceiver(this, intent);
   }

   @Override
   public void onButtonChangeRecord(RecordData  rec_data, String code) {
      recordView.setRecordButtonsVisibility(SERVER_WAIT_FOR_ANSWER);
      modelRecord.changeRecordData( this, rec_data, code);
   }

   @Override
   public void onButtonAddLastCall() {
      modelRecord.getLastIncomingCall(this);
   }

   @Override
   public void onButtonMakeCall( String number ) {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
      context.startActivity(intent);
   }

   @Override
   public void onButtonSendSms(RecordData rec_data, String message) {
      modelRecord.sendSMS(this, rec_data, message);
   }

   @Override
   public void onButtonExit() {
      dataParameters.setStateCode(DATA_WAS_NOT_CHANGED);
      recordView.finishRecordActivity();
   }

   // ************* CallBack methods run from ModelRecord *******************
   @Override
   public void onCloseRecordAction( ) {
      recordView.finishRecordActivity();
   }

   @Override
   public void onFinishedGetLastCall( String value ) {
      recordView.setLastCallText(value);
   }

   @Override
   // method to return code to  MainActivity
   public void onShowToast( String state ) {
      if (recordView != null) {
         recordView.showToast( state );
         if (!state.equals(DATA_WAS_SAVED)) {
            recordView.setRecordButtonsVisibility(state_code);
         }
      }
   }

   @Override
   public void onDestroy() {
      recordView = null;
   }
}
