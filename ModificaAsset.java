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

public class ModificaAsset extends AppCompatActivity {

    EditText editNome, editImporto;
    Button btnModifica;
    String id, nome, old_importo, new_importo;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_asset);

        id = getIntent().getStringExtra("ID");
        nome = getIntent().getStringExtra("NOME");
        old_importo = getIntent().getStringExtra("IMPORTO");

        editNome = findViewById(R.id.editNome);
        editImporto = findViewById(R.id.editImporto);
        editNome.setText(nome);
        editImporto.setText(old_importo);

        btnModifica = findViewById(R.id.btnModifica);
        btnModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                nome = editNome.getText().toString();
                new_importo = editImporto.getText().toString();

                if(!nome.trim().isEmpty() && !new_importo.trim().isEmpty())
                {
                    if(new_importo.matches("(\\d{1,4})\\.(\\d{1,2})") || new_importo.matches("(\\d{1,4})"))
                    {
                        set(id);

                        database = FirebaseDatabase.getInstance().getReference("Saldo");
                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Double saldo = Double.parseDouble(snapshot.getValue().toString());
                                nuovo_saldo(saldo, old_importo, new_importo);
                                startActivity(new Intent(ModificaAsset.this, Main.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(ModificaAsset.this, "Formato importo non valido", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(ModificaAsset.this, "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void set(String id)
    {
        nome = editNome.getText().toString();
        database = FirebaseDatabase.getInstance().getReference("Asset").child(id).child("Nome");
        database.setValue(nome);

        new_importo = editImporto.getText().toString();
        database = FirebaseDatabase.getInstance().getReference("Asset").child(id).child("Importo");
        database.setValue(new_importo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ModificaAsset.this, "Asset modificato", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ModificaAsset.this, "Errore modifica asset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nuovo_saldo(Double saldo, String vecchio_importo, String nuovo_importo)
    {
        Double nuovo = saldo - Double.parseDouble(vecchio_importo) + Double.parseDouble(nuovo_importo);
        String nuovo_saldo = nuovo.toString();

        database = FirebaseDatabase.getInstance().getReference("Saldo");
        database.setValue(nuovo_saldo).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ModificaAsset.this, "Errore modifica saldo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}