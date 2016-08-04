
我相信，在平时的开发过程中，大家一定会或多或少地接触到 SQLite。然而在使用它时，我们往往需要做许多额外的工作，像编写 SQL 语句与解析查询结果等。所以，适用于 Android 的ORM 框架也就孕育而生了，现在市面上主流的框架有 OrmLite、SugarORM、Active Android、Realm 与 GreenDAO。而今天的主角便是 greenDAO，下面，我将详解地介绍如何在 Android Studio 上使用 greenDAO，并结合代码总结一些使用过程中的心得。

关于 greenDAO
Android ORM 框架之 greenDAO 使用心得

简单的讲，greenDAO 是一个将对象映射到 SQLite 数据库中的轻量且快速的 ORM 解决方案。（greenDAO is a light & fast ORM solution that maps objects to SQLite databases.） 
而关于 ORM （Object Relation Mapping - 对象关系映射）的概念，可参见 Wikipedia。

GREENDAO 设计的主要目标
一个精简的库

性能最大化

内存开销最小化

易于使用的 APIs

对 Android 进行高度优化

GREENDAO 设计的主要特点
greenDAO 性能远远高于同类的 ORMLite，具体测试结果可见官网

greenDAO 支持 protocol buffer(protobuf) 协议数据的直接存储，如果你通过 protobuf 协议与服务器交互，将不需要任何的映射。

与 ORMLite 等使用注解方式的 ORM 框架不同，greenDAO 使用「Code generation」的方式，这也是其性能能大幅提升的原因。

DAO CODE GENERATION PROJECT
Android ORM 框架之 greenDAO 使用心得

这是其核心概念：为了在我们的 Android 工程中使用 greenDAO ，我们需要另建一个纯 Java Project，用于自动生成后继 Android 工程中需要使用到的 Bean、DAO、DaoMaster、DaoSession 等类。

CORE CLASSES & MODELLING ENTITIES
关于以上几个类的相关概念与作用，我将在下面的代码（注释）中详细讲解。
当然，你也可以在 官网 中找到相关介绍。

让我们开始吧
一. 在 ANDROID 工程中配置「GREENDAO GENERATOR」模块
在 .src/main 目录下新建一个与 java 同层级的「java-gen」目录，用于存放由 greenDAO 生成的 Bean、DAO、DaoMaster、DaoSession 等类。 
Android ORM 框架之 greenDAO 使用心得Android ORM 框架之 greenDAO 使用心得

配置 Android 工程（app）的 build.gradle，如图分别添加 sourceSets 与dependencies。 Android ORM 框架之 greenDAO 使用心得

sourceSets {
        main {
            java.srcDirs = ['src/main/java', 'src/main/java-gen']
        }
    }
compile 'de.greenrobot:greendao:1.3.7'

二. 新建「GREENDAO GENERATOR」模块 (纯 JAVA 工程）
通过 File -> New -> New Module -> Java Library -> 填写相应的包名与类名 -> Finish.
Android ORM 框架之 greenDAO 使用心得Android ORM 框架之 greenDAO 使用心得Android ORM 框架之 greenDAO 使用心得

配置 daoexamplegenerator 工程的 build.gradle，添加 dependencies.

 Android ORM 框架之 greenDAO 使用心得

compile 'de.greenrobot:greendao-generator:1.3.1'
编写 ExampleDaoGenerator 类，注意： 我们的 Java 工程只有一个类，它的内容决定了「GreenDao Generator」的输出，你可以在这个类中通过对象、关系等创建数据库结构，下面我将以注释的形式详细讲解代码内容。

public class ExampleDaoGenerator {
    public static void main(String[] args) throws Exception {
        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "me.itangqi.greendao");
//      当然，如果你愿意，你也可以分别指定生成的 Bean 与 DAO 类所在的目录，只要如下所示：
//      Schema schema = new Schema(1, "me.itangqi.bean");
//      schema.setDefaultJavaPackageDao("me.itangqi.dao");

        // 模式（Schema）同时也拥有两个默认的 flags，分别用来标示 entity 是否是 activie 以及是否使用 keep sections。
        // schema2.enableActiveEntitiesByDefault();
        // schema2.enableKeepSectionsByDefault();

        // 一旦你拥有了一个 Schema 对象后，你便可以使用它添加实体（Entities）了。
        addNote(schema);

        // 最后我们将使用 DAOGenerator 类的 generateAll() 方法自动生成代码，此处你需要根据自己的情况更改输出目录（既之前创建的 java-gen)。
        // 其实，输出目录的路径可以在 build.gradle 中设置，有兴趣的朋友可以自行搜索，这里就不再详解。
        new DaoGenerator().generateAll(schema, "/Users/tangqi/android-dev/AndroidStudioProjects/MyGreenDAO/app/src/main/java-gen");
    }

    /**
     * @param schema
     */
    private static void addNote(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Note」（既类名）
        Entity note = schema.addEntity("Note");
        // 你也可以重新给表命名
        // note.setTableName("NODE");

        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段：
        note.addIdProperty();
        note.addStringProperty("text").notNull();
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        // For example, a property called “creationDate” will become a database column “CREATION_DATE”.
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }
}
三. 生成 DAO 文件（数据库）
执行 generator 工程，如一切正常，你将会在控制台看到如下日志，并且在主工程「java-gen」下会发现生成了DaoMaster、DaoSession、NoteDao、Note共4个类文件。 Android ORM 框架之 greenDAO 使用心得

