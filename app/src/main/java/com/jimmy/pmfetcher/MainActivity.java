package com.jimmy.pmfetcher;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by yangyoujun on 2019/1/4 .
 */
public class MainActivity extends Activity {

    private RecyclerView newsList;
    private NewsAdapter adapter;
    private Button copyAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsList = ((RecyclerView) findViewById(R.id.rv_list));
        copyAll = (Button) findViewById(R.id.btn_copy_all);
        Button refresh = (Button) findViewById(R.id.btn_refresh);

        newsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter();
        adapter.setOnDataArrivedListener(new OnDataArrivedListener() {

            @Override
            public void onArrived(int size) {
                copyAll.setText(String.format(Locale.getDefault(), "一键复制%d条", size));
            }
        });
        newsList.setAdapter(adapter);
        adapter.init(this);

        copyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.copyAll(v.getContext());
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                adapter.loadNextData(context);
                Toast.makeText(context, "load done.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
