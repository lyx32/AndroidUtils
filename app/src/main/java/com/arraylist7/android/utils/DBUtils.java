package com.arraylist7.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.arraylist7.android.utils.annotation.DBPrimaryKey;
import com.arraylist7.android.utils.listener.SQLUpdateListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DBUtils {

    private static SQLiteOpenHelper sqlLiteOpenHelper;
    private static Map<String, String> checkDatabase = new HashMap<>();

    private DBUtils() {
    }

    public static void init(Context context) {
        init(context, context.getPackageName(), null);
    }

    public static void init(Context context, SQLUpdateListener sqlUpdateListener) {
        init(context, context.getPackageName(), sqlUpdateListener);
    }

    public static void init(Context context, String databaseName) {
        init(context, databaseName, null);
    }

    public static void init(Context context, String databaseName, final SQLUpdateListener sqlUpdateListener) {
        if (null != sqlLiteOpenHelper)
            return;
        sqlLiteOpenHelper = new SQLiteOpenHelper(context, databaseName, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                if (null != sqlUpdateListener)
                    sqlUpdateListener.onUpdate(db, oldVersion, newVersion);
            }
        };
    }

    /**
     * 原理是将class中有的属性，但是数据库里面没有的，add到数据库里面去
     */
    public static void updateTable(Class<?> clazz) {
        DBInfo info = new DBInfo(clazz);
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from " + getTableName(clazz) + " limit  0,1", new String[]{});
        List<Field> insertColumn = new ArrayList<>();
        List<String> columnNames = StringUtils.asList(cursor.getColumnNames());
        for (Field fieldName : info.allFields) {
            boolean isExist = columnNames.contains(fieldName.getName());
            // 不存在，说明需要添加这列
            if (!isExist) {
                insertColumn.add(fieldName);
            }
        }
        if (!StringUtils.isNullOrEmpty(insertColumn)) {
            String alterTable = "alter table " + getTableName(clazz) + " add column ";
            StringBuffer sql = new StringBuffer();
            for (Field field : insertColumn) {
                sql.append(alterTable + " " + field.getName() + " " + getTableColumnType(field.getType()) + " ; ");
            }
            insertColumn.clear();
            database.execSQL(sql.toString(), new String[]{});
        }
    }

    public static void createTable(Class<?> clazz) {
        DBInfo info = new DBInfo(clazz);
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        StringBuffer sql = new StringBuffer(" create table if not exists  " + getTableName(clazz) + " (");
        if (ClassUtils.isInt(info.primaryKeyField.getType())) {
            sql.append(" " + info.primaryKey + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
        } else {
            sql.append(" " + info.primaryKey + " TEXT PRIMARY KEY ,");
        }
        for (Field f : info.allFields) {
            if (f != info.primaryKeyField)
                sql.append(" " + f.getName() + " " + getTableColumnType(f.getType()) + " " + ",");
        }
        if (sql.toString().endsWith(","))
            sql.setLength(sql.length() - 1);
        sql.append(")");
        database.execSQL(sql.toString(), new String[]{});
    }

    private static String getTableColumnType(Class<?> type) {
        if (ClassUtils.isString(type) || ClassUtils.isBoolean(type) || ClassUtils.isChar(type)) {
            return "text";
        } else if (ClassUtils.isDecimal(type)) {
            return "real";
        } else if (ClassUtils.isNumber(type) || (ClassUtils.isByte(type) && !type.isArray())) {
            return "integer";
        } else if ((ClassUtils.isByte(type) && type.isArray()) || ClassUtils.isSerializable(type)) {
            return "blob";
        }
        return "";
    }

    public static void dropTable(Class<?> clazz) {
        String tableName = getTableName(clazz);
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        checkDatabase.remove(tableName);
        database.execSQL(" drop table if exists " + tableName, new String[]{});
    }

    /**
     * 更新表结构
     *
     * @param clazz
     */
    public static void createOrUpdateTable(Class<?> clazz) {
        String tableName = getTableName(clazz);
        if (!checkDatabase.containsKey(tableName)) {
            Object obj = first("select count(*) from sqlite_master where type='table' and name='" + tableName + "'", new String[]{});
            if (StringUtils.getInt(obj, -1) > 0) {
                updateTable(clazz);
            } else {
                createTable(clazz);
            }
            checkDatabase.put(tableName, "1");
        }
    }


    public static <T> int update(T model) {
        return update(StringUtils.asList(model));
    }

    public static <T> int update(List<T> list) {
        if (StringUtils.isNullOrEmpty(list))
            return 0;
        createOrUpdateTable(list.get(0).getClass());
        DBInfo info = new DBInfo(list.get(0).getClass());
        List<SQLParameter> parameters = new ArrayList<>();
        StringBuffer sql = new StringBuffer("update " + getTableName(info.clazz) + " set ");
        int index = 1;
        for (Field field : info.allFields) {
            if (field != info.primaryKeyField) {
                sql.append(" " + field.getName() + "=?,");
                parameters.add(new SQLParameter(index, field.getName(), null));
                index++;
            }
        }
        if (sql.toString().endsWith(","))
            sql.setLength(sql.length() - 1);
        sql.append(" where " + info.primaryKey + "=?");
        parameters.add(new SQLParameter(index, info.primaryKeyField.getName(), null));

        int result = 0;
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        SQLiteStatement statement = builderSQL(database, sql.toString(), parameters, false);
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            statement.clearBindings();
            for (SQLParameter param : parameters) {
                try {
                    param.setValue(ClassUtils.getValue(t, param.getColumnName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bindData(statement, parameters);
            result += statement.executeUpdateDelete();
        }
        statement.clearBindings();
        statement.close();
        database.setTransactionSuccessful();
        database.endTransaction();
        return result;
    }

    public static int update(Class<?> clazz, String column, String value, String primarykey) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        return update(clazz, cv, primarykey);
    }

    public static int update(Class<?> clazz, ContentValues params, String primarykey) {
        DBInfo info = new DBInfo(clazz);
        List<SQLParameter> parameters = new ArrayList<>();
        Set<String> columns = params.keySet();
        StringBuffer sql = new StringBuffer("update " + getTableName(clazz) + " set ");
        int index = 1;
        for (String key : columns) {
            parameters.add(new SQLParameter(index, params.get(key)));
            sql.append(" " + key + "=?,");
            index++;
        }
        if (sql.toString().endsWith(","))
            sql.setLength(sql.length() - 1);
        sql.append(" where " + info.primaryKey + "=?");
        parameters.add(new SQLParameter(index, primarykey));
        return update(clazz, sql.toString(), parameters.toArray(new SQLParameter[]{}));
    }

    public static int update(Class<?> clazz, String sql, SQLParameter... params) {
        createOrUpdateTable(clazz);
        SQLiteStatement statement = builderSQL(sqlLiteOpenHelper.getWritableDatabase(), sql, StringUtils.asList(params), true);
        int result = statement.executeUpdateDelete();
        statement.clearBindings();
        statement.close();
        return result;
    }

    public static <T> int insert(T model) {
        return insert(StringUtils.asList(model));
    }

    public static <T> int insert(List<T> list) {
        if (StringUtils.isNullOrEmpty(list))
            return 0;
        createOrUpdateTable(list.get(0).getClass());
        DBInfo info = new DBInfo(list.get(0).getClass());
        List<SQLParameter> parameters = new ArrayList<>();
        boolean isIntPrimaryKey = ClassUtils.isNumber(info.primaryKeyField.getType());
        StringBuffer sql = new StringBuffer("insert into " + getTableName(info.clazz) + " ( ");
        StringBuffer values = new StringBuffer();
        int index = 1;
        for (Field field : info.allFields) {
            if (field == info.primaryKeyField && isIntPrimaryKey) {
                continue;
            }
            sql.append(" " + field.getName() + ",");
            values.append("?,");
            parameters.add(new SQLParameter(index, field.getName(), null));
            index++;
        }
        if (sql.toString().endsWith(",")) {
            sql.setLength(sql.length() - 1);
            values.setLength(values.length() - 1);
        }
        sql.append(" ) values (" + values.toString() + ")");
        int result = 0;
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        SQLiteStatement statement = builderSQL(database, sql.toString(), parameters, false);
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            statement.clearBindings();
            for (SQLParameter param : parameters) {
                try {
                    param.setValue(ClassUtils.getValue(t, param.getColumnName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bindData(statement, parameters);
            long r = statement.executeInsert();
            if (isIntPrimaryKey) {
                try {
                    Object idValue = ClassUtils.getValue(t, info.primaryKey);
                    if (StringUtils.isNullOrEmpty(idValue)) {
                        ClassUtils.setValue(t, info.primaryKey, r);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result += r;
        }
        statement.clearBindings();
        statement.close();
        database.setTransactionSuccessful();
        database.endTransaction();
        return result;
    }

    public static long insert(Class<?> clazz, ContentValues params) {
        createOrUpdateTable(clazz);
        List<SQLParameter> parameters = new ArrayList<>();
        Set<String> columns = params.keySet();
        StringBuffer sql = new StringBuffer("insert into  " + getTableName(clazz) + " ( ");
        StringBuffer values = new StringBuffer();
        int index = 1;
        for (String key : columns) {
            parameters.add(new SQLParameter(index, params.get(key)));
            sql.append(" " + key + ",");
            values.append("?,");
            index++;
        }
        if (sql.toString().endsWith(",")) {
            sql.setLength(sql.length() - 1);
            sql.setLength(sql.length() - 1);
        }
        sql.append(" ) values ( " + values.toString() + ") ");
        return insert(clazz, sql.toString(), parameters.toArray(new SQLParameter[]{}));
    }

    public static long insert(Class<?> clazz, String sql, SQLParameter... params) {
        createOrUpdateTable(clazz);
        SQLiteStatement statement = builderSQL(sqlLiteOpenHelper.getWritableDatabase(), sql, StringUtils.asList(params), true);
        long result = statement.executeInsert();
        statement.clearBindings();
        statement.close();
        return result;
    }


    public static <T> int saveOrUpdate(T model) {
        List<T> list = new ArrayList<>();
        list.add(model);
        return saveOrUpdate(list);
    }

    public static <T> int saveOrUpdate(List<T> list) {
        if (StringUtils.isNullOrEmpty(list))
            return 0;
        createOrUpdateTable(list.get(0).getClass());
        DBInfo info = new DBInfo(list.get(0).getClass());
        StringBuffer sql = new StringBuffer("insert or replace into  " + getTableName(info.clazz) + "(");
        StringBuffer paramsString = new StringBuffer();
        for (Field fieldName : info.allFields) {
            sql.append(fieldName.getName() + ",");
            paramsString.append("?,");
        }
        if (paramsString.toString().endsWith(",")) {
            sql.setLength(sql.length() - 1);
            paramsString.setLength(paramsString.length() - 1);
        }
        sql.append(") values(" + paramsString.toString() + ")");
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        SQLiteStatement statement = database.compileStatement(sql.toString());
        boolean isUpdatePrimaryKey = ClassUtils.isInt(info.primaryKeyField.getType());
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            statement.clearBindings();
            Object val = null;
            int index = 1;
            for (Field property : info.allFields) {
                try {
                    val = ClassUtils.getValue(t, property.getName());
                    bind(statement, index, val, property.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                index++;
            }
            long rowId = statement.executeInsert();
            if (isUpdatePrimaryKey) {
                try {
                    ClassUtils.setValue(t, info.primaryKey, rowId);
                } catch (Exception e) {
                }
            }
        }
        statement.clearBindings();
        statement.close();
        database.setTransactionSuccessful();
        database.endTransaction();
        return list.size();
    }


    public static <T> int delete(Class<?> clazz, int id) {
        return delete(clazz, id + "");
    }

    public static <T> int delete(Class<?> clazz, String primaryKey) {
        createOrUpdateTable(clazz);
        DBInfo info = new DBInfo(clazz);
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        return database.delete(ClassUtils.getPackageNameAndClassName(info.clazz).replaceAll("\\.", "_"), info.primaryKey + "=?", new String[]{primaryKey});
    }

    public static <T> int delete(T model) {
        return delete(StringUtils.asList(model));
    }

    public static <T> int delete(List<T> list) {
        if (StringUtils.isNullOrEmpty(list))
            return 0;
        createOrUpdateTable(list.get(0).getClass());
        DBInfo info = new DBInfo(list.get(0).getClass());
        List<SQLParameter> parameters = new ArrayList<>();
        StringBuffer where = new StringBuffer(info.primaryKey + " in (");
        int index = 1;
        for (T t : list) {
            try {
                parameters.add(new SQLParameter(index, ClassUtils.getValue(t, info.primaryKey)));
                where.append("?,");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            index++;
        }
        if (where.length() > 5)
            where.setLength(where.length() - 1);
        where.append(")");
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        SQLiteStatement statement = builderSQL(database, "delete from " + getTableName(info.clazz) + " where " + where.toString(), parameters, true);
        int result = statement.executeUpdateDelete();
        statement.clearBindings();
        statement.close();
        return result;
    }

    public static <T> T select(Class<?> clazz, int id) {
        return select(clazz, id + "");
    }

    public static <T> T select(Class<?> clazz, String primaryKey) {
        DBInfo info = new DBInfo(clazz);
        return select(clazz, "select * from " + getTableName(clazz) + " where " + info.primaryKey + "=?", new SQLParameter(1, primaryKey));
    }

    public static <T> T select(Class<?> clazz, String sql, SQLParameter... args) {
        List<T> list = selectAll(clazz, sql, args);
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    public static Cursor select(String sql, SQLParameter... args) {
        SQLiteDatabase database = sqlLiteOpenHelper.getReadableDatabase();
        String[] params = new String[StringUtils.len(args.length)];
        if (!StringUtils.isNullOrEmpty(args)) {
            for (SQLParameter parameter : args) {
                params[parameter.getIndex() - 1] = (String) parameter.getValue();
            }
        }
        return database.rawQuery(sql, params);
    }

    public static int count(Class<?> clazz) {
        createOrUpdateTable(clazz);
        SQLiteDatabase database = sqlLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select count(*) from " + getTableName(clazz), new String[]{});
        if (cursor.getColumnCount() > 0) {
            if (cursor.moveToNext()) {
                int val = cursor.getInt(0);
                IOUtils.close(cursor);
                return val;
            }
        }
        return -1;
    }

    public static String first(String sql, String... args) {
        SQLiteDatabase database = sqlLiteOpenHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(sql, null == args ? new String[]{} : args);
        if (cursor.getColumnCount() > 0) {
            if (cursor.moveToNext()) {
                String val = cursor.getString(0);
                IOUtils.close(cursor);
                return val;
            }
        }
        return null;
    }


    public static <T> List<T> selectAll(Class<?> clazz) {
        return selectAll(clazz, "select * from " + getTableName(clazz));
    }

    /**
     * @param clazz
     * @param sql   sql 语句，DBUtils.getTableName(Class clazz) 获取
     * @param <T>
     * @return
     */
    public static <T> List<T> selectAll(Class<?> clazz, String sql) {
        return selectAll(clazz, sql, null);
    }

    /**
     * @param clazz
     * @param sql   sql 语句，DBUtils.getTableName(Class clazz) 获取
     * @param args
     * @param <T>
     * @return
     */
    public static <T> List<T> selectAll(Class<?> clazz, String sql, SQLParameter... args) {
        createOrUpdateTable(clazz);
        DBInfo info = new DBInfo(clazz);
        List<T> list = new ArrayList<>();
        SQLiteDatabase database = sqlLiteOpenHelper.getReadableDatabase();
        database.acquireReference();
        Cursor cursor = null;
        try {
            String[] params = null;
            if (StringUtils.isNullOrEmpty(args))
                params = new String[]{};
            else {
                params = new String[StringUtils.len(args.length)];
                if (!StringUtils.isNullOrEmpty(args)) {
                    for (SQLParameter parameter : args) {
                        params[parameter.getIndex() - 1] = (String) parameter.getValue();
                    }
                }
            }
            cursor = database.rawQuery(sql, params);
            List<String> columnNames = StringUtils.asList(cursor.getColumnNames());
            List<Field> fields = info.allFields;
            List<Field> removeFields = new ArrayList<>();
            for (Field f : fields) {
                if (!columnNames.contains(f.getName())) {
                    removeFields.add(f);
                }
            }
            fields.removeAll(removeFields);
            removeFields.clear();
            while (cursor.moveToNext()) {
                Object instance = clazz.newInstance();
                for (Field f : fields) {
                    int index = cursor.getColumnIndex(f.getName());
                    if (index > -1) {
                        ClassUtils.setValue(instance, f.getName(), getCursorValue(cursor, index, f.getType()));
                    }
                }
                list.add((T) instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(cursor);
            database.releaseReference();
        }
        return list;
    }


    public static SQLiteDatabase getReadableDatabase(){
        return sqlLiteOpenHelper.getReadableDatabase();
    }
    public static SQLiteDatabase getWritableDatabase(){
        return sqlLiteOpenHelper.getWritableDatabase();
    }


    public static String getTableName(Class<?> clazz) {
        return ClassUtils.getPackageNameAndClassName(clazz).replaceAll("\\.", "_");
    }


    public static SQLParameter param(int index, Object val) {
        return new SQLParameter(index, val);
    }

    public static SQLParameter[] param(int index, Object val, int index2, Object val2) {
        SQLParameter[] params = new SQLParameter[2];
        params[0] = new SQLParameter(index, val);
        params[1] = new SQLParameter(index2, val2);
        return params;
    }

    public static SQLParameter[] param(int index, Object val, int index2, Object val2, int index3, Object val3) {
        SQLParameter[] params = new SQLParameter[3];
        params[0] = new SQLParameter(index, val);
        params[1] = new SQLParameter(index2, val2);
        params[2] = new SQLParameter(index3, val3);
        return params;
    }

    public static SQLParameter[] param(int index, Object val, int index2, Object val2, int index3, Object val3, int index4, Object val4) {
        SQLParameter[] params = new SQLParameter[2];
        params[0] = new SQLParameter(index, val);
        params[1] = new SQLParameter(index2, val2);
        params[2] = new SQLParameter(index3, val3);
        params[3] = new SQLParameter(index4, val4);
        return params;
    }

    public static SQLParameter[] param(int index, Object val, int index2, Object val2, int index3, Object val3, int index4, Object val4, int index5, Object val5) {
        SQLParameter[] params = new SQLParameter[2];
        params[0] = new SQLParameter(index, val);
        params[1] = new SQLParameter(index2, val2);
        params[2] = new SQLParameter(index3, val3);
        params[3] = new SQLParameter(index4, val4);
        params[4] = new SQLParameter(index5, val5);
        return params;
    }


    private static SQLiteStatement builderSQL(SQLiteDatabase database, String sql, List<SQLParameter> params, boolean isBindData) {
        SQLiteStatement statement = database.compileStatement(sql);
        if (isBindData) {
            if (!StringUtils.isNullOrEmpty(params)) {
                for (SQLParameter parameter : params) {
                    bind(statement, parameter.getIndex(), parameter.getValue(), parameter.getClazz());
                }
            }
        }
        return statement;
    }

    private static void bindData(SQLiteStatement statement, List<SQLParameter> params) {
        if (!StringUtils.isNullOrEmpty(params)) {
            for (SQLParameter parameter : params) {
                bind(statement, parameter.getIndex(), parameter.getValue(), parameter.getClazz());
            }
        }
    }

    private static void bind(SQLiteStatement statement, int index, Object val, Class<?> valType) {
        if (null == val)
            statement.bindNull(index);
        else if (ClassUtils.isNumber(valType) || ClassUtils.isByte(valType)&& !valType.isArray()) {
            statement.bindLong(index, Long.parseLong(val.toString()));
        } else if (ClassUtils.isDecimal(valType)) {
            statement.bindDouble(index, Double.parseDouble(val.toString()));
        } else if (ClassUtils.isString(valType)) {
            statement.bindString(index, (String) val);
        } else if (ClassUtils.isByte(valType) && valType.isArray()) {
            statement.bindBlob(index, (byte[]) val);
        }
    }

    private static Object getCursorValue(Cursor cursor, int index, Class<?> type) {
        Object val = null;
        if (ClassUtils.isString(type))
            val = cursor.getString(index);
        else if (ClassUtils.isDecimal(type))
            val = cursor.getDouble(index);
        else if (ClassUtils.isFloat(type))
            val = cursor.getFloat(index);
        else if (ClassUtils.isInt(type))
            val = cursor.getInt(index);
        else if (ClassUtils.isLong(type))
            val = cursor.getLong(index);
        else if (ClassUtils.isShort(type))
            val = cursor.getShort(index);
        else if (ClassUtils.isByte(type) || ClassUtils.isSerializable(type))
            val = cursor.getBlob(index);
        return val;
    }


    private static class DBInfo {

        private Class clazz;
        private String table;
        private String primaryKey;
        private Field primaryKeyField;
        private List<Field> allFields;

        public DBInfo(Class clazz) {
            this.clazz = clazz;
            this.table = ClassUtils.getPackageNameAndClassName(clazz);

            List<Field> fields = StringUtils.asList(clazz.getFields());
            Field[] fieldList2 = clazz.getDeclaredFields();
            for (Field f : fieldList2) {
                if (!fields.contains(f)) {
                    fields.add(f);
                }
            }
            Iterator<Field> iterator = fields.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                if (null != field.getAnnotation(DBPrimaryKey.class)) {
                    primaryKeyField = field;
                    primaryKey = field.getName();
                    break;
                }
            }
            this.allFields = fields;
        }

        public Class getClazz() {
            return clazz;
        }

        public String getTable() {
            return table;
        }

        public String getPrimaryKey() {
            return primaryKey;
        }

        public List<Field> getAllFields() {
            return allFields;
        }
    }

    public static class SQLParameter {
        private int index;
        private String columnName;
        private Object value;

        private SQLParameter(int index, String columnName, Object value) {
            this.index = index;
            this.columnName = columnName;
            this.value = value;
        }

        /**
         * sql参数话
         *
         * @param index 参数下标，从1开始
         * @param value 参数值
         */
        public SQLParameter(int index, Object value) {
            this.index = index;
            this.value = value;
        }


        public int getIndex() {
            return index;
        }

        public String getColumnName() {
            return columnName;
        }

        public Object getValue() {
            return value;
        }

        public Class<?> getClazz() {
            if (null == value)
                return String.class;
            return value.getClass();
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
