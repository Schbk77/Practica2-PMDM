package com.example.serj.editortexto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Principal extends Activity {

    /**********************************************************************************************/
    /**************************************VARIABLES***********************************************/
    /**********************************************************************************************/

    private Uri data;              // Variable donde almaceno la Uri del archivo que llama el Intent
    private EditText et;           // Variable para usar el EditText del layout

    /**********************************************************************************************/
    /**************************************ON...***************************************************/
    /**********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneja las opciones del menú de la ActionBar
        int id = item.getItemId();

        if (id == R.id.action_guardar) {
            // Realiza un método guardar() en función de la Uri del archivo de texto...
            try {
                guardar();
            } catch (IOException e) {
                guardar2();
            }

            return true;
        } else if (id == R.id.action_guardarsalir) {
            try {
                guardar();
            } catch (IOException e) {
                guardar2();
            }
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
    /***************************************EDICIÓN************************************************/
    /**********************************************************************************************/

    public StringBuilder leer(InputStream in) {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder texto = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                texto.append(line+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return texto;
    }

    public void guardar() throws IOException {
        FileWriter fw = new FileWriter(new File(data.getPath()).getAbsolutePath(), false);
        String texto = et.getText().toString();
        fw.write(texto);
        fw.close();
        et.requestFocus();
        Toast.makeText(this, getString(R.string.tostada_guardar), Toast.LENGTH_SHORT).show();
    }

    public void guardar2() {
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(getRealPathFromURI(this, data)), false);
            String texto = et.getText().toString();
            fw.write(texto);
            fw.close();
            et.requestFocus();
            Toast.makeText(this, getString(R.string.tostada_guardar), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************************************************/
    /***********************************MÉTODOS AUXILIARES*****************************************/
    /**********************************************************************************************/

    public void recogerIntent(Intent intent) {
        // Elige qué hacer dependiendo del tipo de Intent
        if (intent.getType().equals(getString(R.string.texto_plano))) {
            // Maneja los Intents de tipo text/plain

            try {
                InputStream in = getContentResolver().openInputStream(data);

                /*
                 * Haciendo pruebas incluí una librería externa
                 * que convierte directamente el InputStream a texto
                 * con la codificación que queramos
                 *
                 * String texto = IOUtils.toString(in, "UTF-8");
                 */

                // Guarda el texto en una variable leyendo desde el InputStream
                StringBuilder texto = leer(in);

                // Coloca el texto leído en el EditText
                et.setText(texto);

                // Coloca el cursor al final del texto
                et.setSelection(et.getText().length());
            } catch (FileNotFoundException e) { e.printStackTrace();}
        }
    }

    public void initComponents() {
        // Recoge el Intent que empezó esta actividad
        Intent intent = getIntent();

        // Almacena la Uri del Intent
        data = intent.getData();

        // Se inicializa el EditText
        et = (EditText)findViewById(R.id.etTexto);

        recogerIntent(intent);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        // Coge el path real del archivo de texto en lugar de su enlace simbólico
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
