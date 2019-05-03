package com.chuson.fantazaka;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;


public class EditActivity extends AppCompatActivity {
    static final String TAG = "EditActivity";
    static final int MENU_ID_SAVE = 1;

    static final int RESULT_PICK_IMAGE = 1000;

    String memberSpinnerItems[] = {
            "齋藤飛鳥",
            "与田祐希",
            "生田絵梨花",
            "秋元真夏",
            "山下美月",
            "大園桃子"
    };

    Item item;

    ImageButton imageButton;
    EditText urlEditText;
    Spinner memberSpinner;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        findViews();

        prepareMemberSpinner();
        prepareImageButton();

        Intent intent = getIntent();
        item = (Item) intent.getSerializableExtra("Item");
        loadItemData();

        // ソフトウェアキーボード表示設定
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    protected void findViews(){
        urlEditText = (EditText)findViewById(R.id.urlEditText);
        memberSpinner = (Spinner)findViewById(R.id.memberSpinner);
    }

    private void prepareMemberSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, memberSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(adapter);
        // 特になし
        memberSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    private void prepareImageButton(){
        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGE);
            }
        });
    }

    private void loadItemData()
    {
        //if(item.getId() == 0) return;
        urlEditText.setText(item.getUrl());

        if(!item.getMember().equals("")) {
            memberSpinner.setSelection(
                    ((ArrayAdapter) memberSpinner.getAdapter()).getPosition(item.getMember())
            );
        }
        if(!item.getAddedDate().equals("")){
            ((TextView)findViewById(R.id.addedDateText)).setText(
                    "Added date:"+item.getAddedDate()
            );
        }
        if(!item.getImageUri().equals("")){
            setImage(Uri.parse(item.getImageUri()));
        }

    }
    protected void saveItemData(){
        item.setUrl(urlEditText.getText().toString());
        item.setMember((String)memberSpinner.getSelectedItem());
    }

    protected void setImage(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            Long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                int w = bitmap.getWidth();
                int maxSize = 512;

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, w, null, true);
                bitmap = Bitmap.createScaledBitmap(bitmap, maxSize, maxSize, true);

                imageButton.setImageBitmap(bitmap);
                item.setImageUri(uri.toString());
            }catch(IOException e){
                e.printStackTrace();
            }

            // 生画像
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                imageButton.setImageBitmap(bitmap);
//                item.setImageUri(uri.toString());
//            }catch(IOException e){
//                e.printStackTrace();
//            }

            // サムネ画像
            //Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            //imageButton.setImageBitmap(bitmap);
            //item.setImageUri(uri.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_PICK_IMAGE & resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();

            setImage(uri);
        }
    }

    /*
     *  オプションメニュー作成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //return super.onCreateOptionsMenu(menu);
        menu.add(0,MENU_ID_SAVE,0,"SAVE").setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == MENU_ID_SAVE) {
            saveItemData();

            Intent intent = new Intent();
            intent.putExtra("RESULT", this.item);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
