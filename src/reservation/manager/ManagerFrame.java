package reservation.manager;

import reservation.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ManagerFrame extends JFrame {
    private Connection conn;

    public ManagerFrame() {
        setTitle("관리자 메인 페이지");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Connection 초기화
        loginServer();

        JLabel welcomeLabel = new JLabel("환영합니다, 관리자님!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        JButton initDatabaseButton = new JButton("데이터베이스 초기화");
        initDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseInitDialog.show(conn);
            }
        });

        JButton modifyDatabaseButton = new JButton("데이터베이스 수정");
        modifyDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DatabaseModifyFrame(conn).setVisible(true);
            }
        });

        JButton showTablesButton = new JButton("전체 테이블 보기");
        showTablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShowTablesFrame(conn).setVisible(true);
            }
        });

        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 버튼 사이의 간격을 설정
        gbc.fill = GridBagConstraints.HORIZONTAL; // 버튼이 가로로 확장되도록 설정
        gbc.weightx = 1.0; // 버튼이 가로로 동일한 크기를 가지도록 설정

        // 중앙에 버튼 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(initDatabaseButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(modifyDatabaseButton, gbc);

        gbc.gridy = 2;
        buttonPanel.add(showTablesButton, gbc);

        // 로그아웃 버튼은 가장 아래에 배치
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.PAGE_END; // 버튼을 아래쪽에 고정
        buttonPanel.add(logoutButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loginServer() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "lldj123414");
            // 사용자에 해당하는 데이터 베이스 연결 필요
            System.out.println("DB 연결 완료");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
