package com.uh.urbanhouserowner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class Expandablelistviewadapter extends BaseExpandableListAdapter {

    public Context context;
    public List<String> listdataholder;
    public HashMap<String, List<String>> listHashMap;

    public Expandablelistviewadapter(Context context, List<String> listdataholder, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.listdataholder = listdataholder;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listdataholder.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listdataholder.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listdataholder.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listdataholder.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String headertitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group, null);
        }

        TextView lb1listheader = (TextView) view.findViewById(R.id.lblListHeader);
        lb1listheader.setTypeface(null, Typeface.BOLD);
        lb1listheader.setText(headertitle);
        lb1listheader.setPadding(100, 0, 20, 0);
        lb1listheader.setTextSize(17);
        lb1listheader.setTextColor(Color.rgb(0, 0, 0));

        return view;


    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final String childText = (String) getChild(i, i1);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) view.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        txtListChild.setPadding(125, 0, 15, 20);
        txtListChild.setTextSize(16);

        txtListChild.setTextColor(Color.rgb(0, 0, 0));

        return view;

       /* final TextView txtview = new TextView(context);
        txtview.setText(childNames[i][i1]);
        txtview.setPadding(100, 0, 0, 0);
        txtview.setTextColor(Color.RED);
        txtview.setTextSize(30);*/

        //return txtview;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
