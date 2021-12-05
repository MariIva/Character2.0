package ru.arizara.character20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class CharActivity extends AppCompatActivity {
    // код запроса выбора картинки
    final int SELECT_PICTURE = 1;

    ImageView imageView;
    // путь к картинке
    Uri selectedImageURI = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_char);

        Toolbar toolbar = findViewById(R.id.toolbar_char);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.image_char);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // при клике на imageView открывается стандартный выбор картинки из памяти
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_char, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_char) {
            // обработка выбора пользователем картинки
            String img = "";
            if (selectedImageURI !=null){
                img = selectedImageURI.toString();
            }

            TextInputLayout m_et_name = (TextInputLayout) findViewById(R.id.m_et_name) ;
            TextInputLayout m_et_age = (TextInputLayout) findViewById(R.id.m_et_age) ;
            TextInputLayout m_et_sex = (TextInputLayout) findViewById(R.id.m_et_sex) ;
            TextInputLayout m_et_race = (TextInputLayout) findViewById(R.id.m_et_race) ;
            TextInputLayout m_et_str = (TextInputLayout) findViewById(R.id.m_et_str) ;
            TextInputLayout m_et_dex = (TextInputLayout) findViewById(R.id.m_et_dex) ;
            TextInputLayout m_et_con = (TextInputLayout) findViewById(R.id.m_et_con) ;
            TextInputLayout m_et_intl = (TextInputLayout) findViewById(R.id.m_et_intl) ;
            TextInputLayout m_et_wis = (TextInputLayout) findViewById(R.id.m_et_wis) ;
            TextInputLayout m_et_charm = (TextInputLayout) findViewById(R.id.m_et_charm) ;

            // считываение введенных данных
            try {
                String name = m_et_name.getEditText().getText().toString();
                int age = Integer.parseInt(m_et_age.getEditText().getText().toString());
                String sex = m_et_sex.getEditText().getText().toString();
                String race = m_et_race.getEditText().getText().toString();
                int str = Integer.parseInt(m_et_str.getEditText().getText().toString());
                int dex = Integer.parseInt(m_et_dex.getEditText().getText().toString());
                int con = Integer.parseInt(m_et_con.getEditText().getText().toString());
                int intl = Integer.parseInt(m_et_intl.getEditText().getText().toString());
                int wis = Integer.parseInt(m_et_wis.getEditText().getText().toString());
                int charm = Integer.parseInt(m_et_charm.getEditText().getText().toString());

                // если не введены текстовые значения, то бросить исключение
                if (name.equals("")||sex.equals("")||race.equals("")){
                    NullPointerException exception=new NullPointerException("Текст не введен");
                    throw exception;
                }
                // создаем объект персонажа
                Character character = new Character(img, name, age, sex, race,
                        str, dex, con, intl, wis, charm);
                // передаем объект обратно в MainActivity
                Intent i = new Intent();
                i.putExtra("NEW_CHARACTER", character);
                setResult(RESULT_OK, i);
                finish();
            }
            /* NumberFormatException - если пользователь не ввел ни одной цифры, то будет ошибка
            перевода строки "" в число
            NullPointerException -  брошеная в if ошибка
             */
            // обработчик исключений
            catch (NumberFormatException | NullPointerException e){
                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.c_l);
                //создаем Snackbar
                Snackbar snackbar = Snackbar.make(layout, "Введите все данные", Snackbar.LENGTH_INDEFINITE);
                // добавляем кнопку на Snackbar и описываем клик на нее
                snackbar.setAction("OK", new View.OnClickListener (){
                    @Override
                    public void onClick(View v) {
                        // при клике закрываем Snackbar
                        snackbar.dismiss();
                    }
                });
                // показываем Snackbar
                snackbar.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // если пользователь выбирал и выбрал картинку
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // получаем Uri
                selectedImageURI = data.getData();
                // переводим полученных Uri в реальный Uri
                File imageFile = new File(getRealPathFromURI(selectedImageURI));
                selectedImageURI = Uri.fromFile(imageFile);
                // отображаем картинку
                imageView.setImageURI(selectedImageURI);
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
        if (theme.equals("costom_theme")){
            setTheme(R.style.Theme_CUSTOM_THEME_NoActionBar);
        }
        else{
            setTheme(R.style.Theme_Character20_NoActionBar);
        }
        // установка темной темы
        if (dark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // метод переводящий полученную Uri в реальный Uri
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}