package com.heliohost.kakapo.datitalia;

/**
 * Created by Simone on 30/12/2017.
 */

class GameDatiPrepartita {
    private double entrateProvincia;
    private double usciteProvincia;
    private String titolo;

    public GameDatiPrepartita(String titolo, double entrateProvincia, double usciteProvincia) {
        this.entrateProvincia = entrateProvincia;
        this.usciteProvincia = usciteProvincia;
        this.titolo = titolo;
    }

    public double getEntrateProvincia() {
        return entrateProvincia;
    }

    public double getUsciteProvincia() {
        return usciteProvincia;
    }

    public String getTitolo() {
        return titolo;
    }
}
