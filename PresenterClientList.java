package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

class PresenterClientList implements Contract.PresenterListClient, Constants, Contract.ModelClientList.OnPresenterClientListCallback {

   private Contract.ViewClientList clientListView;
   private Contract.ModelClientList modelClientList;
   private DataParameters dataParameters;

   // initiating the objects of View and Model Interface
   public PresenterClientList ( Context context, Contract.ViewClientList clientListView ) {

      this.clientListView = clientListView;
      dataParameters = DataParameters.getInstance();
      modelClientList = new ModelClientList( context, dataParameters );
      clientListView.showClients(dataParameters.getClientDataArray());
   }

   // ************* start methods passed from RecordActivity to ModelRecord *******************
   @Override
   // operations to be performed - BroadcastReceiver trigger
   public void onBroadcastReceive(Intent intent) {
      modelClientList.getFromModelBroadcastReceiver( this, intent );
   }

   @Override
   public void onItemChangeClientClick( int position, String name, String phone ) {
      ClientData clientData = getClientData (name, phone);
      modelClientList.changeClient( position, clientData);
   }

   @Override
   public void onButtonAddNewClient(String name, String phone ) {
      ClientData clientData = getClientData (name, phone);
      modelClientList.getClientID( clientData );
   }

   private ClientData getClientData (String name, String phone) {
      ClientData clientData = new ClientData();
      clientData.setName(name);
      clientData.setPhone(phone);
      return clientData;
   }

   @Override
   public void onItemDeleteClientClick( int position ) {
      modelClientList.deleteClient( position );
   }

   @Override
   public void onItemShowClientJob( String id ) {
      modelClientList.getArchiveClientById( id );
   }

   // ************* CallBack methods run from ModelRecord *******************
   @Override
   // method to return code to  MainActivity
   public void onShowToast( String state ) {
      if (clientListView != null) {
         clientListView.showToast( state );
      }
   }

   @Override
   public void onUpdateRecycleData(ArrayList<ClientData> client_data_array) {
      clientListView.showClients(client_data_array);
   }

   @Override
   public void onDestroy() {
      clientListView = null;
   }
}
