package ru.volganap.nikolay.excavate_coordinator;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpRequest implements KM_Constants, Enums{
    private String status = DATA_IS_NOT_READY;
    //int attempt = 0;
    //private static Context context;

    public void serverGetback(Context context, String command, String depID, String data) {
        OkHttpClient client = new OkHttpClient();
        //Log.d(LOG_TAG, "OkHttpRequest. Command is:" + command + ";  Data: " + data);
        RequestBody formBody = new FormBody.Builder()
                .add("command", command)
                .add("depID", depID)
                .add("data", data)
                .build();
        Request request = new Request.Builder()
                .url(URL_ADDR)
                .post(formBody)
                .build();

        //исполняем запрос асинхронно
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                Log.d(LOG_TAG, "OkHttpRequest: Server ERROR is: " + e.toString());
                callbackSender(context, DATA_IS_NOT_READY, NET_ERROR_STATE);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                String message = res;

                if ( command.equals(SERVER_CLEAR_BUSY) ) {
                    if (res.contains(SERVER_BASE_HAS_BEEN_RELEASED_BY)) {
                        status = SERVER_BASE_HAS_BEEN_RELEASED_BY;
                    }

                } else if ( res.equals(DATA_WAS_SAVED ) || (res.startsWith("[\"{")) ) {
                    status = DATA_IS_READY;

                } else if ( command.equals(SERVER_GET_NEXT_ID) ) {
                    status = command;

                } else if (res.contains(SERVER_ANSWER_CONFIG)) {
                    status = SERVER_ANSWER_CONFIG;

                } else if (res.contains(URL_WAS_NOT_FOUND)){
                    message = URL_WAS_NOT_FOUND;
                }

                //Log.d(LOG_TAG, "OkHttpRequest: Get back with server, response is: " + res);
                callbackSender(context, status, message);
            }
        });
    }

    private void callbackSender(Context context, String status, String message) {
        Intent intent = new Intent();
        //intent.setAction(ACTION_FROM_OKHTTP);
        String action = context.getClass().getSimpleName();
        Log.d(LOG_TAG, "OkHttpRequest: callbackSender, Action is: " + action);
        intent.setAction(action);
        intent.putExtra(SENDER, status);
        intent.putExtra(MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

