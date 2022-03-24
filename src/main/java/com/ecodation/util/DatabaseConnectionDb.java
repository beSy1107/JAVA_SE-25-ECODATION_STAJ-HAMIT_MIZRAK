package com.ecodation.util;

import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log

public class DatabaseConnectionDb {

    private String url="jdbc:mysql://localhost:3306/bas?useUnicode=yes&characterEncoding=UTF-8";
    private String user="root";
    private String password="";

    private Connection connection;

    public Connection getConnection(){

        try{
            if(this.connection == null || this.connection.isClosed()){
                Class.forName("com.mysql.jdbc.Driver");
                log.info("Driver yüklendi");
                connection= DriverManager.getConnection(url, user, password);
                log.info("Bağlantı kuruldu");
            }else{
                return connection;
            }
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            log.warning("Warning! "+DatabaseConnectionDb.class+" "+NowDateUtil.DateUtil());
        }

        return connection;
    }

}
