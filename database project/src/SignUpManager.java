import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpManager {

    private JFrame parentFrame;

    public SignUpManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void showSignUpDialog() throws Exception {
        String username = JOptionPane.showInputDialog(parentFrame, "사용자 이름을 입력하세요:");
        String password = JOptionPane.showInputDialog(parentFrame, "비밀번호를 입력하세요:");

        // 오라클 데이터베이스 연결 정보
        String driver = "oracle.jdbc.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String user = "choiha";
        String pass = "1234";
        Class.forName(driver);

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String sql = "INSERT INTO member (member_id, password_hash) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(parentFrame, "회원가입 성공!");
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "회원가입 실패. 다시 시도하세요.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "데이터베이스 연결 오류.");
        }
    }
}
