package com.example.cocktail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cocktail.Fragment.RecipeFragment;
import com.example.cocktail.View.IngerInfo;
import com.example.cocktail.View.OriginalInfo;
import com.example.cocktail.adapter.IngredientAdpater;
import com.example.cocktail.adapter.OriginalRecipeAdpater;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OriginalRecipeActivity extends AppCompatActivity {
    private TextView gettitle;
    private ImageView getimage;
    private TextView getcontent;
    private TextView gettaste;
    private TextView getalcoholicity;
    private TextView gettechnique;
    private TextView getglass;
    private TextView getcolor;
    private TextView getlink;
    private TextView getgarnish;
    private TextView getmain_Alcohol;
    private TextView getingredients;
    private RecyclerView recyclerView;
    private VideoView videoView;
    private ArrayList<IngerInfo> CustomList=new ArrayList<>();
    private ArrayList uid = new ArrayList();
    FirebaseFirestore db;

    private String title;
    private static final String TAG="CustomRecipe";
    private DrawerLayout drawerLayout;
    private View drawerView;
    private ListView listView;
    private String [] menu = {"레시피","커스텀 레시피","마이페이지","주조기능사","로그아웃"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_originalrecipe);

        recyclerView= findViewById(R.id.customrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //맨 위에 툴바 적용
        Toolbar toolbar;
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //navigation menu
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerRecipe);
        drawerView=(View)findViewById(R.id.drawer);
        listView = (ListView) findViewById (R.id. list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1 ,menu);
        listView.setAdapter (adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (menu[position]){
                    case "레시피":
                        startRecipeActivity();
                        break;
                    case "마이페이지":
                        startUserInfoActivity();
                        break;
                    case "커스텀 레시피":
                        startCustomActivity();
                        break;
                    case "주조기능사":
                        startTestActivity();
                        break;
                    case "로그아웃":
                        FirebaseAuth.getInstance().signOut();
                        startLoginActivity();
                        break;
                }
            }
        });

        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        getimage=(ImageView)findViewById(R.id.imageget);
        Glide.with(this).load(intent.getStringExtra("image")).into(getimage);
        gettitle=(TextView)findViewById(R.id.titleget);
        gettitle.setText(title);
        getcontent=(TextView)findViewById(R.id.contentget);
        getcontent.setText("설명 : "+intent.getStringExtra("content"));
        gettaste=(TextView)findViewById(R.id.tasteeget);
        gettaste.setText("Taste(Sweet) : "+intent.getStringExtra("taste"));
        getalcoholicity=(TextView)findViewById(R.id.alcoholget);
        getalcoholicity.setText("Alcoholicity : "+intent.getStringExtra("alcoholicity"));
        gettechnique=(TextView)findViewById(R.id.techget);
        gettechnique.setText("Technique : "+intent.getStringExtra("technique"));
        getglass=(TextView)findViewById(R.id.glassget);
        getglass.setText("Glass : "+intent.getStringExtra("glass"));
        getcolor=(TextView)findViewById(R.id.colorget);
        getcolor.setText("Color : "+intent.getStringExtra("color"));
        getgarnish=(TextView)findViewById(R.id.garnishget);
        getgarnish.setText("Garnish : "+intent.getStringExtra("garnish"));
        getmain_Alcohol=(TextView)findViewById(R.id.Mainget);
        getmain_Alcohol.setText("Main Alcohol : "+intent.getStringExtra("main_Alcohol"));
        getingredients=(TextView)findViewById(R.id.ingredient);

        String[] ingred={"ingredients","ingredients2","ingredients3","ingredients4","ingredients5",
                "ingredients6","ingredients7",};
        String ingre="";
        for(int i=0;i<7;i++){
            if(intent.getStringExtra(ingred[i])!=null)
                ingre=ingre+"\n"+intent.getStringExtra(ingred[i]);
        }
        getingredients.setText("Ingredients : "+ingre);
        
        String url = intent.getStringExtra("link");
        Button startBtn = (Button) findViewById(R.id.videoBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(url));
                Log.d("test", url);
                startActivity(intent1);
            }
        });

        CustomList.clear();
        for(int i=0;i<7;i++){
            if(intent.getStringExtra(ingred[i])!=null) {
                String[] splitText=intent.getStringExtra(ingred[i]).split(",");
                db = FirebaseFirestore.getInstance();
                db.collection("ingredient").whereEqualTo("Name",splitText[0]).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        CustomList.add(new IngerInfo(
                                                document.getData().get("Name").toString(),
                                                document.getData().get("설명").toString(),
                                                document.getData().get("Image").toString()));
                                    }
                                    RecyclerView.Adapter mAdapter=new IngredientAdpater(OriginalRecipeActivity.this, CustomList);
                                    recyclerView.setAdapter(mAdapter);
                                } else {
                                }
                            }
                        });
            }
        }

    }



    //툴바 왼쪽에 아이콘 클릭시 navigation menu 열리도록 만듦
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(drawerView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //로그인 activity로 이동
    private void startLoginActivity() {
        Intent intent=new Intent(this, loginActivity.class);
        startActivity(intent);
    }

    //recipe activity로 이동
    private void startRecipeActivity() {
        Intent intent=new Intent(this, RecipeActivity.class);
        startActivity(intent);
    }

    //userInfo activity로 이동
    private void startUserInfoActivity() {
        Intent intent=new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }

    //custom recipe activity로 이동
    private void startCustomActivity() {
        Intent intent=new Intent(this, CustomActivity.class);
        startActivity(intent);
    }

    // test activity로 이동
    private void startTestActivity() {
        Intent intent = new Intent(this, testActivity.class);
        startActivity(intent);
    }
}
