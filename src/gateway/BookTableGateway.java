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
import model.Publisher;
import model.Audit;

import java.time.*;

public class BookTableGateway {

	private Connection conn;
	private static BookTableGateway instance = null;
	
	
	public BookTableGateway() throws Exception{	// TimeStamp : X/X
		
		//TimeZone timeZone = TimeZone.getTimeZone("CDT");
		//TimeZone.setDefault(timeZone);
		
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
			
			st = conn.prepareStatement( "INSERT INTO Book ("
									  + "title, "
									  + "summary, "
									  + "year_published, "
									  + "publisher_id, "
									  + "isbn"
									  + ") VALUES ( ?, ?, ?, ?, ?)");
			st.setString	(1, Book.getTitle());
			st.setString	(2, Book.getSummary());
			st.setInt		(3, Book.getYearPublished());
			st.setInt		(4, Book.getPublisher().getPublisherId());
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

	public void addAudits(List<Audit> audits) throws Exception{
		
		PreparedStatement st = null;
		conn.setAutoCommit(false);
		
		st = conn.prepareStatement("INSERT INTO book_audit_trail ("
								  + "book_id, "
								  + "entry_msg"
								  + ") VALUES (?, ?)");
		
		for(Audit entry : audits) {
			st.setInt(1, entry.getBookID());
			st.setString(2, entry.getAuditMsg());
			
			st.addBatch();
		}
		
		try {
			st.executeBatch();
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
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

	public Book getBook(int book_id) throws Exception {
		
		Book 			   book = null;
		PreparedStatement 	 st	= null;
		ResultSet 			 rs	= null;
		LocalDateTime 	   last	= null;
		LocalDateTime	  first = null;
		Publisher	  publisher = null;
		
		try {
			
			st = conn.prepareStatement( "SELECT b.*, p.* "
	                  				  + "FROM Book b, Publisher p "
	                  				  + "WHERE b.publisher_id = p.publisher_id "
	                  				  + "AND b.id = ?");

			st.setInt(1, book_id);

			rs = st.executeQuery();
			
			rs.next();
			
			publisher = new Publisher(rs.getInt("publisher_id")
									 ,rs.getString("name"));
			
			last = rs.getTimestamp("last_modified").toLocalDateTime();
			first = rs.getTimestamp("date_added").toLocalDateTime();
			
			book = new Book( rs.getInt("id")
				 	, rs.getString("title")
				 	, rs.getString("summary")
				 	, rs.getInt("year_published")
				 	, rs.getString("ISBN")
				 	, publisher);
			
			book.setLastModified(last);
			book.setDateAdded(first);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
			if(rs != null) {
				rs.close();
			}
			if(st != null) {
				st.close();
			}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return book;
	}
	
	public List<Audit> getAudits(int book_id){
		
		List<Audit> 		Audits  = new ArrayList<>();
		PreparedStatement 	st 		= null;
		ResultSet 			rs		= null;
		
		try {
			st = conn.prepareStatement( "SELECT a.* from book_audit_trail a WHERE a.book_id = ?");
		
			st.setInt(1, book_id);
			
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				LocalDateTime ldt = null;
				
				ldt = rs.getTimestamp("date_added").toLocalDateTime();
				
				Audit Audit = new Audit( rs.getInt("book_id")
								 	   , ldt
								 	   , rs.getString("entry_msg"));
				
				Audits.add(Audit);
			}
		} catch (Exception e) {
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
		
		return Audits;
		
	}
	
	public List<Book> getBooks() throws Exception{	// TimeStamp : O/O
		List<Book> Books = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement( "select b.*, p.* FROM Book b, Publisher p "
									  + "WHERE b.publisher_id = p.publisher_id "
									  + "ORDER BY title ASC");
		
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				LocalDateTime ldt = null;
				
				Publisher publisher = new Publisher(rs.getInt("publisher_id"), rs.getString("name"));
				
				Book Book = new Book( rs.getInt("id")
								 	, rs.getString("title")
								 	, rs.getString("summary")
								 	, rs.getInt("year_published")
								 	, rs.getString("ISBN")
								 	, publisher);
				
				Book.setDateAdded(rs.getTimestamp("date_added").toLocalDateTime());
				
				ldt = rs.getTimestamp("last_modified").toLocalDateTime();
				
				Book.setLastModified(ldt);
				
				Books.add(Book);
			}
		} catch (Exception e) {
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
	
	public List<Publisher> getPublishers(){
		
		List<Publisher> publishers = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("select * FROM Publisher ORDER BY name ASC");
			
			rs = st.executeQuery();
			
			while(rs.next()) {
				publishers.add(new Publisher(rs.getInt("publisher_id"), rs.getString("name")));
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
	
	public void updateBook(Book Book) throws Exception {// TimeStamp : X/X
		
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
			st.setInt(4, Book.getPublisher().getPublisherId());
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
		
		try {
			st   = conn.prepareStatement("SELECT * FROM Book WHERE id = ?");
			st.setInt(1,  key);
			rs   = st.executeQuery();
			rs.next();
			ldt  = rs.getTimestamp("last_modified").toLocalDateTime();
			
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
