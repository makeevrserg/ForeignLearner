package com.blueberryinc.foreignlearner;

import android.app.VoiceInteractor;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.blueberryinc.foreignlearner.Adapters.Options;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentOptions extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options, container, false);
    }
    Button buttonJapanese;
    Button buttonEnglish;
    Button buttonImportExport;
    Button buttonTheme;
    Button buttonStatistic;
    Button buttonHardWordsTag;
    ImageView imageViewAdd;
    ImageView imageViewMinus;
    private static final String TAG="FragmentOptions";


    Toolbar toolbar;
    @Override
    public void onResume() {
        super.onResume();
        toolbar.getMenu().clear();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        toolbar.setTitle("Настройки");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        toolbar=getActivity().findViewById(R.id.toolbar);
        buttonJapanese = view.findViewById(R.id.buttonJapanese);
        buttonEnglish = view.findViewById(R.id.buttonEnglish);
        buttonImportExport = view.findViewById(R.id.button_import_export);
        buttonTheme = view.findViewById(R.id.buttonThemes);
        buttonStatistic = view.findViewById(R.id.buttonStatistic);

        onClickChangeFragment(buttonImportExport,new FragmentImportExport());
        onClickChangeFragment(buttonStatistic,new FragmentStatistic());
        final Context mContext = getContext();
        final Options mOptions = new Options(getActivity());
        mOptions.PREVIOUS_MENU="OPTIONS";
        mOptions.Save_CURRENT_MENU();
        buttonJapanese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptions.SELECTED_LANGUAGE=Options.JAPANESE;
                mOptions.Save_selectedLanguage();
                toolbar.setLogo(R.drawable.ic_japan);
                Toast.makeText(mContext, "Выбран японский язык", Toast.LENGTH_SHORT).show();
            }
        });
        buttonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptions.SELECTED_LANGUAGE=Options.ENGLISH;
                mOptions.Save_selectedLanguage();
                toolbar.setLogo(R.drawable.ic_english);
                Toast.makeText(mContext, "Выбран английский язык", Toast.LENGTH_SHORT).show();
            }
        });


        buttonHardWordsTag=view.findViewById(R.id.buttonHardWordsTag);
        imageViewAdd=view.findViewById(R.id.imageViewAdd);
        imageViewMinus=view.findViewById(R.id.imageViewMinus);
        mOptions.Load_hardWordTagCount();

        buttonHardWordsTag.setText("\t\tТяжелые "+mOptions.hardWordTagCount);
        imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptions.Save_hardWordTagCount(-1);
                buttonHardWordsTag.setText("\t\tТяжелые "+mOptions.hardWordTagCount);
                Log.d(TAG, "onClick: "+mOptions.hardWordTagCount);
            }
        });
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptions.Save_hardWordTagCount(1);
                buttonHardWordsTag.setText("\t\tТяжелые "+mOptions.hardWordTagCount);
                Log.d(TAG, "onClick: "+mOptions.hardWordTagCount);
            }
        });

    }



    private void onClickChangeFragment(Button button, final Fragment selectedFragment){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right,R.anim.exit_left_to_right).
                        replace(R.id.fragment_container,
                                selectedFragment).addToBackStack(null).commit();
            }
        });

    }



}
