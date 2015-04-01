package com.example.angel.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {
    private Button Insertar;
    private Button Actualizar;

    private EditText txtNombre;
    private EditText txtEdad;
    private EditText txtID;

    private TextView Resultado;
    private ListView ListaResultado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Insertar = (Button)findViewById(R.id.Insertar);
        Actualizar = (Button)findViewById(R.id.Actualizar);

        txtNombre = (EditText)findViewById(R.id.editTextNombre);
        txtEdad = (EditText)findViewById(R.id.editTextEdad);
        txtID = (EditText)findViewById(R.id.editTextID);

        Resultado = (TextView)findViewById(R.id.Resultado);
        ListaResultado = (ListView)findViewById(R.id.Listlista);

        Insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TareaInsertar tarea = new TareaInsertar();
                tarea.execute(txtNombre.getText().toString(),
                        txtEdad.getText().toString(),
                        txtID.getText().toString());
            }
        });

        Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TareaListar lis = new TareaListar();
                lis.execute();

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TareaInsertar extends AsyncTask<String,Integer,Boolean>{


        @Override
        protected Boolean doInBackground(String... params) {
            boolean resul = true;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://192.168.0.194:3000/api/users");
            post.setHeader("content-type","application/json");

            try{
                JSONObject dato = new JSONObject();
                dato.put("Nombre", params[0]);
                dato.put("Edad", Integer.parseInt(params[1]));
                dato.put("ID", Integer.parseInt(params[2]));

                StringEntity entity = new StringEntity(dato.toString());
                post.setEntity(entity);

                HttpResponse resp = httpClient.execute(post);
                String respStr = EntityUtils.toString(resp.getEntity());

                if (!respStr.equals("true"))
                    resul = false;
            }catch (Exception ex){
                Log.e("Servicio Rest error", ex.toString());
                resul = false;
            }
            return resul;
        }
        @Override
        protected void onPostExecute(Boolean result){
            if(result)
                Resultado.setText("Insertado OK!!");
        }
    }

    private class TareaListar extends AsyncTask<String,Integer,Boolean>{

        private String[] lista;

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;

            HttpClient httpClient = new DefaultHttpClient();
            //HttpGet get = new HttpGet("http://192.168.0.194:3000/api/users");
            HttpGet del = new HttpGet("http://192.168.0.194:3000/api/users");
            del.setHeader("content-type","application/json");
            //get.setHeader("content-type","application/json");

            try{
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                JSONArray respJSON = new JSONArray(respStr);
                lista = new String[respJSON.length()];

                for (int i=0; i<respJSON.length(); i++){
                    JSONObject obj = respJSON.getJSONObject(i);

                    String Nombre = obj.getString("Nombre");
                    int ed = obj.getInt("Edad");
                    int id = obj.getInt("Id");

                    lista[i] = "" + Nombre + "-"+ed +"-"+ id;
                }

            }catch (Exception ex){
                Log.e("Error servicio REST!!", ex.toString());
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,lista);

                ListaResultado.setAdapter(adaptador);
            }
        }
    }
}
