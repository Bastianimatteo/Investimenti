package com.example.investimenti;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    Context context;
    ArrayList<AssetModel> listAsset;

    public Adapter(Context context, ArrayList<AssetModel> list)
    {
        this.context = context;
        this.listAsset = list;
    }

    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row, parent, false);
        return new Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {

        holder.tvId.setText(listAsset.get(position).getId());
        holder.tvNome.setText(listAsset.get(position).getNome());
        holder.tvImporto.setText(listAsset.get(position).getImporto() + " €");
        holder.tvPercentuale.setText(listAsset.get(position).getPercentuale() + " %");

    }

    @Override
    public int getItemCount() {
        return listAsset.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvNome, tvImporto, tvPercentuale;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvId = itemView.findViewById(R.id.txtId);
            tvNome = itemView.findViewById(R.id.txtNome);
            tvImporto = itemView.findViewById(R.id.txtImporto);
            tvPercentuale = itemView.findViewById(R.id.txtPercentuale);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION);
                    {
                        Main main = (Main) itemView.getContext();
                        main.onItemClick(position);
                    }
                }
            });
        }
    }
}
