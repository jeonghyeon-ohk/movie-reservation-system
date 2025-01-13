package reservation.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShowTablesFrame extends JFrame {
    private Connection conn;

    public ShowTablesFrame(Connection conn) {
        setTitle("테이블 정보 보기");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        this.conn = conn;

        JTabbedPane tabbedPane = new JTabbedPane();

        // 각 테이블에 대한 정보를 탭 형태로 추가
        tabbedPane.addTab("영화 (movie)", createTablePanel("SELECT * FROM movie"));
        tabbedPane.addTab("상영일정 (schedule)", createTablePanel("SELECT * FROM schedule"));
        tabbedPane.addTab("상영관 (theater)", createTablePanel("SELECT * FROM theater"));
        tabbedPane.addTab("티켓 (ticket)", createTablePanel("SELECT * FROM ticket"));
        tabbedPane.addTab("좌석 (seat)", createTablePanel("SELECT * FROM seat"));
        tabbedPane.addTab("회원고객 (user)", createTablePanel("SELECT * FROM user"));
        tabbedPane.addTab("예매정보 (Reservation)", createTablePanel("SELECT * FROM Reservation"));

        add(tabbedPane);

        setVisible(true);
    }

    private JPanel createTablePanel(String query) {
        JPanel tablePanel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // 결과의 메타데이터 가져오기 (열 이름)
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(rs.getMetaData().getColumnName(i));
            }

            // 결과의 데이터 가져오기 (행)
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "테이블 정보를 가져오는 도중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }
}
