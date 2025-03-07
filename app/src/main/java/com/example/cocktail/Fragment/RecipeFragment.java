package com.example.cocktail.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktail.AddCustomActivity;
import com.example.cocktail.R;
import com.example.cocktail.View.AddInfo;
import com.example.cocktail.View.OriginalInfo;
import com.example.cocktail.adapter.OriginalRecipeAdpater;
import com.example.cocktail.adapter.RecipeAdpater;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecipeFragment extends Fragment {
    private static final String TAG="Custom";
    private RecyclerView recyclerView;
    FirebaseFirestore db;
    ArrayAdapter<CharSequence> adspin1, adspin2;
    String choice1="";
    String choice2="";
    private EditText editText;
    private ArrayList<OriginalInfo> CustomList=new ArrayList<>();
    private ArrayList<OriginalInfo> FilterdList=new ArrayList<>();
    private ArrayList uid = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_recipe, container, false);
        final Spinner spin1=(Spinner)rootView.findViewById(R.id.spinner1);
        final Spinner spin2=(Spinner)rootView.findViewById(R.id.spinner2);
        rootView.findViewById(R.id.btnsearch).setOnClickListener(onClickListener);
        editText=(EditText)rootView.findViewById(R.id.search);

        adspin1=ArrayAdapter.createFromResource(getContext(), R.array.spinner_do2, R.layout.spinnertext);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1);

        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(adspin1.getItem(position).equals("taste")){
                    choice1="taste";
                    adspin2=ArrayAdapter.createFromResource(view.getContext(), R.array.spinner_do_taste,R.layout.spinnertext);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);

                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            choice2=adspin2.getItem(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText=editText.getText().toString();
                serachFilter(searchText);
            }
        });

        recyclerView= rootView.findViewById(R.id.customrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return rootView;
    }


 @Override
    public void onResume() {
        super.onResume();
        CustomList.clear();
        db = FirebaseFirestore.getInstance();
        db.collection("cocktail").orderBy("Name", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CustomList.add(new OriginalInfo(
                                        document.getData().get("Name").toString(),
                                        document.getData().get("설명").toString(),
                                        document.getData().get("ImageURL").toString(),
                                        document.getData().get("Taste(Sweet)").toString(),
                                        document.getData().get("Alcoholicity").toString(),
                                        document.getData().get("Technique").toString(),
                                        document.getData().get("Glass").toString(),
                                        document.getData().get("Color").toString(),
                                        document.getData().get("VideoLink").toString(),
                                        document.getData().get("Garnish").toString(),
                                        document.getData().get("Ingredients").toString(),
                                        document.getData().get("Ingredients2").toString(),
                                        document.getData().get("Ingredients3").toString(),
                                        document.getData().get("Ingredients4").toString(),
                                        document.getData().get("Ingredients5").toString(),
                                        document.getData().get("Ingredients6").toString(),
                                        document.getData().get("Ingredients7").toString(),
                                        document.getData().get("Main Alcohol").toString(),
                                        document.getData().get("TPO").toString(),
                                        document.getData().get("Tag").toString()));
                                uid.add(document.getId());
                            }
                            RecyclerView.Adapter mAdapter=new OriginalRecipeAdpater(RecipeFragment.this, CustomList, uid);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                        }
                    }
                });
    }

    public void serachFilter(String searchText){
        FilterdList.clear();
        for(int i=0;i<CustomList.size();i++){
            if(CustomList.get(i).getName().toLowerCase().contains(searchText.toLowerCase()) ||CustomList.get(i).getTag().toLowerCase().contains(searchText.toLowerCase())){
                FilterdList.add(CustomList.get(i));
            }
        }
        RecyclerView.Adapter mAdapter=new OriginalRecipeAdpater(RecipeFragment.this, FilterdList, uid);
        recyclerView.setAdapter(mAdapter);

    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.btnsearch:
                    db = FirebaseFirestore.getInstance();
                    CustomList.clear();
                    db.collection("cocktail").whereEqualTo(choice1,choice2).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            CustomList.add(new OriginalInfo(
                                                    document.getData().get("Name").toString(),
                                                    document.getData().get("설명").toString(),
                                                    document.getData().get("ImageURL").toString(),
                                                    document.getData().get("Taste(Sweet)").toString(),
                                                    document.getData().get("Alcoholicity").toString(),
                                                    document.getData().get("Technique").toString(),
                                                    document.getData().get("Glass").toString(),
                                                    document.getData().get("Color").toString(),
                                                    document.getData().get("VideoLink").toString(),
                                                    document.getData().get("Garnish").toString(),
                                                    document.getData().get("Ingredients").toString(),
                                                    document.getData().get("Ingredients2").toString(),
                                                    document.getData().get("Ingredients3").toString(),
                                                    document.getData().get("Ingredients4").toString(),
                                                    document.getData().get("Ingredients5").toString(),
                                                    document.getData().get("Ingredients6").toString(),
                                                    document.getData().get("Ingredients7").toString(),
                                                    document.getData().get("Main Alcohol").toString(),
                                                    document.getData().get("TPO").toString(),
                                                    document.getData().get("Tag").toString()));
                                            uid.add(document.getId());
                                        }
                                        RecyclerView.Adapter mAdapter=new OriginalRecipeAdpater(RecipeFragment.this, CustomList, uid);
                                        recyclerView.setAdapter(mAdapter);
                                    } else {
                                    }
                                }
                            });
                    break;
            }
        }
    };

}
