package com.example.investimenti;

public class AssetModel
{
    String Id;
    String Nome;
    String Importo;
    Double Percentuale;

    public AssetModel(String id, String nome, String importo, Double percentuale)
    {
        this.Id = id;
        this.Nome = nome;
        this.Importo = importo;
        this.Percentuale = percentuale;
    }

    public String getId() {
        return Id;
    }

    public String getNome() {
        return Nome;
    }

    public String getImporto() {
        return Importo;
    }

    public Double getPercentuale() { return Percentuale;}
}
