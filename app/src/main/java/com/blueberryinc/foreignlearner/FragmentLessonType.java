package com.blueberryinc.foreignlearner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blueberryinc.foreignlearner.Adapters.ChoosedTagItemAdapter;
import com.blueberryinc.foreignlearner.Adapters.Options;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FragmentLessonType extends Fragment {
    Toolbar toolbar;
    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Тип занятия");
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_lesson_type, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                bottomSheetDialog.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lesson_type, container, false);
    }
    Options mOptions;
    Context mContext;
    BottomSheetDialog bottomSheetDialog;
    ArrayList<String> mTagName;
    ChoosedTagItemAdapter adapter;
    TextView textViewWordCount;
    RecyclerView recyclerView;
    Button buttonForeignToWord;
    BottomNavigationView bottomNavigationView;
    Button buttonWordCount;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar=getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        mContext=getContext();
        mOptions = new Options(getActivity());
        mOptions.LoadJsonSettings();
        mOptions.LoadWords();
        bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomNavigationView=getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        buttonWordCount = view.findViewById(R.id.button_wordCount);
        buttonWordCount.setText("\t\tКоличество выбранных слов:"+Integer.toString(mOptions.countSelectedWods));
        View bottomSheetView = LayoutInflater.from(mContext)
                .inflate(
                        R.layout.bottom_sheet_learning_options,
                        (NestedScrollView) view.findViewById(R.id.bottomSheetContainer)
                );
        bottomSheetDialog.setContentView(bottomSheetView);
        Button button_bSheet_close=bottomSheetView.findViewById(R.id.buttonClose);
        button_bSheet_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        textViewWordCount=bottomSheetView.findViewById(R.id.textViewWordCount);
        Switch switch_inf_repeat=bottomSheetView.findViewById(R.id.switchInfRepeat);
        switch_inf_repeat.setChecked(mOptions.boolInfRepeat);
        switch_inf_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOptions.boolInfRepeat = b;
                mOptions.Save_boolInfRepeat();

            }
        });
        buttonForeignToWord=view.findViewById(R.id.button_foreign_to_word);
        buttonForeignToWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOptions.countSelectedWods<4) {
                    Toast.makeText(mContext, "Необходимо хотя бы 4 слова", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mContext,LessonActivity.class);
                intent.putExtra("LESSON_TYPE",0);
                startActivity(intent);
                getActivity().overridePendingTransition(R.menu.right_slide_in, R.menu.right_slide_out);
            }
        });
        Button buttonWordToForeign=view.findViewById(R.id.button_word_to_foreign);
        buttonWordToForeign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOptions.countSelectedWods<4) {
                    Toast.makeText(mContext, "Необходимо хотя бы 4 слова", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mContext,LessonActivity.class);
                intent.putExtra("LESSON_TYPE",1);
                startActivity(intent);
                getActivity().overridePendingTransition(R.menu.right_slide_in, R.menu.right_slide_out);
            }
        });
        Button buttonMatchWords=view.findViewById(R.id.button_match);
        buttonMatchWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOptions.countSelectedWods<4) {
                    Toast.makeText(mContext, "Необходимо хотя бы 4 слова", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mContext,LessonActivity.class);
                intent.putExtra("LESSON_TYPE",2);
                startActivity(intent);
                getActivity().overridePendingTransition(R.menu.right_slide_in, R.menu.right_slide_out);
            }
        });


        recyclerView=bottomSheetView.findViewById(R.id.recyclerView);
        ImageButton buttonShowTags = bottomSheetView.findViewById(R.id.imageButtonTagSelect);
        buttonShowTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                mBuilder.setTitle("Выберите теги");
                mBuilder.setMultiChoiceItems(mOptions.ArrayStringOfAllTags, mOptions.boolCheckedTags, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        mOptions.boolCheckedTags[i]=b;

                        if (!b)
                            for(int j=0;j<mTagName.size();++j)
                                if (mTagName.get(j).contains(mOptions.ArrayStringOfAllTags[i]))
                                    mTagName.remove(j);

                        if (mOptions.boolCheckedTags[i]){
                            if (!mTagName.contains(mOptions.ArrayStringOfAllTags[i]))
                                mTagName.add(mOptions.ArrayStringOfAllTags[i]);
                        }else{

                        }
                        adapter.notifyDataSetChanged();
                        textViewWordCount.setText("Выбрано слов:"+Integer.toString(mOptions.CountWords(mTagName)));
                        buttonWordCount.setText("\t\tКоличество выбранных слов:"+Integer.toString(mOptions.countSelectedWods));
                        mOptions.Save_boolCheckedTags();
                    }

                });

                mBuilder.setCancelable(true);
                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mOptions.Save_boolCheckedTags();
                    }
                });
                mBuilder.show();
            }
        });
        NumberPicker numberPicker = bottomSheetView.findViewById(R.id.numberPickerOptions);
        numberPicker.setMinValue(4);
        numberPicker.setMaxValue(20);
        setNumberPickerTextColor(numberPicker,0xFFEEEEEE);
        numberPicker.setValue(mOptions.showWordsMatchWordsCount);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int value) {
                mOptions.showWordsMatchWordsCount=value;
                mOptions.SaveJsonSettings();
            }
        });
        InitTagsAdapter();
    }
    private void InitTagsAdapter() {
        mTagName = new ArrayList<>();
        if (mOptions.boolCheckedTags!=null)
            for (int i = 0; i < mOptions.ArrayStringOfAllTags.length; ++i)
                if (mOptions.boolCheckedTags[i])
                    mTagName.add(mOptions.ArrayStringOfAllTags[i]);
        initRecyclerViewOption();
    }
    @SuppressLint("SetTextI18n")
    private void initRecyclerViewOption() {

        GridLayoutManager gridLayoutManager= new GridLayoutManager(mContext, 6,GridLayoutManager.VERTICAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (position % 5) {
                    // first two items span 3 columns each
                    case 0:
                    case 1:
                        return 3;
                    // next 3 items span 2 columns each
                    case 2:
                    case 3:
                    case 4:
                        return 2;
                }
                return (position%3==0?2:1);
            }
        });




        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ChoosedTagItemAdapter(mTagName);
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String tagName=adapter.getTagAt(viewHolder.getAdapterPosition());
                if (mOptions.ArrayStringOfAllTags !=null)
                    for(int i = 0; i<mOptions.ArrayStringOfAllTags.length; ++i){
                        if (mOptions.ArrayStringOfAllTags[i].contains(tagName)){
                            mOptions.boolCheckedTags[i]=false;
                            mOptions.SaveJsonSettings();
                        }
                    }
                mTagName.remove(tagName);
                adapter.notifyDataSetChanged();
                textViewWordCount.setText("Выбрано слов:"+Integer.toString(mOptions.CountWords(mTagName)));
                buttonWordCount.setText("\t\tКоличество выбранных слов:"+Integer.toString(mOptions.countSelectedWods));
                mOptions.SaveJsonSettings();

            }
        }).attachToRecyclerView(recyclerView);
        textViewWordCount.setText("Выбрано слов:"+Integer.toString(mOptions.CountWords(mTagName)));
        buttonWordCount.setText("\t\tКоличество выбранных слов:"+Integer.toString(mOptions.countSelectedWods));
    }

    private static void setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        try{
            Field selectorWheelPaintField = numberPicker.getClass()
                    .getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
        }
        catch(NoSuchFieldException e){
            //Log.w("setNumberPickerTextColor", e);
        }
        catch(IllegalAccessException e){
            //Log.w("setNumberPickerTextColor", e);
        }
        catch(IllegalArgumentException e){
            //Log.w("setNumberPickerTextColor", e);
        }

        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText)
                ((EditText)child).setTextColor(color);
        }
        numberPicker.invalidate();
    }
}
