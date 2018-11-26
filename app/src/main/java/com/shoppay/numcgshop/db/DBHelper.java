package com.shoppay.numcgshop.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库创建类 封装创建SQLite数据库及卷烟信息表
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shoppay";
    public static final int DATABASE_VERSION = 11;
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";


    public static final String SHOP_TABLE_NAME = "SHOP";
    public static final String NUMSHOP_TABLE_NAME = "NUMSHOP";
    public static final String JIFENSHOP_TABLE_NAME = "JIFENSHOP";
    public static final String YINPIAN_TABLE_NAME = "YINPIAN";
    public static final String ID = "id";

    /**
     * 选择商品
     */

    public static final String shop_goodsid = "goodsid";//商品id
    public static final String shop_count = "count"; //商品数量
    public static final String shop_price = "price";  //商品单价
    public static final String shop_max = "maxnum";  //商品单价
    public static final String shop_pointPercent = "pointPercent"; //获得积分比例
    public static final String shop_discount = "discount";//折扣比例
    public static final String shop_shopname = "shopname";//折扣比例
    public static final String shop_discountmoney = "discountmoney";//消费折后金额  数量*单价的折后
    public static final String shop_goodspoint = "goodspoint";
    public static final String shop_point = "point";//消费获得的积分  根据折后金额
    public static final String shop_goodsType = "goodsType";//商品类型
    public static final String shop_handler = "account";//商品类型
    public static final String shop_classid = "goodsclassid";//商品类型
    public static final String shop_pihao = "batchnumber";//商品类型
    public static final String numhop_CountDetailGoodsID = "CountDetailGoodsID";//商品类型
    public static final String numshop_count = "count";//商品类型
    public static final String numshop_account = "account";//商品类型
    public static final String numshop_allnum = "allnum";//商品类型
    public static final String numshop_shopname = "shopnae";//商品类型


    //    private String GiftID;
//    private String GiftName;
//    private String GiftCode;
//    private String GiftClassID;
//    private String GiftExchangePoint;
//    private String GiftStockNumber;
    public static final String jifenshop_id = "staid";//商品编号
    public static final String jifenshop_name = "title";//商品名称
    public static final String jifenshop_code = "entitle";//英文商品名称
    public static final String jifenshop_point = "price";//价格
    public static final String jifenshop_count = "num";//数量
    public static final String jifenshop_account = "stockcode";//指点ID

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static final String yp_goodid = "GoodsID";
    public static final String yp_classid = "GoodsClassID";
    public static final String yp_goodcode = "GoodsCode";
    public static final String yp_goodname = "GoodsName";
    public static final String yp_money = "money";
    public static final String yp_account = "account";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createShopMsg());// 创建车辆信息表
        db.execSQL(createNumShopMsg());
        db.execSQL(createJifenShopMsg());
        db.execSQL(createYinpianMsg());
    }

    private String createYinpianMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE);
        sb.append(YINPIAN_TABLE_NAME);
        sb.append("(");
        sb.append(ID).append(" integer primary key autoincrement,");
        sb.append(yp_classid).append(" varchar(32) ,");
        sb.append(yp_goodcode).append(" varchar(32) ,");
        sb.append(yp_goodid).append(" varchar(32) ,");
        sb.append(yp_goodname).append(" varchar(32) ,");
        sb.append(yp_account).append(" varchar(32) ,");
        sb.append(yp_money).append(" varchar(32)");
        sb.append(")");
        return sb.toString();
    }

    private String createShopMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE);
        sb.append(SHOP_TABLE_NAME);
        sb.append("(");
        sb.append(ID).append(" integer primary key autoincrement,");
        sb.append(shop_count).append(" varchar(32) ,");
        sb.append(shop_discount).append(" varchar(32),");
        sb.append(shop_discountmoney).append(" varchar(32),");
        sb.append(shop_shopname).append(" varchar(32),");
        sb.append(shop_goodsid).append(" varchar(32),");
        sb.append(shop_goodspoint).append(" varchar(32),");
        sb.append(shop_goodsType).append(" varchar(32),");
        sb.append(shop_max).append(" varchar(32),");
        sb.append(shop_point).append(" varchar(32),");
        sb.append(shop_classid).append(" varchar(32),");
        sb.append(shop_pihao).append(" varchar(32),");
        sb.append(shop_pointPercent).append(" varchar(32),");
        sb.append(shop_handler).append(" varchar(32),");
        sb.append(shop_price).append(" varchar(32)");
        sb.append(")");
        return sb.toString();
    }

    private String createNumShopMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE);
        sb.append(NUMSHOP_TABLE_NAME);
        sb.append("(");
        sb.append(ID).append(" integer primary key autoincrement,");
        sb.append(numshop_account).append(" varchar(32) ,");
        sb.append(numhop_CountDetailGoodsID).append(" varchar(32) ,");
        sb.append(numshop_allnum).append(" varchar(32) ,");
        sb.append(numshop_shopname).append(" varchar(32) ,");
        sb.append(numshop_count).append(" varchar(32)");
        sb.append(")");
        return sb.toString();
    }

    private String createJifenShopMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE);
        sb.append(JIFENSHOP_TABLE_NAME);
        sb.append("(");
        sb.append(ID).append(" integer primary key autoincrement,");
        sb.append(jifenshop_code).append(" varchar(32) ,");
        sb.append(jifenshop_count).append(" varchar(32) ,");
        sb.append(jifenshop_id).append(" varchar(32) ,");
        sb.append(jifenshop_name).append(" varchar(32) ,");
        sb.append(jifenshop_account).append(" varchar(32) ,");
        sb.append(jifenshop_point).append(" varchar(32)");
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + SHOP_TABLE_NAME);// 保存所有车辆信息
        db.execSQL(DROP_TABLE + NUMSHOP_TABLE_NAME);
        db.execSQL(DROP_TABLE + JIFENSHOP_TABLE_NAME);
        db.execSQL(DROP_TABLE + YINPIAN_TABLE_NAME);
        onCreate(db);
    }

}
