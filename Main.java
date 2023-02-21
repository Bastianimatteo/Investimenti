package com.example.investimenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Executor;


public class Main extends AppCompatActivity{
    RecyclerView recyclerView;
    DatabaseReference database;
    ArrayList<AssetModel> listAsset = new ArrayList<>();
    Adapter adapter;
    Button btnNuovo, btnGrafico, btnShow;
    TextView txtSaldo;
    Boolean show;

    private Executor executorImporti;
    private BiometricPrompt biometricPromptImporti;
    private BiometricPrompt.PromptInfo promptInfoImporti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSaldo = findViewById(R.id.txtSaldo);
        recyclerView = findViewById(R.id.myRecyclerView);

        show = false;
        saldo(show);

        adapter = new Adapter(Main.this, listAsset);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnNuovo = findViewById(R.id.btnNuovo);
        btnNuovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(show == true) {
                    startActivity(new Intent(Main.this, NuovoAsset.class));
                }
                else
                {
                    biometricPromptImporti.authenticate(promptInfoImporti);
                }
            }
        });

        btnGrafico = findViewById(R.id.btnGrafico);
        btnGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main.this, Grafico.class));
            }
        });

        btnShow = findViewById(R.id.btnShow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(show == true)
                {
                    show = false;
                    saldo(show);
                }
                else
                {
                    biometricPromptImporti.authenticate(promptInfoImporti);
                }
            }
        });

        //AUTENTICAZIONE IMPORTI
        executorImporti = ContextCompat.getMainExecutor(this);
        promptInfoImporti = new BiometricPrompt.PromptInfo.Builder().setTitle("Autenticazione").setNegativeButtonText("Annulla").build();
        biometricPromptImporti = new BiometricPrompt(Main.this, executorImporti, new BiometricPrompt.AuthenticationCallback()
        {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                show = true;
                saldo(show);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Main.this, "Autenticazione fallita", Toast.LENGTH_SHORT).show();
                saldo(show);
            }
        });
    }

    private void set_lista(Double saldo, Boolean show)
    {
        database = FirebaseDatabase.getInstance().getReference().child("Asset");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                listAsset.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String id = dataSnapshot.getKey();
                    String nome = dataSnapshot.child("Nome").getValue(String.class);
                    String importo = dataSnapshot.child("Importo").getValue(String.class);

                    Double imp = Double.parseDouble(importo);

                    Double percentuale = imp/saldo*100;
                    BigDecimal bd = new BigDecimal(percentuale).setScale(2, RoundingMode.HALF_UP);
                    percentuale = bd.doubleValue();

                    AssetModel model;
                    if(show == false)
                    {
                        model = new AssetModel(id, nome, "---", percentuale);
                    }
                    else
                    {
                        model = new AssetModel(id, nome, importo, percentuale);
                    }
                    listAsset.add(model);

                    // ordina la lista in ordine decrescente di percentuale
                    Collections.sort(listAsset, new Comparator<AssetModel>() {
                        @Override
                        public int compare(AssetModel o1, AssetModel o2) {
                            return Double.compare(o2.getPercentuale(), o1.getPercentuale());
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saldo(Boolean show)
    {
        database = FirebaseDatabase.getInstance().getReference();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map <String, String> map = (Map<String, String>) snapshot.getValue();
                String sal = map.get("Saldo");

                Double saldo = Double.parseDouble(sal);
                BigDecimal bd = new BigDecimal(saldo).setScale(2, RoundingMode.HALF_UP);
                saldo = bd.doubleValue();

                if(show == true)
                {
                    txtSaldo.setText("SALDO: " + saldo.toString() + " €");
                    set_lista(saldo, true);
                }
                else
                {
                    txtSaldo.setText("SALDO: ---");
                    set_lista(saldo, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void onItemClick(int position)
    {
        if(show == true)
        {
            Intent intent = new Intent(Main.this, ModificaAsset.class);
            intent.putExtra("ID", listAsset.get(position).getId());
            intent.putExtra("NOME", listAsset.get(position).getNome());
            intent.putExtra("IMPORTO", listAsset.get(position).getImporto());
            startActivity(intent);
        }
        else
        {
            biometricPromptImporti.authenticate(promptInfoImporti);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        saldo(show);
    }
}