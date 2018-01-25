package com.heliohost.kakapo.datitalia;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simone on 22/12/2017.
 */

class Provincia implements Serializable {

    public Map<String, Long> entrate;
    public Map<String, Long> uscite;
    public String nomeProvincia;
    public String regione;
    public Double latitude;
    public Double longitude;

    public Provincia() {
        //Costruttore di default vuoto per usare le API di firebase.
    }

    public Provincia(Map<String, Long> entrate, Map<String, Long> uscite, String nomeProvincia) {
        this.entrate = entrate;
        this.uscite = uscite;
        this.nomeProvincia = nomeProvincia;
    }

    public Provincia(String nomeProvincia, String nomeRegione, Map<String, Long> entrate, Map<String, Long> uscite) {
        this.entrate = entrate;
        this.uscite = uscite;
        this.nomeProvincia = nomeProvincia;
        this.regione = nomeRegione;
    }


    public String getNomeProvincia() {
        return nomeProvincia;
    }

    public void setNomeProvincia(String nome) {
        this.nomeProvincia = nome;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String nome) {
        this.regione = nome;
    }

    public Map<String, Long> getEntrate() {
        return entrate;
    }

    public void setEntrate(Map<String, Long> newEntrate) {
        this.entrate = newEntrate;
    }

    public Map<String, Long> getUscite() {
        return uscite;
    }

    public void setUscite(Map<String, Long> newUscite) {
        this.uscite = newUscite;
    }

    public int getCompartoValore(String comparto) {
        return entrate.get(comparto).intValue();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return nomeProvincia;
    }

    @Override
    public boolean equals(Object obj) {
        boolean ret = false;
        if (obj instanceof Provincia) {
            Provincia p = (Provincia) obj;
            ret = (p.getNomeProvincia() == this.getNomeProvincia()) &&
                    (p.getRegione() == this.getRegione()) &&
                    (p.getEntrate().equals(this.getEntrate())) &&
                    (p.getUscite().equals(this.getUscite()));
        }
        return ret;
    }
}
