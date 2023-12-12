import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Component;
import java.sql.SQLException;
import java.awt.Desktop;
import java.net.URI;

public class IngredientInputGUI extends JFrame {
    private JTextField ingredientField;
    private JButton saveButton;
    private JButton myFridgeButton; // 내 냉장고 버튼 추가

    static final String driver = "oracle.jdbc.OracleDriver";
    static final String url = "jdbc:oracle:thin:@localhost:1521:XE";
    static final String user = "choiha";
    static final String pass = "1234";

    public IngredientInputGUI(String member_id) {
        setTitle("식자재 입력");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 현재 창만 닫기
        setLocationRelativeTo(null);

        // 패널 설정
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // 식자재 입력 필드
        JLabel label = new JLabel("식자재 입력:");
        label.setBounds(20, 20, 100, 25);
        ingredientField = new JTextField();
        ingredientField.setBounds(120, 20, 200, 25);

        // 저장 버튼
        saveButton = new JButton("저장");
        saveButton.setBounds(150, 80, 100, 30);

        // 내 냉장고 버튼
        myFridgeButton = new JButton("내 냉장고");
        myFridgeButton.setBounds(270, 80, 100, 30);

        // 패널에 컴포넌트 추가
        panel.add(label);
        panel.add(ingredientField);
        panel.add(saveButton);
        panel.add(myFridgeButton);

        // 프레임에 패널 추가
        add(panel);
        // 창을 보이도록 설정
        setVisible(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ingredient = ingredientField.getText();
                try {
                    saveIngredient(member_id, ingredient);
                    JOptionPane.showMessageDialog(null, "입력");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        myFridgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showMyFridge(member_id);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void saveIngredient(String member_id, String ingredient) throws Exception {
        Class.forName(driver);
        try (Connection db = DriverManager.getConnection(url, user, pass)) {
            String sql = "INSERT INTO ingredients (member_id, ingredient_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = db.prepareStatement(sql)) {
                pstmt.setString(1, member_id);
                pstmt.setString(2, ingredient);
                pstmt.execute();
            }
        }
    }

    private void showMyFridge(String member_id) throws Exception {
        Class.forName(driver);
        try (Connection db = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT ingredient_id FROM ingredients WHERE member_id = ?";
            try (PreparedStatement pstmt = db.prepareStatement(sql)) {
                pstmt.setString(1, member_id);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    StringBuilder fridgeContents = new StringBuilder();
                    while (resultSet.next()) {
                        String ingredient = resultSet.getString("ingredient_id");
                        fridgeContents.append(ingredient).append("\n");
                    }

                    if (fridgeContents.length() > 0) {
                        // 다이얼로그 생성
                        JDialog dialog = new JDialog();
                        dialog.setSize(300, 400);
                        dialog.setLocationRelativeTo(this);

                        // 버튼 패널 생성
                        JPanel buttonPanel = new JPanel();
                        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));


                        // 내 냉장고 출력
                        JTextArea fridgeTextArea = new JTextArea("<<<<<내 냉장고>>>>>:\n" + fridgeContents.toString());
                        fridgeTextArea.setEditable(false);
                        buttonPanel.add(fridgeTextArea);

                        buttonPanel.add(Box.createHorizontalGlue());// 가운데 정렬을 위한 공백

                        // 추천 레시피 버튼 추가

                        JButton recommendRecipeButton = new JButton("추천 레시피");
                        buttonPanel.add(recommendRecipeButton);
                        recommendRecipeButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    // Create a database connection
                                    try (Connection connection = DriverManager.getConnection(url, user, pass)) {
                                        // Construct the SQL query to retrieve recipes based on ingredients
                                        String recipeSql = "SELECT DISTINCT foodname, link FROM recipes WHERE ingredient_id IN (SELECT ingredient_id FROM ingredients WHERE member_id = ?)";
                                        try (PreparedStatement recipeStmt = connection.prepareStatement(recipeSql)) {
                                            recipeStmt.setString(1, member_id);

                                            try (ResultSet recipeResultSet = recipeStmt.executeQuery()) {

                                                // Display the recommended recipes
                                                StringBuilder recommendedRecipes = new StringBuilder("추천 레시피:\n");
                                                while (recipeResultSet.next()) {
                                                    String foodname = recipeResultSet.getString("foodname");
                                                    String link = recipeResultSet.getString("link");

                                                    recommendedRecipes.append(foodname).append("\n");

                                                    // JButton을 생성하고 ActionListener를 추가
                                                    JButton foodnameButton = new JButton(foodname);
                                                    foodnameButton.addActionListener(new ActionListener() {
                                                        @Override
                                                        public void actionPerformed(ActionEvent e) {
                                                            try {
                                                                // Desktop 클래스를 사용하여 기본 브라우저를 열고 인터넷 주소로 이동
                                                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                                                    Desktop.getDesktop().browse(new URI(link));
                                                                } else {
                                                                    JOptionPane.showMessageDialog(null, "인터넷 브라우징을 지원하지 않습니다.");
                                                                }
                                                            } catch (Exception ex) {
                                                                ex.printStackTrace();
                                                                JOptionPane.showMessageDialog(null, "인터넷 주소로 이동 중 오류가 발생했습니다.");
                                                            }
                                                        }
                                                    });

                                                    buttonPanel.add(foodnameButton);
                                                }



                                                }





                                                // Show the recommended recipes in a dialog or another UI component
                                                JOptionPane.showMessageDialog(null, recommendedRecipes.toString());
                                            }
                                        }
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "데이터베이스 연결 또는 쿼리 실행 중 오류가 발생했습니다.");
                                }
                            }
                        });




                        JButton deleteButton = new JButton("삭제");
                        deleteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                        deleteButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Prompt the user for the ingredient name
                                String ingredient = JOptionPane.showInputDialog(null, "삭제할 식재료의 이름을 입력하세요:");

                                if (ingredient != null && !ingredient.trim().isEmpty()) {
                                    try {
                                        // Create a database connection (replace 'your_connection_url', 'your_username', and 'your_password' with your actual database information)
                                        Connection connection = DriverManager.getConnection(url, user, pass);
                                        // Create a PreparedStatement with a DELETE query
                                        String deleteQuery = "DELETE FROM ingredients WHERE ingredient_id = ?";
                                        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                                            // Set the parameter in the PreparedStatement
                                            preparedStatement.setString(1, ingredient);

                                            // Execute the DELETE query
                                            int rowsAffected = preparedStatement.executeUpdate();

                                            if (rowsAffected > 0) {
                                                JOptionPane.showMessageDialog(null, "삭제 완료");

                                                // Refresh the displayed contents of the fridge
                                                String sql = "SELECT ingredient_id FROM ingredients WHERE member_id = ?";
                                                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                                                    pstmt.setString(1, member_id);
                                                    try (ResultSet resultSet = pstmt.executeQuery()) {
                                                        StringBuilder fridgeContents = new StringBuilder();
                                                        while (resultSet.next()) {
                                                            String ingredientId = resultSet.getString("ingredient_id");
                                                            fridgeContents.append(ingredientId).append("\n");
                                                        }

                                                        // Update the JTextArea with the refreshed fridge contents
                                                        JTextArea fridgeTextArea = new JTextArea("<<<<<내 냉장고>>>>>:\n" + fridgeContents.toString());
                                                        fridgeTextArea.setEditable(false);
                                                        // Assuming 'buttonPanel' is the container for your JTextArea
                                                        buttonPanel.remove(0); // Remove the existing JTextArea
                                                        buttonPanel.add(fridgeTextArea, 0); // Add the updated JTextArea at the beginning
                                                        buttonPanel.revalidate(); // Revalidate the panel to reflect the changes
                                                        buttonPanel.repaint(); // Repaint the panel to update the UI
                                                    }
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "해당 식재료를 찾을 수 없습니다.");
                                            }
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "유효한 식재료 이름을 입력하세요.");
                                }
                            }
                        });

                        buttonPanel.add(deleteButton);

                        // 다이얼로그에 패널 추가
                        dialog.add(buttonPanel);
                        dialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "냉장고가 비어있습니다.");
                    }
                }
            }
        }
    }
}