如果在此处出错，你可以依据错误日志进行排查，主要看是否输出目录存在？其他配置是否正确？等

四. 在 ANDROID 工程中进行数据库操作
这里，我们只创建一个 NodeActivity 类，用于测试与讲解 greenDAO 的增、删、查功能。

activity_note.xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:orientation="vertical">

    <LinearLayout
        android:id="@+id/linearLayout1"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <EditText
            android:id="@+id/editTextNote"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="Enter new note"
            android:inputType="text"></EditText>

        <Button
            android:id="@+id/buttonAdd"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:onClick="onMyButtonClick"
            android:text="Add"></Button>

        <Button
            android:id="@+id/buttonSearch"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:onClick="onMyButtonClick"
            android:text="Search"></Button>
    </LinearLayout>

    <ListView
        android:id="@android:id/list"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"></ListView>
</LinearLayout>
NoteActivity.java

public class NoteActivity extends ListActivity {
    private SQLiteDatabase db;
    private EditText editText;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Cursor cursor;
    public static final String TAG = "DaoExample";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase();
        // 获取 NoteDao 对象
        getNoteDao();

        String textColumn = NoteDao.Properties.Text.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(getNoteDao().getTablename(), getNoteDao().getAllColumns(), null, null, null, null, orderBy);
        String[] from = {textColumn, NoteDao.Properties.Comment.columnName};
        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from,
                to);
        setListAdapter(adapter);

        editText = (EditText) findViewById(R.id.editTextNote);
    }

    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private NoteDao getNoteDao() {
        return daoSession.getNoteDao();
    }

    /**
     * Button 点击的监听事件
     *
     * @param view
     */
    public void onMyButtonClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                addNote();
                break;
            case R.id.buttonSearch:
                search();
                break;
            default:
                Log.d(TAG, "what has gone wrong ?");
                break;
        }
    }

    private void addNote() {
        String noteText = editText.getText().toString();
        editText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        // 插入操作，简单到只要你创建一个 Java 对象
        Note note = new Note(null, noteText, comment, new Date());
        getNoteDao().insert(note);
        Log.d(TAG, "Inserted new note, ID: " + note.getId());
        cursor.requery();
    }

    private void search() {
        // Query 类代表了一个可以被重复执行的查询
        Query query = getNoteDao().queryBuilder()
                .where(NoteDao.Properties.Text.eq("Test1"))
                .orderAsc(NoteDao.Properties.Date)
                .build();

//      查询结果以 List 返回
//      List notes = query.list();
        // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * ListView 的监听事件，用于删除一个 Item
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // 删除操作，你可以通过「id」也可以一次性删除所有
        getNoteDao().deleteByKey(id);
//        getNoteDao().deleteAll();
        Log.d(TAG, "Deleted note, ID: " + id);
        cursor.requery();
    }
}
五. 运行结果
一切就绪，让我们看看效果吧！运行程序，分别执行添加按钮、删除（点击 List 的 Item）与查询按钮，可以在控制台得到如下日志：
Android ORM 框架之 greenDAO 使用心得 Android ORM 框架之 greenDAO 使用心得 Android ORM 框架之 greenDAO 使用心得
