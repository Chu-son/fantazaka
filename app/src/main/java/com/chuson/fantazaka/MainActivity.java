package com.chuson.fantazaka;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    static final int MENUITEM_ID_DELETE = 1;
    static final int MENUITEM_ID_EDIT = 2;

    protected ListView itemListView;
    static ItemListAdapter itemListAdapter;
    static List<Item> itemList = new ArrayList<>();

    static DBAdapter dbAdapter;

    //
    // Adapter class
    //
    private class ItemListAdapter extends BaseAdapter {
        @Override public int getCount() {
            return itemList.size();
        }

        @Override public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            TextView urlTextView;
            View v = convertView;

            if(v==null){
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row, null);
            }

            Item item = (Item)getItem(position);
            if(item != null){
                urlTextView = v.findViewById(R.id.listlabelTextView);
                urlTextView.setText(item.getMember());

                TextView lastLotteryDateText = (TextView)findViewById(R.id.lastlotterydateText);
                if(!item.getLastLotteryDate().equals("")){
                    lastLotteryDateText.setText("Last lottery:"+item.getLastLotteryDate());
                }

                if(!item.getImageUri().equals("")){
                    Uri uri = Uri.parse(item.getImageUri());

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        int w = bitmap.getWidth();
                        int maxSize = 512;
                        float scale = maxSize/w;

                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, w, null, true);
                        bitmap = Bitmap.createScaledBitmap(bitmap, maxSize, maxSize, true);
                        ((ImageView)v.findViewById(R.id.thumbnail_imageView)).setImageBitmap(bitmap);

                    }catch (IOException e){}

                    //ContentResolver cr = getContentResolver();
                    //Cursor cursor = cr.query(uri, null, null, null, null);
                    //if(cursor != null){
                    //    cursor.moveToFirst();
                    //    Long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

                    //    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                    //    ImageView iv = v.findViewById(R.id.thumbnail_imageView);
                    //    iv.setImageBitmap(bitmap);
                    //}
                }
            }

            return v;
        }
    }

    //
    // onCreate
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        dbAdapter = new DBAdapter(this);

        itemListAdapter = new ItemListAdapter();

        itemListView = (ListView)findViewById(R.id.itemListView);
        itemListView.setAdapter(itemListAdapter);
        setListeners();

        loadItems();

        Intent intent = getIntent();
        if(intent.getAction().equals(Intent.ACTION_SEND)){
            editItem(new Item(0, intent.getClipData().getItemAt(0).getText().toString(), "", "", "",""));
        }

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    protected void setListeners(){
        itemListView.setOnItemClickListener(
                new ListView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemList.get(position).getUrl()));
                        startActivity(intent);

                    }
                }
        );
        itemListView.setOnCreateContextMenuListener(
                new View.OnCreateContextMenuListener(){
                    @Override public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add(0,MENUITEM_ID_EDIT,0,"Edit");
                        menu.add(0, MENUITEM_ID_DELETE, 0, "Delete");
                    }
                }
        );
    }

    protected void loadItems(){
        itemList.clear();
        // Read
        dbAdapter.open();
        Cursor c = dbAdapter.getAllItems();
        if(c.moveToFirst()){
            do {
                Item item = new Item(
                        c.getInt(c.getColumnIndex(DBAdapter.COL_ID)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_URL)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_MEMBER)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_ADDEDDATE)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_LASTLOTTERYDATE)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_IMAGEURI))

                );
                itemList.add(item);
            } while(c.moveToNext());
        }
        dbAdapter.close();
        itemListAdapter.notifyDataSetChanged();
    }

    private void editItem(Item item)
    {
        Intent intent = new Intent(getApplication(), EditActivity.class);
        intent.putExtra("Item", item);
        int requestCode ;
        if(item.getId() == 0) requestCode = 1000;
        else requestCode = 2000;
        startActivityForResult( intent, requestCode );
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo();
        Item item = itemList.get(menuInfo.position);
        final int itemId = item.getId();

        switch(menuItem.getItemId()){
            case MENUITEM_ID_DELETE:
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Are you sure you want to delete this item?")
                        .setPositiveButton( "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialog, int which) {
                                        dbAdapter.open();
                                        if(dbAdapter.deleteItem(itemId)){
                                            Toast.makeText( getBaseContext(),
                                                    "The item was successfully deleted.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                            loadItems();
                                        }
                                        dbAdapter.close();
                                    }
                                })
                        .setNegativeButton( "Cancel", null)
                        .show();
                return true;

            case MENUITEM_ID_EDIT:
                editItem(item);

        }
        return super.onContextItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode != RESULT_OK) return;

        dbAdapter.open();

        if(requestCode == 1000) dbAdapter.saveItem((Item)intent.getSerializableExtra("RESULT"));
        if(requestCode == 2000) dbAdapter.update((Item)intent.getSerializableExtra("RESULT"));

        dbAdapter.close();
        loadItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addItem) {
            editItem(new Item(0, "", "", "", "", ""));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
