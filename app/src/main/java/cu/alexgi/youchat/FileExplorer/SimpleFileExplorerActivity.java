package cu.alexgi.youchat.FileExplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cu.alexgi.youchat.R;


public class SimpleFileExplorerActivity extends AppCompatActivity implements ActivityListener{

    private String STACK_KEY = "FRAGMENT_STACK_KEY";
    public static String ENABLE_DIRECTORY_SELECT_KEY = "DIRECTORY_SELECT_KEY";
    public static String ON_ACTIVITY_RESULT_KEY = "ABSOLUTE_PATH_KEY";

    private FloatingActionButton fab_selected_file;
    private String selectedAbsolutePath;
    private boolean isDirectorySelectEnabled;
    private TextView titulo_file;
    private View atras_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer_activity);

        fab_selected_file = findViewById(R.id.fab_selected_file);
        fab_selected_file.hide();

        titulo_file = findViewById(R.id.titulo_file);
        atras_file = findViewById(R.id.atras_file);

        titulo_file.setText("Elige un archivo");
        atras_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.setSelectButtonClickListener();
        this.configureButtonFromUserPreferences();
        SimpleFileExplorerFragment fragment = new SimpleFileExplorerFragment();
        fragment.setListeners(this);

        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_explorer, fragment).commit();
        this.selectedAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    private void setSelectButtonClickListener(){
        fab_selected_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(ON_ACTIVITY_RESULT_KEY, selectedAbsolutePath);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onDirectoryChanged(String absolutePath, String name) {
        this.selectedAbsolutePath = absolutePath;

        if(selectedAbsolutePath.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) titulo_file.setText("Elige un archivo");
        else titulo_file.setText(name);

        this.updateButtonSelectStateOnDirectory();
        SimpleFileExplorerFragment fragment = new SimpleFileExplorerFragment();
        fragment.setListeners(this);
        fragment.setDirectory(absolutePath);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_explorer, fragment).addToBackStack(STACK_KEY).commit();
    }

    @Override
    public void onFileSelect(FileModel fileModel) {
        if(fileModel.isSelected()){
            fab_selected_file.show();
            this.selectedAbsolutePath = fileModel.getAbsolutePath();
            fab_selected_file.setEnabled(true);
        }
        else{
            if(fab_selected_file.isShown()) fab_selected_file.hide();
            this.selectedAbsolutePath = fileModel.getDirectoryPath();
            this.updateButtonSelectStateOnDirectory();
        }
    }

    @Override
    public void onBackButtonPressed(String absolutePath) {
        if(fab_selected_file.isShown()) fab_selected_file.hide();
        this.selectedAbsolutePath = absolutePath;

        if(selectedAbsolutePath.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) titulo_file.setText("Elige un archivo");
        else titulo_file.setText(getName(selectedAbsolutePath));
    }

    public String getName(String absolutePath){
        String nombre = "";
        int l = absolutePath.length()-1;
        for(int i=l; i>=0; i--){
            char a = absolutePath.charAt(i);
            if(a=='/') break;
            else nombre = a + nombre;
        }
        return nombre;
    }


    private void configureButtonFromUserPreferences(){
        Intent intent = getIntent();
        if(intent.hasExtra(ENABLE_DIRECTORY_SELECT_KEY)){
            this.isDirectorySelectEnabled = intent.getBooleanExtra(ENABLE_DIRECTORY_SELECT_KEY, true);
        }
        else{
            this.isDirectorySelectEnabled = true;
        }
        fab_selected_file.setEnabled(false);
    }

    private void updateButtonSelectStateOnDirectory(){
        if(this.isDirectorySelectEnabled){
            fab_selected_file.setEnabled(false);
        }
    }
}
