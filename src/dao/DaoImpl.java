package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Card;
import model.Player;

public class DaoImpl implements Dao {

	@Override
	public void connect() throws SQLException {
		final String URL = "jdbc:mysql://localhost:3306/uno";
		final String USER = "root";
		final String PASSWORD = "";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("Se ha estableecido la conexión");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void disconnect() throws SQLException {

	}

	@Override
	public int getLastIdCard(int playerId) throws SQLException {
	    final String QUERY = "SELECT IFNULL(MAX(id), 0) AS last_id FROM card WHERE id_player = ?";
	    final String URL = "jdbc:mysql://localhost:3306/uno";
	    final String USER = "root";
	    final String PASSWORD = "";

	    int lastCardId = 0;

	    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement statement = conn.prepareStatement(QUERY)) {

	        statement.setInt(1, playerId);

	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                lastCardId = resultSet.getInt("last_id") + 1; 
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error al obtener el último ID de carta: " + e.getMessage());
	        throw e;
	    }

	    return lastCardId;
	}

	
	@Override
	public Player getPlayer(String user, String pass) throws SQLException {
		final String QUERY = "SELECT id, user, games, victories FROM player WHERE user = ? AND password = ?";

		final String URL = "jdbc:mysql://localhost:3306/uno";
		final String USER = "root";
		final String PASSWORD = "";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Player player = null;

		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			statement = connection.prepareStatement(QUERY);
			statement.setString(1, user);
			statement.setString(2, pass);

			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("user");
				int games = resultSet.getInt("games");
				int victories = resultSet.getInt("victories");
				player = new Player(id,name,games,victories);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null || statement != null || connection != null) {
				resultSet.close();
			}

		}

		return player;
	}

	@Override
	 public ArrayList<Card> getCards(int playerId) throws SQLException {
        ArrayList<Card> cards = new ArrayList<>();
        final String URL = "jdbc:mysql://localhost:3306/uno";
        final String USER = "root";
        final String PASSWORD = "";

        String sql = "SELECT id, number, color, id_player FROM card WHERE id_player = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                	int id = resultSet.getInt("id");
                    String number = resultSet.getString("number");
                    String color = resultSet.getString("color");
                    int id_player = resultSet.getInt("id_player");

                    Card card = new Card(id, number, color, id_player);
                    cards.add(card);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener las cartas del jugador desde la base de datos: " + e.getMessage());
            throw e;
        }
        return cards;
    }

	/*@Override
	public Card getCard(int cardId) throws SQLException {
		
		return null;
	}*/

	@Override
	public void saveGame(Card card) throws SQLException {
		
		final String URL = "jdbc:mysql://localhost:3306/uno";
		final String USER = "root";
		final String PASSWORD = "";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String sql = "INSERT INTO game (id_card) VALUES (?)";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setInt(1, card.getId());
		
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Error al guardar el juego en la base de datos: " + e.getMessage());
			throw e;
		}
		

	}

	@Override
	public void saveCard(Card card) throws SQLException {

		final String URL = "jdbc:mysql://localhost:3306/uno";
		final String USER = "root";
		final String PASSWORD = "";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String sql = "INSERT INTO card (number, color, id_player) VALUES (?, ?, ?)";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setString(1, card.getNumber());
				statement.setString(2, card.getColor());
				statement.setInt(3, card.getPlayerId());

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Error al guardar la carta en la base de datos: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public void deleteCard(Card card) throws SQLException {
		final String URL = "jdbc:mysql://localhost:3306/uno";
		final String USER = "root";
		final String PASSWORD = "";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String sql = "DELETE FROM card WHERE id = ?";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setInt(1, card.getId());
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Error al borrar la carta en la base de datos: " + e.getMessage());
			throw e;
		}

	}

	@Override
	public void clearDeck(int playerId) throws SQLException {
	    final String URL = "jdbc:mysql://localhost:3306/uno";
	    final String USER = "root";
	    final String PASSWORD = "";
	    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
	        String sql = "DELETE FROM card WHERE id_player = ?";

	        try (PreparedStatement statement = conn.prepareStatement(sql)) {
	            statement.setInt(1, playerId);
	            statement.executeUpdate();
	        }
	    } catch (SQLException e) {
	        System.err.println("Error al borrar las cartas del jugador en la base de datos: " + e.getMessage());
	        throw e;
	    }
	}

	@Override
	public void addVictories(int playerId) throws SQLException {
		
		final String URL = "jdbc:mysql://localhost:3306/uno";
		final String USER = "root";
		final String PASSWORD = "";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String sql = "UPDATE player SET victories = victories + 1 WHERE id = ?";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setInt(1, playerId);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Error al borrar la carta en la base de datos: " + e.getMessage());
			throw e;
		}
		

	}

	@Override
	public void addGames(int playerId) throws SQLException {

		final String URL = "jdbc:mysql://localhost:3306/uno";
		final String USER = "root";
		final String PASSWORD = "";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String sql = "UPDATE player SET games = games + 1 WHERE ID = ?";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setInt(1, playerId);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Error al borrar la carta en la base de datos: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public Card getCard(int cardId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Card getLastCard(int playerId) throws SQLException {
		 Card lastCard = null;
		    final String URL = "jdbc:mysql://localhost:3306/uno";
		    final String USER = "root";
		    final String PASSWORD = "";

		    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
		        String sql = "SELECT * FROM card WHERE id_player = ? ORDER BY id DESC LIMIT 1";

		        try (PreparedStatement statement = conn.prepareStatement(sql)) {
		            statement.setInt(1, playerId);

		            try (ResultSet resultSet = statement.executeQuery()) {
		                if (resultSet.next()) {
		                    int cardId = resultSet.getInt("id");
		                    String cardNumber = resultSet.getString("number");
		                    String cardColor = resultSet.getString("color");
		                    lastCard = new Card(cardId, cardNumber,cardColor,playerId);
		                }
		            }
		        }
		    } catch (SQLException e) {
		        System.err.println("Error al obtener la última carta del jugador de la base de datos: " + e.getMessage());
		        throw e;
		    }

		    return lastCard;
	}

	@Override
	public Card getLastCard() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
