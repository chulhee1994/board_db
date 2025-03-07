 package BoardUser.function;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import BoardUser.information.Board;
import BoardUser.information.User;

public class BoardCrud {
	//스캐너 사용
	Scanner sc = new Scanner(System.in);
	List<Board> boardList;
	
	//jdbc 사용
	PreparedStatement pstmt;
	Statement stmt;
	ResultSet rs;
	
	String bno;
	String sql;
	int rows;
	
	//게시판 전체 출력
	public List<Board> selectMainBoard() {
		
		ConnectionClass.conn = ConnectionClass.connection();
		
		String sql = "SELECT * FROM BOARD1";
		
		try {
			Statement st = ConnectionClass.conn.createStatement();
			ResultSet rs =	st.executeQuery(sql);
			boardList = new ArrayList<Board>();
			
			//한 행씩 가져와서 객체배열에  넣어준다.
			while (rs.next()) {
				int bno = rs.getInt("bno");
				String btitle = rs.getString("btitle");
				String bcontent = rs.getString("bcontent");
				String bwriter = rs.getString("bwriter");
				Date bdate = rs.getDate("bdate");
				Board b = new Board(bno, btitle, bcontent, bwriter, bdate);
				boardList.add(b);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionClass.connectionLogout(ConnectionClass.conn);
		}
		return boardList;
		
		//배열 출력부 
		
	}
	
	//게시판 일부 출력 아이디 받아서 가져옴
	public List<Board> selectUserBoard(User u){
		boardList = new ArrayList<Board>();
		ConnectionClass.conn = ConnectionClass.connection();
		
		String sql = "SELECT * FROM BOARD1 WHERE BWRITER = ?";
		try {
			pstmt = ConnectionClass.conn.prepareStatement(sql);
			pstmt.setString(1, u.getUserid());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Board b = new Board();
				b.setBno(rs.getInt("bno"));
				b.setBtitle(rs.getString("btitle"));
				b.setBcontent(rs.getString("bcontent"));
				b.setBwriter(rs.getString("bwriter"));
				b.setBdate(rs.getDate("bdate"));
				boardList.add(b);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return boardList;
		
	}
	
	//유저 로그인 메서드
	public boolean isUserLogin(User u) {
		ConnectionClass.conn = ConnectionClass.connection();
		sql = "SELECT USERID, USERPASSWORD FROM USERS WHERE USERID =? AND USERPASSWORD =?";
		//System.out.println(sql);
		try {
			pstmt = ConnectionClass.conn.prepareStatement(sql);
			
			pstmt.setString(1, u.getUserid());
			pstmt.setString(2, u.getUserpassword());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	//게시글 생성 기능
	public void insertBoard(User u) {
		
		ConnectionClass.conn = ConnectionClass.connection();
		
		sql = "INSERT INTO BOARD1(bno, btitle, bcontent, bwriter, bdate)"+
				" values(BNO_SEQ.NEXTVAL,?,?,?,SYSDATE)";
		//sql 출력문
		//System.out.println(sql);
		
		//제목의 길이 30 이상 받을경우 예외 처리
		System.out.println("제목을 입력해주세요.");
		String btitle = sc.nextLine();
		
		
		//내용의 길이 50이상 받을경우 예외처리
		System.out.println("내용을 입력해주세요.");
		String bcontent = sc.nextLine();
		
		//작성자 입력을 생략하고 정보를 가지고 포맷
		
		String bwriter = u.getUserid();
		
		try {
			pstmt = ConnectionClass.conn.prepareStatement(sql);
			pstmt.setString(1, btitle);
			pstmt.setString(2, bcontent);
			pstmt.setString(3, bwriter);
			
			rows  = pstmt.executeUpdate();
			
			if(rows ==1) {
				System.out.println("게시글을 올렸습니다.");
			}else {
				System.out.println("게시글을 올리지 못했습니다.");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			System.out.println("입력 하려는 길이가 저장하는 길이보다 깁니다.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionClass.connectionLogout(ConnectionClass.conn);
		}
		
	}
		
	//게시판 업데이트 기능
	public void updateBoard(User u){
		try {
		
		boardList = selectUserBoard(u);
		if(!boardList.isEmpty()) {
			System.out.println("수정 가능한 게시글 목록입니다.");
			System.out.printf("%5s %20s %20s %20s %20s\n","글번호","제목","내용","ID","날짜");
			System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
			for(Board b : boardList) {
					System.out.printf("%5s %20s %20s %20s %20s\n",b.getBno(),b.getBtitle(),b.getBcontent(),b.getBwriter(),b.getBdate());
			}
			
			
			System.out.println("수정할 게시글 번호를 입력해주세요.");
			bno = sc.nextLine().trim();
			
			boolean bnoCheck = false;
			for(Board b : boardList) {
				if(b.getBno() == Integer.parseInt(bno)) {
					bnoCheck = true;
					break;
				}
			}
			
			if(bnoCheck) {
				System.out.println("수정할 제목을 입력해주세요.");
				String btitle = sc.nextLine();
				
				System.out.println("수정할 내용을 입력해주세요.");
				String bcontent = sc.nextLine();
				String sql = "UPDATE BOARD1 SET BTITLE = ?,BCONTENT =? WHERE bno =? AND bwriter = (SELECT USERID FROM USERS WHERE USERID = ?)";
				
				ConnectionClass.conn = ConnectionClass.connection();
				pstmt = ConnectionClass.conn.prepareStatement(sql);
				pstmt.setString(1, btitle);
				pstmt.setString(2, bcontent);
				pstmt.setInt(3, Integer.parseInt(bno));
				pstmt.setString(4, u.getUserid());
				rows = pstmt.executeUpdate();
			}else {
				System.out.println("수정할수 없는 게시글입니다.");
			}
		}else {
			System.out.println("수정할 게시글이 없습니다.");
		}
		
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				ConnectionClass.connectionLogout(ConnectionClass.conn);
			}
			
	}//update
	
	//게시판 삭제 기능
	public void deleteBoard(User u) {
		ConnectionClass.conn = ConnectionClass.connection();
		
		boardList = selectUserBoard(u);
		
		if(!boardList.isEmpty()) {
			System.out.println("삭제 가능한 게시글입니다.");
			System.out.printf("%5s %20s %20s %20s %20s\n","글번호","제목","내용","ID","날짜");
			System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
			for(Board b : boardList) {
					System.out.printf("%5s %20s %20s %20s %20s\n",b.getBno(),b.getBtitle(),b.getBcontent(),b.getBwriter(),b.getBdate());
			}
		}
		System.out.println("삭제할 게시글 번호를 입력해주세요.");
		String bno = sc.nextLine().trim();
		
		sql = "DELETE FROM BOARD1 WHERE bno =? AND bwriter = (select USERID FROM USERS WHERE USERID =?)";
		
		try {
			pstmt = ConnectionClass.conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(bno));
			pstmt.setString(2, u.getUserid());
			rows = pstmt.executeUpdate();
			
			if(rows ==1) {
				System.out.println(bno+"번 게시글을 삭제했습니다.");
			}else {
				System.out.println("삭제할 게시글이 없습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	finally {
			ConnectionClass.connectionLogout(ConnectionClass.conn);
		}
	}
	
	//게시판 리스트 출력 기능(bno 번호를 받아서 검색한다.)
	public void selectBoard() {
		ConnectionClass.conn = ConnectionClass.connection();
		System.out.print("검색할 게시글 번호를 입력해주세요.");
		String bno = sc.nextLine().trim();
		
		sql = "SELECT * FROM BOARD1 WHERE BNO =?";
		
		try {
			pstmt = ConnectionClass.conn.prepareStatement(sql);
			
			pstmt.setInt(1, Integer.parseInt(bno));
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				System.out.println(bno +"번 게시글이 존재합니다.");
				System.out.printf("%5s %20s %20s %20s %20s\n","글번호","제목","내용","ID","날짜");
				System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
					Board b = new Board();
					b.setBno(rs.getInt("bno"));
					b.setBtitle(rs.getString("btitle"));
					b.setBcontent(rs.getString("bcontent"));
					b.setBwriter(rs.getString("bwriter"));
					b.setBdate(rs.getDate("bdate"));
					System.out.printf("%5s %20s %20s %20s %20s\n",b.getBno(),b.getBtitle(),b.getBcontent(),b.getBwriter(),b.getBdate());
			}else {
				System.out.println(bno +"번 게시글이 존재하지 않습니다.");
			}			
			} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionClass.connectionLogout(ConnectionClass.conn);
		}
	}
	
	//게시판 내용삭제 
	public void deleteAllBoard() {
		ConnectionClass.conn = ConnectionClass.connection();
		sql = "delete from board1";
		
		try {
			stmt = ConnectionClass.conn.createStatement();
			rows = stmt.executeUpdate(sql);
			if(rows !=0) {
				System.out.println("게시판이 삭제 되었습니다.");
			}else {
				System.out.println("게시판을 삭제하지 못했습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionClass.connectionLogout(ConnectionClass.conn);
		}
	}
	//회원 삭제
	public void deleteUser(User u) {
	ConnectionClass.conn  = ConnectionClass.connection();
	sql = "delete from users where userid =?";
	try {
		pstmt = ConnectionClass.conn.prepareStatement(sql);
		System.out.println("아이디" + u.getUserid());
		String id = u.getUserid();
		pstmt.setString(1, id);
		rows = pstmt.executeUpdate(sql);
		
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		ConnectionClass.connectionLogout(ConnectionClass.conn);
	}
		
	}
	
	//아이디 중복확인 메서드
	public boolean userIdCheck(String id) {
		ConnectionClass.conn = ConnectionClass.connection();
		sql = "select * from users where userid =?";
		try {
			pstmt = ConnectionClass.conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			ConnectionClass.connectionLogout(ConnectionClass.conn);
		}
		return false;
	}
	
	//회원가입 메서드
	public int userSignup(User u) {
		ConnectionClass.conn = ConnectionClass.connection();
		sql = "INSERT INTO USERS VALUES(?,?,?,?,?)";
		try {
			pstmt = ConnectionClass.conn.prepareStatement(sql);
			pstmt.setString(1, u.getUserid());
			pstmt.setString(2, u.getUsername());
			pstmt.setString(3, u.getUserpassword());
			pstmt.setInt(4, u.getUserage());
			pstmt.setString(5, u.getUseremail());
			rows = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			ConnectionClass.connectionLogout(ConnectionClass.conn);
		}
		return rows;
	}
	
}
