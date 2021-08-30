package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDao accountDao;

    public JdbcTransferDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Transfer getTransfer(int transferId) throws TransferNotFoundException {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        while(results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) throws TransferNotFoundException{
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";

        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(),
                transfer.getAccountTo(), transfer.getAmount());
        //checks to see if transfer is approved (2 = approved)
        if(getTransfer(id).getTransferStatusId() == 2){
        accountDao.updateFromBalance(transfer.getAmount(), transfer.getAccountFrom());
        accountDao.updateToBalance(transfer.getAmount(), transfer.getAccountTo());}

        return getTransfer(id);
    }

    @Override
    public List<Transfer> getTransferList(String username) throws TransferNotFoundException {
        List<Transfer> transferList = new ArrayList<Transfer>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers t RIGHT JOIN accounts a ON t.account_from = a.account_id OR t.account_to = a.account_id " +
                "JOIN users u ON a.user_id = u.user_id WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while(results.next()){
            Transfer transfer = mapRowToTransfer(results);

            String sqlStr = "SELECT username FROM accounts a JOIN users u " +
                    "ON a.user_id = u.user_id WHERE account_id = ?";
            SqlRowSet resultsForToUser = jdbcTemplate.queryForRowSet(sqlStr, transfer.getAccountTo());

            while(resultsForToUser.next()){
                transfer.setUserTo(mapRowToUserId(resultsForToUser));
            }
            SqlRowSet resultsForFromUser = jdbcTemplate.queryForRowSet(sqlStr, transfer.getAccountFrom());

            while(resultsForFromUser.next()){
                transfer.setUserFrom(mapRowToUserId(resultsForFromUser));
            }

            transferList.add(transfer);
        }
        return transferList;
    }

    @Override
    public void updateTransfer(Transfer transfer) throws TransferNotFoundException {
        String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transfer.getTransferStatusId(), transfer.getTransferId());

        if(transfer.getTransferStatusId() == 2){
            accountDao.updateFromBalance(transfer.getAmount(), transfer.getAccountFrom());
            accountDao.updateToBalance(transfer.getAmount(), transfer.getAccountTo());
        }
    }

    private Transfer mapRowToTransfer(SqlRowSet sqlRowSet){
        Transfer transfer = new Transfer();
        transfer.setTransferId(sqlRowSet.getInt("transfer_id"));
        transfer.setTransferTypeId(sqlRowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(sqlRowSet.getInt("transfer_status_id"));
        transfer.setAccountFrom(sqlRowSet.getInt("account_from"));
        transfer.setAccountTo(sqlRowSet.getInt("account_to"));
        transfer.setAmount(sqlRowSet.getBigDecimal("amount"));
        return  transfer;
    }

    private String mapRowToUserId(SqlRowSet sqlRowSet){
        String username  = sqlRowSet.getString("username");
        return username;
    }
}
