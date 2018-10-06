package gateway;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;

import model.Book;

import java.time.*;

public class BookTableGateway {

	private Connection conn;
	private static BookTableGateway instance = null;
	
	
	public BookTableGateway() throws Exception{
		
		conn = null;
		
		Properties props = new Properties();
		FileInputStream fis = null;
        try {
			fis = new FileInputStream("db.properties");
	        props.load(fis);
	        fis.close();

	        //create the data source
	        MysqlDataSource ds = new MysqlDataSource();
	        ds.setURL(props.getProperty("MYSQL_DB_URL"));
	        ds.setUser(props.getProperty("MYSQL_DB_USERNAME"));
	        ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));

			//create the connection
			conn = ds.getConnection();

        } catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static BookTableGateway getInstance() {
		if(instance == null) {
			instance = new BookTableGateway();
		}
		return instance;
	}
	
	public void createBook(Book book) throws Exception{
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("update BOOK "
					+ "set title = ?"
					+ ", summary = ?"
					+ ", year_published = ?"
					+ ", publisher_id = ?"
					+ ", isbn = ?"
					+ " where id = ?");
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			// TODO: Change yearpublished to an INT.
			st.setInt(3, book.getYearPublished());
			// TODO: Add getPublisher method.
			st.setInt(4, book.getPublisherId());
			st.setString(5,  book.getIsbn());
			st.executeQuery();
			
			conn.commit();
		} catch(SQLException e) {
			try {
				conn.rollback();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if(st != null) {
					st.close();
				}
				
				conn.setAutoCommit(true);
				
			} catch (SQLException e) {
				throw new SQLException("SQL Error: " + e.getMessage());
			}
		}
		
	}
	public List<Book> getBooks(){
		List<Book> books = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select a.id as book_id "
					+ ", a.title, a.summary, a.summary, "
					+ "ifnull(b.summary, '') as summary, a.year_published"
					+ " a.publisher_id, a.isbn, a.date_added from "
					+ "BOOK a left join make b on a.published_id = b.id");
		
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				// TODO: LocalDateTime object artifact from previous build
				//		 to be updated.
//				Date date = new Date(rs.getTimestamp("date_added").getTime());
//				LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				Book book = new Book( rs.getInt("id")
								 	, rs.getString("title")
								 	, rs.getString("summary")
								 	, rs.getInt("year_published")
								 	, rs.getString("ISBN"));
				
				book.setDateAdded(rs.getTimestamp("date_added"));
				books.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return books;
	}
	
	public void updateBook (Book book) throws Exception {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("update BOOK "
					+ "set title = ?"
					+ ", summary = ?"
					+ ", year_published = ?"
					+ ", publisher_id = ?"
					+ ", isbn = ?"
					+ " where id = ?");
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPublished());
			st.setInt(4, book.getPublisherId());
			st.setString(5,  book.getIsbn());
			st.executeUpdate();
			
			conn.commit();
		} catch(SQLException e) {
			try {
				conn.rollback();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if(st != null) {
					st.close();
				}
				
				conn.setAutoCommit(true);
				
			} catch (SQLException e) {
				throw new SQLException("SQL Error: " + e.getMessage());
			}
		}
		
	}
	
	public void deleteBook(Book book) throws Exception{
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE from BOOK"
					  +"WHERE id = ?");
			st.setInt(1, book.getId());
			st.executeUpdate();
			
			conn.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(st != null) {
					st.close();
				}
				
				conn.setAutoCommit(true);
				
			} catch (SQLException e) {
				throw new Exception("SQL Error: " + e.getMessage());
			}
		}
	}
	
	public void close() {
		if(conn!=null) {
			try {
				conn.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
