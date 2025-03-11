package casino;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class CasinoGames {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Random random = new Random();
		String url = "jdbc:mysql://localhost:3306/casinodb";
		String username = "root";
		String password = "";
		String[] coinSlotFruits = {"🍇", "🍉", "🍌"};
		
		while(true) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();
				
		        System.out.println("  ####     #     #####   #   #     #   	###   ");
		        System.out.println(" #    #  #   #   #       #   # #   #   #   #  ");
		        System.out.println(" #       #####   #####   #   #  #  #   #   #  ");
		        System.out.println(" #       #   #       #   #   #   # #   #   #  ");
		        System.out.println("  ####   #   #   #####   #   #    ##    ###   ");
		        System.out.println("Log in to your account to play :)");
		        
		        // User name
		        System.out.print("Enter username: ");
		        String _username = scanner.nextLine();
		        
		        String getUserQuery = "SELECT* FROM user_table WHERE username = '" + _username + "';";
		        // Execute sql query to get columns from user_table table
		        
		        
		        // Password
		        System.out.print("Enter password: ");
		        String _password = scanner.nextLine();
		        try(ResultSet userCredentials = statement.executeQuery(getUserQuery)){     	
			        if(userCredentials.next()) {
			        	// Store the data from the database
			        	String casinoUser = userCredentials.getString("username");
			        	String casinoUserPass = userCredentials.getString("password");
			        	int balanceMoney = userCredentials.getInt("balance");
			        	int casinoUserId = userCredentials.getInt("id");
			        	while(true) {
			        		ResultSet updateUserInfo = statement.executeQuery(getUserQuery);
			        		updateUserInfo.next();
				        	casinoUser = updateUserInfo.getString("username");
				        	casinoUserPass = updateUserInfo.getString("password");
				        	balanceMoney = updateUserInfo.getInt("balance");
				        	casinoUserId = updateUserInfo.getInt("id");
				        	if(casinoUser.equals(_username) && casinoUserPass.equals(_password)) {
				    	        System.out.println("  ####     #     #####   #   #     #   	###   ");
				    	        System.out.println(" #    #  #   #   #       #   # #   #   #   #  ");
				    	        System.out.println(" #       #####   #####   #   #  #  #   #   #  ");
				    	        System.out.println(" #       #   #       #   #   #   # #   #   #  ");
				    	        System.out.println("  ####   #   #   #####   #   #    ##    ###   ");
				    	        System.out.println("============Menu===========");
				    	        
				    	        System.out.println("========================");
				    	        System.out.println("Welcome! " + casinoUser + "! ");
				    	        System.out.println("Current Balance: " + balanceMoney);
				    	        System.out.println("========================");
				    	        
				    	        System.out.println("1.Play Coin Slot Machine \n2. Top up Money \n3. Show Game History \n0. Exit");
				    	        

				    	        
				    	        System.out.print("What would you like to do? (1/2/3/0) ");
				    	        int _userChoiceMenu = scanner.nextInt();
				    	        scanner.nextLine();
				    	        if(_userChoiceMenu == 1) {
				    	        	String[] playCoinSlot = randomFruit(coinSlotFruits, random, scanner, balanceMoney, statement, casinoUserId, connection);
				    	        }
				    	        else if(_userChoiceMenu == 3) {
				    	        	showGameHistory(statement, casinoUserId);
				    	        }
				        	}
				        	else {
				        		System.out.println("Wrong Credentials");
				        		break;
				        	}
				        	
			        	}
			        }
			        else {
			        	System.out.println("User not Found");
			        }
		        }
		        catch(SQLException er) {
		        	System.out.println(er);
		        }
				
			}
			catch(Exception error) {
				System.out.println(error);
			}
		}	
	}
	
	public static void showGameHistory(Statement statement, int id) throws SQLException {
		ResultSet gameHistory = statement.executeQuery("SELECT* FROM user_table JOIN casinodata ON user_table.id = casinodata.userID WHERE casinodata.userID = '" + id + "' ORDER BY game_id DESC LIMIT 10;");
		System.out.println("Game History");
		System.out.println("------------------------------");
		while(gameHistory.next()) {
			System.out.println("Game ID. " + gameHistory.getInt("game_id") + "\n" + gameHistory.getString("first_slot") + gameHistory.getString("second_slot") +
					gameHistory.getString("third_slot") + "\n" + gameHistory.getString("game_result"));
			System.out.println("----------------------");
		}
		
	}
	public static String[] randomFruit(String[] coinSlotFruits, Random random, Scanner scanner, int balanceMoney, Statement statement, int userID, Connection connection) throws SQLException{
		String[] gameResult = new String[3];
		do {
			if(balanceMoney < 20) {
				System.out.print("You don't have enough balance. Enter '1' to top up. '0' to exit.");
				int _userChoiceTopUp = scanner.nextInt();
				if(_userChoiceTopUp == 1) {
					System.out.println("Top up");
				}
				else {
					System.out.println("Exit...");
					break;
				}
			}
			balanceMoney = balanceMoney - 20;
			String updateMoney = "UPDATE user_table SET balance = " + balanceMoney + " WHERE id =" + userID + ";";
			statement.executeUpdate(updateMoney);
			
			for(int i = 0; i < gameResult.length; i++) {
				int randomIdx = random.nextInt(gameResult.length);
				String randomFruit = coinSlotFruits[randomIdx];
				gameResult[i] = randomFruit;
				System.out.print(randomFruit + "|");
				
			}
			if(gameResult[0].equals(gameResult[1]) && gameResult[1].equals(gameResult[2])) {
				System.out.println("You win !!");
				insertGameData(statement, userID, gameResult[0], gameResult[1], gameResult[2], "win");
				balanceMoney = balanceMoney + 60;
				updateMoney = "UPDATE user_table SET balance = " + balanceMoney + " WHERE id =" + userID + ";";
				statement.executeUpdate(updateMoney);
				System.out.println("Total Balance: " + balanceMoney);	
			}
			else {
				System.out.println("You Lose. Try Again");
				insertGameData(statement, userID, gameResult[0], gameResult[1], gameResult[2], "lose");
			}
			System.out.print("Enter '1' to continue playing. '0' to exit.");
			int _userChoiceContinue = scanner.nextInt();
			scanner.nextLine();
			if(_userChoiceContinue == 0) {
				System.out.println("See you again. Bye...");
				break;
			}
			
		}while(true);
		return gameResult;
	}
	public static void insertGameData(Statement statement, int userID, String first_slot, String second_slot, String third_slot, String gameResult) throws SQLException{
		String insertGameData = "INSERT INTO casinodata (userID, first_slot, second_slot, third_slot, game_result) " + 
								"VALUES(" + userID + ", '" + first_slot + "', '" + second_slot + "', '" + third_slot + "', '" + gameResult + "');";
		statement.executeUpdate(insertGameData, Statement.RETURN_GENERATED_KEYS);	
	}
	
}
