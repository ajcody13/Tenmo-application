package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
private final JdbcTemplate jdbcTemplate;
public JdbcAccountDao(DataSource dataSource){
    this.jdbcTemplate = new JdbcTemplate(dataSource);
}

    @Override
    public BigDecimal getBalance(String user) {
        BigDecimal balance = null;
        Account account = null;
        String sql = "SELECT * FROM accounts a JOIN users u ON " +
                "a.user_id = u.user_id WHERE u.username = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, user);
        while (result.next()){
            account = mapRowToAccount(result);
            balance = account.getBalance();
        }
        return balance;
    }

    @Override
    public Account getAccount(int userId) {
        Account account = new Account();
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while(results.next()){
            account = mapRowToAccount(results);
        }
        return account;
    }


    @Override
    public void updateToBalance(BigDecimal amountToDeposit, int accountId) {
    String sql = "UPDATE accounts a SET balance = balance + ? WHERE account_id = ?";
    jdbcTemplate.update(sql, amountToDeposit, accountId);


}

    @Override
    public void updateFromBalance(BigDecimal amountToWithdraw, int accountId) {
        String sql = "UPDATE accounts a SET balance = balance - ? WHERE account_id = ?";
        jdbcTemplate.update(sql, amountToWithdraw, accountId);
    }


    private Account mapRowToAccount(SqlRowSet rowSet){
    Account account = new Account();
    account.setAccountId(rowSet.getInt("account_id"));
    account.setUserId(rowSet.getInt("user_id"));
    account.setBalance(rowSet.getBigDecimal("balance"));
    return account;
    }
}
