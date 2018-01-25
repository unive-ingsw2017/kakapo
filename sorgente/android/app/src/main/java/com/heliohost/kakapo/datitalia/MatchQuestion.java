package com.heliohost.kakapo.datitalia;

import android.content.Intent;
import android.support.annotation.Keep;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Gregory on 1/17/2018.
 */

@IgnoreExtraProperties
@Keep
public class MatchQuestion implements Serializable{
    private String comparto;
    private Long valueProvincia1;
    private Long valueProvincia2;
    private String questionType;
    private Integer player1response;
    private Integer player2response;
    private Integer correctAnswer;


    public MatchQuestion() {
        //Defaul constructor required by DB API
    }

    public MatchQuestion(String comparto, String questionType, Long valueProvincia1, Long valueProvincia2, Integer player1response, Integer player2response, Integer correctAnswer) {
        this.comparto = comparto;
        this.valueProvincia1 = valueProvincia1;
        this.valueProvincia2 = valueProvincia2;
        this.questionType = questionType;
        this.player1response = player1response;
        this.player2response = player2response;
        this.correctAnswer = correctAnswer;
    }


    public String getComparto() {
        return comparto;
    }

    public void setComparto(String comparto) {
        this.comparto = comparto;
    }

    public Long getValueProvincia1() {
        return valueProvincia1;
    }

    public void setValueProvincia1(Long valueProvincia1) {
        this.valueProvincia1 = valueProvincia1;
    }

    public Long getValueProvincia2() {
        return valueProvincia2;
    }

    public void setValueProvincia2(Long valueProvincia2) {
        this.valueProvincia2 = valueProvincia2;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getPlayer1response() {
        return player1response;
    }

    public void setPlayer1response(Integer player1response) {
        this.player1response = player1response;
    }

    public Integer getPlayer2response() {
        return player2response;
    }

    public void setPlayer2response(Integer player2response) {
        this.player2response = player2response;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
