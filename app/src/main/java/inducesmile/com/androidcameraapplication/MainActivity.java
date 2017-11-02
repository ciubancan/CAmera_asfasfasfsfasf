package inducesmile.com.androidcameraapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.opencv.core.Core;


import static inducesmile.com.androidcameraapplication.R.id.captured_photo;

public class MainActivity extends ActionBarActivity {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private ImageView imageHolder;
    private final int requestCode = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageHolder = (ImageView)findViewById(captured_photo);
        Button capturedImageButton = (Button)findViewById(R.id.photo_button);
        capturedImageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            ImageView image = (ImageView) findViewById(captured_photo);
            imageHolder.setRotation(90.0f);
            //Bitmap bMap = BitmapFactory.decodeFile("/sdcard1/DCIM/100ANDRO/DSC_0055.JPG");
            //image.setImageBitmap(bMap);
            //
            final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
            final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
            Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
            imageCursor.moveToFirst();
            do {
                String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (fullPath.contains("DCIM")) {
                    //--last image from camera --
                    Bitmap bMap = BitmapFactory.decodeFile(fullPath);
                    image.setImageBitmap(bMap);
                    return;
                }
            }
            while (imageCursor.moveToNext());


            //Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            //imageHolder.setImageBitmap(bitmap);
        }
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
}
