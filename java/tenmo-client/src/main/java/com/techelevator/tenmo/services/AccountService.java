package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import io.cucumber.java.an.E;
import io.cucumber.java.bs.A;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    public static String AUTH_TOKEN;
    private final String BASE_URL;
    public RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url){
        this.BASE_URL = url;
    }

    public BigDecimal getBalance(){
        BigDecimal balance = null;
        try {
           balance = restTemplate.exchange(BASE_URL + "balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }
                return balance;
    }

    public Integer getAccountIdByUserId(int userId){
        Integer accountId = null;
        try{
            accountId = restTemplate.exchange(BASE_URL + "account/" + userId, HttpMethod.GET, makeAuthEntity(), Integer.class).getBody();
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }return accountId;
    }

    public User[] listUsers(){
        User[] users = null;
        try{
            users = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }return users;
    }

    public Transfer makeTransferTo(BigDecimal amount, int toUser, int fromUser){
        Transfer transfer = new Transfer();
        transfer.setAccountTo(getAccountIdByUserId(toUser));
        transfer.setAmount(amount);
        transfer.setAccountFrom(getAccountIdByUserId(fromUser));
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(2);
        return transfer;
    }
    public Transfer makeTransferFrom( int toUser, BigDecimal amount, int fromUser){
        Transfer transfer = new Transfer();
        transfer.setAccountTo(getAccountIdByUserId(toUser));
        transfer.setAmount(amount);
        transfer.setAccountFrom(getAccountIdByUserId(fromUser));
        transfer.setTransferTypeId(1);
        transfer.setTransferStatusId(1);
        return transfer;
    }

    public Transfer transferTo(Transfer transfer) {
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try {
            transfer = restTemplate.postForObject(BASE_URL + "/transfer", entity, Transfer.class);
        } catch (RestClientResponseException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (ResourceAccessException ex){
            System.out.println(ex.getLocalizedMessage());
        }
        return transfer;
    }

    public Transfer[] viewTransferHistory(){
        Transfer[] transfers = null;
        try{
            transfers = restTemplate.exchange(BASE_URL + "users/transfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }return transfers;

    }

    public void updateTransfer(Transfer transfer){
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try{
            transfer = restTemplate.exchange(BASE_URL + "users/transfers", HttpMethod.PUT, entity, Transfer.class).getBody();
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity(httpHeaders);
        return entity;
    }

}
