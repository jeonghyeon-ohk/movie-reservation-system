package reservation.manager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

public class DatabaseModifyFrame extends JFrame {
    private Connection conn;

    public DatabaseModifyFrame(Connection conn) {
        this.conn = conn;

        setTitle("데이터베이스 수정 페이지");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("SQL 쿼리문 형태로 입력해주세요:", JLabel.CENTER);

        JTextArea textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // 실행 버튼 생성 및 액션 리스너 추가
        JButton executeButton = new JButton("실행");
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = textArea.getText().trim();
                if (!sql.isEmpty()) {
                    try {
                        executeSQL(sql);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(DatabaseModifyFrame.this, "SQL 문장을 실행하는 도중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(DatabaseModifyFrame.this, "SQL 문장을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // 닫기 버튼 생성 및 액션 리스너 추가
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // 버튼 패널에 버튼 추가
        buttonPanel.add(executeButton);
        buttonPanel.add(closeButton);

        panel.add(instructionLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        setVisible(true);
    }

    private void executeSQL(String sql) throws SQLException {
        try (var stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            JOptionPane.showMessageDialog(this, "SQL 문장이 성공적으로 실행되었습니다. 영향을 받은 행 수: " + affectedRows, "성공", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
