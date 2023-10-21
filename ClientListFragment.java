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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

public class ClientListFragment extends Fragment implements Constants, Enums, Contract.ViewClientList, Contract.RecordActivityToFragmentBroadcast,
        Contract.Recycle.ClientInterface, Contract.RecordActivityToSomeFragment {

    private Contract.ClientFragmentToRecordActivity callbackToActivity;
    Contract.PresenterListClient presenterListClient;
    private Context context;
    private String command = DATA_WAS_NOT_CHANGED;
    private int position;
    private ArrayList<ClientData> data = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClientRecycleAdapter adapter;
    private EditText et_name, et_phone, et_comment;
    private Button bt_back, bt_add_client, bt_save;
    private LinearLayout ll_new_client;
    public ClientListFragment () {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            callbackToActivity = (Contract.ClientFragmentToRecordActivity) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Contract.RecordFragmentToRecordActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.client_list_holder, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.d(LOG_TAG, "ClientListFragment - onViewCreated ");
        bt_back = view.findViewById(R.id.bt_back);
        bt_add_client = view.findViewById(R.id.bt_add_client);
        bt_save = view.findViewById(R.id.bt_save);

        ll_new_client = view.findViewById(R.id.ll_new_client);
        et_name = view.findViewById(R.id.et_name);
        et_phone = view.findViewById(R.id.et_phone);
        et_comment = view.findViewById(R.id.et_comment);

        showNewClientLayout(View.GONE);
        buttonsSetOnClickListener();

        recyclerView = view.findViewById(R.id.rv_client);
        adapter = new ClientRecycleAdapter(context, this,  data);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.layout_devider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

        recyclerView.setAdapter(adapter);
        presenterListClient = new PresenterClientList( context, this);

    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        presenterListClient.onBroadcastReceive(intent);
    }

    private void buttonsSetOnClickListener() {
        // show a list of jobs for a client
        bt_back.setOnClickListener(v -> {
            showNewClientLayout(View.GONE);
            setRecordButtonsVisibility(DATA_WAS_NOT_CHANGED);
        });

        // add a client number from a phone book
        bt_add_client.setOnClickListener(v -> {
            fillInClientFields ("", "", "");
        });

        // save a client
        bt_save.setOnClickListener(v -> {
            checkCorrectInput();
        });
    }

    private void fillInClientFields (String name, String phone, String comment) {
        command = SERVER_ADD_CLIENT;
        showNewClientLayout(View.VISIBLE);
        et_name.setText(name);
        et_phone.setText(phone);
        et_comment.setText(comment);
    }

    private void showNewClientLayout( int state ) {
        ll_new_client.setVisibility( state );
        setRecordButtonsVisibility(command);
    }

    @Override
    public void showClients(ArrayList<ClientData> data) {
        adapter.swap(data);
        //this.data = data;
        adapter.notifyDataSetChanged();
        setRecordButtonsVisibility(DATA_WAS_SAVED);
    }

    @Override
    public void onGetClientDataToFragment(String name, String phone) {
        fillInClientFields (name, phone, "");
    }

    @Override
    public void setRecordButtonsVisibility (String value) {
        // Check for if a new permit
        switch ( value ) {
            case SERVER_ADD_CLIENT:
            case SERVER_CHANGE_CLIENT:

                bt_back.setVisibility(View.VISIBLE);
                bt_add_client.setVisibility(View.GONE);
                bt_save.setVisibility(View.VISIBLE);
                break;

            case SERVER_DELETE_CLIENT:

                bt_back.setVisibility(View.VISIBLE);
                bt_add_client.setVisibility(View.VISIBLE);
                bt_save.setVisibility(View.VISIBLE);
                break;

            case DATA_WAS_NOT_CHANGED:
            case DATA_WAS_SAVED:
            default:
                bt_back.setVisibility(View.INVISIBLE);
                bt_add_client.setVisibility(View.VISIBLE);
                bt_save.setVisibility(View.GONE);
                break;
        }
    }

    private void checkCorrectInput() {
        String name = et_name.getText().toString();
        String phone = et_phone.getText().toString();
        String comment = et_comment.getText().toString();

        if (name.trim().length() == 0 || phone.trim().length() == 0 ) {
            showToast(context.getResources().getString(R.string.incorrect_name_phone));

        } else {
            if (command.equals(SERVER_ADD_CLIENT)) {
                presenterListClient.onButtonAddNewClient( name, phone, comment );
            } else {
                presenterListClient.onItemChangeClientClick( position, name, phone, comment );
            }

            showNewClientLayout(View.GONE);
        }
    }

    @Override
    public void showToast(String status) {
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position, String command, ClientData clientData ) {

        this.command = command;
        this.position = position;

        switch ( command ) {
            case SERVER_CHANGE_CLIENT:
                showNewClientLayout(View.VISIBLE);
                et_name.setText(clientData.getName());
                et_phone.setText(clientData.getPhone());
                et_comment.setText(clientData.getComment());
                break;

            case SERVER_DELETE_CLIENT:
                presenterListClient.onItemDeleteClientClick( position );
                break;

            case GET_CLIENT_DATA_FROM_BASE:
                callbackToActivity.onGetClientDataToActivity( clientData.getName(), clientData.getPhone(), false);
                break;

            case SHOW_CLIENT_JOB:
                callbackToActivity.onGetClientDataToActivity( clientData.getName(), clientData.getPhone(), true);
                presenterListClient.onItemShowClientJob( clientData.getPhone() );
                break;
            default:
        }
    }

    @Override
    public void onDetach() {
        callbackToActivity = null;
        presenterListClient.onDestroy();
        super.onDetach();
    }
}
