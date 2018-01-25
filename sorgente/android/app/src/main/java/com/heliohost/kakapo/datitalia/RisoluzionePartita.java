package com.heliohost.kakapo.datitalia;

/**
 * Created by simonescaboro on 22/01/18.
 */

public class RisoluzionePartita {
    private Integer rispostaPlayer1;
    private Integer rispostaPlayer2;
    private Integer rispostaEsatta;
    private String domanda;

    public RisoluzionePartita(Integer rispostaPlayer1, Integer rispostaPlayer2, Integer rispostaEsatta, String domanda) {
        this.rispostaPlayer1 = rispostaPlayer1;
        this.rispostaPlayer2 = rispostaPlayer2;
        this.rispostaEsatta = rispostaEsatta;
        this.domanda = domanda;
    }


    public Integer getRispostaPlayer1() {
        return rispostaPlayer1;
    }

    public Integer getRispostaPlayer2() {
        return rispostaPlayer2;
    }

    public Integer getRispostaEsatta() {
        return rispostaEsatta;
    }

    public String getDomanda() {
        return domanda;
    }
}
