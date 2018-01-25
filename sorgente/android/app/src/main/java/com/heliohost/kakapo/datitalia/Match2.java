package com.heliohost.kakapo.datitalia;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by simonescaboro on 18/01/18.
 */

public class Match2 {
    private List<String> compartiUsati = new LinkedList<>();
    //lista di ritorno con le domande
    private List<MatchQuestion> questions = new LinkedList<>();
    // comparti in comune
    private String[] arrayComparti;
    // tipologia delle domande
    private List<String> tipologiaDomande = new LinkedList<>();
    private static final String SPENDE_MENO = "Spende meno ";
    private static final String SPENDE_DIPIU = "Spende di più ";
    private static final String MAGGIORI_ENTRATE = "Ha maggiori entrate ";
    private static final String MINORI_ENTRATE = "Ha minori entrate ";

    private Provincia provincia1;
    private Provincia provincia2;

    Match2() {
    }

    Match2(Provincia provincia1, Provincia provincia2) {
        this.provincia1 = provincia1;
        this.provincia2 = provincia2;
        addTipologiaDomande();
        creaProvince(provincia1,provincia2);
    }

    // assegno i vari tipi di domande
    private void addTipologiaDomande() {
        tipologiaDomande.add(SPENDE_DIPIU);
        tipologiaDomande.add(SPENDE_MENO);
        tipologiaDomande.add(MAGGIORI_ENTRATE);
        tipologiaDomande.add(MINORI_ENTRATE);
    }

    // creo le due province
    private void creaProvince(Provincia provincia1, Provincia provincia2) {
        Set<String> setComparti;
        setComparti = new HashSet<>(provincia1.getEntrate().keySet());
        setComparti.retainAll(provincia2.getEntrate().keySet());
        setComparti.retainAll(provincia1.getUscite().keySet());
        setComparti.retainAll(provincia2.getUscite().keySet());
        arrayComparti = setComparti.toArray(new String[setComparti.size()]);
        generateQuestions();

    }

    // generazione delle domande le domande
    private void generateQuestions() {
        long val1;
        long val2;
        int i = 0;
        // creo la lista dei comparti e la mischio
        List<String> listComparti = Arrays.asList(arrayComparti);
        Collections.shuffle(listComparti);
        Iterator<String> comps = listComparti.iterator();
        while(comps.hasNext() && i < 5) {
            String comparto = comps.next();
            i++;
            Collections.shuffle(tipologiaDomande);
            if (tipologiaDomande.get(0).equals(SPENDE_DIPIU) || tipologiaDomande.get(0).equals(SPENDE_MENO)) {
                val1 = provincia1.getUscite().get(comparto);
                val2 = provincia2.getUscite().get(comparto);
            } else {
                val1 = provincia1.getEntrate().get(comparto);
                val2 = provincia2.getEntrate().get(comparto);
            }
            int risposta;
            if (tipologiaDomande.get(0).equals(SPENDE_DIPIU) || tipologiaDomande.get(0).equals(MAGGIORI_ENTRATE))
                risposta = (val1 > val2) ? 1 : 2;
            else
                risposta = (val2 > val1) ? 1 : 2;
            MatchQuestion matchQuestion = new MatchQuestion(comparto, tipologiaDomande.get(0), val1, val2, 0, 0,risposta);
            questions.add(matchQuestion);
        }
    }


    public List<MatchQuestion> getQuestions(){
        return questions;
    }

}

