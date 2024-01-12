package ru.volganap.nikolay.haircut_schedule;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainRecycleAdapter  extends RecyclerView.Adapter<MainRecycleAdapter.ViewHolder> implements Constants {

   private final Contract.Recycle.MainInterface clickListener;
   private Context context;
   private ViewGroup parent;
   private final LayoutInflater inflater;
   private List<MainScreenData> data;
   private int theme_type;
   private boolean holiday;
   private  float weight_job, weight_name, weight_ll_image;

   MainRecycleAdapter(Context context, List<MainScreenData> data, int theme_type, boolean holiday ) {
      this.data = data;
      this.theme_type = theme_type;
      this.holiday = holiday;
      this.inflater = LayoutInflater.from(context);
      this.context = context;
      this.clickListener = (Contract.Recycle.MainInterface) context;
   }

   @Override
   public MainRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = inflater.inflate(R.layout.main_list_item, parent, false);
      this.parent = parent;
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(MainRecycleAdapter.ViewHolder holder, int position) {
      MainScreenData mainScreenData = data.get(position);

      if (!mainScreenData.getTime().equals(START_HOLIDAY_TIME)) {

         LinearLayout.LayoutParams job_params = (LinearLayout.LayoutParams) holder.tv_rec_job.getLayoutParams();
         LinearLayout.LayoutParams name_params = (LinearLayout.LayoutParams) holder.tv_rec_name.getLayoutParams();
         LinearLayout.LayoutParams ll_img_params = (LinearLayout.LayoutParams) holder.ll_img_name.getLayoutParams();

         if (mainScreenData.getJob().equals(INDEX_NOTE)) {
            job_params.weight = 0;
            name_params.weight = weight_job + weight_name;
         } else {
            job_params.weight = weight_job;
            name_params.weight = weight_name;
         }

         if (mainScreenData.getIndex().equals(INDEX_FREE_RECORD)) {
            ll_img_params.weight = 0;
         } else {
            ll_img_params.weight = weight_ll_image;
         }

         holder.tv_rec_job.setLayoutParams(job_params);
         holder.tv_rec_name.setLayoutParams(name_params);
         holder.ll_img_name.setLayoutParams(ll_img_params);

         holder.imageView.setImageResource(mainScreenData.getResource());

         holder.tv_rec_time.setText(mainScreenData.getTime());
         holder.tv_rec_job.setText(mainScreenData.getJob());
         holder.tv_rec_name.setText(mainScreenData.getName());

         holder.tv_rec_time.setTextColor(mainScreenData.getColor());
         holder.tv_rec_job.setTextColor(mainScreenData.getColor());

         if (mainScreenData.getIndex().equals(INDEX_FREE_RECORD)) {
            holder.tv_rec_name.setTextColor(mainScreenData.getColor());
         } else {

            switch (theme_type) {
               case THEME_DARK_SMALL:
               case THEME_DARK_MEDIUM:
               case THEME_DARK_BIG:
                  holder.tv_rec_name.setTextColor(Color.YELLOW);
                  break;

               case THEME_LIGHT_SMALL:
               case THEME_LIGHT_MEDIUM:
               case THEME_LIGHT_BIG:
               default:
                  holder.tv_rec_name.setTextColor(Color.BLACK);
                  break;
            }
         }
      }
   }

   @Override
   public int getItemCount() {
      return data.size();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      final ImageView imageView;
      final TextView tv_rec_time, tv_rec_job, tv_rec_name;
      final LinearLayout ll_img_name;
      int position;

      ViewHolder(View view){
         super(view);

         tv_rec_time = view.findViewById(R.id.tv_rec_time);
         tv_rec_job = view.findViewById(R.id.tv_rec_job);
         tv_rec_name = view.findViewById(R.id.tv_rec_name);
         imageView = view.findViewById(R.id.img_name);
         ll_img_name = view.findViewById(R.id.ll_img_name);

         LinearLayout.LayoutParams job_params = (LinearLayout.LayoutParams) tv_rec_job.getLayoutParams();
         LinearLayout.LayoutParams name_params = (LinearLayout.LayoutParams) tv_rec_name.getLayoutParams();
         LinearLayout.LayoutParams ll_img_params = (LinearLayout.LayoutParams) ll_img_name.getLayoutParams();
         weight_job = job_params.weight;
         weight_name = name_params.weight;
         weight_ll_image = ll_img_params.weight;

         view.setOnClickListener(v -> {
            position = getAdapterPosition();

            String s_index = data.get(position).getIndex();
            String time = data.get(position).getTime();
            String type = data.get(position).getType();

            try {

               if (Integer.parseInt(s_index) < 0 ) {
                  //Creating the instance of PopupMenu for New Haircut record or Note record
                  PopupMenu popup = new PopupMenu(context, parent);
                  //Inflating the Popup using xml file
                  popup.getMenuInflater().inflate(R.menu.main_list_menu, popup.getMenu());

                  if (holiday) {
                        popup.getMenu().findItem(R.id.menu_set_on_hol).setVisible(false);
                        popup.getMenu().findItem(R.id.menu_set_off_hol).setVisible(true);

                  }  else {
                        popup.getMenu().findItem(R.id.menu_set_on_hol).setVisible(true);
                        popup.getMenu().findItem(R.id.menu_set_off_hol).setVisible(false);
                  }

                  //registering popup with OnMenuItemClickListener
                  popup.setOnMenuItemClickListener(item -> {
                     switch (item.getItemId()) {
                        case R.id.menu_add_rec ->
                                clickListener.onItemClick(INDEX_FREE_RECORD, time, type);
                        case R.id.menu_add_note ->
                                clickListener.onItemClick(INDEX_NOTE, time, INDEX_NOTE);
                        case R.id.menu_set_on_hol ->
                                clickListener.onItemClick(INDEX_SET_ON_HOLIDAY, START_HOLIDAY_TIME, "");
                        case R.id.menu_set_off_hol ->
                                clickListener.onItemClick(INDEX_SET_OFF_HOLIDAY, START_HOLIDAY_TIME, "");
                        default -> {
                        }
                     }
                     return true;
                  });

                  popup.show(); //showing popup menu

               } else {
                  clickListener.onItemClick(s_index, time, type);
               }

            } catch (NullPointerException npe) {
               Log.d(LOG_TAG, "Exception: " + npe );
            }
         });
      }
   }
}
      /*
      @Override
      public void onClick(View v) {
         int position = getAdapterPosition();
         if (position >= 0) {
            clickListener.onItemClick(position, v);
         }
      }

      @Override
      public boolean onLongClick(View v) {
         int position = getAdapterPosition();
         if (position >= 0) {
            clickListener.onItemLongClick(position, v);
            return true;
         }
         return false;
      } */
