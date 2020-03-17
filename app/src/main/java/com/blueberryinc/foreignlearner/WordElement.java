package com.blueberryinc.foreignlearner;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class WordElement {
    public String word;
    public String transcription;
    public String[] translation;
    public String[] tags;
    public int progressBar;
    public String idIndex;
    public  static String TAG="WordElement";

    public WordElement(String word, String transcription, String translation, String tags,String idIndex,int progress) {
        this.word = word;
        this.transcription = transcription;
        this.translation = translation.split(",");
        this.idIndex=idIndex;
        this.progressBar=progress;
        Log.d(TAG, "WordElement: "+word+";"+progressBar);
        for(int i =0;i<this.translation.length;++i)
            this.translation[i].trim();
        this.tags = tags.split(",");
        for(int i =0;i<this.tags.length;++i)
            this.tags[i].trim();
    }

    public void Print(){
        System.out.println("Word="+word);
        System.out.println("Transcription="+transcription);
        System.out.println("Translation="+translation);
        System.out.println("Tags="+tags);
    }

    public WordElement(WordElement wordElement){
        word=wordElement.word;
        transcription=wordElement.transcription;
        translation= Arrays.copyOf(wordElement.translation,wordElement.translation.length);
        tags=Arrays.copyOf(wordElement.tags,wordElement.tags.length);
        progressBar=wordElement.progressBar;
        idIndex=wordElement.idIndex;
    }
    public WordElement(String word, String transcription, JSONArray translation, JSONArray tags, String progress,String idIndex) {
        this.word = word;
        this.transcription = transcription;
        this.progressBar=Integer.parseInt(progress);
        this.idIndex=idIndex;

        this.translation = new String[translation.length()];
        for(int i =0;i<translation.length();++i){
            try {
                this.translation[i]=translation.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        this.tags = new String[tags.length()];
        for(int i =0;i<tags.length();++i){
            try {
                this.tags[i]=tags.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
