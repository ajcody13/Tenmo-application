package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public interface TransferDao {

    public Transfer createTransfer(Transfer transfer) throws TransferNotFoundException;
    public Transfer getTransfer(int transferId) throws TransferNotFoundException;
    public List<Transfer> getTransferList(String username) throws TransferNotFoundException;
    public void updateTransfer(Transfer transfer) throws  TransferNotFoundException;
}
