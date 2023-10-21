package ru.volganap.nikolay.haircut_schedule;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

   private final List<Fragment> fragments = new ArrayList<>();

   public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity ) {
      super(fragmentActivity);
   }

   void addFragment(Fragment fragment) {
      fragments.add(fragment);
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

}
