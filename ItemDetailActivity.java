package ru.myseolife.a99torrentsalpha2;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ItemDetailActivity extends BaseActivity {
    private static final String TAG = "ItemDetailActivity";
    private long enqueue;
    private DownloadManager mDownloadManager;
    String torrentId;

    public void onClick(View view) {
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse("http://99torrents.net/engine/download.php?id=" + torrentId));
//        enqueue = mDownloadManager.enqueue(request);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activateToolbar(true);
        torrentId = "";

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = mDownloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {


                            String uriString = c
                                    .getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        }
                    }
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Intent intent = getIntent(); // retreive the intent that started this activity
        Item item = (Item) intent.getSerializableExtra(PHOTO_TRANSFER);
        if(item!=null){
            Resources resources = getResources();


            this.setTitle(item.getTitle());

            TextView itemTitle = (TextView) findViewById(R.id.item_title);
            TextView itemYear = (TextView) findViewById(R.id.item_year);
            TextView itemGlobalRating = (TextView) findViewById(R.id.item_global_rating);
            TextView itemCategories = (TextView) findViewById(R.id.item_categories);
            TextView itemOriginalName = (TextView) findViewById(R.id.item_original_name);
            TextView itemCountry = (TextView) findViewById(R.id.item_country);
            TextView itemActors = (TextView) findViewById(R.id.item_actors);
            TextView itemDirectors = (TextView) findViewById(R.id.item_directors);
            TextView itemCompany = (TextView) findViewById(R.id.item_company);
            TextView itemShortStory = (TextView) findViewById(R.id.item_short_story);


            String text = resources.getString(R.string.item_title_text, item.getTitle()); // attributes - (ID, placeholder)
            itemTitle.setText(text);

            text = "";
            text = resources.getString(R.string.item_year_text, item.getGod());
            if(text.equals("")){
                text = resources.getString(R.string.item_year_text, item.getGod2());
            }
            itemYear.setText(text);

            text = "";
            text = resources.getString(R.string.item_globalRating_text, item.getRating());
            if(text.equals("")){
                text = resources.getString(R.string.item_globalRating_text, item.getImdb());
            }
            itemGlobalRating.setText(text);

            int[] categories = item.getCategories();
            if (categoryMap!=null){
                fullCategories();
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i<categories.length;i++){
               builder.append(categoryMap.get(categories[i]));
                if(!(categories.length-i==1)){
                    builder.append(", ");
                }
            }

            text = resources.getString(R.string.item_categories_text, builder.toString());
            itemCategories.setText(text);

            text = resources.getString(R.string.item_original_text, item.getOriginal());
            itemOriginalName.setText(text);

            text = resources.getString(R.string.item_country_text, item.getCountry());
            itemCountry.setText(text);

            text = resources.getString(R.string.item_actors_text, item.getRol());
            itemActors.setText(text);

            text = resources.getString(R.string.item_directors_text, item.getRe());
            itemDirectors.setText(text);

            text = resources.getString(R.string.item_company_text, item.getCompany());
            itemCompany.setText(text);

            text = resources.getString(R.string.item_ShortStory_text, item.getShortStory());
            itemShortStory.setText(text);

            ImageView photoImage = (ImageView) findViewById(R.id.image_poster);
            Picasso.with(this).load(item.getPoster())
                    .error(R.drawable.ic_action_poster_holder)
                    .placeholder(R.drawable.ic_action_poster_holder)
                    .into(photoImage); // get a Picasso object
            // load - load the image frome the url and store the thumbnail url in the image field of the Photo class
            // into - is where we store the downloaded image into the ImageView widget in the ViewHolder

            String torrent = android.text.Html.fromHtml(item.getTorrent()).toString();
            String torrents = android.text.Html.fromHtml(item.getTorrents()).toString();

            final LinearLayout lm = (LinearLayout) findViewById(R.id.item_detail_linear_in_scrollview);

            // create the layout params that will be used to define how your
            // button will be displayed



                torrentId = torrent.replaceAll("\\D","");
            if (!(torrentId.equals(""))) {
                Log.d(TAG, "onCreate: torrent id:" + torrentId);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);

                // Create LinearLayout
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);

                // Create Button
                final Button btn = new Button(this);
                // Give button an ID
                btn.setText("Скачать торрент");
                // set the layoutParams on the button
                btn.setLayoutParams(params);
                // Set click listener for button
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if (isStoragePermissionGranted()){
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://99torrents.net/engine/download.php?id=" + torrentId));
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://99torrents.net/search-torrents/download/rutor-504784"));
                        startActivity(browserIntent);

//                        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(
//                                Uri.parse("http://99torrents.net/engine/download.php?id=" + torrentId));
////                        Uri.parse("http://99torrents.net/uploads/files/2015-03/1425239578_svalka-2014.torrent"));
//                        File extStore = Environment.getExternalStorageDirectory();
//                        Uri dst_uri = Uri.parse("file:///" + extStore + "/download/File.torrent");
//                        request.setMimeType("application/torrent");
//                        request.setDestinationUri(dst_uri);
//                        request.setNotificationVisibility(1);
//                        enqueue = mDownloadManager.enqueue(request);
                    }
                    }
                });

                //Add button to LinearLayout
                ll.addView(btn);
                //Add button to LinearLayout defined in XML
                lm.addView(ll);
            }

            TextView itemTorrent = (TextView) findViewById(R.id.item_torrent);
            TextView itemTorrents = (TextView) findViewById(R.id.item_torrents);
            itemTorrent.setText(torrent);
            itemTorrents.setText(torrents);


        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

}
