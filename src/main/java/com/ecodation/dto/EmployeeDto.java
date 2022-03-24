package com.ecodation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EmployeeDto implements Serializable {

    private long employeeId;
    private String employeeName;
    private String employeeSurname;
    private String employeeEmailAdress;
    private String employeePassword;
    private Date eCretionDate;
    private String customerEmailAdress;

}
