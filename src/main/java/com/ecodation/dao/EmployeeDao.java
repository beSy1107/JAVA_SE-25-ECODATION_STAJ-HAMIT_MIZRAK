package com.ecodation.dao;



import com.ecodation.application.ApplicationInterface;
import com.ecodation.dto.CustomerDto;
import com.ecodation.dto.EmployeeDto;
import com.ecodation.util.IEmployeeDbConnection;
import com.ecodation.util.NowDateUtil;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

@Log

public class EmployeeDao implements IEmployeeDbConnection<EmployeeDto> {

    Scanner scannerStr = new Scanner(System.in);
    Scanner scannerInt = new Scanner(System.in);


    @Override
    public void employeeMailControl(EmployeeDto employeeDto) {

        if(employeeDto.getEmployeeEmailAdress() == "0"){
            ApplicationInterface applicationInterface = new ApplicationInterface();
            applicationInterface.main();
        }
        try(Connection connection = getEmployeeIConnection()){
            String sql = "select * from employee where employeeMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, employeeDto.getEmployeeEmailAdress());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                employeePasswordControl(employeeDto);
            }else {
                System.err.println("Employee could not found.");
                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.main();
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+ NowDateUtil.DateUtil());
        }
    }

    @Override
    public void employeePasswordControl(EmployeeDto employeeDto) {
        try(Connection connection = getEmployeeIConnection()){
            for(int i = 3; i >= 0; i--){
                if(i == 0){
                    ApplicationInterface applicationInterface = new ApplicationInterface();
                    applicationInterface.main();
                }
                System.out.print("Password (Rights:"+i+"): ");
                employeeDto.setEmployeePassword(scannerStr.nextLine());

                String sql = "select * from employee where employeeMail=? and employeePassword=?";
                PreparedStatement preparedStatement=connection.prepareStatement(sql);
                preparedStatement.setString(1, employeeDto.getEmployeeEmailAdress());
                preparedStatement.setString(2, employeeDto.getEmployeePassword());
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    log.info("Login successful.");

                    //transaction log
                    addStt(employeeDto.getEmployeeEmailAdress(),"Successful Login",0,0);

                    ApplicationInterface applicationInterface = new ApplicationInterface();
                    applicationInterface.adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
                    break;
                }else{
                    System.out.println("WARNING");
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

    @Override
    public void customerControl(EmployeeDto employeeDto) {
        if(employeeDto.getCustomerEmailAdress() == "0"){
            ApplicationInterface applicationInterface = new ApplicationInterface();
            applicationInterface.adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
        }
        try(Connection connection = getEmployeeIConnection()){
            //Two queries created for active/passive status of the customer
            String sql = "select * from customer where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, employeeDto.getCustomerEmailAdress());
            ResultSet resultSet = preparedStatement.executeQuery();

            String sql2 = "select * from disabled where customerMail=?";
            PreparedStatement preparedStatement2=connection.prepareStatement(sql2);
            preparedStatement2.setString(1, employeeDto.getCustomerEmailAdress());
            ResultSet resultSet2 = preparedStatement2.executeQuery();

            if(resultSet.next()) {
                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminCOperations1(employeeDto.getCustomerEmailAdress(),employeeDto.getEmployeeEmailAdress());
            }else if(resultSet2.next()){
                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminCOperations2(employeeDto.getCustomerEmailAdress(),employeeDto.getEmployeeEmailAdress());
            }else {
                System.err.println("Customer mail is wrong.");
                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

    @Override
    public void disableCustomer(EmployeeDto employeeDto) {
        try(Connection connection = getEmployeeIConnection()){
            connection.setAutoCommit(false);
            System.out.println(employeeDto.getCustomerEmailAdress()+" "+employeeDto.getEmployeeEmailAdress());
            String sql = "insert into disabled (customerId,customerName,customerSurname,customerMail,customerPassword,customerBalance,creationDate) " +
                    "select customerId,customerName,customerSurname,customerMail,customerPassword,customerBalance,creationDate from customer " +
                    "where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, employeeDto.getCustomerEmailAdress());
            int rowEffect = preparedStatement.executeUpdate();

            String query ="delete from customer where customerMail=?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query);
            preparedStatement1.setString(1, employeeDto.getCustomerEmailAdress());
            int rowEffect1 = preparedStatement1.executeUpdate();

            if(rowEffect>0 && rowEffect1>0) {
                System.out.println(employeeDto.getCustomerEmailAdress()+" disabled successfuly.");
                connection.commit();

                //transaction log
                addStt(employeeDto.getEmployeeEmailAdress(), employeeDto.getCustomerEmailAdress()+": Customer Disabled",0,0);

                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
            }else {
                System.err.println("Failed. Try again, later.");
                connection.rollback();
                connection.setAutoCommit(true);
                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminCOperations1(employeeDto.getCustomerEmailAdress(),employeeDto.getEmployeeEmailAdress());
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

    @Override
    public void enableCustomer(EmployeeDto employeeDto) {
        try(Connection connection = getEmployeeIConnection()){
            connection.setAutoCommit(false);
            String sql = "insert into customer (customerId,customerName,customerSurname,customerMail,customerPassword,customerBalance,creationDate) " +
                    "select customerId,customerName,customerSurname,customerMail,customerPassword,customerBalance,creationDate from disabled " +
                    "where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, employeeDto.getCustomerEmailAdress());
            int rowEffect = preparedStatement.executeUpdate();

            String query ="delete from disabled where customerMail=?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query);
            preparedStatement1.setString(1, employeeDto.getCustomerEmailAdress());
            int rowEffect1 = preparedStatement1.executeUpdate();

            if(rowEffect>0 && rowEffect1>0) {
                System.out.println(employeeDto.getCustomerEmailAdress()+" enabled successfuly.");
                connection.commit();

                //transaction log
                addStt(employeeDto.getEmployeeEmailAdress(), employeeDto.getCustomerEmailAdress()+": Cuustomer Enabled",0,0);

                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
            }else {
                System.err.println("Failed. Try again, later.");
                connection.rollback();
                connection.setAutoCommit(true);
                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminCOperations2(employeeDto.getCustomerEmailAdress(),employeeDto.getEmployeeEmailAdress());
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

    @Override
    public void statement(EmployeeDto employeeDto) {
        try(Connection connection = getEmployeeIConnection()){
            String sql = "Select * from statement where customerMail=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, employeeDto.getCustomerEmailAdress());
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("*******  "+employeeDto.getCustomerEmailAdress()+" - Customer' Statements  *******\n");
            while(resultSet.next()){
                System.out.println("Id:"+resultSet.getLong("statementId")+
                        "   //   Customer Mail:"+resultSet.getString("customerMail")+
                        "   //   Process:"+resultSet.getString("process")+
                        "   //   Amount:"+resultSet.getLong("amount")+
                        "   //   Balance:"+resultSet.getLong("balance")+
                        "   //   Date:"+resultSet.getDate("date"));
            }

            //transaction log
            addStt(employeeDto.getEmployeeEmailAdress(),employeeDto.getCustomerEmailAdress()+": customer' statement viewed.",0,0);

            System.out.println("Press 9 to return to the menu");
            int key=scannerInt.nextInt();
            while(key != 9){
                System.out.println("Wrong key. Please, press 9 to return to the menu");
                key=scannerInt.nextInt();
                if(key == 9){
                    break;
                }
            }
            if(key == 9) {
                ApplicationInterface applicationInterface = new ApplicationInterface();
                applicationInterface.adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
         }
    }

    public void addStt(String mail,String process, long amount, long balance){
        try(Connection connection = getEmployeeIConnection()){
            connection.setAutoCommit(false);
            String sql = "insert into statement(customerMail,process,amount,balance) values (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, mail);
            preparedStatement.setString(2, process);
            preparedStatement.setLong(3, amount);
            preparedStatement.setLong(4, balance);
            int rowEffect = preparedStatement.executeUpdate();
            if(rowEffect>0){
                connection.commit();
                log.info("Statement added.");
            }else{
                connection.rollback();
                connection.setAutoCommit(true);
                log.info("Statement error.");
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }
}
