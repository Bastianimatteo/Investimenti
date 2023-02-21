package com.example.investimenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Map;

public class Grafico extends AppCompatActivity {

    PieEntry pieEntry;
    PieChart pieChart;
    PieDataSet pieDataSet;
    Legend legend;
    ArrayList<PieEntry> listEntries = new ArrayList<>();
    DatabaseReference database;
    Double percentuale_altri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        pieChart = findViewById(R.id.pieChart);

        saldo();
    }

    private void set_lista(double saldo)
    {
        percentuale_altri = 0.00d;
        database = FirebaseDatabase.getInstance().getReference().child("Asset");
        database.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Double importo = Double.parseDouble(dataSnapshot.child("Importo").getValue(String.class));
                    Double percentuale = importo/saldo*100;

                    if (percentuale < 3) //assett sotto il 5 percento del portafoglio
                    {
                        percentuale_altri += percentuale;
                    }
                    else
                    {
                        String nome = dataSnapshot.child("Nome").getValue(String.class);

                        pieEntry = new PieEntry(Float.parseFloat(percentuale.toString()), nome);
                        listEntries.add(pieEntry);
                    }
                }

                pieEntry = new PieEntry(Float.parseFloat(percentuale_altri.toString()), "Altri");
                listEntries.add(pieEntry);

                Collections.sort(listEntries, new Comparator<PieEntry>() {
                    @Override
                    public int compare(PieEntry o1, PieEntry o2) {
                        return Double.compare(o2.getValue(), o1.getValue());
                    }
                });

                pieDataSet = new PieDataSet(listEntries, "");
                pieDataSet.setColors(getResources().getIntArray(R.array.colors));
                pieDataSet.setValueTextSize(11); //grandezza valori y, cioè quelli sul grafico
                pieChart.getDescription().setEnabled(false);
                pieChart.setDrawEntryLabels(false); //nascondere i valori x, cioè le scritte sul grafico
                pieChart.invalidate(); //altrimenti "no data chart available"
                pieChart.setTouchEnabled(false);

                legend = pieChart.getLegend();
                legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
                legend.setWordWrapEnabled(true);
                legend.setTextSize(18);

                ValueFormatter formatter = new ValueFormatter();
                pieDataSet.setValueFormatter(formatter);

                pieChart.setData(new PieData(pieDataSet));

            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){ }
        });
    }

    private void saldo()
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

                set_lista(saldo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}