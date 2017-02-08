package ru.myseolife.a99torrentsalpha2;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ItemDetailActivity extends BaseActivity {
    private static final String TAG = "ItemDetailActivity";
    private final Context mContext = this;
    String torrentId;
    String mime;
    private DownloadManager dm;


    private String createLocalFile(InputStream inputStream, String fileName) {
        try {
            String folderName = "Download";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//            String extStorageDirectory = Environment.getDataDirectory().toString();
            Log.d(TAG, "createLocalFile: saved to directory:" + extStorageDirectory);
            File folder = new File(extStorageDirectory, folderName);
            folder.mkdir();
            final File file = new File(folder, fileName);
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                f.write(buffer, 0, length);
            }
            //f.flush();
            f.close();
//            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            String filePath = Uri.fromFile(file).toString();
            Log.d(TAG, "createLocalFile: filePath:" + filePath);
            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.addCompletedDownload(fileName, "Загрузка файла завершена", true, "application/x-bittorrent", file.getAbsolutePath(), file.length(), true);
            Toast.makeText(this, "Файл успешно загружен", Toast.LENGTH_LONG).show();
//            Snackbar snackbar = Snackbar
//                    .make(findViewById(R.id.content_item_detail), "Файл успешно скачан", Snackbar.LENGTH_LONG)
//                    .setAction("Открыть", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Just example, you should parse file name for extension
//                            Intent intent = new Intent();
//                            intent.setAction(android.content.Intent.ACTION_VIEW);
//                            Uri uri = Uri.fromFile(file);
//                            Log.d(TAG, "onClick: URI:" + uri);
//                            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
//                            Log.d(TAG, "onClick: fileUri:" + fileUri);
////                            ContentResolver cR = getApplicationContext().getContentResolver();
////                            MimeTypeMap mime = MimeTypeMap.getSingleton();
////                            String nType = mime.getExtensionFromMimeType(cR.getType(fileUri));
//                            String type ="application/x-bittorrent";
//                            intent.setDataAndType(fileUri, type);
//                            Log.d(TAG, "onClick1: mimetype is:" + type);
//                            if(intent.resolveActivity(getPackageManager()) != null){
////                                startActivityForResult(intent, 10);
//                                startActivity(intent);
//
//                            }
//                            else {
//                                Snackbar snackbar = Snackbar
//                                        .make(findViewById(R.id.content_item_detail), "Сначала установите торрент-клиент!", Snackbar.LENGTH_INDEFINITE)
//                                        .setAction("Скачать", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                String url = "https://play.google.com/store/apps/details?id=com.utorrent.client";
//                                                Intent i = new Intent(Intent.ACTION_VIEW);
//                                                i.setData(Uri.parse(url));
//                                                startActivity(i);
//                                            }
//                                        });
//
//                                snackbar.show();
//                            }
//                        }
//                    });
//
//            snackbar.show();


            return file.getPath();
        } catch (IOException e) {
            return e.getMessage();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activateToolbar(true);
        torrentId = "";


        Intent intent = getIntent(); // retreive the intent that started this activity
        Item item = (Item) intent.getSerializableExtra(PHOTO_TRANSFER);
        if (item != null) {
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
            if (text.equals("")) {
                text = resources.getString(R.string.item_year_text, item.getGod2());
            }
            itemYear.setText(text);

            text = "";
            text = resources.getString(R.string.item_globalRating_text, item.getRating());
            if (text.equals("")) {
                text = resources.getString(R.string.item_globalRating_text, item.getImdb());
            }
            itemGlobalRating.setText(text);

            int[] categories = item.getCategories();
            if (categoryMap != null) {
                fullCategories();
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < categories.length; i++) {
                builder.append(categoryMap.get(categories[i]));
                if (!(categories.length - i == 1)) {
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
//            String torrents = android.text.Html.fromHtml(item.getTorrents()).toString();
            String torrents = item.getTorrents();

            final LinearLayout lm = (LinearLayout) findViewById(R.id.item_detail_linear_in_scrollview);

            // create the layout params that will be used to define how your
            // button will be displayed
            torrentId = torrent.replaceAll("\\D", "");
            if (!(torrentId.equals(""))) {
                Log.d(TAG, "onCreate: torrent id:" + torrentId);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);

                //Scale pixels to dp
                final float scale = getResources().getDisplayMetrics().density;
                int margin_4dp = (int) (4 * scale + 0.5f);
                int margin_8dp = (int) (8 * scale + 0.5f);
                params.setMargins(margin_8dp, margin_4dp, margin_8dp, margin_4dp);
                // Create LinearLayout
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Формат: ").append(item.getFormat()).append("\n")
                        .append("Аудио: ").append(item.getAudio()).append("\n")
                        .append("Видео: ").append(item.getVideo()).append("\n")
                        .append("Продолжительность: ").append(item.getLength()).append("\n")
                        .append("Размер: ").append(item.getSize());

                // Create TextView
                TextView product = new TextView(this);
                product.setText(stringBuilder.toString());
                product.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ViewCompat.setElevation(product, 2);
                product.setPadding(margin_8dp, margin_8dp, margin_8dp, margin_8dp);
                product.setLayoutParams(params);

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
                        startLoading(torrentId);
                    }
                });

                //Add button to LinearLayout
                ll.addView(product);
                ll.addView(btn);
                //Add button to LinearLayout defined in XML
                lm.addView(ll);
            }

            if (!(torrents.equals(""))) {
                builder = new StringBuilder();
                String attach = "";
                String[] torrentsarray = torrents.split("<br />");
                for (int i = 0; i < torrentsarray.length; i++) {
                    Log.d(TAG, "onCreate:" + torrentsarray[i]);
                    Log.d(TAG, "onCreate: _____________");
                    if (!(torrentsarray[i].contains("attachment"))) {
                        Log.d(TAG, "onCreate: inLoop");
                        if (!(torrentsarray[i].equals("")) && !(torrentsarray[i].equals("<hr />")))
                            builder.append(torrentsarray[i]).append("<br />");
                        Log.d(TAG, "onCreate: builder=" + builder.toString());
                    }
                    if (torrentsarray[i].contains("attachment")) {
                        Log.d(TAG, "onCreate: Attachment detected!");
                        attach = torrentsarray[i];
                        attachConstructor(attach, builder.toString());
                        builder.setLength(0);
                        attach = "";
                    }
                }
            }

//            TextView itemTorrent = (TextView) findViewById(R.id.item_torrent);
//            TextView itemTorrents = (TextView) findViewById(R.id.item_torrents);
//            itemTorrent.setText(torrent);
//            itemTorrents.setText(torrents);


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


    public void attachConstructor(String attach, String description) {
        Log.d(TAG, "attachConstructor: starts");
        Log.d(TAG, "attachConstructor: attach is: " + attach);
        Log.d(TAG, "attachConstructor: _____________");
        Log.d(TAG, "attachConstructor: description is: " + description);

        String clearId = "";

        if (attach.contains(":")) {
            Log.d(TAG, "attachConstructor: We have a long attach here!");
            String[] splitAttach = attach.split(":");
            clearId = splitAttach[0].replaceAll("\\D", "");
            Log.d(TAG, "attachConstructor: clearId =[" + clearId + "]");
        } else {
            clearId = attach.replaceAll("\\D", "");
            Log.d(TAG, "attachConstructor: clearId =[" + clearId + "]");
        }

        String descriptionForTextview = android.text.Html.fromHtml(description).toString();

        final LinearLayout lm = (LinearLayout) findViewById(R.id.item_detail_linear_in_scrollview);

        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);

        //Scale pixels to dp
        final float scale = getResources().getDisplayMetrics().density;
        int margin_4dp = (int) (4 * scale + 0.5f);
        int margin_8dp = (int) (8 * scale + 0.5f);

        params.setMargins(margin_8dp, margin_4dp, margin_8dp, margin_4dp);

        // Create LinearLayout
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        // Create TextView
        TextView product = new TextView(this);
        product.setText(descriptionForTextview);
        product.setBackgroundColor(Color.parseColor("#FFFFFF"));
        ViewCompat.setElevation(product, 2);
        product.setPadding(margin_8dp, margin_8dp, margin_8dp, margin_8dp);
        product.setLayoutParams(params);


        // Create Button
        final Button btn1 = new Button(this);
        // Give button an ID
        btn1.setText("Скачать торрент");
        // set the layoutParams on the button
        btn1.setLayoutParams(params);

        // Set click listener for button
        final String finalClearId = clearId;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoading(finalClearId);
            }
        });

        //Add button to LinearLayout
        ll.addView(product);
        ll.addView(btn1);
        //Add button to LinearLayout defined in XML
        lm.addView(ll);


    }


