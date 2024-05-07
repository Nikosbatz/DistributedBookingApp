package com.example.myapplication.frontend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.myapplication.R;
import com.example.myapplication.backend.src.Entities.AccommodationRoom;

import java.util.ArrayList;

public class ListingsAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<AccommodationRoom> rooms;

    public ListingsAdapter(Context context, ArrayList<AccommodationRoom> items){
        this.context = context;
        this.rooms = items;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int i) {
        return rooms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, container, false);
        }

        // Room name rendering
        ((TextView) convertView.findViewById(R.id.list_text))
                .setText(rooms.get(position).getName());

        Log.d("DEBUG", rooms.get(position).getImageData().length + "");

        // Room photo rendering
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        Bitmap bitmap = BitmapFactory.decodeByteArray(rooms.get(position).getImageData(), 0, rooms.get(position).getImageData().length, options);
        Log.d("DEBUG", String.valueOf(bitmap.getRowBytes()));
        ((ImageView)convertView.findViewById(R.id.image)).setImageBitmap(bitmap);
        /*((ImageView)convertView.findViewById(R.id.image)).setImageResource(R.drawable.room2);*/
        return convertView;

    }
}
