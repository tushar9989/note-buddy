package com.tushar.spen_helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AutoLaunchActivity extends Activity {
    static int resultCode = 15122;
    int count;
    String listID;
    ListView launchList;
    ArrayList<LaunchableItem> items;
    MultiItemAdapter itemAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    static int currentlyDragging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_launch);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        launchList = (ListView) findViewById(R.id.launchList);

        listID = getIntent().getStringExtra("listID");

        items = new ArrayList<>();

        count = pref.getInt("multi" + listID + "count", 0);

        for(int i = 0; i < count; i++) {
            items.add(new LaunchableItem("multi" + listID + i));
            items.get(i).load(this);
        }
        itemAdapter = new MultiItemAdapter(this, items, this, R.layout.multi_item_layout);

        launchList.setAdapter(itemAdapter);

        ImageView multi_add = (ImageView) findViewById(R.id.multi_list_add);

        multi_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.globalChooser(AutoLaunchActivity.this, "multi" + listID + count, resultCode, null, null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            if(requestCode == AutoLaunchActivity.resultCode)
            {
                add_item();
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int i = 0; i < count; i++)
        {
            items.get(i).setTag("multi" + listID + i);
            items.get(i).save(this);
        }
    }

    void add_item()
    {
        items.add(Utilities.item);
        items.get(items.size() - 1).save(this);
        items.get(items.size() - 1).load(this);
        launchList.invalidateViews();
        count++;
        edit = pref.edit();
        edit.putInt("multi" + listID + "count", count);
        edit.apply();
    }

    class MultiItemAdapter extends ArrayAdapter<LaunchableItem> {
        Context ctx;
        Activity myact;
        int layoutID;

        MultiItemAdapter(Context ctx, List<LaunchableItem> items, Activity myact, int layoutID) {
            super(ctx, layoutID, items);
            this.ctx = myact.getApplicationContext();
            this.myact = myact;
            this.layoutID = layoutID;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = newView(parent);
            }

            bindView(position, convertView);

            return(convertView);
        }

        private View newView(ViewGroup parent) {
            return(myact.getLayoutInflater().inflate(layoutID, parent, false));
        }

        private void bindView(final int position, final View row) {
            TextView label=(TextView)row.findViewById(R.id.launcher_title);

            label.setText(getItem(position).title);

            ImageView icon=(ImageView)row.findViewById(R.id.launcher_icon);

            icon.setImageDrawable(getItem(position).icon);

            ImageView delete = (ImageView) row.findViewById(R.id.multi_item_remove);
            delete.setOnClickListener(new deleteListener(position));

            ImageView move = (ImageView) row.findViewById(R.id.multi_item_move);
            row.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent e) {
                    ImageView row_top = (ImageView) row.findViewById(R.id.multi_bar_top);
                    ImageView row_bottom = (ImageView) row.findViewById(R.id.multi_bar_bottom);

                    if(e.getAction() == DragEvent.ACTION_DRAG_STARTED)
                        return true;

                    if(e.getAction() == DragEvent.ACTION_DRAG_ENTERED)
                    {
                        if(currentlyDragging != position)
                        {
                            if(position == count - 1)
                                row_bottom.setVisibility(View.VISIBLE);
                            else
                                row_top.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                    if(e.getAction() == DragEvent.ACTION_DRAG_EXITED)
                    {
                        row_top.setVisibility(View.GONE);
                        return true;
                    }
                    if(e.getAction() == DragEvent.ACTION_DROP)
                    {
                        LaunchableItem temp = items.remove(currentlyDragging);
                        if(currentlyDragging > position)
                        {
                            items.add(position, temp);
                            launchList.invalidateViews();
                        }
                        else if(currentlyDragging < position)
                        {
                            items.add(position, temp);
                            launchList.invalidateViews();
                        }
                    }
                    if(e.getAction() == DragEvent.ACTION_DRAG_ENDED)
                    {
                        row_top.setVisibility(View.GONE);
                        row_bottom.setVisibility(View.GONE);
                        return true;
                    }
                    return false;
                }
            });

            move.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    float x = v.getX();
                    x += v.getWidth()/2;
                    float y = v.getY();
                    y += v.getHeight()/2;
                    customShadowBuilder shadowBuilder = new customShadowBuilder(row, (int)x, (int)y);
                    row.startDrag(null, shadowBuilder, row, 0);
                    currentlyDragging = position;
                    return true;
                }
            });
        }
    }

    class customShadowBuilder extends View.DragShadowBuilder {

        int touchPointXCoord, touchPointYCoord;

        public customShadowBuilder(View view, int touchPointXCoord,
                                      int touchPointYCoord) {

            super(view);
            this.touchPointXCoord = touchPointXCoord;
            this.touchPointYCoord = touchPointYCoord;
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize,
                                           Point shadowTouchPoint) {
            super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);

            shadowTouchPoint.set(touchPointXCoord, touchPointYCoord);
        }
    }

    class deleteListener implements ImageView.OnClickListener
    {
        int position;

        deleteListener(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder confirm = new AlertDialog.Builder(AutoLaunchActivity.this);
            confirm.setTitle(R.string.confirm_action);
            confirm.setMessage(AutoLaunchActivity.this.getString(R.string.confirm_remove_body) + " " + items.get(position).title + "?");

            confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    items.get(position).delete(AutoLaunchActivity.this);
                    items.remove(position);
                    launchList.invalidateViews();
                    count--;
                    edit = pref.edit();
                    edit.putInt("multi" + listID + "count", count);
                    edit.apply();
                }
            });

            confirm.setNegativeButton("No", null);

            confirm.show();
        }
    }
}


