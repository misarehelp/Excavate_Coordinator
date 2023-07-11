package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//private static int NUM_ITEMS = 3;
public class MainPagerAdapter extends FragmentStateAdapter {

   private final List<Fragment> fragments = new ArrayList<>();
   //private final List<String> titles = new ArrayList<>();

   private Context context;
   public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity, @NonNull Lifecycle lifecycle, Context context) {
      super(fragmentActivity);
      this.context = context;
   }

   void addFragment(Fragment fragment) {
      fragments.add(fragment);
      //titles.add(title);
   }

   @NonNull
   @Override
   public Fragment createFragment(int page) {

      return(fragments.get(page));
   }

   @Override
   public int getItemCount() {
      return fragments.size();
   }

   /* public View getTabView(int position) {
      View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
      //ImageView tabIcon = view.findViewById(R.id.tab_icon);
      TextView tabText = view.findViewById(R.id.tab_text);
      //tabIcon.setImageResource(R.drawable.ic_launcher_foreground);
      tabText.setText(titles.get(position));
      return view;
   } */

}
