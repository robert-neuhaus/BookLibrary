package gateway;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import model.Audit;
import model.Author;
import model.AuthorBook;
import model.Book;
import model.Publisher;

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
				LocalDateTime ldt = null;
				
				dob = rs.getTimestamp("dob").toLocalDateTime().toLocalDate();
				
				Author author = new Author( rs.getInt("id")
					 	   , rs.getString("first_name")
					 	   , rs.getString("last_name")
					 	   , dob
					 	   , rs.getString("gender")
					 	   , rs.getString("website"));
				
				author.setDateAdded(rs.getTimestamp("date_added").toLocalDateTime());
				
				ldt = rs.getTimestamp("last_modified").toLocalDateTime();
				
				author.setLastModified(ldt);
				
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
	
	public Author getAuthor(int id){
		
		Author author = null;
		PreparedStatement 	st 		= null;
		ResultSet 			rs		= null;
		LocalDateTime 	   last	= null;
		LocalDateTime	  first = null;
		
		try {
			st = conn.prepareStatement( "SELECT a.* FROM Author a WHERE a.id = ? ORDER BY a.last_name ASC");
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				LocalDate dob = null;
				
				dob = rs.getTimestamp("dob").toLocalDateTime().toLocalDate();
				last = rs.getTimestamp("last_modified").toLocalDateTime();
				first = rs.getTimestamp("date_added").toLocalDateTime();
				
				author = new Author( rs.getInt("id")
					 	   , rs.getString("first_name")
					 	   , rs.getString("last_name")
					 	   , dob
					 	   , rs.getString("gender")
					 	   , rs.getString("website"));

				author.setLastModified(last);
				author.setDateAdded(first);
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
		
		return author;
		
	}
	
	public void createAuthor(Author author) throws Exception{// TimeStamp : X/O
		PreparedStatement st = null;
		ResultSet 		  rs = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("INSERT INTO Author("
					+ "first_name, "
					+ "last_name, "
					+ "gender, "
					+ "dob, "
					+ "website "
					+ ") VALUES ( ?, ?, ?, ?, ?)");
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setString(3, author.getGender());
			st.setDate(4, Date.valueOf(author.getDOBDate()));
			st.setString(5, author.getWebsite());
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
			st = conn.prepareStatement("SELECT id FROM Author ORDER BY id DESC");
			rs = st.executeQuery();
			rs.next();
			author.setId(rs.getInt("id"));
			
		}catch(SQLException e) {
			throw e;
		}finally {
			if(st != null) {
				st.close();
			}
		}
		
	}
	
	public void updateAuthor(Author author) throws Exception {// TimeStamp : X/X
		
		if(author.getId() == -1) {
			this.createAuthor(author);
		}
		
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("update Author "
					+ "set first_name = ?"
					+ ", last_name = ?"
					+ ", gender = ?"
					+ ", dob = ?"
					+ ", website = ?"
					+ " where id = ?");
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setString(3, author.getGender());
			st.setDate(4, Date.valueOf(author.getDOBDate()));
			st.setString(5, author.getWebsite());
			st.setInt(6, author.getId());
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
	
	public void deleteAuthor(Author author) throws Exception{// TimeStamp : X/X
		
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE FROM Author WHERE id = ?");
			st.setInt(1, author.getId());
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
	
public List<AuthorBook> getBooksForAuthor(Author author){
		
		List<AuthorBook> 	books  = new ArrayList<>();
		PreparedStatement 	st 		= null;
		ResultSet 			rs		= null;
		LocalDateTime 	   last	= null;
		LocalDateTime	  first = null;
		Publisher	  publisher = null;
		
		try {
			st = conn.prepareStatement( "SELECT ab.*, b.*, p.* FROM AuthorBook ab, Author a, Book b, Publisher p "
					+ "WHERE a.id = ? "
					+ "AND a.id = ab.author_id "
					+ "AND b.id = ab.book_id "
					+ "AND p.publisher_id = b.publisher_id "
					+ "ORDER BY b.title ASC");
			
			st.setInt(1, author.getId());
			
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				BigDecimal royalty = rs.getBigDecimal("ab.royalty");
				
				publisher = new Publisher(rs.getInt("p.publisher_id")
						 ,rs.getString("p.name"));

				last = rs.getTimestamp("last_modified").toLocalDateTime();
				first = rs.getTimestamp("date_added").toLocalDateTime();
				
				Book book = new Book( rs.getInt("id")
					 	, rs.getString("title")
					 	, rs.getString("summary")
					 	, rs.getInt("year_published")
					 	, rs.getString("ISBN")
					 	, publisher);	
				
				book.setLastModified(last);
				book.setDateAdded(first);
				
				AuthorBook authorBook = new AuthorBook(author, book, royalty);
				authorBook.setNewRecord(false);
				
				books.add(authorBook);
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
		
		return books;
		
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
	
public void addAudits(List<Audit> audits) throws Exception{
		
		PreparedStatement st = null;
		conn.setAutoCommit(false);
		
		st = conn.prepareStatement("INSERT INTO author_audit_trail ("
								  + "author_id, "
								  + "entry_msg"
								  + ") VALUES (?, ?)");
		
		for(Audit entry : audits) {
			st.setInt(1, entry.getId());
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
	
	public List<Audit> getAudits(int author_id){
		
		List<Audit> 		Audits  = new ArrayList<>();
		PreparedStatement 	st 		= null;
		ResultSet 			rs		= null;
		
		try {
			st = conn.prepareStatement( "SELECT a.* from author_audit_trail a WHERE a.author_id = ? ORDER BY a.date_added DESC");
		
			st.setInt(1, author_id);
			
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				LocalDateTime ldt = null;
				
				ldt = rs.getTimestamp("date_added").toLocalDateTime();
				
				Audit Audit = new Audit( rs.getInt("author_id")
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
	
public LocalDateTime getLastModified(int key){// TimeStamp : O/O
		
		PreparedStatement st = null;
		ResultSet 		  rs = null;
		LocalDateTime    ldt = null;
		
		try {
			st   = conn.prepareStatement("SELECT * FROM Author WHERE id = ?");
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
}
