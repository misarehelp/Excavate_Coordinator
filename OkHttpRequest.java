package ru.volganap.nikolay.haircut_schedule;

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

public class OkHttpRequest implements Constants, Enums {
    private String status = DATA_IS_NOT_READY;

    public void serverGetback( Context context, String command, String dateID, String data) {

        OkHttpClient client = new OkHttpClient();
        Log.d(LOG_TAG, "OkHttpRequest. Command is:" + command + ";  Data: " + data);
        RequestBody formBody = new FormBody.Builder()
                .add("command", command)
                .add("dateID", dateID)
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
                callbackSender(context, command, DATA_IS_NOT_READY, NET_ERROR_STATE);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                String message = res;

                if (res.startsWith("[\"{") || res.startsWith("[{") ) {
                    status = DATA_IS_READY;

                } else if ( res.equals(DATA_WAS_SAVED )) {
                    status = DATA_WAS_SAVED;

                } else if (res.contains(URL_WAS_NOT_FOUND)){
                    message = URL_WAS_NOT_FOUND;

                } else if ( command.equals(SERVER_GET_CLIENT_ID) ) {
                    status = SERVER_GET_CLIENT_ID;

                } else if (res.contains(SERVER_ANSWER_CONFIG_CHANGED)) {
                    status = SERVER_ANSWER_CONFIG_CHANGED;
                }

                callbackSender(context, command, status, message);
            }
        });
    }

    private void callbackSender(Context context, String command, String status, String message) {
        Intent intent = new Intent();
        String action = context.getClass().getSimpleName();
        Log.d(LOG_TAG, "OkHttpRequest: callbackSender, Action is: " + action);
        intent.setAction(action);
        intent.putExtra(COMMAND, command);
        intent.putExtra(SENDER, status);
        intent.putExtra(MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

