package inducesmile.com.androidcameraapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.UUID;

import static inducesmile.com.androidcameraapplication.R.id.captured_photo;

public class MainActivity extends ActionBarActivity {

    private ImageView _imageHolder;
    private String _pictureFilePath;
    private final int TAKE_PICTURE_ACTIVITY = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch(status){
                    case LoaderCallbackInterface.SUCCESS:
                        Toast.makeText(getApplicationContext(), "manager connected", Toast.LENGTH_LONG).show();
                        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, this, loaderCallback))
        {
        }

        _imageHolder = (ImageView)findViewById(captured_photo);
        Button capturedImageButton = (Button)findViewById(R.id.photo_button);
        capturedImageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID guid = UUID.randomUUID();
                String pictureFileName = guid.toString() + ".jpg";
                String destination =  Environment.getExternalStorageDirectory().getPath() + "/" + pictureFileName;
                File tempFile = new File(destination);
                Uri outputUri = Uri.fromFile(tempFile);
                _pictureFilePath = tempFile.getAbsolutePath();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                startActivityForResult(intent, TAKE_PICTURE_ACTIVITY);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(this.TAKE_PICTURE_ACTIVITY == requestCode && resultCode == RESULT_OK){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap myBitmap32 = BitmapFactory.decodeFile(_pictureFilePath);
            Mat matImage = new Mat(myBitmap32.getHeight(), myBitmap32.getWidth(), CvType.CV_8UC3);
            Utils.bitmapToMat(myBitmap32, matImage);
            Mat gray = new Mat(matImage.size(), CvType.CV_8UC1);
            Imgproc.cvtColor(matImage, gray, Imgproc.COLOR_RGB2GRAY, 4);
            Mat edge = new Mat();
            Mat dst = new Mat();
            Imgproc.Canny(gray, edge, 60, 80);
            Imgproc.cvtColor(edge, dst, Imgproc.COLOR_GRAY2RGBA, 4);
            Bitmap resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, resultBitmap);

            _imageHolder.setRotation(90);
            _imageHolder.setImageBitmap(resultBitmap);
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