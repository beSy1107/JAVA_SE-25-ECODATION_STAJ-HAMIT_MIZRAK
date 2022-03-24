package com.ecodation.application;

import com.ecodation.dao.CustomerDao;
import com.ecodation.dao.EmployeeDao;
import com.ecodation.dto.CustomerDto;
import com.ecodation.dto.EmployeeDto;

import java.util.ArrayList;
import java.util.Scanner;

public class ApplicationInterface {
    Scanner scannerStr = new Scanner(System.in); // for string data
    Scanner scannerInt = new Scanner(System.in); // for integer data
    int nmbr; //Process number
    CustomerDto customerDto = new CustomerDto();
    CustomerDao customerDao = new CustomerDao();
    EmployeeDao employeeDao = new EmployeeDao();
    EmployeeDto employeeDto = new EmployeeDto();

    public void main(){
        System.out.print("***********  WELCOME TO ECOBANK  **********\n\n" +
                "1. Register\n" +
                "2. Customer Login\n" +
                "3. Employee Login\n" +
                "0. Quit\n" +
                "Please select process: ");

        nmbr=scannerInt.nextInt();

        switch (nmbr){
            case 1:
                register();
                break;
            case 2:
                customerLogin();
                break;
            case 3:
                employeeLogin();
                break;
            case 0:

                break;
        }
    }

    // --------------- EMPLOYEE INTERFACES ---------------

    public void employeeLogin(){
        System.out.print("***********  EMPLOYEE LOGIN -Ecobank  **********\n\n");
        System.out.print("<< Back (\"Press 0\")\n" +
                "Mail Adress: ");
        employeeDto.setEmployeeEmailAdress(scannerStr.nextLine());
        employeeDao.employeeMailControl(employeeDto);
    }
    public void adminMenu(String name, String surname, String mail){
        //Synchronization of the information held by the objects created in EmployeeDao and ApplicationInterface.
        employeeDto.setEmployeeName(name);
        employeeDto.setEmployeeSurname(surname);
        employeeDto.setEmployeeEmailAdress(mail);

        System.out.print("***********  EMPLOYEE MENU -Ecobank  **********\n\n" +
                "1. Customer Operations\n" +
                "0. Quit\n" +
                "Please select process: ");
        nmbr = scannerInt.nextInt();

        switch (nmbr){
            case 1:
                System.out.print("<< Back (\"Press 0\")\n" +
                        "Please enter the customer mail: ");
                employeeDto.setCustomerEmailAdress(scannerStr.nextLine());
                employeeDao.customerControl(employeeDto);
                break;
            case 0:
                main();
                break;
        }
    }
    //Customer operations panel for active customers
    public void adminCOperations1(String mail, String email){
        //Synchronization of the information held by the objects created in EmployeeDao and ApplicationInterface.
        employeeDto.setCustomerEmailAdress(mail);
        employeeDto.setEmployeeEmailAdress(email);

        System.out.print("***********  CUSTOMER OPERATIONS FOR EMPLOYEES -Ecobank  **********\n\n" +
                "1. Disable customer\n" +
                "2. Show customer statement\n" +
                "9. Back\n" +
                "0. Quit\n" +
                "Please select process: ");
        nmbr = scannerInt.nextInt();

        switch (nmbr){
            case 1:
                employeeDao.disableCustomer(employeeDto);
                break;
            case 2:
                employeeDao.statement(employeeDto);
                break;
            case 9:
                adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
                break;
            case 0:
                main();
                break;
        }
    }
    //Customer operations panel for passive customers
    public void adminCOperations2(String mail, String email){
        //Synchronization of the information held by the objects created in EmployeeDao and ApplicationInterface.
        employeeDto.setCustomerEmailAdress(mail);
        employeeDto.setEmployeeEmailAdress(email);

        System.out.print("***********  CUSTOMER OPERATIONS FOR EMPLOYEES -Ecobank  **********\n\n" +
                "1. Enable customer\n" +
                "2. Show customer statement\n" +
                "9. Back\n" +
                "0. Quit\n" +
                "Please select process: ");
        nmbr = scannerInt.nextInt();

        switch (nmbr){
            case 1:
                employeeDao.enableCustomer(employeeDto);
                break;
            case 2:
                employeeDao.statement(employeeDto);
                break;
            case 9:
                adminMenu(employeeDto.getEmployeeName(), employeeDto.getEmployeeSurname(), employeeDto.getEmployeeEmailAdress());
                break;
            case 0:
                main();
                break;
        }
    }

    // --------------- CUSTOMER INTERFACES ---------------

    public void register(){
        System.out.print("***********  REGISTER -Ecobank  **********\n\n");
        System.out.print("Name: ");
        customerDto.setCustomerName(scannerStr.nextLine());
        System.out.print("Surname: ");
        customerDto.setCustomerSurname(scannerStr.nextLine());
        System.out.print("Mail Adress: ");
        customerDto.setCustomerEmailAdress(scannerStr.nextLine());
        System.out.print("Password: ");
        customerDto.setCustomerPassword(scannerStr.nextLine());
        customerDao.createCustomer(customerDto);
    }
    public void customerLogin(){
        System.out.print("***********  CUSTOMER LOGIN -Ecobank  **********\n\n");
        System.out.print("<< Back (\"Press 0\")\n" +
                "Mail Adress: ");
        customerDto.setCustomerEmailAdress(scannerStr.nextLine());
        customerDao.loginMailControl(customerDto);
    }

    public void customerMenu(String name, String surname, String mail, long balance){
        //Synchronization of the information held by the objects created in EmployeeDao and ApplicationInterface.
        customerDto.setCustomerBalance(balance);
        customerDto.setCustomerName(name);
        customerDto.setCustomerSurname(surname);
        customerDto.setCustomerEmailAdress(mail);

        System.out.print("***********  CUSTOMER MENU -Ecobank  **********\n\n" +
                "1. Deposit\n" +
                "2. Withdraw / Transfer\n" +
                "0. Quit\n" +
                "Please select process: ");
        nmbr = scannerInt.nextInt();

        switch (nmbr){
            case 1:
                System.out.print("<< Back (\"Press 0\")\n" +
                        "Please enter the amount: ");
                customerDto.setAmount(scannerInt.nextLong());
                customerDao.deposit(customerDto);
                break;
            case 2:
                System.out.print("<< Back (\"Press 0\")\n" +
                        "Please enter the amount: ");
                customerDto.setAmount(scannerInt.nextLong());
                customerDao.balanceControl(customerDto);
                break;
            case 0:
                main();
                break;
        }
    }

    public void withdrawTransfer(long balance,long amount,String mail){
        //Synchronization of the information held by the objects created in EmployeeDao and ApplicationInterface.
        customerDto.setCustomerBalance(balance);
        customerDto.setCustomerEmailAdress(mail);
        customerDto.setAmount(amount);

        System.out.print("***********  CUSTOMER MENU -Ecobank  **********\n\n" +
                "1. Withdraw\n" +
                "2. Transfer\n" +
                "0. Back\n" +
                "\"Please select process: \"");
        nmbr = scannerInt.nextInt();

        switch (nmbr){
            case 1:
                customerDao.withdraw(customerDto);
                break;
            case 2:
                System.out.print("Please enter the mail: ");
                customerDto.setRecipientMail(scannerStr.nextLine());
                customerDao.transferMailControl(customerDto);
                break;
            case 0:
                customerMenu(customerDto.getCustomerName(), customerDto.getCustomerSurname(), customerDto.getCustomerEmailAdress(), customerDto.getCustomerBalance());
        }
    }
}
