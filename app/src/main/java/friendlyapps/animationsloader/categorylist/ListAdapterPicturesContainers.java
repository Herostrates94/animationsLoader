package friendlyapps.animationsloader.categorylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import friendlyapps.animationsloader.MainActivity;
import friendlyapps.animationsloader.R;
import friendlyapps.animationsloader.api.entities.Picture;
import friendlyapps.animationsloader.api.entities.PicturesContainer;
import friendlyapps.animationsloader.api.managers.StorageAnimationsManager;
import friendlyapps.animationsloader.database.DatabaseHelper;

public class ListAdapterPicturesContainers extends ArrayAdapter<PicturesContainer> {

    DatabaseHelper databaseHelper;
    List<PicturesContainer> items;

    public ListAdapterPicturesContainers(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        databaseHelper = new DatabaseHelper(context);
    }

    public ListAdapterPicturesContainers(Context context, int resource, List<PicturesContainer> items) {
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
            v = vi.inflate(R.layout.categoryrow, null);
        }

        final PicturesContainer picturesContainer = getItem(position);

        if (picturesContainer != null) {
            TextView tt1 = v.findViewById(R.id.categoryName);
            final CheckBox tt2 = v.findViewById(R.id.isEnabled);
            final ImageButton deleteButton = v.findViewById(R.id.delete_btn);
            final ImageButton editButton = v.findViewById(R.id.edit_btn);

            if (tt1 != null) {
                tt1.setText(picturesContainer.getCategoryName());
            }

            if (tt2 != null) {

                if(picturesContainer.getEnabled() == 1){
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
                        picturesContainer.setEnabled(1);
                    }
                    else{
                        picturesContainer.setEnabled(0);
                    }

                    try {
                        databaseHelper.getPictureContainerDao().update(picturesContainer);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    try {
                        StorageAnimationsManager.getInstance().deletePicturesContainerFromStorage(picturesContainer);

                        for(Picture picture : picturesContainer.getPicturesInCategory()){
                            databaseHelper.getPictureDao().delete(picture);
                        }

                        databaseHelper.getPictureContainerDao().delete(picturesContainer);
                        items.remove(picturesContainer);
                        notifyDataSetChanged();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    loadPicturesToRightPanel(picturesContainer);

                }
            });
        }

        return v;
    }

    private void loadPicturesToRightPanel(PicturesContainer picturesContainer){

        MainActivity mainActivity = (MainActivity) getContext();

        ListView yourListView = mainActivity.findViewById(R.id.newList);

        // get data from the table by the ListAdapter
        ListAdapterPictures customAdapter =
                new ListAdapterPictures(getContext(), R.layout.picturerow, new ArrayList<>(picturesContainer.getPicturesInCategory()), picturesContainer);

        yourListView.setAdapter(customAdapter);
        mainActivity.setCurrentPicturesContainer(picturesContainer);
        makeAnimationTypesCheckBoxesVisible(mainActivity);

    }

    void makeAnimationTypesCheckBoxesVisible(MainActivity mainActivity){

        final CheckBox checkBoxLEFT_TO_RIGHT = mainActivity.findViewById(R.id.checkBoxLEFT_TO_RIGHT);
        final CheckBox checkBoxSPIRAL = mainActivity.findViewById(R.id.checkBoxSPIRAL);
        final CheckBox checkBoxUP_DOWN = mainActivity.findViewById(R.id.checkBoxUP_DOWN);

        checkBoxLEFT_TO_RIGHT.setVisibility(View.VISIBLE);
        checkBoxSPIRAL.setVisibility(View.VISIBLE);
        checkBoxUP_DOWN.setVisibility(View.VISIBLE);

        checkBoxLEFT_TO_RIGHT.setChecked(mainActivity.getCurrentPicturesContainer().getAnimationTypes().contains("LEFT_TO_RIGHT"));
        checkBoxSPIRAL.setChecked(mainActivity.getCurrentPicturesContainer().getAnimationTypes().contains("SPIRAL"));
        checkBoxUP_DOWN.setChecked(mainActivity.getCurrentPicturesContainer().getAnimationTypes().contains("UP_DOWN"));

    }

}