package reservation.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ChangeBookingScheduleFrame extends JFrame {
    private Connection conn;
    private String customerId;
    private int reservationNumber;
    private int currentScheduleNumber;
    private int currentTheaterNumber;
    private int currentSeatNumber;
    private JComboBox<String> scheduleComboBox;
    private JButton selectSeatButton;
    private ViewBookingInfoFrame parentFrame;
    private Map<String, Integer> scheduleMap;

    public ChangeBookingScheduleFrame(Connection conn, String customerId, int reservationNumber, int currentScheduleNumber, int currentTheaterNumber, int currentSeatNumber, ViewBookingInfoFrame parentFrame) {
        this.conn = conn;
        this.customerId = customerId;
        this.reservationNumber = reservationNumber;
        this.currentScheduleNumber = currentScheduleNumber;
        this.currentTheaterNumber = currentTheaterNumber;
        this.currentSeatNumber = currentSeatNumber;
        this.parentFrame = parentFrame;

        setTitle("예매 일정 변경");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("새로운 일정 선택:"));

        scheduleComboBox = new JComboBox<>();
        panel.add(scheduleComboBox);

        selectSeatButton = new JButton("좌석 선택");
        selectSeatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = scheduleComboBox.getSelectedIndex();
                if (selectedIndex >= 0) {
                    String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
                    int newScheduleNumber = getScheduleNumberFromSelection(selectedSchedule);
                    if (newScheduleNumber != -1) {
                        int newTheaterNumber = getTheaterNumberFromScheduleNumber(newScheduleNumber);
                        cancelCurrentSeat();
                        new SelectSeatFrame(conn, customerId, reservationNumber, currentScheduleNumber, currentTheaterNumber, currentSeatNumber, newScheduleNumber, newTheaterNumber) {
                            @Override
                            public void dispose() {
                                super.dispose();
                                ChangeBookingScheduleFrame.this.dispose();
                                parentFrame.refreshTable();
                            }
                        }.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(ChangeBookingScheduleFrame.this, "선택한 일정의 정보를 찾을 수 없습니다.");
                    }
                } else {
                    JOptionPane.showMessageDialog(ChangeBookingScheduleFrame.this, "변경할 일정을 선택하세요.");
                }
            }
        });
        panel.add(selectSeatButton);

        add(panel);

        scheduleMap = new HashMap<>();
        loadSchedules();
        setVisible(true);
    }

    private void loadSchedules() {
        scheduleComboBox.removeAllItems();
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.ScheduleNumber, s.ScreeningStartDate, s.ScreeningStartTime, m.MovieName " +
                "FROM schedule s " +
                "JOIN movie m ON s.MovieNumber = m.MovieNumber " +
                "WHERE m.MovieName = (SELECT m2.MovieName FROM schedule s2 JOIN movie m2 ON s2.MovieNumber = m2.MovieNumber WHERE s2.ScheduleNumber = ?) " +
                "AND s.ScheduleNumber != ?");
            stmt.setInt(1, currentScheduleNumber);
            stmt.setInt(2, currentScheduleNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String screeningDate = rs.getString("ScreeningStartDate");
                String screeningTime = rs.getString("ScreeningStartTime");
                String movieName = rs.getString("MovieName");
                String displayText = "[" + screeningDate + " " + screeningTime + "] " + movieName;
                scheduleMap.put(displayText, rs.getInt("ScheduleNumber"));
                scheduleComboBox.addItem(displayText);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "스케줄 정보를 가져오는 중 오류가 발생했습니다.");
        }
    }

    private int getScheduleNumberFromSelection(String selectedSchedule) {
        return scheduleMap.getOrDefault(selectedSchedule, -1);
    }

    private int getTheaterNumberFromScheduleNumber(int scheduleNumber) {
        int theaterNumber = -1;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT TheaterNumber FROM schedule WHERE ScheduleNumber = ?");
            stmt.setInt(1, scheduleNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                theaterNumber = rs.getInt("TheaterNumber");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "상영관 정보를 가져오는 중 오류가 발생했습니다.");
        }
        return theaterNumber;
    }

    private void cancelCurrentSeat() {
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE seat SET SeatAvailability = false WHERE ScheduleNumber = ? AND SeatNumber = ?");
            stmt.setInt(1, currentScheduleNumber);
            stmt.setInt(2, currentSeatNumber);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "현재 좌석 정보를 취소하는 중 오류가 발생했습니다.");
        }
    }
}
