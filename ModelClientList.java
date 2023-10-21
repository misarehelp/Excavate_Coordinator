package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.gson.Gson;

import java.util.ArrayList;

class ModelClientList implements Contract.ModelClientList, Constants {

   private DataParameters dataParameters;
   private Context context;
   private ClientData clientData = new ClientData();
   private String code;

   public ModelClientList (Context context, DataParameters dataParameters ) {
      this.context = context;
      this.dataParameters = dataParameters;
   }

   // Convert Client Data into Json
   private String getFromClientDataToJson(ClientData client ) {
      return new Gson().toJson(client);
   }

   @Override
   public void addClient ( ClientData client ) {
      //
      code = SERVER_ADD_CLIENT;
      clientData = client;
      sendClientDataToServer( code, "", getFromClientDataToJson( client ));
   }

   @Override
   public void changeClient ( int position, ClientData client ) {
      //
      code = SERVER_CHANGE_CLIENT;
      dataParameters.setClientPosition(position);
      clientData.setName(client.getName());
      clientData.setPhone(client.getPhone());
      clientData.setComment(client.getComment());
      String value = dataParameters.getClientDataArray().get(position).getPhone();
      sendClientDataToServer( code, value, getFromClientDataToJson( client ));
   }

   @Override
   public void deleteClient ( int position ) {
      //
      code = SERVER_DELETE_CLIENT;
      dataParameters.setClientPosition(position);
      String phone = dataParameters.getClientDataArray().get(position).getPhone();
      sendClientDataToServer( code, phone, "");
   }

   @Override
   public void getArchiveClientByPhone( String phone) {
      //
      code = SERVER_GET_ARCHIVE_BY_PHONE;
      sendClientDataToServer( code, phone, "");
   }

   @Override
   public void getFromModelBroadcastReceiver( Contract.ModelClientList.OnPresenterClientListCallback act_listener, Intent intent ) {
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {

            ArrayList<ClientData> client_data_array = new ArrayList<>();
            client_data_array = dataParameters.getClientDataArray();

            String status = intent.getStringExtra(SENDER);

            if (status.equals(DATA_WAS_SAVED)) {
               switch (code) {
                  // Record Data was changed on the server
                  case SERVER_ADD_CLIENT:
                     client_data_array.add(clientData);
                     break;

                  case SERVER_DELETE_CLIENT:
                     client_data_array.remove(dataParameters.getClientPosition());
                     break;

                  case SERVER_CHANGE_CLIENT:
                     int pos = dataParameters.getClientPosition();
                     client_data_array.set( pos, clientData );
                     break;
                  //Config Data has got or confirmation of saving Depline Data to Server
                  default:
                     break;
               }

               dataParameters.setClientDataArray(client_data_array);
               act_listener.onShowToast(DATA_WAS_SAVED);
               act_listener.onUpdateRecycleData(client_data_array);

            } else {
               act_listener.onShowToast(DATA_WAS_NOT_SAVED);
            }
         }
      }, 0);
   }

   void sendClientDataToServer ( String command, String dateID, String value) {
      new OkHttpRequest().serverGetback( context, command, dateID, value );
   }
}
