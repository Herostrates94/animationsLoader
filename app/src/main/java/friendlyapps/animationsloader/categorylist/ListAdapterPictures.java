package friendlyapps.animationsloader.categorylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import friendlyapps.animationsloader.R;
import friendlyapps.animationsloader.api.entities.Picture;
import friendlyapps.animationsloader.api.entities.PicturesContainer;
import friendlyapps.animationsloader.database.DatabaseHelper;

public class ListAdapterPictures extends ArrayAdapter<Picture> {

    DatabaseHelper databaseHelper;
    List<Picture> items;

    public ListAdapterPictures(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        databaseHelper = new DatabaseHelper(context);
    }

    public ListAdapterPictures(Context context, int resource, List<Picture> items) {
        super(context, resource, items);
        this.items = items;
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.picturerow, null);
        }

        final Picture picture = getItem(position);

        if (picture != null) {

            final CheckBox tt2 = v.findViewById(R.id.isEnabled);
            final ImageButton deleteButton = v.findViewById(R.id.delete_btn);
            final ImageView imageView = v.findViewById(R.id.imageView);

            if (tt2 != null) {

                if(picture.getEnabled() == 1){
                    tt2.setChecked(true);
                }
                else{
                    tt2.setChecked(false);
                }

            }

            tt2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    if(tt2.isChecked()){
                        picture.setEnabled(1);
                    }
                    else{
                        picture.setEnabled(0);
                    }

                    try {
                        databaseHelper.getPictureDao().update(picture);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    try {
                        databaseHelper.getPictureDao().delete(picture);
                        items.remove(picture);
                        notifyDataSetChanged();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            loadPictureToImageView(imageView, picture);


        }

        return v;
    }

    private void loadPictureToImageView(ImageView imageView, Picture picture){

        File imgFile = new  File(picture.getPath());

        if(imgFile.exists()){

            try {
                FileInputStream fileInputStream = new FileInputStream(imgFile);
                Bitmap myBitmap = BitmapFactory.decodeStream(fileInputStream);
                Bitmap resized = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth()*2, myBitmap.getHeight()*2, true);
                imageView.setImageBitmap(resized);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            Log.i("Files", "Picture loaded from path: " + picture.getPath());

        }
    }


}