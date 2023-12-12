import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GUI extends JFrame {

    private JButton signUpButton;
    private JButton loginButton;
    private SignUpManager signUpManager;

    public GUI() {
        // 프레임 설정
        setTitle("Recipe Recommendation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 창을 화면 중앙에 표시
        setLayout(null); // null 레이아웃을 사용하여 직접 좌표 지정

        // 회원가입 버튼
        signUpButton = new JButton("회원가입");
        signUpButton.setBounds(100, 100, 150, 50); // x, y, width, height
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 회원가입 기능 수행하는 메서드 호출
                try {
                    signUpManager.showSignUpDialog();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.setBounds(300, 100, 150, 50); // x, y, width, height
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 로그인 기능 수행하는 메서드 호출
                try {
                    performLogin();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        signUpManager = new SignUpManager(this);

        // 프레임에 버튼 추가
        add(signUpButton);
        add(loginButton);

        // 창을 보이도록 설정
        setVisible(true);
    }

    // 로그인 기능 수행하는 메서드
    private void performLogin() throws Exception {
        // 여기에 로그인 로직을 추가하세요
        String username = JOptionPane.showInputDialog(this, "사용자 이름을 입력하세요:");
        String password = JOptionPane.showInputDialog(this, "비밀번호를 입력하세요:");

        // 오라클 데이터베이스 연결 정보
        String driver = "oracle.jdbc.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String user = "choiha";
        String pass = "1234";
        Class.forName(driver);
        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT * FROM member WHERE member_id = ? AND password_hash = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "로그인 성공!");
                        SwingUtilities.invokeLater(() -> openIngredientInputGUI(username));
                    } else {
                        JOptionPane.showMessageDialog(this, "로그인 실패. 잘못된 사용자 이름 또는 비밀번호.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 연결 오류.");
        }
    }

    private void openIngredientInputGUI(String username) {
        // 로그인 성공 시 호출되는 메서드
        IngredientInputGUI ingredientInputGUI = new IngredientInputGUI(username);
        ingredientInputGUI.setVisible(true); // 창을 보이도록 설정
    }
}
