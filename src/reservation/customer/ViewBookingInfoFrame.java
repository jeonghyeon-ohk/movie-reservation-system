package reservation.customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class NonEditableModel extends DefaultTableModel {
    public NonEditableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

public class ViewBookingInfoFrame extends JFrame {
    private Connection conn;
    private String customerId;
    private NonEditableModel model;

    public ViewBookingInfoFrame(Connection conn, String customerId) {
        this.conn = conn;
        this.customerId = customerId;
        setTitle("예매 정보 보기");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // 안내 문구 레이블
        JLabel infoLabel = new JLabel("예매 내역을 더블클릭하면 상세정보 페이지로 이동합니다.", JLabel.CENTER);
        infoLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        infoLabel.setForeground(Color.BLACK);

        model = new NonEditableModel(new Object[][]{}, new String[]{"예매번호", "영화명", "상영일", "상영관번호", "좌석번호", "판매가격"});

        JTable table = new JTable(model);
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(200, 220, 255));
        header.setForeground(Color.black);
        header.setFont(new Font("Dialog", Font.BOLD, 12));
        table.setRowHeight(25);
        table.setFont(new Font("Serif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelBookingButton = new JButton("예매 취소");
        JButton changeMovieButton = new JButton("예매 영화 변경");
        JButton changeDateButton = new JButton("예매 날짜 변경");
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(changeMovieButton);
        buttonPanel.add(changeDateButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadData();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int reservationNumber = (int) table.getValueAt(selectedRow, 0);
                        new BookingDetailFrame(conn, reservationNumber).setVisible(true);
                    }
                }
            }
        });
        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CancelBookingFrame(conn, customerId, ViewBookingInfoFrame.this).setVisible(true);
            }
        });

        changeMovieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int reservationNumber = (int) table.getValueAt(selectedRow, 0);
                    int currentMovieNumber = getMovieNumber(reservationNumber);
                    int currentScheduleNumber = getScheduleNumber(reservationNumber);
                    int currentSeatNumber = getSeatNumber(reservationNumber);
                    int currentTheaterNumber = getTheaterNumber(currentScheduleNumber);

                    new ChangeBookedMovieFrame(conn, customerId, reservationNumber, currentMovieNumber, currentScheduleNumber, currentSeatNumber, currentTheaterNumber, ViewBookingInfoFrame.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(ViewBookingInfoFrame.this, "예매를 변경할 영화를 선택하세요.", "선택 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        changeDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int reservationNumber = (int) table.getValueAt(selectedRow, 0);
                    int currentScheduleNumber = getScheduleNumber(reservationNumber);
                    int currentSeatNumber = getSeatNumber(reservationNumber);
                    int currentTheaterNumber = getTheaterNumber(currentScheduleNumber);
                    int currentMovieNumber = getMovieNumber(currentScheduleNumber);
                    new ChangeBookingScheduleFrame(conn, customerId, reservationNumber, currentScheduleNumber, currentTheaterNumber, currentSeatNumber, ViewBookingInfoFrame.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(ViewBookingInfoFrame.this, "예매를 변경할 영화를 선택하세요.", "선택 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(infoLabel, BorderLayout.NORTH); // 안내 문구를 패널 상단에 추가
        add(panel);
        setVisible(true);
    }

    void loadData() {
        model.setRowCount(0);
        try {
            String query = "SELECT DISTINCT r.ReservationNumber, m.MovieName, s.ScreeningStartDate, tk.TheaterNumber, tk.SeatNumber, tk.SellingPrice " +
                           "FROM reservation r " +
                           "JOIN ticket tk ON r.ReservationNumber = tk.ReservationNumber " +
                           "JOIN schedule s ON r.ScheduleNumber = s.ScheduleNumber " +
                           "JOIN movie m ON s.MovieNumber = m.MovieNumber " +
                           "WHERE r.UserId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ReservationNumber"),
                        rs.getString("MovieName"),
                        rs.getString("ScreeningStartDate"),
                        rs.getInt("TheaterNumber"),
                        rs.getInt("SeatNumber"),
                        rs.getInt("SellingPrice")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "예매 정보를 가져오는 중 오류가 발생했습니다.");
        }
    }

    // 테이블 새로고침 메서드 추가
    void refreshTable() {
        loadData();
    }

    private int getScheduleNumber(int reservationNumber) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT ScheduleNumber FROM reservation WHERE ReservationNumber = ?");
            stmt.setInt(1, reservationNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ScheduleNumber");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Error case
    }

    private int getSeatNumber(int reservationNumber) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT SeatNumber FROM ticket WHERE ReservationNumber = ?");
            stmt.setInt(1, reservationNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("SeatNumber");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Error case
    }

    private int getTheaterNumber(int scheduleNumber) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT TheaterNumber FROM schedule WHERE ScheduleNumber = ?");
            stmt.setInt(1, scheduleNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("TheaterNumber");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Error case
    }

    private int getMovieNumber(int reservationNumber) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT MovieNumber FROM schedule WHERE ScheduleNumber = (SELECT ScheduleNumber FROM reservation WHERE ReservationNumber = ?)");
            stmt.setInt(1, reservationNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MovieNumber");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Error case
    }
}
