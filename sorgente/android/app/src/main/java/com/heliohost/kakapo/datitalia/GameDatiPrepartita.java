package com.heliohost.kakapo.datitalia;

/**
 * Created by Simone on 30/12/2017.
 */

class GameDatiPrepartita {
    private Long entrateProvincia;
    private Long usciteProvincia;
    private String titolo;

    public GameDatiPrepartita(String titolo, Long entrateProvincia, Long usciteProvincia) {
        this.entrateProvincia = entrateProvincia;
        this.usciteProvincia = usciteProvincia;
        this.titolo = titolo;
    }

    public Long getEntrateProvincia() {
        return entrateProvincia;
    }

    public Long getUsciteProvincia() {
        return usciteProvincia;
    }

    public String getTitolo() {
        return titolo;
    }
}
