package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    public BigDecimal getBalance(String user);

    public Account getAccount(int userId);
    public void updateToBalance(BigDecimal amountToDeposit, int accountId);
    public void updateFromBalance(BigDecimal amountToWithdraw, int accountId);
}
