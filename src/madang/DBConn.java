package madang;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConn {
  public static Connection makeConnection(){
    String url = "jdbc:mysql://127.0.0.1/madang?serverTimezone=Asia/Seoul";
    Connection conn = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      System.out.println("데이터베이스 연결중....");
      conn = DriverManager.getConnection(url, "root", "1111");
      System.out.println("데이터베이스 연결 성공!!!");
    } catch (SQLException e) {
      System.out.println("JDBC 드라이버를 찾지 못함");
    } catch (ClassNotFoundException e) {
      System.out.println("데이터베이스 연결 실패");
    }
    return conn;
  }
}
