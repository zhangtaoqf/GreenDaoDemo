package com.example;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class Main {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "zt.qf.com.greendaodemo2.db");
        Entity node = schema.addEntity("Node");
        node.addIdProperty();
        node.addStringProperty("name");
        node.addDateProperty("birthday");
        new DaoGenerator().generateAll(schema,"./app/src/main/java");
    }
}
