package com.example.investimenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class NuovoAsset extends AppCompatActivity {
    EditText editNome, editImporto;
    Button btnInserisci;
    String nome, importo, ultimo, saldo;
    DatabaseReference database;
    Map<String, String> map_ultimo, map_saldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_asset);

        editNome = findViewById(R.id.editNome);
        editImporto = findViewById(R.id.editImporto);
        btnInserisci = findViewById(R.id.btnModifica);

        btnInserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome = editNome.getText().toString();
                importo = editImporto.getText().toString();

                if(!nome.trim().isEmpty() && !importo.trim().isEmpty())
                {
                    if(importo.matches("(\\d{1,4})\\.(\\d{1,2})") || importo.matches("(\\d{1,4})"))
                    {
                        database = FirebaseDatabase.getInstance().getReference();
                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.exists())
                                {
                                    map_ultimo = (Map<String, String>) snapshot.getValue();
                                    ultimo = map_ultimo.get("Ultimo");
                                    saldo(ultimo);
                                    startActivity(new Intent(NuovoAsset.this, Main.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(NuovoAsset.this, "Formato importo non valido", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(NuovoAsset.this, "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saldo(String u)
    {
        database = FirebaseDatabase.getInstance().getReference();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    map_saldo = (Map<String,String>) snapshot.getValue();
                    saldo = map_saldo.get("Saldo");
                    set(u, saldo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void set(String ultimo, String saldo)
    {
        Double imp = Double.parseDouble(importo);
        Double sal = Double.parseDouble(saldo);

        ultimo = String.valueOf(Integer.parseInt(ultimo) + 1);

        String nome = editNome.getText().toString();
        database = FirebaseDatabase.getInstance().getReference("Asset").child(ultimo).child("Nome");
        database.setValue(nome);

        String importo = editImporto.getText().toString();
        database = FirebaseDatabase.getInstance().getReference("Asset").child(ultimo).child("Importo");
        database.setValue(importo);

        database = FirebaseDatabase.getInstance().getReference("Ultimo");
        database.setValue(ultimo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(NuovoAsset.this, "Asset inserito", Toast.LENGTH_SHORT).show();
            }
        });

        sal +=imp;
        database = FirebaseDatabase.getInstance().getReference("Saldo");
        database.setValue(sal.toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NuovoAsset.this, "Errore modifica saldo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}