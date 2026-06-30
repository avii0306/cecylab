package com.example.cecylab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // 1. Declaramos las variables de los componentes del diseño verde
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Vincula con tu XML de diseño verde

        // 2. Enlazamos los componentes con los IDs del XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 3. Configuramos el evento Click del botón
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    validarUsuarioBaseDatos(email, password);
                }
            }
        });
    }

    private void validarUsuarioBaseDatos(final String email, final String password) {
        // Tu IP exacta apuntando a tu servidor local
        String urlAPI = "http://192.168.100.218/laboratorio/login.php";

        // Creamos la cola de peticiones de Volley
        RequestQueue queue = Volley.newRequestQueue(this);

        // Creamos la petición POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { // <--- CORREGIDO: Cambiado onClick por onResponse
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String mensaje = jsonObject.getString("mensaje");

                            if (!error) {
                                String nombre = jsonObject.getString("nombre");
                                int idRol = jsonObject.getInt("id_rol"); // 1: admin, 2: docente, 3: alumno

                                Toast.makeText(MainActivity.this, "¡Bienvenido " + nombre + "!", Toast.LENGTH_SHORT).show();

                                // Evaluamos el rol con un switch de Java para redirigir
                                switch (idRol) {
                                    case 1:
                                        // Es Administrador -> Abres su Activity
                                        Intent intentAdmin = new Intent(MainActivity.this, AdminActivity.class); // Corregida mayúscula
                                        startActivity(intentAdmin);
                                        break;
                                    case 2:
                                        // Es Docente/Profesor -> Abres su Activity
                                        Intent intentProfe = new Intent(MainActivity.this, ProfesorActivity.class);
                                        startActivity(intentProfe);
                                        break;
                                    case 3:
                                        // Es Alumno -> Abres su Activity
                                        Intent intentAlumno = new Intent(MainActivity.this, AlumnoActivity.class); // Corregida mayúscula
                                        startActivity(intentAlumno);
                                        break;
                                    default:
                                        Toast.makeText(MainActivity.this, "Rol no reconocido", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                finish(); // Cerramos el Login para que no puedan volver atrás

                            } else {
                                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error en el formato de respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error de red: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Pasamos los parámetros por POST hacia el PHP
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Sumamos la petición a la cola para que se ejecute
        queue.add(stringRequest);
    }
}