package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.chatme.Fragment.Fragment_Analytics;
import com.example.chatme.Fragment.Fragment_Chat;
import com.example.chatme.Fragment.Fragment_Info;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Stack;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNV;
    private Stack<Fragment> fragmentStack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBottomNV = findViewById(R.id.navigation_view);
        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() { //NavigationItemSelecte
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                bottomNavigate(menuItem.getItemId());
                return true;
            }
        });
        mBottomNV.setSelectedItemId(R.id.navigation_analytics);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager
                fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, fragment).addToBackStack(null).commit();
    }

    private void bottomNavigate(int id) {  //BottomNavigation 페이지 변경
        String tag = String.valueOf(id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment_Analytics fragment_Home = new Fragment_Analytics();
        Fragment_Chat fragment_Chat = new Fragment_Chat();
        Fragment_Info fragment_MyInfo = new Fragment_Info();



        fragmentStack = new Stack<>();
        fragmentStack.push(fragment_Home);

        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            if (id == R.id.navigation_analytics) {
                fragment = new Fragment_Analytics();

            }
            else if (id == R.id.navigation_chat){
                fragment = new Fragment_Chat();
            }
            else if(id==R.id.navigation_info){
                fragment = new Fragment_Info();
            }



            fragmentTransaction.add(R.id.content_layout, fragment, tag);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentStack.push(fragment);
        fragmentTransaction.replace(R.id.content_layout, fragment, tag);
        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNow();
    }
}