//    public void getTorrentFromTorrents(String data){
//        StringBuilder builder = new StringBuilder();
//        for (int i=0; i<data.length(); i++){
//
//            while(!(data.substring(i,i+10).equals("[attachment="))){
//                builder.append(data.substring(i,i+1));
//            }
//            if (data.substring(i,i+10).equals("[attachment=")){
//                String attach = data.substring(i,i+16);
//                Log.d(TAG, "getTorrentFromTorrents: Attach = " + attach);
//                String info = builder.toString();
//                Log.d(TAG, "getTorrentFromTorrents: INFO = " + info);
//                builder = new StringBuilder();
//                Log.d(TAG, "getTorrentFromTorrents: Bilder after cleaning = " + builder.toString());
//
//
//
//            }
//        }
//    }


    public void startLoading(String torrentId) {
        if (isStoragePermissionGranted()) {
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            String url = "http://99torrents.net/engine/download.php?id=" + torrentId;
            BaseVolleyRequest volleyRequest = new BaseVolleyRequest(url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    Map<String, String> headers = response.headers;
                    String contentDisposition = headers.get("Content-Disposition");
                    // String contentType = headers.get("Content-Type");
                    String[] temp = contentDisposition.split("filename=");
                    String fileName = temp[1].replace("\"", "");
                    InputStream inputStream = new ByteArrayInputStream(response.data);
                    createLocalFile(inputStream, fileName);
//                                Toast.makeText(getApplicationContext(), "Download complete.", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                }
            });

            volleyRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(volleyRequest);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

}
