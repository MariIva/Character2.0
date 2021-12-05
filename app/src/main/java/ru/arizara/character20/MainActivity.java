package ru.arizara.character20;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import ru.arizara.character20.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SETTING = 1;    // код запроса на активность настроек
    public static final int REQUEST_CODE_CREATE = 2;    // код запроса на создание персонажа

    // код запроса разрешения на запись
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    // путь к файлу
    String path;

    Character character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        path = getFilesDir().toString() + "/Character.dat";
        permission();

        seeCharacter();


        // кнопка для перехода в активность создания персонажа
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CharActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTING);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // если пользователь сменил цветовую схему, то пересоздаем активность
        if (requestCode == REQUEST_CODE_SETTING) {
            this.recreate();
        }
        // если пользователь создал персонажа
        if (requestCode == REQUEST_CODE_CREATE) {
            switch (resultCode) {
                case RESULT_OK:
                    // получаем персонажа как поток битов и переводим в объект
                    Character character = (Character) data.getSerializableExtra("NEW_CHARACTER");
                    // сохраняем персонажа
                    saveCharacter(character);
                    this.character = character;
                    seeCharacter();
                    break;
            }
        }
    }

    // метод устанавливающий тему активности
    public void setTheme() {
        // берем контекст приложения
        Context context = getApplicationContext();
        // получаем предпочтения всего приложения
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // берем значение цветовой темы
        String theme = prefs.getString("view", "standart");
        // берем значение включения режима темной темы
        boolean dark = prefs.getBoolean("dark_theme", false);
        // установка нужной цветовой схемы
        if (theme.equals("costom_theme")) {
            setTheme(R.style.Theme_CUSTOM_THEME_NoActionBar);
        } else {
            setTheme(R.style.Theme_Character20_NoActionBar);
        }
        // установка темной темы
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    public void permission() {
        int permissionStatus_write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus_write == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,     // эта активность
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},     // список размешений
                    PERMISSION_WRITE_EXTERNAL_STORAGE);   // код запроса разрешения
        } else {
            try {
                // считываем персонажa из файла
                loadCharacter();
            } catch (IOException | ClassNotFoundException ex) {
                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_layout);
                //создаем Snackbar
                Snackbar snackbar = Snackbar.make(layout, "Ошибка чтения файла", Snackbar.LENGTH_LONG);
                // показываем Snackbar
                snackbar.show();
            }

        }

    }

    private void seeCharacter() {
        if(character != null){
            TextView tv_name = (TextView) findViewById(R.id.tv_name);
            TextView tv_race = (TextView) findViewById(R.id.tv_race);
            TextView tv_str = (TextView) findViewById(R.id.tv_str);
            TextView tv_dex = (TextView) findViewById(R.id.tv_dex);
            TextView tv_con = (TextView) findViewById(R.id.tv_con);
            TextView tv_int = (TextView) findViewById(R.id.tv_int);
            TextView tv_wis = (TextView) findViewById(R.id.tv_wis);
            TextView tv_charm = (TextView) findViewById(R.id.tv_charm);
            ImageView img_icon = (ImageView) findViewById(R.id.img_icon);
            img_icon.setImageURI(Uri.parse(character.img_uri));
            tv_name.setText(character.name);
            tv_race.setText(character.race);
            tv_str.setText(""+character.feature.str);
            tv_dex.setText(""+character.feature.dex);
            tv_con.setText(""+character.feature.con);
            tv_int.setText(""+character.feature.intl);
            tv_wis.setText(""+character.feature.wis);
            tv_charm.setText(""+character.feature.charm);
        }
    }

    // сохрание списка персонажей, как сериаливанные объекты
    public void saveCharacter(Character character)  {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(character);
            oos.close();
        } catch (IOException e) {
            CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.main_layout);
            //создаем Snackbar
            Snackbar snackbar = Snackbar.make(layout, "Ошибка записи в файл", Snackbar.LENGTH_LONG);
            // показываем Snackbar
            snackbar.show();
        }
    }

    // загрузка персонажа
    public void loadCharacter() throws IOException, ClassNotFoundException {
        // создаем описание файла
        File f = new File(path);
        // если файла не существует, то создаем его
        if (!f.exists()) {
            f.createNewFile();
        }
        FileInputStream stream = new FileInputStream(path);
        if (stream.available()>0) {
            ObjectInputStream ois = new ObjectInputStream(stream);
            character = (Character) ois.readObject();
            ois.close();
        }
    }

}