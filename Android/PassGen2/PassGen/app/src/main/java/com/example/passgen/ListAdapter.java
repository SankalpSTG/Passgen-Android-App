package com.example.passgen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ListAdapter extends BaseAdapter
{
    Context context;
    ArrayList<ListItem> newslist;

    public ListAdapter(Context context, ArrayList<ListItem> newslist) {
        this.context = context;
        this.newslist = newslist;
    }

    @Override
    public int getCount() {
        return  newslist.size();
    }

    @Override
    public Object getItem(int position) {
        return newslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= View.inflate(context,R.layout.custom_list_item,null);
        }
        ListItem currentNews=newslist.get(position);
        TextView website=(TextView)convertView.findViewById(R.id.website_name_list);
        TextView username=(TextView)convertView.findViewById(R.id.username_name_list);


        final ImageView imageView=(ImageView)convertView.findViewById(R.id.copy_in_list);
        imageView.setTag(currentNews.password);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass= (String) imageView.getTag();
                final android.content.ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Source Text", ""+pass);
                clipboardManager.setPrimaryClip(clipData);
                Log.d("",""+pass);
            }
        });
        website.setText(currentNews.website);
        username.setText(currentNews.username);
        return convertView;    }
}
