package com.heliohost.kakapo.datitalia;

/**
 * Created by simonescaboro on 22/01/18.
 */

public class RisoluzionePartita {
    private String rispostaPlayer1;
    private String rispostaPlayer2;
    private String rispostaEsatta;
    private String domanda;

    public RisoluzionePartita(String rispostaPlayer1, String rispostaPlayer2, String rispostaEsatta, String domanda) {
        this.rispostaPlayer1 = rispostaPlayer1;
        this.rispostaPlayer2 = rispostaPlayer2;
        this.rispostaEsatta = rispostaEsatta;
        this.domanda = domanda;
    }


    public String getRispostaPlayer1() {
        return rispostaPlayer1;
    }

    public String getRispostaPlayer2() {
        return rispostaPlayer2;
    }

    public String getRispostaEsatta() {
        return rispostaEsatta;
    }

    public String getDomanda() {
        return domanda;
    }
}
