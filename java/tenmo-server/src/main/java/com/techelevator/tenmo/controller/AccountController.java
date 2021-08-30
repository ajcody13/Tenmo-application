package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TransferDao transferDao;

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal){
        return accountDao.getBalance(principal.getName().trim());
    }


    @RequestMapping(path= "/transfer", method = RequestMethod.POST)
    public Transfer transfer(@Valid @RequestBody Transfer transfer) throws TransferNotFoundException {
        return transferDao.createTransfer(transfer);
    }

    @RequestMapping (path = "/users", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

    @RequestMapping (path = "/account/{id}", method = RequestMethod.GET)
    public Integer getAccountId(@PathVariable Integer id){
        System.out.println(accountDao.getAccount(id).getAccountId());
        return accountDao.getAccount(id).getAccountId();
    }

    @RequestMapping (path = "/users/transfers", method = RequestMethod.GET)
    public List<Transfer> getTransferList(Principal principal) throws TransferNotFoundException{
        return transferDao.getTransferList(principal.getName().trim());
    }
    @RequestMapping (path = "/users/transfers/{id}", method = RequestMethod.GET )
    public Transfer getTransferById(@PathVariable int id) throws TransferNotFoundException {
        return transferDao.getTransfer(id);
    }

    @RequestMapping (path = "/users/transfers", method = RequestMethod.PUT)
    public void updateTransfer(@Valid @RequestBody Transfer transfer) throws TransferNotFoundException {
        transferDao.updateTransfer(transfer);
    }
}