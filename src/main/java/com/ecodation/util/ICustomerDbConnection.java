package com.ecodation.util;

import java.sql.Connection;

public interface ICustomerDbConnection<T> {

    default Connection getCustomerIConnection(){

        DatabaseConnectionDb connectionDb = new DatabaseConnectionDb();
        return connectionDb.getConnection();

    }

    public void createCustomer(T t);
    public void loginMailControl(T t);
    public void passwordControl(T t);
    public void balanceControl(T t);
    public void deposit(T t);
    public void withdraw(T t);
    public void transferMailControl(T t);
    public void transfer(T t);
    public void blocked(T t);

}
