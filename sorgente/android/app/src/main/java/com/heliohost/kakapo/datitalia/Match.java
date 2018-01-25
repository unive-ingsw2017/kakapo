package com.heliohost.kakapo.datitalia;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Simone on 22/12/2017.
 */

abstract class Match{

    private Map<String, LinkedList<Long>> valori;
    private int actualAnswer;
    private List<String> compartiUsati;
    private List<String> tipologiaDomandeUsate;
    private List<Integer> correctAnswer;
    private List<Integer> risposte;
    private Provincia provincia1;
    private Provincia provincia2;
    private String nomeProvincia1;
    private String nomeProvincia2;

    private List<String> domande;
    private List<MatchQuestion> questions = new LinkedList<>();
    private Set<String> setComparti;

    // comparti in comune
    private String[] arrayComparti;
    // tipologia delle domande
    private List<String> tipologiaDomande;
    private static final String SPENDE_MENO = "Spende meno ";
    private static final String SPENDE_DIPIU = "Spende di più ";
    private static final String MAGGIORI_ENTRATE = "Ha maggiori entrate ";
    private static final String MINORI_ENTRATE = "Ha minori entrate ";

    //Match(){}

    Match(String nomeProvincia1, String nomeProvincia2) {
        this.nomeProvincia1 = nomeProvincia1;
        this.nomeProvincia2 = nomeProvincia2;
        actualAnswer = -1;
        this.tipologiaDomande = new LinkedList<>();
        this.tipologiaDomandeUsate = new LinkedList<>();
        this.correctAnswer = new LinkedList<>();
        this.domande = new LinkedList<>();
        this.compartiUsati = new LinkedList<>();
        this.valori = new HashMap<>();
        this.risposte = new LinkedList<>();
        addTipologiaDomande();
        creaProvince(nomeProvincia1, nomeProvincia2);
    }

    public Map<String, LinkedList<Long>> getValori() {
        return valori;
    }

    public List<String> getCompartiUsati() {
        return compartiUsati;
    }

    /*public List<String> getTipologiaDomandeUsate() {
        return tipologiaDomandeUsate;
    }*/

    public List<Integer> getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getDomande() {
        return domande;
    }


    // assegno i vari tipi di domande
    private void addTipologiaDomande() {
        tipologiaDomande.add(SPENDE_DIPIU);
        tipologiaDomande.add(SPENDE_MENO);
        tipologiaDomande.add(MAGGIORI_ENTRATE);
        tipologiaDomande.add(MINORI_ENTRATE);
    }

    // creo le due province
    private void creaProvince(final String nomeProvincia1, final String nomeProvincia2) {
        FirebaseDatabase.getInstance().getReference("provinces").addValueEventListener(
                new ValueEventListener() {
                    Set<String> setComparti;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot p : dataSnapshot.getChildren()) {
                            Provincia pr = p.getValue(Provincia.class);
                            if (pr.getNomeProvincia().equals(nomeProvincia1)) {
                                provincia1 = pr;
                            }
                            if (pr.getNomeProvincia().equals(nomeProvincia2)) {
                                provincia2 = pr;
                            }
                        }
                        setComparti = new HashSet<>(provincia1.getEntrate().keySet());
                        setComparti.retainAll(provincia2.getEntrate().keySet());
                        setComparti.retainAll(provincia1.getUscite().keySet());
                        setComparti.retainAll(provincia2.getUscite().keySet());

                        arrayComparti = setComparti.toArray(new String[setComparti.size()]);
                        Log.d("TAG", "SOno in firebase");
                        generateQuestions();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }


    void addCorrectAnswer(String comparto, long val1, long val2) {
        compartiUsati.add(comparto);
        String domanda = new String(tipologiaDomande.get(0) + nomeProvincia1 + " o " + nomeProvincia2 + ", in ambito di '" + comparto + "'?");
        domande.add(domanda);
        correctAnswer.add(checkCorrectAnswer(tipologiaDomande.get(0), val1, val2));
        LinkedList<Long> tmp = new LinkedList<>();
        tmp.add(val1);
        tmp.add(val2);
        valori.put(comparto, tmp);
    }

    private int checkCorrectAnswer(String tipologiaDomanda, long val1, long val2) {
        if (tipologiaDomanda.equals(SPENDE_DIPIU) || tipologiaDomanda.equals(MAGGIORI_ENTRATE))
            return (val1 > val2) ? 1 : 2;
        return (val2 > val1) ? 1 : 2;
    }

    public void generateQuestions() {
        long val1;
        long val2;

        // creo la lista dei comparti e la mischio
        List<String> listComparti = Arrays.asList(arrayComparti);
        Collections.shuffle(listComparti);
        int i = 0;
        while (compartiUsati.size() < 5) {
            String comparto = listComparti.get(i);
            MatchQuestion matchQuestion = new MatchQuestion();
            matchQuestion.setComparto(comparto);
            i++;
            Collections.shuffle(tipologiaDomande);
            matchQuestion.setQuestionType(tipologiaDomande.get(0));
            tipologiaDomandeUsate.add(tipologiaDomande.get(0));

            if (tipologiaDomande.get(0).equals(SPENDE_DIPIU) || tipologiaDomande.get(0).equals(SPENDE_MENO)) {
                val1 = provincia1.getUscite().get(comparto);
                val2 = provincia2.getUscite().get(comparto);
            } else {
                val1 = provincia1.getEntrate().get(comparto);
                val2 = provincia2.getEntrate().get(comparto);
            }
            MatchQuestion matchQuestion1 = new MatchQuestion(comparto,tipologiaDomande.get(0),val1,val2,0,0,checkCorrectAnswer(tipologiaDomande.get(0),val1,val2));
            questions.add(matchQuestion1);
            addCorrectAnswer(comparto, val1, val2);
        }
        nextQuestion();
    }


    boolean isCorrect(int userAnswer) {
        if (getCorrectAnswer().get(actualAnswer) == userAnswer)
            return true;
        return false;
    }

    public void nextQuestion() {
        actualAnswer++;
        Log.d("TAG", "" + actualAnswer);
        if (actualAnswer < 5)
            domanda(getDomande().get(actualAnswer));
    }

    public int getCorrect() {
        return getCorrectAnswer().get(actualAnswer);
    }

    public void rispondi(int risposta){
        risposte.add(risposta);
        nextQuestion();
    }

    public abstract void domanda(String comparto);
}
