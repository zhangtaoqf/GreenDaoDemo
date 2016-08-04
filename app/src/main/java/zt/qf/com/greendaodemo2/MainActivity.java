package zt.qf.com.greendaodemo2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import zt.qf.com.greendaodemo2.db.DaoMaster;
import zt.qf.com.greendaodemo2.db.DaoSession;
import zt.qf.com.greendaodemo2.db.Node;
import zt.qf.com.greendaodemo2.db.NodeDao;

/**
 * 1.生成实体类和Dao(greendao-generator)
 * 2.开始使用(greendao)
 */
public class MainActivity extends AppCompatActivity {

    private NodeDao nodeDao;
    private DaoSession daoSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daoSession = DaoMaster.newDevSession(this,"nodes");

        nodeDao = daoSession.getNodeDao();

        //保存数据：nodeDao.save(new Node("zhangsan"));

        //开启事务
        nodeDao.getDatabase().beginTransaction();

        //成功关闭事务
        nodeDao.getDatabase().setTransactionSuccessful();

        nodeDao.getDatabase().endTransaction();
        List<Node> nodes = daoSession.getNodeDao().queryBuilder().list();

        for (int i = 0; i < nodes.size(); i++) {
            Log.i("info",nodes.toString());
        }

    }
}
