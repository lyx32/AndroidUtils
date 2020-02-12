package com.arraylist7.android.utils.demo.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.arraylist7.android.utils.DBUtils;
import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.base.Base;
import com.arraylist7.android.utils.demo.model.DemoModel;

import java.util.ArrayList;
import java.util.List;

public class Database extends Base {


    @Views(R.id.ui_database_button1)
    private Button button1;
    @Views(R.id.ui_database_button2)
    private Button button2;
    @Views(R.id.ui_database_button3)
    private Button button3;
    @Views(R.id.ui_database_button4)
    private Button button4;
    @Views(R.id.ui_database_button5)
    private Button button5;
    @Views(R.id.ui_database_button6)
    private Button button6;

    public static void instance(Activity from, boolean finish) {
        IntentUtils.activity(from, Database.class, finish);
    }


    @Override
    public int getLayoutId() {
        return R.layout.ui_database;
    }

    @Override
    public void initWidget() {
        ViewUtils.inject(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saveData = saveData();
                DemoModel model = DBUtils.select(DemoModel.class, (int) StringUtils.random(0, saveData - 1));
                LogUtils.d("id=" + model.id + " 的random值为" + model.random);
                model.random = StringUtils.random(100000000, 999999999) + "";
                LogUtils.d("修改id=" + model.id + " 的random值为" + model.random + " 并保存");
                DBUtils.saveOrUpdate(model);
                model = DBUtils.select(DemoModel.class, model.id);
                LogUtils.d("读取id=" + model.id + " 的random值为" + model.random);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saveData = saveData();
                DemoModel model = DBUtils.select(DemoModel.class, (int) StringUtils.random(0, saveData - 1));
                LogUtils.d("id=" + model.id + " 的random值为" + model.random);
                DBUtils.update(DemoModel.class, "name", "newName", model.id);
                model = DBUtils.select(DemoModel.class, model.id);
                LogUtils.d("读取id=" + model.id + " 的name值为" + model.name);
            }
        });


        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saveData = saveData();
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                LogUtils.d("剩余数量" + DBUtils.count(DemoModel.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saveData = saveData();
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                DBUtils.delete(DemoModel.class,(int) StringUtils.random(0, saveData - 1));
                List<DemoModel> list = DBUtils.selectAll(DemoModel.class, "select id,dateTime from " + DBUtils.getTableName(DemoModel.class) + " where id < ?", DBUtils.param(1,"12"));
                for (DemoModel model : list) {
                    LogUtils.e("id=[" + model.id + "]   name=[" + model.name + "]  datetime=[" + model.dateTime + "]  picUrl=[" + model.picUrl + "]");
                }
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.dropTable(DemoModel.class);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DemoModel> list = new ArrayList<>();
                String[] img = new String[]{
                        "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823189_976723.jpg&size=100_100",
                        "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823202_862185.jpg&size=100_100",
                        "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823231_621330.jpg&size=100_100",
                        "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823232_489289.jpg&size=100_100",
                        "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823262_955160.jpg&size=100_100",
                };
                DemoModel model = null;
                for (int i = 100000; i < 200000; i++) {
                    model = new DemoModel();
                    model.id = i + "";
                    model.name = "name-" + i;
                    model.dateTime = StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss.SSS");
                    model.picUrl = img[i % img.length];
                    model.random = StringUtils.random(100000000, 999999999) + "";
                    list.add(model);
                    model = null;
                }
                long start = System.currentTimeMillis();
                DBUtils.saveOrUpdate(list);
                long end = System.currentTimeMillis();
                LogUtils.e("10万条新增或修改用时" + (end - start));
                LogUtils.e("当前" + DBUtils.count(DemoModel.class) + "条数据");
                int random = (int) StringUtils.random(0, list.size() - 1);
                for (int i = 0; i < random; i++) {
                    list.get((int) StringUtils.random(0, list.size() - 1)).name = "new_name_" + i;
                }
                start = System.currentTimeMillis();
                DBUtils.update(list);
                end = System.currentTimeMillis();
                LogUtils.e("10万条数据修改"+random+"条用时" + (end - start));

                start = System.currentTimeMillis();
                List<DemoModel> list2 = DBUtils.selectAll(DemoModel.class, "select * from " + DBUtils.getTableName(DemoModel.class) + " where name like '%"+StringUtils.random(10,99)+"'");
                end = System.currentTimeMillis();
                LogUtils.e("10万条中查询" + list2.size() + "条用时" + (end - start));

                start = System.currentTimeMillis();
                DemoModel _temp = DBUtils.select(DemoModel.class, StringUtils.random(100000,200000)+"");
                end = System.currentTimeMillis();
                LogUtils.e("10万条中随机查询一条" + _temp.id+"="+_temp.name + "条用时" + (end - start));

                start = System.currentTimeMillis();
                _temp = DBUtils.select(DemoModel.class, "select * from "+DBUtils.getTableName(DemoModel.class)+" where name='new_name_"+StringUtils.random(0, random)+"'",null);
                end = System.currentTimeMillis();
                LogUtils.e("10万条中随机查询一条" + _temp.id+"="+_temp.name + "条用时" + (end - start));
            }
        });
    }

    private int saveData() {
        List<DemoModel> list = new ArrayList<>();
        String[] img = new String[]{
                "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823189_976723.jpg&size=100_100",
                "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823202_862185.jpg&size=100_100",
                "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823231_621330.jpg&size=100_100",
                "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823232_489289.jpg&size=100_100",
                "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823262_955160.jpg&size=100_100",
        };
        DemoModel model = null;
        for (int i = 0; i < 20; i++) {
            model = new DemoModel();
            model.id = i + "";
            model.name = "name-" + i;
            model.dateTime = StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss.SSS");
            model.picUrl = img[i % img.length];
            model.random = StringUtils.random(100000000, 999999999) + "";
            if ( i %2 == 0) {
                model.testAddColumn2 = i;
                model.testAddColumn="测试新增列"+i;
            }
            list.add(model);
            model = null;
        }
        return DBUtils.saveOrUpdate(list);
    }
}
