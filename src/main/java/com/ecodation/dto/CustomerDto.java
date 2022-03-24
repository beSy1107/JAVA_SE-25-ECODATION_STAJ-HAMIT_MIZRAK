package com.ecodation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CustomerDto implements Serializable {

    private long customerId;
    private String customerName;
    private String customerSurname;
    private String customerEmailAdress;
    private String customerPassword;
    private long customerBalance;
    private Date creationDate;
    private long amount;
    private String recipientMail;
    private long recipientBalance;

}
