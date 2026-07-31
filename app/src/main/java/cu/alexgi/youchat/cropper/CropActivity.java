package cu.alexgi.youchat.cropper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import cu.alexgi.youchat.R;

public class CropActivity extends AppCompatActivity {

  //DrawerLayout mDrawerLayout;

  //private ActionBarDrawerToggle mDrawerToggle;
  private MainFragment mCurrentFragment;
  private Uri mCropImageUri;
  private CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();
  public void setCurrentFragment(MainFragment fragment) {
    mCurrentFragment = fragment;
  }
  public void setCurrentOptions(CropImageViewOptions options) {
    mCropImageViewOptions = options;
    //updateDrawerTogglesByOptions(options);
  }

  private String rutaImg;
  public String getRutaImg(){
    return rutaImg;
  }
  int status_rotate=0;
  Animation anim;
  FrameLayout Frag;
  RelativeLayout menu_flip;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cropper);

    Frag=findViewById(R.id.container);
    menu_flip=findViewById(R.id.menu_flip);
    menu_flip.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if(menu_flip.getVisibility()==View.VISIBLE) cerrarFlip();
        return true;
      }
    });

    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //getSupportActionBar().setTitle("Recortar");

    Bundle mibundle=this.getIntent().getExtras();
    if(mibundle!=null){
      rutaImg=mibundle.getString("rutaImg", "");
      if(rutaImg.equals("")) finish();
    }
    else finish();

   // mDrawerLayout = findViewById(R.id.drawer_layout);
    //mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.main_drawer_open, R.string.main_drawer_close);
    //mDrawerToggle.setDrawerIndicatorEnabled(true);
    //mDrawerLayout.setDrawerListener(mDrawerToggle);

    if (savedInstanceState == null) {
      setMainFragmentByPreset(CropDemoPreset.RECT);
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    //mDrawerToggle.syncState();
    mCurrentFragment.updateCurrentCropViewOptions();
  }

  /*@Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.crop_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    //if (mDrawerToggle.onOptionsItemSelected(item)) {
      //return true;
    //}
    if (mCurrentFragment != null && mCurrentFragment.onOptionsItemSelected(item)) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }*/

  @Override
  @SuppressLint("NewApi")
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
        && resultCode == AppCompatActivity.RESULT_OK) {
      Uri imageUri = CropImage.getPickImageResultUri(this, data);

      // For API >= 23 we need to check specifically that we have permissions to read external
      // storage,
      // but we don't know if we need to for the URI so the simplest is to try open the stream and
      // see if we get error.
      boolean requirePermissions = false;
      if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

        // request permissions and handle the result in onRequestPermissionsResult()
        requirePermissions = true;
        mCropImageUri = imageUri;
        requestPermissions(
            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
      } else {

        mCurrentFragment.setImageUri(Uri.fromFile(new File(rutaImg)));
//        mCurrentFragment.setImageUri(imageUri);
      }
    }
  }

  /*@Override
  public void onRequestPermissionsResult(
      int requestCode, String permissions[], int[] grantResults) {
    if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        CropImage.startPickImageActivity(this);
      } else {
        //Toa/st.make/Text(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
      }
    }
    if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
      if (mCropImageUri != null
          && grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mCurrentFragment.setImageUri(Uri.fromFile(new File(rutaImg)));
//        mCurrentFragment.setImageUri(mCropImageUri);
      } else {
        //Toa/st.make/Text(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
      }
      //CropImage.startPickImageActivity(this);
    }
  }*/

  private void setMainFragmentByPreset(CropDemoPreset demoPreset) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager
        .beginTransaction()
        .replace(R.id.container, MainFragment.newInstance(demoPreset))
        .commit();
  }

  public void crop(View view) {
    mCurrentFragment.cropper();
  }
  public void rotate(View view) {
    /*status_rotate++;
    if(status_rotate==1){
      anim= AnimationUtils.loadAnimation(this,R.anim.rotate_delete);
      Frag.startAnimation(anim);
    }
    else if(status_rotate==2){
      anim= AnimationUtils.loadAnimation(this,R.anim.rotate_delete);
      Frag.startAnimation(anim);
    }
    else if(status_rotate==3){
      anim= AnimationUtils.loadAnimation(this,R.anim.rotate_delete);
      Frag.startAnimation(anim);
    }
    else{
      anim= AnimationUtils.loadAnimation(this,R.anim.rotate_delete);
      Frag.startAnimation(anim);
      status_rotate=0;
    }*/
    anim= AnimationUtils.loadAnimation(this,R.anim.rotate_photo);
    Frag.startAnimation(anim);
    mCurrentFragment.rotate();
  }

  public void flip_vertically(View view)
  {
    cerrarFlip();
    mCurrentFragment.flipVertically();
  }
  public void flip_horizontally(View view) {
    cerrarFlip();
    mCurrentFragment.flipHorizontally();
  }

  public void flip(View view) {
    mostrarFlip();
  }

  private void mostrarFlip() {
    anim=AnimationUtils.loadAnimation(getBaseContext(),R.anim.right_in_fast);
    menu_flip.setVisibility(View.VISIBLE);
    menu_flip.startAnimation(anim);
  }
  private void cerrarFlip(){
    anim=AnimationUtils.loadAnimation(getBaseContext(),R.anim.left_out_fast);
    menu_flip.setVisibility(View.GONE);
    menu_flip.startAnimation(anim);
  }

  @Override
  public void onBackPressed() {
    if(menu_flip.getVisibility()==View.VISIBLE) cerrarFlip();
    else super.onBackPressed();
  }

  /*private void updateDrawerTogglesByOptions(CropImageViewOptions options) {
    ((TextView) findViewById(R.id.drawer_option_toggle_scale))
        .setText(
            getResources()
                .getString(R.string.drawer_option_toggle_scale, options.scaleType.name()));
    ((TextView) findViewById(R.id.drawer_option_toggle_shape))
        .setText(
            getResources()
                .getString(R.string.drawer_option_toggle_shape, options.cropShape.name()));
    ((TextView) findViewById(R.id.drawer_option_toggle_guidelines))
        .setText(
            getResources()
                .getString(R.string.drawer_option_toggle_guidelines, options.guidelines.name()));
    ((TextView) findViewById(R.id.drawer_option_toggle_multitouch))
        .setText(
            getResources()
                .getString(
                    R.string.drawer_option_toggle_multitouch,
                    Boolean.toString(options.multitouch)));
    ((TextView) findViewById(R.id.drawer_option_toggle_show_overlay))
        .setText(
            getResources()
                .getString(
                    R.string.drawer_option_toggle_show_overlay,
                    Boolean.toString(options.showCropOverlay)));
    ((TextView) findViewById(R.id.drawer_option_toggle_show_progress_bar))
        .setText(
            getResources()
                .getString(
                    R.string.drawer_option_toggle_show_progress_bar,
                    Boolean.toString(options.showProgressBar)));

    String aspectRatio = "FREE";
    if (options.fixAspectRatio) {
      aspectRatio = options.aspectRatio.first + ":" + options.aspectRatio.second;
    }
    ((TextView) findViewById(R.id.drawer_option_toggle_aspect_ratio))
        .setText(getResources().getString(R.string.drawer_option_toggle_aspect_ratio, aspectRatio));

    ((TextView) findViewById(R.id.drawer_option_toggle_auto_zoom))
        .setText(
            getResources()
                .getString(
                    R.string.drawer_option_toggle_auto_zoom,
                    options.autoZoomEnabled ? "Enabled" : "Disabled"));
    ((TextView) findViewById(R.id.drawer_option_toggle_max_zoom))
        .setText(
            getResources().getString(R.string.drawer_option_toggle_max_zoom, options.maxZoomLevel));
  }*/
}
