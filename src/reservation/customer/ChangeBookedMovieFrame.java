package reservation.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangeBookedMovieFrame extends JFrame {
    private Connection conn;
    private String customerId;
    private int reservationNumber;
    private JComboBox<String> movieScheduleComboBox;
    private int currentScheduleNumber;
    private int currentSeatNumber;
    private int currentTheaterNumber;
    private ViewBookingInfoFrame parentFrame;

    public ChangeBookedMovieFrame(Connection conn, String customerId, int reservationNumber, int currentMovieNumber, int currentScheduleNumber, int currentSeatNumber, int currentTheaterNumber, ViewBookingInfoFrame parentFrame) {
        this.conn = conn;
        this.customerId = customerId;
        this.reservationNumber = reservationNumber;
        this.currentScheduleNumber = currentScheduleNumber;
        this.currentSeatNumber = currentSeatNumber;
        this.currentTheaterNumber = currentTheaterNumber;
        this.parentFrame = parentFrame;

        setTitle("예매 영화 변경");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("새로운 영화 일정 선택:"));

        movieScheduleComboBox = new JComboBox<>();
        panel.add(movieScheduleComboBox);

        JButton changeButton = new JButton("예매 변경");
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = movieScheduleComboBox.getSelectedIndex();
                if (selectedIndex >= 0) {
                    String selectedSchedule = (String) movieScheduleComboBox.getSelectedItem();
                    int newScheduleNumber = getScheduleNumberFromSelection(selectedSchedule);
                    if (newScheduleNumber != -1) {
                        int newTheaterNumber = getTheaterNumberFromScheduleNumber(newScheduleNumber);
                        cancelCurrentSeat();
                        new SelectSeatFrame(conn, customerId, reservationNumber, currentScheduleNumber, currentTheaterNumber, currentSeatNumber, newScheduleNumber, newTheaterNumber) {
                            @Override
                            public void dispose() {
                                super.dispose();
                                ChangeBookedMovieFrame.this.dispose();
                                parentFrame.refreshTable();
                            }
                        }.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(ChangeBookedMovieFrame.this, "선택한 영화의 정보를 찾을 수 없습니다.");
                    }
                } else {
                    JOptionPane.showMessageDialog(ChangeBookedMovieFrame.this, "변경할 영화를 선택하세요.");
                }
            }
        });
        panel.add(changeButton);

        add(panel);

        loadMovies(currentMovieNumber);
        setVisible(true);
    }

    private void loadMovies(int currentMovieNumber) {
        movieScheduleComboBox.removeAllItems();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM schedule WHERE MovieNumber != ?");
            stmt.setInt(1, currentMovieNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String screeningDate = rs.getString("ScreeningStartDate");
                String screeningTime = rs.getString("ScreeningStartTime");
                String movieTitle = getMovieTitle(rs.getInt("MovieNumber"));
                movieScheduleComboBox.addItem("[" + screeningDate + " " + screeningTime + "] " + movieTitle);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "영화 정보를 가져오는 중 오류가 발생했습니다.");
        }
    }

    private String getMovieTitle(int movieNumber) {
        String movieTitle = "";
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT MovieName FROM movie WHERE MovieNumber = ?");
            stmt.setInt(1, movieNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                movieTitle = rs.getString("MovieName");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "영화 정보를 가져오는 중 오류가 발생했습니다.");
        }
        return movieTitle;
    }

    private int getScheduleNumberFromSelection(String selectedSchedule) {
        int scheduleNumber = -1;
        try {
            String[] parts = selectedSchedule.split("] ");
            String dateAndTime = parts[0].substring(1);
            String[] dateAndTimeParts = dateAndTime.split(" ");
            String screeningDate = dateAndTimeParts[0];
            String screeningTime = dateAndTimeParts[1];
            String movieTitle = parts[1];

            PreparedStatement stmt = conn.prepareStatement("SELECT s.ScheduleNumber FROM schedule s JOIN movie m ON s.MovieNumber = m.MovieNumber WHERE m.MovieName = ? AND s.ScreeningStartDate = ? AND s.ScreeningStartTime = ?");
            stmt.setString(1, movieTitle);
            stmt.setString(2, screeningDate);
            stmt.setString(3, screeningTime);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                scheduleNumber = rs.getInt("ScheduleNumber");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "스케줄 정보를 가져오는 중 오류가 발생했습니다.");
        }
        return scheduleNumber;
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
