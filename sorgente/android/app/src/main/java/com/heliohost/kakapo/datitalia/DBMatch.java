package com.heliohost.kakapo.datitalia;

import android.support.annotation.Keep;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gregory on 1/17/2018.
 */

@IgnoreExtraProperties
@Keep
public class DBMatch implements Serializable {

    private String gameRef;
    private String player1ID;
    private String player2ID;
    private String player1Province;
    private String player2Province;
    private String player1Status;
    private String player2Status;
    private Integer correttePlayer1;
    private Integer correttePlayer2;
    private Integer sbagliatePlayer1;
    private Integer sbagliatePlayer2;

    public List<MatchQuestion> questions = new ArrayList<>();

    public DBMatch() {
        //Default construtctor required by DB API
    }

    public String getPlayer1Status() {
        return player1Status;
    }


    public Integer getCorrettePlayer1() {
        return correttePlayer1;
    }

    public void setCorrettePlayer1(Integer correttePlayer1) {
        this.correttePlayer1 = correttePlayer1;
    }

    public Integer getCorrettePlayer2() {
        return correttePlayer2;
    }

    public void setCorrettePlayer2(Integer correttePlayer2) {
        this.correttePlayer2 = correttePlayer2;
    }

    public Integer getSbagliatePlayer1() {
        return sbagliatePlayer1;
    }

    public void setSbagliatePlayer1(Integer sbagliatePlayer1) {
        this.sbagliatePlayer1 = sbagliatePlayer1;
    }

    public Integer getSbagliatePlayer2() {
        return sbagliatePlayer2;
    }

    public void setSbagliatePlayer2(Integer sbagliatePlayer2) {
        this.sbagliatePlayer2 = sbagliatePlayer2;
    }

    public void setPlayer1Status(String player1Status) {
        this.player1Status = player1Status;
    }

    public String getPlayer2Status() {
        return player2Status;
    }

    public void setPlayer2Status(String player2Status) {
        this.player2Status = player2Status;
    }

    public String getGameRef() {
        return gameRef;
    }

    public void setGameRef(String gameRef) {
        this.gameRef = gameRef;
    }

    public String getPlayer1ID() {
        return player1ID;
    }

    public void setPlayer1ID(String player1ID) {
        this.player1ID = player1ID;
    }

    public String getPlayer2ID() {
        return player2ID;
    }

    public void setPlayer2ID(String player2ID) {
        this.player2ID = player2ID;
    }

    public String getPlayer1Province() {
        return player1Province;
    }

    public void setPlayer1Province(String player1Province) {
        this.player1Province = player1Province;
    }

    public String getPlayer2Province() {
        return player2Province;
    }

    public void setPlayer2Province(String player2Province) {
        this.player2Province = player2Province;
    }

    public List<MatchQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MatchQuestion> questions) {
        this.questions = questions;
    }

    public DBMatch(String player1ID, String player1Province) {
        this.player1ID = player1ID;
        this.player1Province = player1Province;
        this.player2ID = MatchMaker.NONE;
        this.player2Province = MatchMaker.NONE;
    }

    @Override
    public String toString() {
        return "["+player1ID+" vs "+player2ID+"] province ["+player1Province+ " vs " + player2Province+"]";
    }
}
