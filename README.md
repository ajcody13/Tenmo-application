# Tenmo-application
## Introduction
This application lets a user either register as a new uesr or sign in as an existing user. Once you are logged in as a valid user you can:
1. Send money to another registered user. This will first check your balance to see if you have enough money. It will access the database and update both of the user's balances depending on how much money was sent.
2. Request money from another registered user. This sends a transfer object to the database with a "pending" status.
3. Look at your list of past transfers.
4. Look at your list of pending transfers. It will give you the option to accept or decline the transfer if you are the requested user.

This application connects the client side to the server side with a RESTful API and the server side accesses data from the database using JdbcTemplate.
