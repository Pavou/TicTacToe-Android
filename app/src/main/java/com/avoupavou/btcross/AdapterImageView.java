package com.avoupavou.btcross;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pantazis on 2/2/2016.
 */
public class AdapterImageView extends ArrayAdapter<String> {
    private final String LOG_TAG="ImageAdapter";
    private final int imageViewId;
    private Activity mContext;
    private int textViewId;
    private int resource;
    private ArrayList<String> listOfValues;

    public AdapterImageView(Activity context, int resource, int textViewResourceId, int imageViewResourceId,ArrayList<String> listOfValues) {
        super(context, resource,textViewResourceId, listOfValues);
        mContext = context;
        textViewId =textViewResourceId;
        imageViewId =imageViewResourceId;
        this.resource=resource;
        this.listOfValues=listOfValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);
        ((TextView)rowView.findViewById(textViewId)).setText(listOfValues.get(position));
        return rowView;
    }

}
