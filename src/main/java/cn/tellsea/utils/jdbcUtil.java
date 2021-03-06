package cn.tellsea.utils;


/**
 *@class_name：jdbcutils
 *@comments:
 *@param:
 *@return:
 *@author:xx
 *@createtime:2019-8-28
 */


import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.mysql.cj.jdbc.Driver;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@PropertySource({"classpath:para.properties"})
@Component
final public class jdbcUtil {
    @Value("${mysql.ip}")
    public   String IP;//=PropertyUtil.getProperty("db3rd_ip","10.15.7.25");
    //数据库用户名
    @Value("${mysql.username}")
    public   String USERNAME;// = PropertyUtil.getProperty("db3rd_user","root");

    //数据库用户名
    @Value("${mysql.dbname}")
    public   String dbname;// = PropertyUtil.getProperty("db3rd_dbname","energy");

    //数据库密码
    @Value("${mysql.password}")
    public   String PASSWORD;//= PropertyUtil.getProperty("db3rd_pwd","root");
    //驱动信息
    @Value("${mysql.driver}")
    public  String DRIVER;// = "com.mysql.jdbc.Driver";
    //数据库地址
    public   String URL = "jdbc:mysql://"+IP+":3306/"+dbname;

    public  Connection connection;
    public  PreparedStatement pstmt;
    public  ResultSet resultSet;


    /**
     * 获得数据库的连接
     * @return
     */
    public  Connection getConnection(){
        try{
            //System.out.println(DRIVER);
            URL = "jdbc:mysql://"+IP+":3306/"+dbname+"?serverTimezone=GMT%2B8&useSSL=false&connectTimeout=3000&socketTimeout=60000";
            try {
                //connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                Class.forName(DRIVER);
                 connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                log.info("数据库连接成功！");
            } catch (SQLException e) {
                log.info(e.toString());
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }


        }catch(Exception e){
            log.info(e.toString());
            //e.printStackTrace();
        }

        return connection;
    }


    /**
     * 增加、删除、改
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public  boolean updateByPreparedStatement(String sql, List<Object>params)throws SQLException{
        boolean flag = false;
        int result = -1;
        pstmt = connection.prepareStatement(sql);
        int index = 1;
        if(params != null && !params.isEmpty()){
            for(int i=0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        result = pstmt.executeUpdate();
        flag = result > 0 ? true : false;
        return flag;
    }

    /**
     * 查询单条记录
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public  Map<String, Object> findSimpleResult(String sql, List<Object> params) throws SQLException{
        Map<String, Object> map = new HashMap<String, Object>();
        int index  = 1;
        pstmt = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i=0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();//返回查询结果
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col_len = metaData.getColumnCount();
        while(resultSet.next()){
            for(int i=0; i<col_len; i++ ){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
        }
        return map;
    }

    /**查询多条记录
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public  List<Map<String, Object>> findModeResult(String sql, List<Object> params) throws SQLException{
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int index = 1;
        pstmt = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i = 0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            Map<String, Object> map = new HashMap<String, Object>();
            for(int i=0; i<cols_len; i++){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
            list.add(map);
        }

        return list;
    }

    /**通过反射机制查询单条记录
     * @param sql
     * @param params
     * @param cls
     * @return
     * @throws Exception
     */
    public  <T> T findSimpleRefResult(String sql, List<Object> params,
                                            Class<T> cls )throws Exception{
        T resultObject = null;
        int index = 1;
        pstmt = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i = 0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData  = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            //通过反射机制创建一个实例
            resultObject = cls.newInstance();
            for(int i = 0; i<cols_len; i++){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                Field field = cls.getDeclaredField(cols_name);
                field.setAccessible(true); //打开javabean的访问权限
                field.set(resultObject, cols_value);
            }
        }
        return resultObject;

    }

    /**通过反射机制查询多条记录
     * @param sql
     * @param params
     * @param cls
     * @return
     * @throws Exception
     */
    public  <T> List<T> findMoreRefResult(String sql, List<Object> params,
                                                Class<T> cls )throws Exception {
        List<T> list = new ArrayList<T>();
        int index = 1;
        pstmt = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i = 0; i<params.size(); i++){
                pstmt.setObject(index++, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData  = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            //通过反射机制创建一个实例
            T resultObject = cls.newInstance();
            for(int i = 0; i<cols_len; i++){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                Field field = cls.getDeclaredField(cols_name);
                field.setAccessible(true); //打开javabean的访问权限
                field.set(resultObject, cols_value);
            }
            list.add(resultObject);
        }
        return list;
    }

    /**
     * 释放数据库连接
     */
    public  void releaseConn() throws SQLException{
        if (!connection.isClosed())
        {
            try{
                connection.close();
            }catch(SQLException e){
                log.info("1");
                e.printStackTrace();
            }
        }
        if(resultSet != null){
            try{
                resultSet.close();
            }catch(SQLException e){
                log.info("2");
                e.printStackTrace();
            }
        }
    }

}