package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

class PresenterRecord implements Contract.PresenterRecord, KM_Constants, Contract.ViewMainLayout, Enums, Contract.ModelRecord.OnPresenterRecordCallback {

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

      if (!(state_code.equals(ADD_CODE))) {
         recordView.fillRecordInfoFields(modelRecord.getSelectedRecordData());
      }

   }
   // ************* start methods passed from RecordActivity to ModelRecord *******************
   @Override
   // operations to be performed - BroadcastReceiver trigger
   public void onBroadcastReceive(Intent intent) {
      modelRecord.getFromModelBroadcastReceiver(this, this, intent);
   }

   @Override
   public void onButtonSave( ArrayList<String> rec_data ) {
      modelRecord.addRecordData( this, rec_data);
   }

   @Override
   public void onButtonDeleteRecord() {
      modelRecord.deleteRecordData( this);
   }

   @Override
   public void onButtonChangeRecord(ArrayList<String> rec_data) {

      modelRecord.changeRecordData( this, rec_data);
   }

   @Override
   public void onButtonExit() {

      dataParameters.setStateCode(DATA_WAS_NOT_CHANGED);
      onCloseRecordAction();
   }

   @Override
   public void onButtonShowClientJob() {
      //modelMain.getBackWithServer(SERVER_GET_NEXT_ID,"","");
      //modelMain.setModelMainButtonNewClick( this, this );
   }

   @Override
   public void onButtonAddFromBook() {
      //modelMain.getBackWithServer(SERVER_GET_NEXT_ID,"","");
      //modelMain.setModelMainButtonNewClick( this, this );
   }

   @Override
   public void onRecordListViewItemClick(int position, int id, int viewItew) {
      //modelMain.getBackWithServer(SERVER_GET_NEXT_ID,"","");
   }

// ************* CallBack methods run from ModelRecord *******************
   @Override
   public void onCloseRecordAction( ) {

      recordView.finishRecordActivity();
   }

   @Override
   public void OnFinishedButtonSaveSetViewButtonsVisibility( String state ) {
      //
   }

   @Override
   // method to return code to  MainActivity
   public void OnFinishedRefreshViewStatus( String state ) {
      if (recordView != null) {
         //recordView.refreshMainStatus( state );
      }
   }

   @Override
   // method to return code to  MainActivity
   public void onShowToast( String state ) {
      if (recordView != null) {
         recordView.showToast( state );
      }
   }


   @Override
   public void onDestroy() {
      //modelMain.sendModelDataToServer(this, SERVER_CLEAR_BUSY, dataParameters.getModelDepartmentUser(), "" );
      recordView = null;
   }
}
