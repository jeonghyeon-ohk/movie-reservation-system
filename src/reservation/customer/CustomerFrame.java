package reservation.customer;

import reservation.LoginFrame;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerFrame extends JFrame {
    private Connection conn;
    private String customerId;

    public CustomerFrame(String customerId) {
        this.customerId = customerId;
        setTitle("메가박스 영화 예매");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(50, 0, 60)); // 배경색 변경

        // Connection 초기화
        loginServer();

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(new Color(50, 0, 60)); // 배경색 맞춤

        JLabel titleLabel = new JLabel("메가박스 영화 예매", JLabel.CENTER);
        titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24)); // 한글 폰트 사용
        titleLabel.setForeground(new Color(255, 215, 0)); // 금색 글자

        JLabel welcomeLabel = new JLabel("환영합니다, " + customerId + "님!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16)); // 한글 폰트 사용
        welcomeLabel.setForeground(Color.white); // 흰색 글자


        LocalDateTime now = LocalDateTime.now();
        String currentDate = "오늘은 " + now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시")) + "입니다";

        JLabel dateLabel = new JLabel(currentDate, JLabel.CENTER);
        dateLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        northPanel.add(titleLabel);
        northPanel.add(welcomeLabel);
        northPanel.add(dateLabel);

        add(northPanel, BorderLayout.NORTH);

        JButton viewMoviesButton = new JButton("영화 조회 및 예매");
        JButton viewBookingInfoButton = new JButton("예매정보 확인");
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.setForeground(Color.white);
        logoutButton.setBackground(new Color(100, 50, 120));

        styleButton(viewMoviesButton);
        styleButton(viewBookingInfoButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.add(viewMoviesButton);
        buttonPanel.add(viewBookingInfoButton);
        buttonPanel.setBackground(new Color(50, 0, 60));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(50, 0, 60));
        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(50, 0, 60));
        logoutPanel.add(logoutButton);
        add(logoutPanel, BorderLayout.SOUTH);

        viewMoviesButton.addActionListener(e -> new ViewMoviesFrame(conn, customerId).setVisible(true));
        viewBookingInfoButton.addActionListener(e -> new ViewBookingInfoFrame(conn, customerId).setVisible(true));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

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
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 14)); // 한글 폰트
        button.setForeground(Color.white);
        button.setBackground(new Color(100, 50, 120));
    }
}
