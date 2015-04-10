package com.dzt.httpposttest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    Button post, select_picture, take_picture, add_contact, take_video, send_location, take_audio;
    TextView label, file_name;
    String selectedImagePath="";
    Bitmap bitmap;
    ImageView imageView;
    VideoView videoView;
    final int SELECT_PICTURE = 1, TAKE_PICTURE = 2, ADD_CONTACT = 3, TAKE_VIDEO = 4, SEND_LOCATION = 5, TAKE_AUDIO = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
    }

    private void initializeComponents()
    {
        post = (Button) findViewById(R.id.post);
        post.setOnClickListener(this);
        take_picture = (Button) findViewById(R.id.take_picture);
        take_picture.setOnClickListener(this);
        add_contact = (Button) findViewById(R.id.add_contact);
        add_contact.setOnClickListener(this);
        select_picture = (Button) findViewById(R.id.select_picture);
        select_picture.setOnClickListener(this);
        take_video = (Button) findViewById(R.id.take_video);
        take_video.setOnClickListener(this);
        send_location = (Button) findViewById(R.id.send_location);
        send_location.setOnClickListener(this);
        take_audio = (Button) findViewById(R.id.take_audio);
        take_audio.setOnClickListener(this);

        label = (TextView) findViewById(R.id.info_label);
        //file_name = (TextView) findViewById(R.id.file_name_label);

        imageView = (ImageView) findViewById(R.id.image);

        videoView = (VideoView) findViewById(R.id.video);

        clearContainers();
    }

    private void clearContainers()
    {
        label.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.INVISIBLE);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            default:
                break;
            case R.id.post:
                makeHTTPPost();
                break;
            case R.id.select_picture:
                selectPicture();
                break;
            case R.id.take_picture:
                takePicture();
                break;
            case R.id.add_contact:
                addContact();
                break;
            case R.id.take_video:
                takeVideo();
                break;
            case R.id.send_location:
                sendLocation();
                break;
            case R.id.take_audio:
                takeAudio();
                break;
        }
    }

    private void takeAudio() {
        Intent intent = new Intent(this, RecordAudio.class);
        startActivityForResult(intent, TAKE_AUDIO);
    }

    private void sendLocation()
    {
        Intent intent = new Intent(this, SendLocation.class);
        startActivityForResult(intent, SEND_LOCATION);
    }

    private void takeVideo()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, TAKE_VIDEO);
        }
    }

    private void addContact()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, ADD_CONTACT);
    }

    private void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, TAKE_PICTURE);
        }
    }

    private void selectPicture()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    private void makeHTTPPost()
    {
        URLpetition urLpetition = new URLpetition("post");
        urLpetition.execute("http://192.168.1.81:8000/file/upload");
    }

    private class URLpetition extends AsyncTask<String, Void, String>
    {
        String action;
        public URLpetition(String action)
        {
            this.action = action;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("url = ", params[0]);
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[0]);
                /*
                File file = new File(selectedImagePath);
                InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
                reqEntity.setContentType("multipart/form-data");
                reqEntity.setChunked(true);
                post.setEntity(reqEntity);
                */
                post.setEntity(new FileEntity(new File(selectedImagePath), "application/octet-stream"));
                post.setHeader("enctype", "multipart/form-data");

                HttpResponse response = client.execute(post);
                StringBuilder stringBuilder = new StringBuilder();

                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line;
                while ((line= r.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
            switch (action)
            {
                default:
                    label.setText("default");
                    break;
                case "post":
                    label.setText(result);
                    Log.i("myLog", result);
                    break;
            }
        }

        @Override
        protected void onPreExecute() {}
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            clearContainers();
            switch(requestCode)
            {
                default:
                    break;
                case SELECT_PICTURE:
                    postSelectPicture(data);
                    break;
                case TAKE_PICTURE:
                    postTakePicture(data);
                    break;
                case ADD_CONTACT:
                    postAddContact(data);
                    break;
                case TAKE_VIDEO:
                    postTakeVideo(data);
                    break;
                case SEND_LOCATION:
                    postSendLocation(data);
                    break;
                case TAKE_AUDIO:
                    break;
            }
        }
    }

    private void postSendLocation(Intent data)
    {
        double lat = data.getDoubleExtra("lat",0);
        double lng = data.getDoubleExtra("lng", 0);

        label.setVisibility(View.VISIBLE);
        label.setText("Latitude = " + lat + "\nLongitude = " + lng);

    }

    private void postTakeVideo(Intent data)
    {
        Uri videoUri = data.getData();
        videoView.setVideoURI(videoUri);
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
    }

    private void postAddContact(Intent data)
    {
        Uri contactData = data.getData();
        Cursor c =  getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst())
        {
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

            Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                    new String[]{contactID},
                    null);
            String phone="";
            if (cursorPhone.moveToFirst())
            {
                phone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }

            label.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append("Name = ");
            sb.append(name);
            sb.append("\nPhone number = ");
            sb.append(phone);
            label.setText(sb.toString());
        }
    }

    private void postSelectPicture(Intent data)
    {
        try
        {
            /*
            Uri selectedImageUri = data.getData();
            selectedImagePath = selectedImageUri.getPath();
            file_name.setText(selectedImagePath);
            */

            // We need to recyle unused bitmaps
            if (bitmap != null) {
                bitmap.recycle();
            }
            InputStream stream = getContentResolver().openInputStream(
                    data.getData());
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void postTakePicture(Intent data)
    {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(imageBitmap);
    }
}
