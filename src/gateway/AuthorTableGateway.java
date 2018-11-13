package gateway;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import model.Author;
import model.AuthorBook;
import model.Book;

public class AuthorTableGateway {

	private Connection conn;
	private static AuthorTableGateway instance = null;
	
	
	public AuthorTableGateway() throws Exception{	// TimeStamp : X/X
		
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
	
	public static AuthorTableGateway getInstance() throws Exception{// TimeStamp : X/X
		if(instance == null) {
			instance = new AuthorTableGateway();
		}
		return instance;
	}
	
	public List<Author> getAuthors(){
		
		List<Author> 		authors  = new ArrayList<>();
		PreparedStatement 	st 		= null;
		ResultSet 			rs		= null;
		
		try {
			st = conn.prepareStatement( "SELECT a.* FROM Author a ORDER BY a.last_name ASC");
			
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				LocalDate dob = null;
				
				dob = rs.getTimestamp("dob").toLocalDateTime().toLocalDate();
				
				Author author = new Author( rs.getInt("id")
					 	   , rs.getString("first_name")
					 	   , rs.getString("last_name")
					 	   , dob
					 	   , rs.getString("gender")
					 	   , rs.getString("website"));
				
				authors.add(author);
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
		
		return authors;
		
	}
	
	public List<AuthorBook> getAuthorsForBook(Book book){
		
		List<AuthorBook> 		authors  = new ArrayList<>();
		PreparedStatement 	st 		= null;
		ResultSet 			rs		= null;
		
		try {
			st = conn.prepareStatement( "SELECT ab.*, a.* FROM AuthorBook ab, Author a, Book b "
					+ "WHERE b.id = ? "
					+ "AND a.id = ab.author_id "
					+ "AND b.id = ab.book_id "
					+ "ORDER BY a.last_name ASC");
			
			st.setInt(1, book.getId());
			
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				LocalDate dob = null;
				BigDecimal royalty = rs.getBigDecimal("ab.royalty");
				
				dob = rs.getTimestamp("a.dob").toLocalDateTime().toLocalDate();
				
				Author author = new Author( rs.getInt("a.id")
					 	   , rs.getString("a.first_name")
					 	   , rs.getString("a.last_name")
					 	   , dob
					 	   , rs.getString("a.gender")
					 	   , rs.getString("a.website"));				
				
				AuthorBook authorBook = new AuthorBook(author, book, royalty);
				authorBook.setNewRecord(false);
				
				authors.add(authorBook);
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
		
		return authors;
		
	}

	public void addAuthorBook(AuthorBook authorBook) throws Exception{
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement( "INSERT INTO AuthorBook ("
									  + "author_id, "
									  + "book_id, "
									  + "royalty "
									  + ") VALUES ( ?, ?, ?)");
			st.setInt(1, authorBook.getAuthor().getId());
			st.setInt(2, authorBook.getBook().getId());
			st.setBigDecimal(3, authorBook.getRoyalty());
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
	
	public void updateAuthorBook(AuthorBook authorBook) throws Exception{
		
		if (authorBook.getNewRecord()) {
			addAuthorBook(authorBook);
		} else {
		
			PreparedStatement st = null;
			try {
				conn.setAutoCommit(false);
				
				st = conn.prepareStatement( "UPDATE AuthorBook ab "
										  + "SET royalty = ? "
										  + "WHERE ab.author_id = ? "
										  + "AND ab.book_id = ? ");
				st.setBigDecimal(1, authorBook.getRoyalty());
				st.setInt(2, authorBook.getAuthor().getId());
				st.setInt(3, authorBook.getBook().getId());
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
	}
	
	public void deleteAuthorBook(AuthorBook authorBook) throws Exception{
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("DELETE FROM AuthorBook "
										+ "WHERE author_id = ? "
										+ "AND book_id = ? ");
			st.setInt(1, authorBook.getAuthor().getId());
			st.setInt(2, authorBook.getBook().getId());
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
	
	public Boolean doesExist(AuthorBook authorBook){
		
		PreparedStatement 	st 		= null;
		ResultSet 			rs		= null;
		
		try {
			st = conn.prepareStatement( "SELECT ab.* "
					+ "FROM AuthorBook ab "
					+ "WHERE ab.author_id = ? "
					+ "AND ab.book_id = ? ");
			
			st.setInt(1, authorBook.getAuthor().getId());
			st.setInt(2, authorBook.getBook().getId());
			
			rs = st.executeQuery();
			
			if (rs.next()) {
				return true;
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
		
		return false;
		
	}
}
