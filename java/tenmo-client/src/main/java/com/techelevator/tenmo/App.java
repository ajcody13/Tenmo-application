package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import io.cucumber.java.bs.A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
	private Scanner userInput = new Scanner(System.in);
    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		AccountService accountService = new AccountService(API_BASE_URL);
		accountService.AUTH_TOKEN = currentUser.getToken();
		System.out.println("--------------------------\n Your Balance Is: $" +accountService.getBalance()+ "\n--------------------------");
		userInput.nextLine();

		
	}

	private void viewTransferHistory() {

		AccountService accountService = new AccountService(API_BASE_URL);
		accountService.AUTH_TOKEN = currentUser.getToken();
		String input = "";
		while (!input.equalsIgnoreCase("Q")) {
			boolean didItPrint = false;
			Transfer[] transfers = accountService.viewTransferHistory();
			System.out.println("------------------------------------\n" +
					"Transfers\n"+
					"ID         Amount       From     To\n" + "------------------------------------");
			for (Transfer transfer : transfers) {
				System.out.println(transfer.getTransferId() + "       $"+ transfer.getAmount() + "        " + transfer.getUserFrom() + "     "+ transfer.getUserTo());
			}
			System.out.println("Please enter transfer id to view details or (Q) to return to main menu: ");
			input = userInput.nextLine();
			try {
				int inputAsInt = Integer.parseInt(input);
				for (Transfer transfer : transfers) {
					if (inputAsInt == (transfer.getTransferId())) {
						System.out.println(transfer);
						userInput.nextLine();
						didItPrint = true;
					}
				}if (didItPrint == false){
					System.out.println("Please enter a valid number");
					userInput.nextLine();
				}
			} catch (Exception e) {
			if (!input.equalsIgnoreCase("Q")){
				System.out.println("Please enter a valid transfer number");
				userInput.nextLine();
			}
			}
		}
	}
	private void viewPendingRequests() {

		AccountService accountService = new AccountService(API_BASE_URL);
		accountService.AUTH_TOKEN = currentUser.getToken();
		String input = "";
		while (!input.equalsIgnoreCase("Q")) {
			boolean didItPrint = false;
			Transfer[] transfers = accountService.viewTransferHistory();
			System.out.println("---------------------------------------\n" +
					"Pending\n" +
					"Transfers\n"+
					"ID         Amount       From     To\n" + "---------------------------------------");
			for (Transfer transfer : transfers) {
				if (transfer.getTransferStatusId() == 1){
				System.out.println(transfer.getTransferId() + "       $"+ transfer.getAmount() + "      " + transfer.getUserFrom() + "     "+ transfer.getUserTo());
			}}
			System.out.println("Please enter transfer id to view details or (Q) to return to main menu: ");
			input = userInput.nextLine();
			try {
				int inputAsInt = Integer.parseInt(input);
				for (Transfer transfer : transfers) {
					if (inputAsInt == (transfer.getTransferId())) {
						System.out.println(transfer);
						userInput.nextLine();
						didItPrint = true;

						if(!transfer.getUserTo().equals(currentUser.getUser().getUsername())){
							String pendingInput = "";
							while(!pendingInput.equalsIgnoreCase("N")){
								System.out.println("(A)pprove\n(R)eject\n(N)o Change\nPlease choose an option: ");
								pendingInput = userInput.nextLine();
								if(pendingInput.equalsIgnoreCase("A")){
									if(transfer.getAmount().compareTo(accountService.getBalance()) <= 0){
										transfer.setTransferStatusId(2);
										accountService.updateTransfer(transfer);
										break;
									}
									System.out.println("Insufficient funds to approve request");

								}
								else if(pendingInput.equalsIgnoreCase("R")){
									transfer.setTransferStatusId(3);
									accountService.updateTransfer(transfer);
									break;
								}
								else if(pendingInput.equalsIgnoreCase("N")){
									break;
								}
								else{
									System.out.println("Please enter a valid option");
								}
							}
						}
					}
				}if (didItPrint == false){
					System.out.println("Please enter a valid number");
					userInput.nextLine();
				}
			} catch (Exception e) {
				if (!input.equalsIgnoreCase("Q")){
					System.out.println("Please enter a valid transfer number");
					userInput.nextLine();
				}
			}
		}
	}


	private void sendBucks() {

		AccountService accountService = new AccountService(API_BASE_URL);
		accountService.AUTH_TOKEN = currentUser.getToken();
		User[] users = accountService.listUsers();
		System.out.println("--------------------\nUser ID    Username \n--------------------");
		for (User user : users){
			System.out.println(user.getId() + "       " + user.getUsername());
		}
		System.out.println("Enter a user_id to send TEbucks to: ");
		String input = userInput.nextLine();
		int inputAsInt = 0;
		try{
			inputAsInt = Integer.parseInt(input);
			System.out.println("Enter an amount to send: ");
			input = userInput.nextLine();
			BigDecimal inputMoney = new BigDecimal(input);
			if (inputMoney.compareTo(BigDecimal.valueOf(0)) <= 0){
				System.out.println("Please enter a positive number");
				userInput.nextLine();
			} else if (inputMoney.compareTo(accountService.getBalance()) <= 0){
				Transfer transfer = accountService.makeTransferTo(inputMoney, inputAsInt, currentUser.getUser().getId());
				accountService.transferTo(transfer);
			}else{
				System.out.println("Insufficient funds");
				userInput.nextLine();
			}
		}
		catch(Exception e){
			System.out.println("Please enter a valid user ID and amount");
		}

	}

	private void requestBucks() {
		AccountService accountService = new AccountService(API_BASE_URL);
		accountService.AUTH_TOKEN = currentUser.getToken();
		User[] users = accountService.listUsers();
		System.out.println("--------------------\nUser ID    Username \n--------------------");
		for (User user : users){
			System.out.println(user.getId() + "       " + user.getUsername());
		}
		System.out.println("Enter a user_id to request TEbucks from: ");
		String input = userInput.nextLine();
		int inputAsInt = 0;
		try{
			inputAsInt = Integer.parseInt(input);
			System.out.println("Enter an amount to request: ");
			input = userInput.nextLine();
			BigDecimal inputMoney = new BigDecimal(input);
			if (inputMoney.compareTo(BigDecimal.valueOf(0)) <= 0){
				System.out.println("Please enter a positive number");
				userInput.nextLine();
			} else if (inputMoney.compareTo(accountService.getBalance()) <= 1){
				//needs information on how to update balances
				Transfer transfer = accountService.makeTransferFrom(currentUser.getUser().getId(),inputMoney , inputAsInt);
				accountService.transferTo(transfer);
			}
		}catch(Exception e){
			System.out.println("Please enter a valid user ID and amount");
		}

	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
