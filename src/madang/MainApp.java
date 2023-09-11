package madang;

import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

public class MainApp {
  static Connection conn = DBConn.makeConnection();
  static Scanner in = new Scanner(System.in);
  public static void main(String[] args) {

    int menu =0;
    if(conn != null) {
      while(true){
        System.out.println("메뉴를 선택해주세요 (종료하려면 -1) ");
        System.out.println("(1:도서리스트, 2:고객리스트, 3:주문리스트 ");
        System.out.print("11:도서추가등록, 12:고객추가등록, 13:주문등록) : ");
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
            case 11:
              addBook();
              break;
            case 12:
              addCustomer();
              break;
            case 13:
              addOrder();
              break;
          }
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private static void addBook() throws SQLException {
    // 도서목록 중 bookid 가 가장 큰 번호 가져오기
    String sql = "SELECT max(bookid) FROM book;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    int bookid = 0;
    if(rs != null && rs.next()) {
      bookid = rs.getInt(1);
    }
    // 도서정보 입력받아서 db에 추가하기
    System.out.print("등록할 책 정보를 책이름, 출판사, 가격 순으로 입력하세요.");
    String[] bookinfos = in.next().split(",");
    sql = "insert into book values (?,?,?,?);";
    pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1,bookid + 1);
    pstmt.setString(2,bookinfos[0]);
    pstmt.setString(3,bookinfos[1]);
    pstmt.setInt(4,Integer.parseInt(bookinfos[2]));
    int res = pstmt.executeUpdate();
    if(res == 1) System.out.println("등록완료!");
  }

  private static void addCustomer() throws SQLException {
    // 고객목록 중 custid 가 가장 큰 번호 가져오기
    String sql = "SELECT max(custid) FROM customer;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    int custid = 0;
    if(rs != null && rs.next()) {
      custid = rs.getInt(1);
    }
    // 고객 정보 입력받아서 db에 추가하기
    System.out.print("등록할 고객 정보를 고객이름, 주소, 전화번호 순으로 입력하세요.");
    String[] custinfos = in.next().split(",");
    sql = "insert into customer values (?,?,?,?);";
    pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1,custid + 1);
    pstmt.setString(2,custinfos[0]); // 고객이름
    pstmt.setString(3,custinfos[1]); // 주소
    pstmt.setString(4,custinfos[2]); // 전화번호
    int res = pstmt.executeUpdate();
    if(res == 1) System.out.println("등록완료!");
  }

  private static void addOrder() throws SQLException {
    // 고객정보와 도서정보 validation check
    Integer custId = 0, bookId = 0, salePrice = 0;
    String sql="";
    String name ="", bookInfo = "";

    System.out.print("주문을 등록할 고객id,도서id를 입력하세요.");
    String[] orderinfos = in.next().split(",");
    custId = Integer.parseInt(orderinfos[0]);
    name = validCustomer(custId);
    bookId = Integer.parseInt(orderinfos[1]);
    bookInfo = validBook(bookId);

    // valid한 고객과 도서정보를 이용하여 주문정보를 db에 추가하기
    if(name != null && bookInfo != null) {
      String[] bookInfos = bookInfo.split(",");
      String now = LocalDate.now().toString();
      System.out.printf("고객명 : %s , 책이름 : %s, 가격 : %s 가 맞다면 판매가격을 입력하세요 :",
          name , bookInfos[0], bookInfos[1]);
      salePrice = in.nextInt();
      sql = "insert into orders (custid, bookid, saleprice, orderdate, bname) values " +
            "(?,?,?,?,?);";
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1,custId);
      pstmt.setInt(2,bookId);
      pstmt.setInt(3,salePrice);
      pstmt.setString(4, now);
      pstmt.setString(5,bookInfos[0]); // 책이름
      int res = pstmt.executeUpdate();
      if(res == 1) System.out.println("등록완료!");
    }
  }
  static String validCustomer(int custId) throws SQLException {
    String sql = "select name from customer where custid = ?;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1,custId);
    ResultSet rs = pstmt.executeQuery();

    if(rs != null && rs.next()) {
      return rs.getString(1);
    }
    return null;
  }

  private static String validBook(int bookId) throws SQLException {
    String sql = "select bookname, price from book where bookid = ?;";
    String bookInfo = "";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1,bookId);
    ResultSet rs = pstmt.executeQuery();

    if(rs != null && rs.next()) {
      bookInfo = rs.getString(1);
      bookInfo += ",";
      bookInfo += rs.getInt(2);
    }
    return bookInfo;
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
    } else {
      System.out.println("출력할 책 내역이 없습니다.");
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
    } else {
      System.out.println("출력할 고객 내역이 없습니다.");
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
    } else {
      System.out.println("출력할 주문 내역이 없습니다.");
    }
  }
}
