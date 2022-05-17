package com.spellhaven.MVCforum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.spellhaven.MVCforum.dto.BDto;

public class BDao {
	
	DataSource dataSource;

	public BDao() {
		super();
		
		try {
			
			// 헐! 이 두 라인은 뭔말임! (몰라. 모르면서 쓰는 거지 뭐🥰)
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/Oracle11g");
			
		} catch (NamingException e) {
			e.printStackTrace();
		}	
		
	}
	
	
	public ArrayList<BDto> list() { // 글 목록 보여 주는 놈, list()
		
		// 왜 반환 타입이 ArrayList<BDto>인가? 글 하나하나가 BDto 배열이고, 글 목록은 그거의 리스트이기 때문이다.
		// JSP로 MVC 패턴 게시판 만들기 할 때도 하신 얘기임, ㅋ,
		
		ArrayList<BDto> dtos = new ArrayList<BDto>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; // 글 목록을 볼 거니까 SELECT문. ResultSet으로 반환값을 받아야 해.
		
		try { // DB에 데이터 넣는 건 나름 위험하니까 try-catch 해야 한디.
			
			conn = dataSource.getConnection();
			String query = "SELECT * FROM mvc_board ORDER BY bgroup DESC, bstep ASC";
			// ORDER BY bgroup DESC, bstep ASC는 whyrano? 이 게시판은 글뿐만 아니라 댓글도 불러와야 하기 때문에 그렇다.
			// 오류 나면 쿼리문 다 소문자로 써 보자. 하찮아 보이지만, 혹시 모르니까...
			
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
				
			while(rs.next()) { // 존재하는 글을 다 긁어오는 while문.
				
				// "필드명" 틀리지 마, ㅋ, 오라클 DB 안의 테이블이랑 딱딱 맞춰.
				int bId = rs.getInt("bid");
				String bName = rs.getString("bname");
				String bTitle = rs.getString("btitle");
				Timestamp bDate = rs.getTimestamp("bdate"); // 이 Timestamp는 java.sql이야... 크킄...
				int bHit = rs.getInt("bhit");
				int bGroup = rs.getInt("bgroup");
				int bStep = rs.getInt("bstep");
				int bIndent = rs.getInt("bindent");
				
				// 9개를 세터로 어떻게 해. 생성자 만들어 두길 잘 했다.
				BDto dto = new BDto(bId, bName, bTitle, bDate, bHit, bGroup, bStep, bIndent);
				dtos.add(dto);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}		
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	
		return dtos;
	}

}
