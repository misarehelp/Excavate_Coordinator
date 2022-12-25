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

public class OkHttpRequest implements KM_Constants{
    final String FILE_EMPTY = "file_empty";

    int attempt = 0;
    //private static Context context;

    public void serverGetback(Context context, String command, String depID, String data) {
        OkHttpClient client = new OkHttpClient();
        Log.d(LOG_TAG, "OkHttpRequest. Command is:" + command + ";  Data: " + data);
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
                callbackSender(context, command, NET_ERROR_STATE);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                Log.d(LOG_TAG, "OkHttpRequest: Get back with server, response is: " + res);
                String message = res;
                if (res.equals(FILE_EMPTY)) {
                    message = EMPTY_STORAGE_STATE;
                }

                if (res.contains(URL_WAS_NOT_FOUND)){
                    message = URL_WAS_NOT_FOUND;
                }
                callbackSender(context, command, message);

            }
        });
    }

    public void callbackSender(Context context, String command, String message) {
        Intent intent = new Intent();
        //intent.setAction(ACTION_FROM_OKHTTP);
        String action = context.getClass().getSimpleName();
        Log.d(LOG_TAG, "OkHttpRequest: callbackSender, Action is: " + action);
        intent.setAction(action);
        intent.putExtra(SENDER, command);
        intent.putExtra(MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

