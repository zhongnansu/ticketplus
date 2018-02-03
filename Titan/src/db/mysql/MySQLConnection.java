package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {
	private Connection conn;

	public MySQLConnection() {
		try {
			// Ensure the driver is registered
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			// Save the item into our own database
			saveItem(item);
		}
		return items;

	}

	@Override
	public void saveItem(Item item) {
		 if (conn == null) {
			 return;
		 }
		 try {
			 //First, insert into items tables, if already exists then ignore
			 String sql = "INSERT IGNORE INTO items VALUES (?,?,?,?,?,?,?)";
			 // use "?,?,?.." is easily to maintain, and sql injection
			 PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, item.getItemId()); //based from 1
				statement.setString(2, item.getName());
				statement.setDouble(3, item.getRating());
				statement.setString(4, item.getAddress());
				statement.setString(5, item.getImageUrl());
				statement.setString(6, item.getUrl());
				statement.setDouble(7, item.getDistance());
				statement.execute(); //execute 
				
				// Second, update categories table for each category.
				sql = "INSERT IGNORE INTO categories VALUES (?,?)";
				for (String category : item.getCategories()) {
					statement = conn.prepareStatement(sql);
					statement.setString(1, item.getItemId());
					statement.setString(2, category);
					statement.execute();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
