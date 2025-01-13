package reservation.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SelectSeatFrame extends JFrame {
    private Connection conn;
    private String customerId;
    private int reservationNumber;
    private int currentScheduleNumber;
    private int currentTheaterNumber;
    private int currentSeatNumber;
    private int newScheduleNumber;
    private int newTheaterNumber;
    private boolean[][] seatAvailability;

    public SelectSeatFrame(Connection conn, String customerId, int reservationNumber, int currentScheduleNumber, int currentTheaterNumber, int currentSeatNumber, int newScheduleNumber, int newTheaterNumber) {
        this.conn = conn;
        this.customerId = customerId;
        this.reservationNumber = reservationNumber;
        this.currentScheduleNumber = currentScheduleNumber;
        this.currentTheaterNumber = currentTheaterNumber;
        this.currentSeatNumber = currentSeatNumber;
        this.newScheduleNumber = newScheduleNumber;
        this.newTheaterNumber = newTheaterNumber;

        setTitle("좌석 선택");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 5, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadSeatAvailability();
        loadSeats(panel);

        add(panel);
        setVisible(true);
    }

    private void loadSeatAvailability() {
        try {
            // 상영관의 좌석 정보 가져오기
            String query = "SELECT NumberOfSeats, HorizontalSeat, VerticalSeat FROM theater WHERE TheaterNumber = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, newTheaterNumber);
            ResultSet rs = pstmt.executeQuery();
            int rows = 0;
            int cols = 0;
            if (rs.next()) {
                rows = rs.getInt("HorizontalSeat");
                cols = rs.getInt("VerticalSeat");
            }

            // 좌석 배열 초기화
            seatAvailability = new boolean[rows][cols];

            // 모든 좌석을 사용 가능으로 초기화
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    seatAvailability[row][col] = false; // 사용 가능
                }
            }

            // 예약된 좌석 정보 로드
            query = "SELECT SeatNumber FROM reservation JOIN ticket ON reservation.ReservationNumber = ticket.ReservationNumber WHERE reservation.ScheduleNumber = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, newScheduleNumber);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int seatNumber = rs.getInt("SeatNumber");
                int row = (seatNumber - 1) / cols;
                int col = (seatNumber - 1) % cols;
                seatAvailability[row][col] = true; // 사용 중
            }

            // 좌석 테이블에서 사용 가능 여부 로드
            query = "SELECT SeatNumber, SeatAvailability FROM seat WHERE ScheduleNumber = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, newScheduleNumber);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int seatNumber = rs.getInt("SeatNumber");
                boolean isAvailable = rs.getInt("SeatAvailability") == 0;
                int row = (seatNumber - 1) / cols;
                int col = (seatNumber - 1) % cols;
                seatAvailability[row][col] = seatAvailability[row][col] || !isAvailable;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSeats(JPanel panel) {
        try {
            int rows = seatAvailability.length;
            int cols = seatAvailability[0].length;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int seatNumber = row * cols + col + 1;
                    JButton seatButton = new JButton(String.valueOf(seatNumber));
                    if (seatAvailability[row][col]) {
                        seatButton.setEnabled(false);
                        seatButton.setBackground(Color.RED);
                    } else {
                        seatButton.setBackground(Color.GREEN);
                        seatButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                selectSeat(seatNumber);
                            }
                        });
                    }
                    panel.add(seatButton);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "좌석 정보를 가져오는 중 오류가 발생했습니다.");
        }
    }

    private void selectSeat(int seatNumber) {
        try {
            conn.setAutoCommit(false);

            // 기존 좌석을 해제합니다.
            PreparedStatement updateOldSeatStmt = conn.prepareStatement("UPDATE seat SET SeatAvailability = 0 WHERE ScheduleNumber = ? AND SeatNumber = ? AND TheaterNumber = ?");
            updateOldSeatStmt.setInt(1, currentScheduleNumber);
            updateOldSeatStmt.setInt(2, currentSeatNumber);
            updateOldSeatStmt.setInt(3, currentTheaterNumber);
            updateOldSeatStmt.executeUpdate();

            // 새로운 좌석을 예약합니다.
            // 먼저 좌석이 존재하는지 확인합니다.
            PreparedStatement checkSeatStmt = conn.prepareStatement("SELECT * FROM seat WHERE ScheduleNumber = ? AND SeatNumber = ? AND TheaterNumber = ?");
            checkSeatStmt.setInt(1, newScheduleNumber);
            checkSeatStmt.setInt(2, seatNumber);
            checkSeatStmt.setInt(3, newTheaterNumber);
            ResultSet rs = checkSeatStmt.executeQuery();

            if (!rs.next()) {
                // 좌석이 존재하지 않으면 삽입합니다.
                PreparedStatement insertSeatStmt = conn.prepareStatement("INSERT INTO seat (SeatNumber, TheaterNumber, ScheduleNumber, SeatAvailability) VALUES (?, ?, ?, ?)");
                insertSeatStmt.setInt(1, seatNumber);
                insertSeatStmt.setInt(2, newTheaterNumber);
                insertSeatStmt.setInt(3, newScheduleNumber);
                insertSeatStmt.setInt(4, 1); // 예약 상태로 설정
                insertSeatStmt.executeUpdate();
            } else {
                // 좌석이 존재하면 업데이트합니다.
                PreparedStatement updateNewSeatStmt = conn.prepareStatement("UPDATE seat SET SeatAvailability = 1 WHERE ScheduleNumber = ? AND SeatNumber = ? AND TheaterNumber = ?");
                updateNewSeatStmt.setInt(1, newScheduleNumber);
                updateNewSeatStmt.setInt(2, seatNumber);
                updateNewSeatStmt.setInt(3, newTheaterNumber);
                updateNewSeatStmt.executeUpdate();
            }

            // 예약 정보를 업데이트합니다.
            PreparedStatement updateReservationStmt = conn.prepareStatement("UPDATE reservation SET ScheduleNumber = ? WHERE ReservationNumber = ?");
            updateReservationStmt.setInt(1, newScheduleNumber);
            updateReservationStmt.setInt(2, reservationNumber);
            updateReservationStmt.executeUpdate();

            // 티켓 정보를 업데이트합니다.
            PreparedStatement updateTicketStmt = conn.prepareStatement("UPDATE ticket SET ScheduleNumber = ?, SeatNumber = ?, TheaterNumber = ? WHERE ReservationNumber = ?");
            updateTicketStmt.setInt(1, newScheduleNumber);
            updateTicketStmt.setInt(2, seatNumber);
            updateTicketStmt.setInt(3, newTheaterNumber);
            updateTicketStmt.setInt(4, reservationNumber);
            updateTicketStmt.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "예매가 성공적으로 변경되었습니다.");
            dispose();
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "예매 변경 중 오류가 발생했습니다.");
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
