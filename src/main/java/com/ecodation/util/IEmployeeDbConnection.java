package com.ecodation.util;

import java.sql.Connection;
import java.util.ArrayList;

public interface IEmployeeDbConnection<K> {

    default Connection getEmployeeIConnection(){

        DatabaseConnectionDb connectionDb = new DatabaseConnectionDb();
        return connectionDb.getConnection();

    }

    public void employeeMailControl(K k);
    public void employeePasswordControl(K k);
    public void customerControl(K k);
    public void disableCustomer(K k);
    public void enableCustomer(K k);
    public void statement(K k);

}
