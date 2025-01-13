package reservation.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CancelBookingFrame extends JFrame {
    private Connection conn;
    private String customerId;
    private ViewBookingInfoFrame parentFrame;

    public CancelBookingFrame(Connection conn, String customerId, ViewBookingInfoFrame parentFrame) {
        this.conn = conn;
        this.customerId = customerId;
        this.parentFrame = parentFrame;
        setTitle("예매 취소");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel instructionLabel1 = new JLabel("취소할 예매 번호를 입력하세요 : ");
        instructionLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel1.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

        JLabel instructionLabel2 = new JLabel("여러 개의 번호는 쉼표로 구분");
        instructionLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel2.setFont(new Font(Font.DIALOG, Font.BOLD, 12));

        JTextField reservationNumberField = new JTextField();
        reservationNumberField.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));

        JButton cancelButton = new JButton("예매 취소");
        JButton closeButton = new JButton("닫기");
        cancelButton.setPreferredSize(new Dimension(140, 40));
        closeButton.setPreferredSize(new Dimension(140, 40));
        cancelButton.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.white);

        closeButton.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        closeButton.setBackground(new Color(100, 149, 237));
        closeButton.setForeground(Color.white);

        inputPanel.add(instructionLabel1);
        inputPanel.add(instructionLabel2);
        inputPanel.add(reservationNumberField);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        buttonPanel.add(cancelButton);
        buttonPanel.add(closeButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // 취소 버튼 액션 리스너
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = reservationNumberField.getText();
                if (inputText.isEmpty()) {
                    JOptionPane.showMessageDialog(CancelBookingFrame.this, "예매번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] reservationNumbers = inputText.split(",");
                try {
                    boolean allExists = true; // 모든 예매번호가 존재하는지 여부
                    for (String number : reservationNumbers) {
                        int reservationNumber = Integer.parseInt(number.trim());
                        if (!isBookingExists(reservationNumber)) {
                            allExists = false;
                            break; // 존재하지 않는 예매번호가 있으면 반복문 중단
                        }
                    }
                    if (!allExists) {
                        JOptionPane.showMessageDialog(CancelBookingFrame.this, "입력된 예매번호 중 일부가 존재하지 않거나 예매된 사용자가 아닙니다.", "취소 실패", JOptionPane.ERROR_MESSAGE);
                        return; // 존재하지 않는 예매번호가 있으면 메소드 종료
                    }

                    // 모든 예매번호가 존재하므로 취소 처리
                    for (String number : reservationNumbers) {
                        int reservationNumber = Integer.parseInt(number.trim());
                        cancelBooking(reservationNumber);
                    }
                    JOptionPane.showMessageDialog(CancelBookingFrame.this, "예매가 성공적으로 취소되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);

                    // 예매 정보 테이블 갱신
                    parentFrame.loadData();

                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CancelBookingFrame.this, "유효한 예매번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 닫기 버튼 액션 리스너
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private boolean isBookingExists(int reservationNumber) {
        try {
            PreparedStatement checkReservationStmt = conn.prepareStatement("SELECT * FROM reservation WHERE ReservationNumber = ? AND UserId = ?");
            checkReservationStmt.setInt(1, reservationNumber);
            checkReservationStmt.setString(2, customerId);
            ResultSet rs = checkReservationStmt.executeQuery();
            boolean exists = rs.next();
            rs.close();
            checkReservationStmt.close();
            return exists;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "예매 정보를 확인하는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void cancelBooking(int reservationNumber) {
        try {
            conn.setAutoCommit(false);

            // 티켓 삭제 및 좌석 정보 갱신
            String selectSeatQuery = "SELECT ScheduleNumber, SeatNumber FROM ticket WHERE ReservationNumber = ?";
            try (PreparedStatement selectSeatStmt = conn.prepareStatement(selectSeatQuery)) {
                selectSeatStmt.setInt(1, reservationNumber);
                ResultSet rs = selectSeatStmt.executeQuery();
                if (rs.next()) {
                    int scheduleNumber = rs.getInt("ScheduleNumber");
                    int seatNumber = rs.getInt("SeatNumber");

                    // 좌석 사용 여부 수정
                    String updateSeatQuery = "UPDATE seat SET SeatAvailability = false WHERE ScheduleNumber = ? AND SeatNumber = ?";
                    try (PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatQuery)) {
                        updateSeatStmt.setInt(1, scheduleNumber);
                        updateSeatStmt.setInt(2, seatNumber);
                        updateSeatStmt.executeUpdate();
                    }
                }
            }

            // 티켓 삭제
            String deleteTicketQuery = "DELETE FROM ticket WHERE ReservationNumber = ?";
            try (PreparedStatement deleteTicketStmt = conn.prepareStatement(deleteTicketQuery)) {
                deleteTicketStmt.setInt(1, reservationNumber);
                deleteTicketStmt.executeUpdate();
            }

            // 예매 삭제
            String deleteReservationQuery = "DELETE FROM reservation WHERE ReservationNumber = ?";
            try (PreparedStatement deleteReservationStmt = conn.prepareStatement(deleteReservationQuery)) {
                deleteReservationStmt.setInt(1, reservationNumber);
                deleteReservationStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "예매 취소 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
