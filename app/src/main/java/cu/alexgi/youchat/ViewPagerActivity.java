package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class ViewPagerActivity extends AppCompatActivity implements BlancoFragment.OnFragmentInteractionListener,RojoFragment.OnFragmentInteractionListener,AzulFragment.OnFragmentInteractionListener{

    private ViewPager2 pager;
    public static final int NUM_PAGES = 5;
    private FragmentStateAdapter pagerAdapter;

    private static BlancoFragment blancoFragment;
//    private static RojoFragment rojoFragment;
//    private static VerdeFragment verdeFragment;
//    private static AmarilloFragment amarilloFragment;
    private static AzulFragment azulFragment;
    private LinearLayout layoutPuntos;
    TextView[] puntos;
    private View skip;
    private View ContentView;

    private int[][] colors;

    private final Runnable OcultarBarraSistema = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            ContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        colors= new int[][]{{63, 72, 204},
                            {244, 67, 54},
                            {76, 175, 80},
                            {255, 193, 7},
                            {25, 118, 210}};

        //AZUL 63 72 204
        //ROJO 244 67 54
        //VERDE 76 175 80
        //AMARILLO 255 193 7
        //AZUL ULT 25 118 210

        ContentView=findViewById(R.id.full);
        ContentView.setBackgroundColor(Color.rgb(colors[0][0], colors[0][1], colors[0][2]));
//        OcultarBarraSistema.run();

//        mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());

        pager = findViewById(R.id.view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        pager.setAdapter(pagerAdapter);
//        mviewPager.setAdapter(mSectionsPagerAdapter); TODO: descomentariar

        skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YouChatApplication.setMark(1);
                startActivity(new Intent(ViewPagerActivity.this, LoginActivity.class));
                finish();
            }
        });

        layoutPuntos=findViewById(R.id.LayoutPuntos);
        blancoFragment=new BlancoFragment();
//        rojoFragment=null;
//        verdeFragment=null;
//        amarilloFragment=null;
        azulFragment=new AzulFragment();
        AgregarIndicadorPuntos(0);
//        mviewPager.addOnPageChangeListener(viewListener); TODO: descomentariar

        /*TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mviewPager);*/

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                Log.e("onPageScrolled", "position: "+ position);
//                Log.e("onPageScrolled", "positionOffset: "+ positionOffset);
//                Log.e("onPageScrolled", "positionOffsetPixels: "+ positionOffsetPixels);
                if(position==4)
                    ContentView.setBackgroundColor(Utils.obtenerColorIntermedio(colors[position], colors[position-1], positionOffset));
                else ContentView.setBackgroundColor(Utils.obtenerColorIntermedio(colors[position], colors[position+1], positionOffset));
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AgregarIndicadorPuntos(position);
                if(position==4 && skip.getVisibility()!=View.GONE) skip.setVisibility(View.GONE);
                else if(skip.getVisibility()!=View.VISIBLE) skip.setVisibility(View.VISIBLE);
                ContentView.setBackgroundColor(Color.rgb(colors[position][0], colors[position][1], colors[position][2]));

            }
        });
    }



    private void AgregarIndicadorPuntos(int pos) {
        puntos=new TextView[5];
        layoutPuntos.removeAllViews();

        for(int i=0 ; i<puntos.length ; i++)
        {
            puntos[i]= new TextView(this);
            puntos[i].setText(Html.fromHtml("&#8226"));
            puntos[i].setTextSize(35);
            puntos[i].setTextColor(getResources().getColor(R.color.texto_gris));
            layoutPuntos.addView(puntos[i]);
        }
        if(puntos.length>0)
        {
            puntos[pos].setTextColor(getResources().getColor(R.color.fondo_blanco));
        }

//        if(pos==0 && blancoFragment!=null) blancoFragment.hacerAnim();
//        else if(pos==1 && rojoFragment!=null) rojoFragment.hacerAnim();
//        else if(pos==2 && verdeFragment!=null) verdeFragment.hacerAnim();
//        else if(pos==3 && amarilloFragment!=null) amarilloFragment.hacerAnim();
//        else if(pos==4 && azulFragment!=null) azulFragment.hacerAnim();

    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new BlancoFragment();
            else if(position == 4) return new AzulFragment();
            return RojoFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    /*ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {
            AgregarIndicadorPuntos(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };*/

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /*public static class PlaceholderFragment extends Fragment{
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment(){

        }

        public static Fragment newInstance(int sectionNumber) {

            Fragment fragment=null;
            switch (sectionNumber)
            {
                case 1: fragment=new BlancoFragment();
                blancoFragment=(BlancoFragment) fragment;
                    break;
                case 2:
                    fragment=new RojoFragment();
                    rojoFragment=(RojoFragment) fragment;
                    break;
                case 3: fragment=new VerdeFragment();
                    verdeFragment=(VerdeFragment) fragment;
                    break;
                case 4: fragment=new AmarilloFragment();
                    amarilloFragment=(AmarilloFragment) fragment;
                    break;
                case 5: fragment=new AzulFragment();
                    azulFragment=(AzulFragment) fragment;
                    break;
            }
            return fragment;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter{

        public SectionsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position)
            {
                case 0: return "PAGE 1";
                case 1: return "PAGE 2";
                case 2: return "PAGE 3";
                case 3: return "PAGE 4";
                case 4: return "PAGE 5";
            }
            return null;
        }
    }*/
}