package com.ecodation.dao;

import com.ecodation.application.ApplicationInterface;
import com.ecodation.dto.CustomerDto;
import com.ecodation.util.ICustomerDbConnection;
import com.ecodation.util.NowDateUtil;
import lombok.extern.java.Log;
import org.springframework.integration.json.JsonToObjectTransformer;

import java.sql.*;
import java.util.Scanner;

@Log

public class CustomerDao implements ICustomerDbConnection<CustomerDto> {

    Scanner scannerStr = new Scanner(System.in);
    Scanner scannerInt = new Scanner(System.in);
    int nmbr=100;
    EmployeeDao employeeDao = new EmployeeDao();


    @Override
    public void createCustomer(CustomerDto customerDto) {
        try(Connection connection = getCustomerIConnection()){
            connection.setAutoCommit(false);
            String sql = "insert into customer(customerName,customerSurname,customerMail,customerPassword) values (?,?,?,?)";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, customerDto.getCustomerName());
            preparedStatement.setString(2, customerDto.getCustomerSurname());
            preparedStatement.setString(3, customerDto.getCustomerEmailAdress());
            preparedStatement.setString(4, customerDto.getCustomerPassword());
            int rowEffect = preparedStatement.executeUpdate();

            if(rowEffect>0) {
                log.info(CustomerDto.class+" Added");
                System.out.println("Your account has been successfully created. Please press \"0\" for menu");
                connection.commit();
                while(nmbr!=0) {
                    nmbr=scannerInt.nextInt();
                    if (nmbr == 0) {
                        ApplicationInterface applicationInterface =new ApplicationInterface();
                        applicationInterface.main();
                        break;
                    } else {
                        System.out.println("Please press \" 0 \" for menu");
                    }
                }
            }else {
                log.warning(CustomerDto.class+" Failed"+ NowDateUtil.DateUtil());
                System.out.println("Try again, later.");
                connection.rollback();
                connection.setAutoCommit(true);
                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.main();
            }
        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }

    }

    @Override
    public void loginMailControl(CustomerDto customerDto) {
        if(customerDto.getCustomerEmailAdress() == "0"){
            ApplicationInterface applicationInterface =new ApplicationInterface();
            applicationInterface.main();
        }
        try(Connection connection = getCustomerIConnection()){

            String sql = "select * from customer where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, customerDto.getCustomerEmailAdress());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                passwordControl(customerDto);
            }else {
                System.err.println("Customer could not found.");
                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.main();
            }

        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }

    }

    @Override
    public void passwordControl(CustomerDto customerDto) {
        try(Connection connection = getCustomerIConnection()){

            for(int i = 3; i >= 0; i--){

                if(i>0){
                    System.out.print("Password (Rights:"+i+"): ");
                    customerDto.setCustomerPassword(scannerStr.nextLine());

                    String sql = "select * from customer where customerMail=? and customerPassword=?";
                    PreparedStatement preparedStatement=connection.prepareStatement(sql);
                    preparedStatement.setString(1, customerDto.getCustomerEmailAdress());
                    preparedStatement.setString(2, customerDto.getCustomerPassword());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()){
                        customerDto.setCustomerBalance(resultSet.getLong("customerBalance"));
                        log.info("Login successful.");
                        customerDto.setCustomerName(resultSet.getString("customerName"));
                        customerDto.setCustomerSurname(resultSet.getString("customerSurname"));
                        customerDto.setCustomerEmailAdress(resultSet.getString("customerMail"));
                        customerDto.setCustomerBalance(resultSet.getLong("customerBalance"));

                        //transaction log
                        employeeDao.addStt(customerDto.getCustomerEmailAdress(), "Successful Login",0,0);

                        ApplicationInterface applicationInterface =new ApplicationInterface();
                        applicationInterface.customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());
                    }else{
                        System.out.println("WARNING");
                    }
                }else{
                    blocked(customerDto);
                    break;
                }
            }


        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }



    @Override
    public void deposit(CustomerDto customerDto) {
        if (customerDto.getAmount() == 0){
            ApplicationInterface applicationInterface =new ApplicationInterface();
            applicationInterface.customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());
        }
        try(Connection connection = getCustomerIConnection()){
            connection.setAutoCommit(false);
            String sql = "update customer set customerBalance=? where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setLong(1,customerDto.getAmount()+customerDto.getCustomerBalance());
            preparedStatement.setString(2, customerDto.getCustomerEmailAdress());

            int rowEffect = preparedStatement.executeUpdate();

            if(rowEffect>0) {
                connection.commit();
                System.out.println("Deposit successful.");

                //transaction log
                employeeDao.addStt(customerDto.getCustomerEmailAdress(), "Deposit",customerDto.getAmount(),customerDto.getAmount()+customerDto.getCustomerBalance());

                customerDto.setCustomerBalance(customerDto.getAmount()+customerDto.getCustomerBalance());
                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());
            }else {
                System.err.println("Failed. Try again, later.");
                connection.rollback();
                connection.setAutoCommit(true);
            }

        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }
    @Override
    public void balanceControl(CustomerDto customerDto) {
        if (customerDto.getAmount() == 0){
            ApplicationInterface applicationInterface =new ApplicationInterface();
            applicationInterface.customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());
        }
        if(customerDto.getCustomerBalance()> customerDto.getAmount()){
            ApplicationInterface applicationInterface =new ApplicationInterface();
            applicationInterface.withdrawTransfer(customerDto.getCustomerBalance(),customerDto.getAmount(),customerDto.getCustomerEmailAdress());
        }else{
            System.err.println("Balance is not enough");
            ApplicationInterface applicationInterface =new ApplicationInterface();
            applicationInterface.customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());
        }
    }
    @Override
    public void withdraw(CustomerDto customerDto) {
        try(Connection connection = getCustomerIConnection()){
            connection.setAutoCommit(false);
            String sql = "update customer set customerBalance=? where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setLong(1, customerDto.getCustomerBalance()-customerDto.getAmount());
            preparedStatement.setString(2, customerDto.getCustomerEmailAdress());

            int rowEffect = preparedStatement.executeUpdate();

            if(rowEffect>0) {
                connection.commit();
                System.out.println("Withdraw successful.");

                //transaction log
                employeeDao.addStt(customerDto.getCustomerEmailAdress(), "Withdraw", customerDto.getAmount(), customerDto.getCustomerBalance()-customerDto.getAmount());

                customerDto.setCustomerBalance(customerDto.getCustomerBalance()-customerDto.getAmount());
                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());

            }else {
                System.err.println("Failed. Try again, later.");
                connection.rollback();
                connection.setAutoCommit(true);
            }

        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

    @Override
    public void transferMailControl(CustomerDto customerDto) {
        try(Connection connection = getCustomerIConnection()){

            String sql = "select * from customer where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, customerDto.getRecipientMail());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                customerDto.setRecipientBalance(resultSet.getLong("customerBalance"));
                transfer(customerDto);
            }else {
                System.err.println("Transfer mail is wrong.");
                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.withdrawTransfer(customerDto.getCustomerBalance(), customerDto.getAmount(), customerDto.getCustomerEmailAdress());
            }

        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

    @Override
    public void transfer(CustomerDto customerDto) {
        try(Connection connection = getCustomerIConnection()){
            connection.setAutoCommit(false);
            String sql = "update customer set customerBalance=? where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setLong(1, customerDto.getCustomerBalance()-customerDto.getAmount());
            preparedStatement.setString(2, customerDto.getCustomerEmailAdress());

            int rowEffect = preparedStatement.executeUpdate();
            String query = "update customer set customerBalance=? where customerMail=?";
            PreparedStatement preparedStatement2=connection.prepareStatement(query);
            preparedStatement2.setLong(1, customerDto.getRecipientBalance()+customerDto.getAmount());
            preparedStatement2.setString(2, customerDto.getRecipientMail());

            int rowEffect2 = preparedStatement2.executeUpdate();

            if(rowEffect>0 && rowEffect2>0) {
                connection.commit();
                System.out.println("Transfer successful");

                //transaction log
                employeeDao.addStt(customerDto.getCustomerEmailAdress(), "Outgoing Transfer to "+customerDto.getRecipientMail(), customerDto.getAmount(),customerDto.getCustomerBalance()-customerDto.getAmount());
                employeeDao.addStt(customerDto.getRecipientMail(), "Incoming Transfer by "+customerDto.getCustomerEmailAdress(), customerDto.getAmount(), customerDto.getRecipientBalance()+customerDto.getAmount());

                customerDto.setCustomerBalance(customerDto.getCustomerBalance()-customerDto.getAmount());
                customerDto.setRecipientBalance(customerDto.getRecipientBalance()+customerDto.getAmount());
                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());

            }else {
                System.err.println("Failed. Try again, later.");
                connection.rollback();
                connection.setAutoCommit(true);

                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.withdrawTransfer(customerDto.getCustomerBalance(), customerDto.getAmount(), customerDto.getCustomerEmailAdress());
            }

        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

    @Override
    public void blocked(CustomerDto customerDto) {
        try(Connection connection = getCustomerIConnection()){
            connection.setAutoCommit(false);
            String sql = "insert into disabled (customerId,customerName,customerSurname,customerMail,customerPassword,customerBalance,creationDate) " +
                    "select customerId,customerName,customerSurname,customerMail,customerPassword,customerBalance,creationDate from customer " +
                    "where customerMail=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, customerDto.getCustomerEmailAdress());

            String query ="delete from customer where customerMail=?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query);
            preparedStatement1.setString(1, customerDto.getCustomerEmailAdress());

            int rowEffect = preparedStatement.executeUpdate();
            int rowEffect1 = preparedStatement1.executeUpdate();

            if(rowEffect>0 && rowEffect1>0) {
                System.out.println("Blocked. Reason: Wrong Password! Contact us...");
                connection.commit();

                //transaction log
                employeeDao.addStt(customerDto.getCustomerEmailAdress(), "Blocked due to wrong password",0,0);

                ApplicationInterface applicationInterface =new ApplicationInterface();
                applicationInterface.main();
            }else {
                System.err.println("Failed. Try again, later.");
                connection.rollback();
                connection.setAutoCommit(true);
            }

        }catch (SQLException e) {
            e.printStackTrace();
            log.warning(CustomerDto.class+" Failed"+NowDateUtil.DateUtil());
        }
    }

}
