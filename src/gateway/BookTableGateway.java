package gateway;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import model.Book;

import java.time.*;

public class BookTableGateway {

	private Connection conn;
	private static BookTableGateway instance = null;
	
	
	public BookTableGateway() throws Exception{
		
//		TimeZone timeZone = TimeZone.getTimeZone("CDT");
//		TimeZone.setDefault(timeZone);
		
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
		} 
        catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static BookTableGateway getInstance() throws Exception{
		if(instance == null) {
			instance = new BookTableGateway();
		}
		return instance;
	}
	
	public void createBook(Book Book) throws Exception{
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("insert into Book ('title', summary, year_published, publisher_id, 'isbn') values ( ?, ?, ?, ?, ?)");
			st.setString(1, Book.getTitle());
			st.setString(2, Book.getSummary());
			st.setInt(3, Book.getYearPublished());
			st.setInt(4, Book.getPublisherId());
			st.setString(5,  Book.getIsbn());
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
		List<Book> Books = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select * FROM Book");
		
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				// TODO: LocalDateTime object artifact from previous build
				//		 to be updated.
//				Date date = new Date(rs.getTimestamp("date_added").getTime());
//				LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				Book Book = new Book( rs.getInt("id")
								 	, rs.getString("title")
								 	, rs.getString("summary")
								 	, rs.getInt("year_published")
								 	, rs.getString("ISBN"));
				
				Book.setDateAdded(rs.getTimestamp("date_added"));
				Books.add(Book);
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
		
		return Books;
	}
	
	public void updateBook (Book Book) throws Exception {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("update Book "
					+ "set title = ?"
					+ ", summary = ?"
					+ ", year_published = ?"
					+ ", publisher_id = ?"
					+ ", isbn = ?"
					+ " where id = ?");
			st.setString(1, Book.getTitle());
			st.setString(2, Book.getSummary());
			st.setInt(3, Book.getYearPublished());
			st.setInt(4, Book.getPublisherId());
			st.setString(5,  Book.getIsbn());
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
	
	public void deleteBook(Book Book) throws Exception{
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE from Book"
					  +"WHERE id = ?");
			st.setInt(1, Book.getId());
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
