package BoardUser.function;

import java.util.*;

import BoardUser.information.Board;
import BoardUser.information.User;

public class BoardManager {
	Scanner sc = new Scanner(System.in);
	
	BoardCrud crud = new BoardCrud();
	int choice;
	
	//게시판 글자 포맷 메서드
	private void list() {
		List<Board> boardList = crud.selectMainBoard();
		
		System.out.printf("%5s %20s %20s %20s %20s\n","글번호","제목","내용","ID","날짜");
		System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
		for(Board b : boardList) {
			System.out.printf("%5s %20s %20s %20s %20s\n",b.getBno(),b.getBtitle(),b.getBcontent(),b.getBwriter(),b.getBdate());
		}
	}
	
	//메인 메뉴 메서드
	public void mainMenu() {
	try {
		do {
			list();
			System.out.println("1.유저 로그인");
			System.out.println("2.게시판 초기화");
			System.out.println("3.회원가입");
			System.out.println("0.종료");
			System.out.print("번호를 입력해주세요.");
			choice = Integer.parseInt(sc.nextLine().trim());
			
			switch (choice) {
			case 1: 
				userLogin();
				break;
			case 2:
				crud.deleteAllBoard();
				break;
			case 3:
				userSignup();
			case 0:
				System.out.println("종료합니다.");
				break;
			}
			
		}while(choice !=0);
		} catch (Exception e) {
			System.out.println("잘못 입력하셨습니다.");
			mainMenu();
		}
		
	}
	public void userSignup() {
		System.out.println("아이디를 입력해주세요.");
		String userid = sc.nextLine().trim();
		
		boolean idcheck = crud.userIdCheck(userid);
			if(idcheck) {
				while(crud.userIdCheck(userid)) {
			System.out.println("이미 존재하는 계정입니다. 다른 아이디를 입력해주세요.");
			 userid = sc.nextLine().trim();
				}
			}
		System.out.println("이름을 입력해주세요.");
		String username = sc.nextLine().trim();
		
		System.out.println("비밀번호를 입력해주세요.");
		String userpassword = sc.nextLine().trim();
		
		System.out.println("나이를 입력해주세요.");
		int userage = Integer.parseInt(sc.nextLine().trim());
		
		System.out.println("이메일을 입력해주세요.");
		String useremail = sc.nextLine().trim();
		
		int check =crud.userSignup(new User(userid, username, userpassword, userage, useremail));
		
		if(check !=0) {
			System.out.println(userid+"님 회원가입이 완료되었습니다.");
		}else {
			System.out.println("회원가입을 하지 못했습니다.");
		}
	}

	//유저 로그인 확인
	public void userLogin() {
		
		System.out.println("아이디를 입력해주세요.");
		String id = sc.nextLine().trim();
		
		System.out.println("비밀번호를 입력해주세요.");
		String pwd = sc.nextLine().trim();
		
		User user = new User(id, pwd);
		boolean isLogin = crud.isUserLogin(user);

		if(isLogin) {
			boardManageMenu(user);
		}else {
			System.out.println("로그인 할수 없습니다.");
		}
			
	}
	

	//회원 삭제 메서드
	public void deleteUserMenu(User u) {
		
		System.out.println("회원을 삭제 하겠습니까?");
		System.out.println("1.예");
		System.out.println("2.아니요");
		int choice = Integer.parseInt(sc.nextLine().trim());
		switch (choice) {
		case 1: 
			crud.deleteUser(u);
			mainMenu();
			break;
		case 2:
			System.out.println("로그인 화면으로 돌아갑니다.");
			 boardManageMenu(u);
		}
	}
	
	
	
	//게시글 관리 메뉴 메서드
	public void boardManageMenu(User u) {
	try {
		do {
			list();
			System.out.println(u.getUserid()+"님 로그인");
			System.out.println("1.게시글 생성");
			System.out.println("2.게시글 수정");
			System.out.println("3.게시글 삭제");
			System.out.println("4.게시글 검색");
			System.out.println("5.회원 삭제");
			System.out.println("0.뒤로 가기");
			System.out.print("번호를 입력해주세요.");
			choice = Integer.parseInt(sc.nextLine().trim());
			
			switch (choice) {
			case 1: 
				crud.insertBoard(u);
				break;
			case 2:
				crud.updateBoard(u);
				break;
			case 3:
				crud.deleteBoard(u);
				break;
			case 4:
				crud.selectBoard();
				break;
			case 5:
				deleteUserMenu(u);
				break;
			case 0:
				mainMenu();
				break;
			}
		}while(choice !=0);
		} catch (Exception e) {
		System.out.println("잘못 입력하셨습니다.");
		boardManageMenu(u);
		}
	}
	
}
