package com.example.wp.wp_mvp_fragmentation.mvp.testviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.wp.wp_mvp_fragmentation.R;
import com.example.wp.wp_mvp_fragmentation.mvp.testviews.checkview.CheckView;

public class Main2Activity extends AppCompatActivity {
    CheckView checkview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        checkview = (CheckView) findViewById(R.id.checkview);
       checkview.setAnimDuration(2000);
        checkview.check();

//        PieView view = findViewById(R.id.pie);
//        ArrayList<PieData> datas = new ArrayList<>();
//        PieData pieData = new PieData("sloop", 60);
//        PieData pieData2 = new PieData("sloop", 30);
//        PieData pieData3 = new PieData("sloop", 40);
//        PieData pieData4 = new PieData("sloop", 20);
//        PieData pieData5 = new PieData("sloop", 20);
//        datas.add(pieData);
//        datas.add(pieData2);
//        datas.add(pieData3);
//        datas.add(pieData4);
//        datas.add(pieData5);
//        view.setData(datas);
//        arrayList.add(pieData4);
//        arrayList.add(pieData5);
    }

    public void chlik(View view) {
        checkview.unCheck();
    }
}
