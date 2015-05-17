package com.festinare.discount.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.festinare.discount.R;
import com.festinare.discount.models.Category;
import com.festinare.discount.ui.adapters.CategoriesAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoriesPickerFragment extends DialogFragment {

    /** This object is used to communicate this fragment with its parent */
    private CategoriesPickerListener mListener;
    /** This option is the one that is going to be passed to sort the list */
    private List<Category> categories;

    private ListView lvCategories;

    /** This interface is used to communicate this picker with its parent */
    public interface CategoriesPickerListener {
        /** Passes all categories */
        void onCategoriesSelected(DialogFragment dialog, List<Category> categories);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        /* This makes sure that the container activity has implemented the callback interface. If not, it throws an exception */
        try {
            mListener = (CategoriesPickerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSectionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.picker_categories, null);

        // Instantiate and locate the RecyclerView in fragment_records.xml
        lvCategories = (ListView) view.findViewById(R.id.lvCategories);

        Gson gson= new Gson();
        Type listType = new TypeToken<List<Category>>() {}.getType();
        final List<Category> data=gson.fromJson(loadCategoriesJSON(getActivity()),listType);

        CategoriesAdapter adapter = new CategoriesAdapter(getActivity(), data);
        lvCategories.setAdapter(adapter);

        builder.setTitle(R.string.category_picker_title)
                .setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        categories = getSelectedCategories(data);

                        if (!categories.isEmpty()) {
                            mListener.onCategoriesSelected(CategoriesPickerFragment.this, categories);
                        } else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.no_category_selected), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private List<Category> getSelectedCategories(List<Category> data) {

        List<Category> categories=new ArrayList<>();
        CategoriesAdapter adapter=(CategoriesAdapter)lvCategories.getAdapter();
        for(int i=0;i< data.size();i++)
        {
            if(adapter.mCheckStates.get(i))
            {
                categories.add(data.get(i));
            }

        }
        return categories;
    }

    /** Loads the JSON that contains the categories **/
    private String loadCategoriesJSON(Context context) {

        String json = null;

        try {
            InputStream is = context.getAssets().open("Categories.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int i = is.read(buffer);
            if (i > 0) {
                json = new String(buffer, "UTF-8");
            }
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

}
