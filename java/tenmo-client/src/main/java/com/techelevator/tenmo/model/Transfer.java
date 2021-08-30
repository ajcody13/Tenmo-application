package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transferId = 0;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFrom;
    private int accountTo;
    private BigDecimal amount;
    private String userFrom;
    private String userTo;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    @Override
    public String toString() {
       String transferType = "";
       String transferStatus = "";
       if (transferTypeId == 1){
           transferType = "Request";
       }else if (transferTypeId == 2){
           transferType = "Send";
       }
        if (transferStatusId == 1){
            transferStatus = "Pending";
        }else if (transferStatusId == 2){
            transferStatus = "Approved";
        } else if (transferStatusId == 3){
            transferStatus = "Rejected";
        }
        return "----------------------\n" +
                "TRANSFER DETAILS \n"+
                "----------------------\n" +
                "Transfer ID = " + transferId + "\n" +
                "Transfer Type = " + transferType +"\n" +
                "Transfer Status = " + transferStatus +"\n" +
                "From = " + userFrom +"\n" +
                "To = " + userTo +"\n" +
                "Amount = $" + amount
               ;
    }
}
