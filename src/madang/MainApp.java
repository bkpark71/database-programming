package madang;

import java.sql.*;
import java.util.Scanner;

public class MainApp {
  static Connection conn = DBConn.makeConnection();
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    int menu =0;
    if(conn != null) {
      while(true){
        System.out.print("메뉴를 선택해주세요(1:도서리스트, 2:고객리스트, 3:주문리스트 (종료하려면 -1) : ");
        menu = in.nextInt();
        if(menu < 0){
          System.out.println("프로그램을 종료합니다.");
          break;
        }
        try {
          switch (menu){
            case 1:
              bookList();
              break;
            case 2:
              customerList();
              break;
            case 3:
              orderList();
              break;
          }
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  static void bookList() throws SQLException {
    String sql = "SELECT * FROM book;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    if(rs != null) {
      System.out.println("---------------------------------------------------");
      System.out.println("bookid| price |         bookname      |  publisher  ");
      System.out.println("---------------------------------------------------");
      while (rs.next()) {
        System.out.printf(" %2d ",rs.getInt(1));
        System.out.printf(" %7d ",rs.getInt("price"));
        System.out.printf("  %-20s ", rs.getString("bookname"));
        System.out.printf(" %-20s \n",rs.getString("publisher"));

      }
      System.out.println("---------------------------------------------------");
    }
  }

  static void customerList() throws SQLException {
    String sql = "SELECT * FROM customer;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    if(rs != null) {
      System.out.println("--------------------------------------------");
      System.out.println("고객id|  고객명  |  전화번호    |     주   소   ");
      System.out.println("--------------------------------------------");
      while (rs.next()) {
        System.out.printf(" %2d ",rs.getInt(1));
        System.out.printf(" %5s  ", rs.getString("name"));
        System.out.printf(" %-15s ", rs.getString("phone"));
        System.out.printf(" %-20s \n",rs.getString("address"));

      }
      System.out.println("-------------------------------------------");
    }
  }

  static void orderList() throws SQLException {
    String sql = "SELECT * FROM vorders;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    if(rs != null) {
      System.out.println("---------------------------------------------------------------");
      System.out.println("주문id|  고객정보  | 판매가 |  판매일자  |          도서정보     ");
      System.out.println("---------------------------------------------------------------");
      while (rs.next()) {
        System.out.printf(" %2d ",rs.getInt("orderid"));
        System.out.printf(" %2d ",rs.getInt("custid"));
        System.out.printf(" %5s ",rs.getString("name"));
        System.out.printf(" %6d ", rs.getInt("saleprice"));
        System.out.printf(" %s " , rs.getDate("orderdate"));
        System.out.printf(" %2d ",rs.getInt("bookid"));
        System.out.printf(" %-20s \n", rs.getString("bookname"));
      }
      System.out.println("---------------------------------------------------------------");
    }
  }
}
