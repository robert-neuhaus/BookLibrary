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
	
	
	public BookTableGateway() throws Exception{	// TimeStamp : X/X
		
//		TimeZone timeZone = TimeZone.getTimeZone("CDT");
//		TimeZone.setDefault(timeZone);
		
		conn = null;
		
		Properties 	   props = new Properties();
		FileInputStream  fis = null;
        try {
			fis = new FileInputStream("db.properties");
	        props.load(fis);
	        fis.close();

	        //create the data source
	        MysqlDataSource ds = new MysqlDataSource();
	        ds.setURL		(props.getProperty("MYSQL_DB_URL"));
	        ds.setUser		(props.getProperty("MYSQL_DB_USERNAME"));
	        ds.setPassword	(props.getProperty("MYSQL_DB_PASSWORD"));

			//create the connection
			conn = ds.getConnection();

        } catch (IOException e) {
			e.printStackTrace();
		} 
        catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static BookTableGateway getInstance() throws Exception{// TimeStamp : X/X
		if(instance == null) {
			instance = new BookTableGateway();
		}
		return instance;
	}
	
	public void createBook(Book Book) throws Exception{// TimeStamp : X/O
		PreparedStatement st = null;
		ResultSet 		  rs = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement( "insert into Book ("
									  + "title, "
									  + "summary, "
									  + "year_published, "
									  + "publisher_id, "
									  + "isbn"
									  + ") values ( ?, ?, ?, ?, ?)");
			st.setString	(1, Book.getTitle());
			st.setString	(2, Book.getSummary());
			st.setInt		(3, Book.getYearPublished());
			st.setInt		(4, Book.getPublisherId());
			st.setString	(5, Book.getIsbn());
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
		
		// Retrieve index of newly obtained
		try {
			st = conn.prepareStatement("SELECT id FROM Book ORDER BY id DESC");
			rs = st.executeQuery();
			rs.next();
			Book.setId(rs.getInt("id"));
			
		}catch(SQLException e) {
			throw e;
		}finally {
			if(st != null) {
				st.close();
			}
		}
		
	}
	
	public List<Audit> getAudits(){
		List<Audit> Audits = new ArrayList<>();
		
		// TODO: Add query
		// TODO: Create Audit object
		
	}
	
	public List<Book> getBooks(){	// TimeStamp : O/O
		List<Book> Books = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement( "select b.*, p.* FROM Book b, Publisher p "
									  + "WHERE b.publisher_id = p.publisher_id "
									  + "ORDER BY title ASC");
		
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				Book Book = new Book( rs.getInt("id")
								 	, rs.getString("title")
								 	, rs.getString("summary")
								 	, rs.getInt("year_published")
								 	, rs.getString("ISBN"));
				
				// Retrieve TimeStamp separately to be converted.
				Date date = new Date(rs.getTimestamp("date_added").getTime());
				LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			
				Book.setDateAdded(ldt);
				
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
	
	public List<String> getPublishers(){
		
		List<String> publishers = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("select * FROM Publisher ORDER BY title ASC");
		
			rs = st.executeQuery();
			
			while(rs.next()) {
				publishers.add(rs.getString("name"));
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
		
		
		return publishers;
	}
	
	public void updateBook(Book Book) throws Exception {// TimeStamp : X/O
		
		if(Book.getId() == 0) {
			this.createBook(Book);
		}
		
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
			st.setInt(6, Book.getId());
			st.executeUpdate();
			
			conn.commit();
		} catch(SQLException e) {
			try {
				conn.rollback();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
			throw e;
			
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
	
	public void deleteBook(Book Book) throws Exception{// TimeStamp : X/X
		
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE FROM Book WHERE id = ?");
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
	
	public LocalDateTime getLastModified(int key){// TimeStamp : O/O
		
		PreparedStatement st = null;
		ResultSet 		  rs = null;
		LocalDateTime    ldt = null;
		Date 			date = null;
		
		try {
			st   = conn.prepareStatement("SELECT * FROM Book WHERE id = ?");
			st.setInt(1,  key);
			rs   = st.executeQuery();
			
			date = new Date(rs.getTimestamp("last_modified").getTime());
			ldt  = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			
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
		
		return ldt;
	}
	
	public void close() {// TimeStamp : X/X
		if(conn!=null) {
			try {
				conn.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
