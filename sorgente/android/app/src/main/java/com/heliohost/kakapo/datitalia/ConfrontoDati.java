package com.heliohost.kakapo.datitalia;

/**
 * Created by Simone on 29/12/2017.
 */

public class ConfrontoDati {

    private String titolo;
    private Long entrataProvincia1;
    private Long entrataProvincia2;
    private Long uscitaProvincia1;
    private Long uscitaProvincia2;


    public ConfrontoDati(String titolo, Long entrataProvincia1, Long entrataProvincia2, Long uscitaProvincia1, Long uscitaProvincia2) {
        this.titolo = titolo;
        this.entrataProvincia1 = entrataProvincia1;
        this.entrataProvincia2 = entrataProvincia2;
        this.uscitaProvincia1 = uscitaProvincia1;
        this.uscitaProvincia2 = uscitaProvincia2;
    }

    public String getTitolo() {
        return titolo;
    }

    public Long getEntrataProvincia1() {
        return entrataProvincia1;
    }

    public Long getEntrataProvincia2() {
        return entrataProvincia2;
    }

    public Long getUscitaProvincia1() {
        return uscitaProvincia1;
    }

    public Long getUscitaProvincia2() {
        return uscitaProvincia2;
    }
}


