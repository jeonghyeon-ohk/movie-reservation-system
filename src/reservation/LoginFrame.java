package reservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import reservation.customer.CustomerFrame;
import reservation.manager.ManagerFrame;

public class LoginFrame extends JFrame {
    private JTextField idText;
    private JPasswordField passwordText;
    private JRadioButton managerRadioButton;
    private JRadioButton customerRadioButton;
    private JComboBox<String> customerComboBox;
    private JButton loginButton;

//    private static final String MANAGER_ID = "root";
//    private static final String MANAGER_PASSWORD = "1234";
//    private static final String CUSTOMER_ID = "user1"; 
//    private static final String CUSTOMER_PASSWORD = "user1"; 

    private static final String MANAGER_ID = "";
    private static final String MANAGER_PASSWORD = "";
    private static final String CUSTOMER_ID = ""; 
    private static final String CUSTOMER_PASSWORD = ""; 

    private static final String[] CUSTOMER_IDS = {"apple", "banana", "orange", "grape", "strawberry", 
                                                  "pineapple", "watermelon", "kiwi", "peach", 
                                                  "cherry", "melon", "blueberry"};

    public LoginFrame() {
        setTitle("로그인 페이지");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(50, 0, 60)); // CustomerFrame과 동일한 배경색
        panel.setForeground(Color.WHITE);

        placeComponents(panel);

        add(panel);
        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;  // 모든 요소들 가운데 정렬
        constraints.insets = new Insets(10, 10, 10, 10);  // 요소 사이의 간격을 늘림

        // 메인 타이틀
        JLabel mainTitle = new JLabel("영화 예매 서비스", JLabel.CENTER);
        mainTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 24)); // 한글 폰트 사용
        mainTitle.setForeground(new Color(255, 215, 0)); // 금색 글자
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(mainTitle, constraints);

        // "아이디" 레이블
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(idLabel, constraints);

        // 아이디 입력 필드
        idText = new JTextField(10);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(idText, constraints);

        // "비밀번호" 레이블
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);

        // 비밀번호 입력 필드
        passwordText = new JPasswordField(10);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(passwordText, constraints);

        // "관리자" 라디오 버튼
        managerRadioButton = new JRadioButton("관리자");
        managerRadioButton.setBackground(new Color(50, 0, 60)); // 배경색 맞춤
        managerRadioButton.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(managerRadioButton, constraints);

        // "사용자" 라디오 버튼
        customerRadioButton = new JRadioButton("사용자");
        customerRadioButton.setBackground(new Color(50, 0, 60)); // 배경색 맞춤
        customerRadioButton.setForeground(Color.WHITE);
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(customerRadioButton, constraints);

        // 라디오 버튼 그룹화
        ButtonGroup group = new ButtonGroup();
        group.add(managerRadioButton);
        group.add(customerRadioButton);

        // 사용자 선택 콤보박스
        customerComboBox = new JComboBox<>(CUSTOMER_IDS);
        constraints.gridx = 1;  // 이 부분은 customerComboBox가 customerRadioButton 바로 아래에 위치하도록 설정
        constraints.gridy = 4;
        panel.add(customerComboBox, constraints);
        customerComboBox.setEnabled(false);

        // 로그인 버튼
        loginButton = new JButton("로그인");
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;  // 로그인 버튼이 두 칸을 차지하도록
        panel.add(loginButton, constraints);

        customerRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customerComboBox.setEnabled(true);
            }
        });

        managerRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customerComboBox.setEnabled(false);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String id = idText.getText();
        String password = new String(passwordText.getPassword());

        if (managerRadioButton.isSelected()) {
            if (id.equals(MANAGER_ID) && password.equals(MANAGER_PASSWORD)) {
                goToManagerPage();
            } else {
                JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 잘못되었습니다. 다시 시도해주세요.");
            }
        } else if (customerRadioButton.isSelected()) {
            String selectedCustomerId = (String) customerComboBox.getSelectedItem();
            if (id.equals(CUSTOMER_ID) && password.equals(CUSTOMER_PASSWORD) && selectedCustomerId != null) {
                goToCustomerPage(selectedCustomerId);
            } else {
                JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 잘못되었습니다. 다시 시도해주세요.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "역할을 선택해주세요 (관리자/사용자)");
        }
    }

    private void goToManagerPage() {
        dispose();
        new ManagerFrame().setVisible(true);
    }

    private void goToCustomerPage(String customerId) {
        dispose();
        new CustomerFrame(customerId).setVisible(true);
    }
}
