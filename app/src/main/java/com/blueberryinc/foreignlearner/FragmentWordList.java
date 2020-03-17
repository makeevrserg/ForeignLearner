package com.blueberryinc.foreignlearner;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blueberryinc.foreignlearner.Adapters.Options;
import com.blueberryinc.foreignlearner.Adapters.WordListItemAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

public class FragmentWordList extends Fragment implements WordListItemAdapter.OnNoteListener {
    Toolbar toolbar;

    @Override
    public void onResume() {
        super.onResume();


        toolbar.setTitle("Список слов: "+((mOptions.mWordElement==null)?"":"["+mOptions.mWordElement.length+"]"));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setVisibility(View.VISIBLE);

        mOptions.PREVIOUS_MENU="DICTIONARY";
        mOptions.Save_CURRENT_MENU();

        if (mOptions.isDatasetChanged()) {
            Log.d(TAG, "onResume: Reload words");
            mOptions=new Options(fragmentActivity);
            mOptions.LoadJsonSettings();
            mOptions.LoadWords();
            mWordElements=Arrays.copyOf(mOptions.mWordElement,mOptions.mWordElement.length);

            mOptions.Save_isDatasetChanger(false);
            CreateRecyclerView();
        }

        if (searchView!=null) {
            adapter.getFilter().filter(searchView.getQuery());
        }

        SetLanguageIcon();

    }
    public void SetLanguageIcon(){
        mOptions.Load_SELECTED_LANGUAGE();
        if (mOptions.SELECTED_LANGUAGE.equals(Options.JAPANESE))
            toolbar.setLogo(R.drawable.ic_japan);
        else
            toolbar.setLogo(R.drawable.ic_english);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_words_list, container, false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_word_list, menu);
        MenuItem item = menu.findItem(R.id.action_search_words);
        searchView= (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_startlearning:
                getActivity().getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right).
                        replace(R.id.fragment_container,
                                new FragmentLessonType()).addToBackStack(null).commit();
                break;
            case R.id.action_search_words:
                break;
        }
        return super.onOptionsItemSelected(item);
    }







    private static final String TAG = "FragmentWordList";
    RecyclerView recyclerView;
    Context mContext;
    FragmentActivity fragmentActivity;
    private FloatingActionButton floatingActionButton;
    WordListItemAdapter adapter;
    Options mOptions;
    WordListItemAdapter.OnNoteListener onNoteListener;
    WordElement[] mWordElements;
    SearchView searchView;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        mContext = getContext();
        fragmentActivity=requireActivity();
        onNoteListener = this;
        mOptions = new Options(fragmentActivity);
        ShowProgressDialog();
        SetLanguageIcon();




        new Thread(new Runnable() {
            @Override
            public void run() {

                floatingActionButton = view.findViewById(R.id.floatingButtonAddWord);
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EditWordActivity.class);
                        startActivity(intent);
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().overridePendingTransition(R.menu.right_slide_in,R.menu.right_slide_out);
                    }
                });
                mOptions.PREVIOUS_MENU="DICTIONARY";
                mOptions.Save_CURRENT_MENU();
                recyclerView = view.findViewById(R.id.recyclerView);
                floatingActionButton = view.findViewById(R.id.floatingButtonAddWord);
                mOptions.LoadJsonSettings();
                mOptions.LoadWords();
                mWordElements=Arrays.copyOf(mOptions.mWordElement,mOptions.mWordElement.length);
                Looper.prepare();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CreateRecyclerView();
                        toolbar.setTitle("Список слов: "+((mOptions.mWordElement==null)?"":"["+mOptions.mWordElement.length+"]"));
                        progressDialog.dismiss();
                        progressDialog.cancel();
                    }
                });
            }
        }).start();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK)) {
        }

    }

    public  void CreateRecyclerView(){
        adapter = new WordListItemAdapter(mWordElements, onNoteListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private ProgressDialog progressDialog;
    TextView textViewCustomDialog;

    private void ShowProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        View custom_dialog_view = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.custom_progress_dialog,
                        (RelativeLayout) getView().findViewById(R.id.dialog_container)
                );
        textViewCustomDialog = custom_dialog_view.findViewById(R.id.textView);
        progressDialog.setContentView(custom_dialog_view);

    }


    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(mContext, EditWordActivity.class);
        Log.d(TAG, "onNoteClick: ");
        intent.putExtra("position",position);
        getActivity().overridePendingTransition(R.menu.right_slide_in,R.menu.right_slide_out);
        startActivity(intent);
    }

    @Override
    public void onNoteLongClick(final int position) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.delete_word_dialog);
        TextView textViewWord = dialog.findViewById(R.id.textViewWord);
        Log.d(TAG, "onNoteLongClick: "+mOptions.mWordElement.length+";"+position+";"+mOptions.mWordElement[position].word);
        textViewWord.setText(textViewWord.getText()+" "+mOptions.mWordElement[position].word);
        Log.d(TAG, "onNoteLongClick: ");
        Button buttonReject = dialog.findViewById(R.id.buttonReject);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        Button buttonAccept = dialog.findViewById(R.id.buttonAccept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(mContext);
                String TABLE=(!mOptions.SELECTED_LANGUAGE.equals("JAPANESE"))?DBHelper.TABLE_WORD_ENGLISH:DBHelper.TABLE_WORD_JAPANESE;

                SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                sqLiteDatabase.delete(
                        TABLE,
                        DBHelper.KEY_ID+"=?",
                       new String[]{String.valueOf(mOptions.mWordElement[position].idIndex)});



                //sqLiteDatabase.delete(TABLE, DBHelper.KEY_ID + "=?"+mOptions.mWordElement[position].idIndex+"'", null);

                sqLiteDatabase.close();
                Log.d(TAG, "onClick: "+position);

                mOptions.LoadWords();
                mWordElements=Arrays.copyOf(mOptions.mWordElement,mOptions.mWordElement.length);
                CreateRecyclerView();
                Toast.makeText(mContext, "Слово удалено", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                dialog.dismiss();
                searchView.setQuery("",false);
                searchView.clearFocus();
                searchView.setIconified(true);

            }
        });
        dialog.getWindow();
        dialog.show();
    }
}
