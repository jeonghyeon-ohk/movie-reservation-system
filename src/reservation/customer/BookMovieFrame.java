package reservation.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BookMovieFrame extends JFrame {
    private Connection conn;
    private String customerId;
    private int movieNumber;
    private String scheduleInfo;
    private int scheduleNumber;
    private JLabel movieInfoLabel;
    private JPanel seatPanel;
    private JButton bookButton;
    private boolean[][] seatAvailability;
    private int selectedRow = -1;
    private int selectedCol = -1;

    public BookMovieFrame(Connection conn, String customerId, int movieNumber, String scheduleInfo, int scheduleNumber) {
        this.conn = conn;
        this.customerId = customerId;
        this.movieNumber = movieNumber;
        this.scheduleInfo = scheduleInfo;
        this.scheduleNumber = scheduleNumber;
        setTitle("영화 예매");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        movieInfoLabel = new JLabel(getMovieInfo(), SwingConstants.CENTER);
        contentPane.add(movieInfoLabel, BorderLayout.NORTH);

        loadSeatAvailability();
        populateSeatButtons();
        contentPane.add(seatPanel, BorderLayout.CENTER);

        bookButton = new JButton("예매 완료");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSelectedSeat();
            }
        });
        contentPane.add(bookButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String getMovieInfo() {
        String movieInfo = "";
        try {
            String query = "SELECT * FROM movie WHERE MovieNumber = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, movieNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                movieInfo = "<html>영화명: " + rs.getString("MovieName") +
                            "<br>상영시간: " + scheduleInfo +
                            "<br>상영등급: " + rs.getString("FilmRatings") +
                            "<br>감독: " + rs.getString("Director") +
                            "<br>배우: " + rs.getString("Actor") +
                            "<br>장르: " + rs.getString("Genre") +
                            "<br>영화소개: " + rs.getString("MovieIntroduction") +
                            "<br>개봉일자: " + rs.getDate("ReleaseDate") +
                            "<br>평점: " + rs.getDouble("MovieRating") + "</html>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movieInfo;
    }

    private void loadSeatAvailability() {
        try {
            // 상영관의 좌석 정보 가져오기
            String query = "SELECT NumberOfSeats, HorizontalSeat, VerticalSeat FROM theater WHERE TheaterNumber = (SELECT TheaterNumber FROM schedule WHERE ScheduleNumber = ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, scheduleNumber);
            ResultSet rs = pstmt.executeQuery();
            int rows = 0;
            int cols = 0;
            if (rs.next()) {
                rows = rs.getInt("HorizontalSeat");
                cols = rs.getInt("VerticalSeat");
            }

            // 좌석 배열 초기화
            seatAvailability = new boolean[rows][cols];

            // 좌석 사용 가능 여부 로드
            query = "SELECT SeatNumber, SeatAvailability FROM seat WHERE ScheduleNumber = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, scheduleNumber);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int seatNumber = rs.getInt("SeatNumber");
                boolean isAvailable = rs.getInt("SeatAvailability") == 0;
                int row = (seatNumber - 1) / cols;
                int col = (seatNumber - 1) % cols;
                seatAvailability[row][col] = !isAvailable;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateSeatButtons() {
        int rows = seatAvailability.length;
        int cols = seatAvailability[0].length;
        seatPanel = new JPanel(new GridLayout(rows, cols, 5, 5));

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JButton seatButton = new JButton((row + 1) + "-" + (col + 1));
                if (seatAvailability[row][col]) {
                    seatButton.setEnabled(false);
                    seatButton.setBackground(Color.RED);
                } else {
                    seatButton.setBackground(Color.GREEN);
                    int finalRow = row;
                    int finalCol = col;
                    seatButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            selectSeat(finalRow, finalCol);
                        }
                    });
                }
                seatPanel.add(seatButton);
            }
        }
    }

    private void selectSeat(int row, int col) {
        if (selectedRow != -1 && selectedCol != -1) {
            JButton prevButton = (JButton) seatPanel.getComponent(selectedRow * seatAvailability[0].length + selectedCol);
            prevButton.setBackground(Color.GREEN);
        }
        selectedRow = row;
        selectedCol = col;
        JButton selectedButton = (JButton) seatPanel.getComponent(row * seatAvailability[0].length + col);
        selectedButton.setBackground(Color.YELLOW);
    }

    private void bookSelectedSeat() {
        if (selectedRow == -1 || selectedCol == -1) {
            JOptionPane.showMessageDialog(this, "좌석을 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int newReservationNumber = getNewReservationNumber();
            int newTicketNumber = getNewTicketNumber();
            Date currentDate = Date.valueOf("2024-01-01");
            int paymentAmount = 10000; // 예시로서 10000원으로 설정

            String reservationQuery = "INSERT INTO reservation (ReservationNumber, UserId, PaymentMethod, PaymentStatus, PaymentAmount, PaymentDate, ScheduleNumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement reservationPstmt = conn.prepareStatement(reservationQuery);
            reservationPstmt.setInt(1, newReservationNumber);
            reservationPstmt.setString(2, customerId);
            reservationPstmt.setString(3, "Credit Card"); // 예시로서 신용카드 결제
            reservationPstmt.setInt(4, 1); // 결제 상태 1 (결제 완료)
            reservationPstmt.setInt(5, paymentAmount);
            reservationPstmt.setDate(6, currentDate);
            reservationPstmt.setInt(7, scheduleNumber);
            reservationPstmt.executeUpdate();

            int theaterNumber = getTheaterNumber();
            int seatNumber = selectedRow * seatAvailability[0].length + selectedCol + 1;

            String ticketQuery = "INSERT INTO ticket (TicketNumber, ScheduleNumber, IssuanceStatus, StandardPrice, SellingPrice, SeatNumber, TheaterNumber, ReservationNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ticketPstmt = conn.prepareStatement(ticketQuery);
            ticketPstmt.setInt(1, newTicketNumber);
            ticketPstmt.setInt(2, scheduleNumber);
            ticketPstmt.setInt(3, 1); // 발급 상태 1 (발급 완료)
            ticketPstmt.setInt(4, paymentAmount);
            ticketPstmt.setInt(5, paymentAmount);
            ticketPstmt.setInt(6, seatNumber); // 좌석 번호
            ticketPstmt.setInt(7, theaterNumber);
            ticketPstmt.setInt(8, newReservationNumber);
            ticketPstmt.executeUpdate();

            // 좌석 사용 여부 업데이트
            String updateSeatQuery = "UPDATE seat SET SeatAvailability = 1 WHERE ScheduleNumber = ? AND SeatNumber = ?";
            PreparedStatement updateSeatPstmt = conn.prepareStatement(updateSeatQuery);
            updateSeatPstmt.setInt(1, scheduleNumber);
            updateSeatPstmt.setInt(2, seatNumber);
            updateSeatPstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "예매가 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getNewReservationNumber() {
        int maxReservationNumber = 0;
        try {
            String query = "SELECT MAX(ReservationNumber) AS MaxReservationNumber FROM reservation";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                maxReservationNumber = rs.getInt("MaxReservationNumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxReservationNumber + 1;
    }

    private int getNewTicketNumber() {
        int maxTicketNumber = 0;
        try {
            String query = "SELECT MAX(TicketNumber) AS MaxTicketNumber FROM ticket";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                maxTicketNumber = rs.getInt("MaxTicketNumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxTicketNumber + 1;
    }

    private int getTheaterNumber() {
        int theaterNumber = 0;
        try {
            String query = "SELECT TheaterNumber FROM schedule WHERE ScheduleNumber = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, scheduleNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                theaterNumber = rs.getInt("TheaterNumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theaterNumber;
    }
}
