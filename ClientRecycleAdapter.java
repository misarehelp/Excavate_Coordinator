package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

class ClientRecycleAdapter extends RecyclerView.Adapter<ClientRecycleAdapter.ViewHolder> implements Constants {

   private final Contract.Recycle.ClientInterface clickListener;
   private Context context;
   private ViewGroup parent;
   private final LayoutInflater inflater;
   private List<ClientData> data = new ArrayList<>();

   ClientRecycleAdapter(Context context, Fragment fragment, ArrayList<ClientData> data) {
      this.data = data;
      this.inflater = LayoutInflater.from(context);
      this.context = context;
      this.clickListener = (Contract.Recycle.ClientInterface) fragment;
   }

   public void swap(ArrayList<ClientData> datas)  {
      if (data != null) {
         data.clear();
         data.addAll(datas);
      } else {
         data = datas;
      }
      notifyDataSetChanged();
   }

   @Override
   public ClientRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      View view = inflater.inflate(R.layout.client_list_item, parent, false);
      this.parent = parent;

      return new ClientRecycleAdapter.ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(ClientRecycleAdapter.ViewHolder holder, int position) {
      ClientData clientData = data.get(position);

      holder.tv_id.setText(Integer.toString(position));
      holder.tv_name.setText(clientData.getName());
      holder.tv_phone.setText(clientData.getPhone());
      holder.tv_comment.setText(clientData.getComment());
   }

   @Override
   public int getItemCount() {
      return data.size();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      final TextView tv_id, tv_name, tv_phone, tv_comment;
      int position;

      ViewHolder(View view){
         super(view);

         tv_id = view.findViewById(R.id.tv_id);
         tv_name = view.findViewById(R.id.et_name);
         tv_phone = view.findViewById(R.id.et_phone);
         tv_comment = view.findViewById(R.id.et_comment);

         view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               position = getAdapterPosition();
               ClientData clientData = data.get(position);
            //Creating the instance of PopupMenu for New Haircut record or Note record
               PopupMenu popup = new PopupMenu(context, parent);
               //Inflating the Popup using xml file
               popup.getMenuInflater().inflate(R.menu.client_list_menu, popup.getMenu());

               //registering popup with OnMenuItemClickListener
               popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                  public boolean onMenuItemClick(MenuItem item) {
                     switch (item.getItemId()) {
                        case R.id.menu_add_client_to_record:
                           clickListener.onItemClick( position, GET_CLIENT_DATA_FROM_BASE, clientData );
                           break;

                        case R.id.menu_change_client:
                           //clickListener.onItemClick( position, SERVER_CHANGE_CLIENT, clientData );
                           clickListener.onItemClick( position, SERVER_CHANGE_CLIENT, clientData );
                           break;

                        case R.id.menu_delete_client:
                           //clickListener.onItemClick( position, SERVER_DELETE_CLIENT, clientData );
                           clickListener.onItemClick( position, SERVER_DELETE_CLIENT, clientData );
                           break;

                        case R.id.menu_show_client_job:
                           //clickListener.onItemClick( position, SERVER_DELETE_CLIENT, clientData );
                           clickListener.onItemClick( position, SHOW_CLIENT_JOB, clientData );
                           break;
                        default:
                           break;
                     }

                     return true;
                  }
               });

               popup.show(); //showing popup menu
            }
         });
      }
   }
}

