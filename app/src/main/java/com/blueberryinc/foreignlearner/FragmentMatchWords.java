package com.blueberryinc.foreignlearner;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.blueberryinc.foreignlearner.Adapters.Options;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FragmentMatchWords extends Fragment {
    Options mOptions;
    ProgressBar progressBar;
    ImageView imageViewTranscription;
    boolean isTranscriptionShowed = true;
    private int[] wordPosition;
    private int progressBarProgress=0;
    private boolean[] isLeftClickable;
    private boolean[] isRightClickable;
    private int[] colors;
    LinearLayout linearLayoutWords;
    LinearLayout linearLayoutTranslation;

    Button[] buttonsWords;
    Button[] buttonsTrasnaltion;

    ArrayList<ArrayList<WordElement>> wordStages;
    int currentStage;
    int countStages;
    int sizeOfStage;

    int colorEndIncorrect = 0xFFc72c41;
    int colorDefault = 0xFF393e49;
    int colorEndCorrect = 0xFF91bd3a;
    int colorSelectedButton = 0xFFfe6845;
    int buttonTextColor=0xFFEEEEEE;

    int oldClick=-1;
    int oldSide=-1;
    View oldView;
    boolean allowedToClick=true;

    private ArrayList<String> mWords;
    private ArrayList<String> mTranscription;
    private ArrayList<String> mTranslation;
    Context mContext;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(getActivity());
        return inflater.inflate(R.layout.fragment_match_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorDefault = typedValue.data;
        mContext=getContext();
        theme.resolveAttribute(R.attr.buttoncolorcorrect, typedValue, true);
        colorEndCorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorincorrect, typedValue, true);
        colorEndIncorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorselected, typedValue, true);
        colorSelectedButton = typedValue.data;

        theme.resolveAttribute(R.attr.textColor, typedValue, true);
        buttonTextColor = typedValue.data;

        progressBar = view.findViewById(R.id.progressBarMatchWords);
        imageViewTranscription = view.findViewById(R.id.imageViewShowTranscriptionMatchWords);
        linearLayoutTranslation=view.findViewById(R.id.linearLayoutTranslation);
        linearLayoutWords=view.findViewById(R.id.linearLayoutWords);
        mOptions=new Options(getActivity());
        mOptions.LoadJsonSettings();
        mOptions.LoadWords();
        isTranscriptionShowed=mOptions.isTranscriptionShowed;
        CreateStages();
        Initialize();
    }
    public void CreateStages(){
        wordStages = new ArrayList<>();

        if (mOptions.mWordsToLearn.size()>=mOptions.showWordsMatchWordsCount)
            countStages=mOptions.mWordsToLearn.size()/mOptions.showWordsMatchWordsCount;
        else {
            countStages = 0;
        }

        for(int i =0;i<countStages+1;++i)
            wordStages.add(new ArrayList<WordElement>());
        currentStage=0;
        progressBar.setProgress(0);
        int st=0;
        for(int i =0;i<mOptions.mWordsToLearn.size();++i){

            wordStages.get(st).add(mOptions.mWordsToLearn.get(i));
            if ((i+1)>0 && (i+1)%mOptions.showWordsMatchWordsCount==0)
                st++;
        }
    }
    public void Initialize(){
        sizeOfStage=wordStages.get(currentStage).size();
        //wordStages.set(currentStage,new ArrayList<WordElement>());

        linearLayoutWords.removeAllViews();
        linearLayoutTranslation.removeAllViews();
        imageViewTranscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTranscriptionShowed = !isTranscriptionShowed;
                if (!isTranscriptionShowed) {
                    for(int i =0;i<sizeOfStage;++i)
                        buttonsWords[i].setText(mWords.get(i));
                } else {
                    for(int i =0;i<sizeOfStage;++i){
                        String tr = mTranscription.get(i).replace("[","").replace("]","");
                        if (!tr.isEmpty())
                            tr="\n"+"["+tr+"]";
                        buttonsWords[i].setText(mWords.get(i)+tr);
                    }
                }
                mOptions.isTranscriptionShowed=isTranscriptionShowed;
                mOptions.SaveShowedTranscription();
            }
        });

        wordPosition = new int[sizeOfStage];
        isLeftClickable = new boolean[sizeOfStage];
        isRightClickable = new boolean[sizeOfStage];
        mWords = new ArrayList<>();
        mTranscription=new ArrayList<>();
        mTranslation=new ArrayList<>();
        Collections.shuffle(wordStages.get(currentStage));
        ArrayList<Integer> a = new ArrayList<>();
        for(int i =0;i<sizeOfStage;++i){
            a.add(i);
        }
        Collections.shuffle(a);
        for(int i =0;i<sizeOfStage;++i) {
            wordPosition[i] = a.get(i);
            isLeftClickable[i] = true;
            isRightClickable[i] = true;
            mWords.add(wordStages.get(currentStage).get(i).word);
            mTranscription.add(wordStages.get(currentStage).get(i).transcription.replace("[","").replace("]",""));
            mTranslation.add(Arrays.toString(wordStages.get(currentStage).get(i).translation).replace("[","").replace("]",""));

        }
        progressBar.setMax(mOptions.mWordsToLearn.size());
        buttonsTrasnaltion= new Button[sizeOfStage];
        buttonsWords= new Button[sizeOfStage];

        for(int i =0;i<sizeOfStage;++i){
            buttonsWords[i] = new Button(mContext);
            buttonsTrasnaltion[i] = new Button(mContext);
        }

        for(int i =0;i<sizeOfStage;++i){
            if (isTranscriptionShowed)
                CreateButton(linearLayoutWords,buttonsWords[i],mWords.get(i),mTranscription.get(i).replace("[","").replace("]",""));
            else
                CreateButton(linearLayoutWords,buttonsWords[i],mWords.get(i),"");

            CreateButton(linearLayoutTranslation,buttonsTrasnaltion[i],"","");
            buttonsTrasnaltion[i].setSingleLine(true);

            final int finalI = i;
            buttonsWords[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonClicked(view,buttonsWords[finalI],finalI,0);
                }
            });
            buttonsTrasnaltion[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonClicked(view,buttonsTrasnaltion[finalI],finalI,1);
                }
            });
        }
        for(int i =0;i<sizeOfStage;++i){
            buttonsTrasnaltion[wordPosition[i]].setText(mTranslation.get(i).replace("[","").replace("]",""));
        }
    }

    public void ButtonClicked(View view,Button btn,int position,int side){
        if ((side==0 && !isLeftClickable[position])||(side==1 && !isRightClickable[position]))
            return;
        if (!allowedToClick)
            return;
        if (oldClick==-1){
            oldClick=position;
            oldView=view;
            onTapChangeColor(view);
            oldSide=side;
        }else if(side==oldSide && oldClick==position){
            onResetColor(oldView);
            oldClick=-1;
        }else if (oldSide==side){
            onResetColor(oldView);
            onTapChangeColor(view);
            oldView=view;
            oldClick=position;
        }else if(side!=oldSide){
            if (side==0){
                if (wordPosition[position]==oldClick){
                    onCorrectAnswerAnimation(view);
                    onCorrectAnswerAnimation(oldView);
                    isLeftClickable[position]=false;
                    isRightClickable[oldClick]=false;
                    progressBarProgress++;
                    progressBar.setProgress(progressBarProgress);
                }else{
                    onIncorrectAnswerAnimation(oldView);
                    onIncorrectAnswerAnimation(view);
                }
            }else if(side==1){
                if (wordPosition[oldClick]==position){
                    onCorrectAnswerAnimation(view);
                    onCorrectAnswerAnimation(oldView);
                    progressBarProgress++;
                    progressBar.setProgress(progressBarProgress);
                    isRightClickable[position]=false;
                    isLeftClickable[oldClick]=false;
                }else{
                    onIncorrectAnswerAnimation(oldView);
                    onIncorrectAnswerAnimation(view);
                }
            }
            oldView=null;
            oldSide=-1;
            oldClick=-1;
            if (progressBarProgress>=mOptions.mWordsToLearn.size())

                onCompleteAlert();
            if (progressBarProgress!=0 && (progressBarProgress)%mOptions.showWordsMatchWordsCount==0) {
                currentStage++;
                Initialize();
            }
        }
    }



    private void OnResetViewAnimation(){

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.my_fade_out);
        for(int i =0;i<buttonsTrasnaltion.length;++i){
            buttonsTrasnaltion[i].startAnimation(animation);
            buttonsWords[i].startAnimation(animation);
        }

        progressBarProgress = 0;
        progressBar.setProgress(progressBarProgress);
        CreateStages();
        Initialize();

        animation = AnimationUtils.loadAnimation(mContext, R.anim.my_fade_in);
        for(int i =0;i<buttonsTrasnaltion.length;++i){
            buttonsTrasnaltion[i].startAnimation(animation);
            buttonsWords[i].startAnimation(animation);
        }
    }
    private void onCompleteAlert() {

        if (mOptions.boolInfRepeat) {
            OnResetViewAnimation();
            return;
        }
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("Начать тренировку заново?")
                .setCancelable(false)
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().getFragmentManager().popBackStack();
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBarProgress = 0;
                        progressBar.setProgress(progressBarProgress);
                        CreateStages();
                        Initialize();

                    }
                });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void CreateButton(LinearLayout linearLayout, final Button btn, String word, String transcription){
        btn.setBackgroundColor(colorDefault);
        if (!transcription.isEmpty())
            transcription="\n["+transcription+"]";
        btn.setText(word+transcription);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(5,5,5,5);
        btn.setPadding(5,5,5,5);
        btn.setLayoutParams(params);
        btn.setTextColor(buttonTextColor);
        btn.setLines(2);
        btn.setAllCaps(false);
        btn.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        btn.setMarqueeRepeatLimit(-1);
        btn.setSelected(true);
        linearLayout.addView(btn);
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Выбранное слово")
                        .setMessage(btn.getText())
                        .create().show();
                return false;
            }
        });
    }



    private void onResetColor(View view){

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorSelectedButton, colorDefault);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }


    private void onTapChangeColor(View view) {

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorDefault, colorSelectedButton);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();

    }

    private void onIncorrectAnswerAnimation(final View view) {
        //int colorStart = 0xFF393e49;
        allowedToClick=false;
        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorDefault, colorEndIncorrect);
        colorAnim.setDuration(600);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(2);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {


                ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                        "backgroundColor", colorEndIncorrect, colorDefault);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.setRepeatMode(ValueAnimator.REVERSE);
                colorAnim.start();
                allowedToClick=true;
                //onResetColor(view);
            }
        }, 1200);
    }


    private void onCorrectAnswerAnimation(View view) {

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorDefault, colorEndCorrect);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }
}
