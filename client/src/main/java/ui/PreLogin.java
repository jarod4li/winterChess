package ui;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static ui.PostLogin.postLogin;

public class PreLogin {
    private static Scanner scanner = new Scanner(System.in);
    private static ServerFacade serverFacade = null;
    private static int portNumber;

    public static void preLogin(int portNumber){

        serverFacade = new ServerFacade(portNumber);


        PreLogin.portNumber = portNumber;

        String username;
        String password;
        String email;
        String token;

        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.println("Chess Game CS240");
        while(true){
            System.out.println("Choose one of the options:");
            System.out.println("Register");
            System.out.println("Login");
            System.out.println("Help");
            System.out.println("Quit");

            String input = scanner.nextLine().toLowerCase().trim();
            switch (input){
                case "register":
                    System.out.print("Username: ");
                    username = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    password = scanner.nextLine().trim();
                    System.out.print("email: ");
                    email = scanner.nextLine().trim();
                    token = serverFacade.register(username, password, email);
                    if (token != null){
                        System.out.println("Registration success! Welcome " + username);
                        postLogin(token, portNumber, serverFacade);
                    }
                    else{
                        System.out.println("Error: Not registered in");
                        preLogin(portNumber);
                    }
                    break;


                case "login":
                    System.out.print("Username: ");
                    username = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    password = scanner.nextLine().trim();
                    token = serverFacade.login(username, password);
                    if (token != null){
                        System.out.println("\nLogin success! Welcome " + username);
                        postLogin(token, portNumber, serverFacade);
                    }
                    else{
                        System.out.println("No success :(");
                        preLogin(portNumber);
                    }
                    break;


                case "help":
                    System.out.println("Select one of the given options and provide the requested information.");
                    break;
                case "quit":
                    System.out.println("Already leaving? Okay :*");
                    System.exit(0);
                    break;
                case "clear":
                    serverFacade.clear();
                default:
                    preLogin(portNumber);
                    break;
            }
        }
    }
}
