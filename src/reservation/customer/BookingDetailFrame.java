package reservation.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BookingDetailFrame extends JFrame {
    public BookingDetailFrame(Connection conn, int reservationNumber) {
        setTitle("예매 상세 정보 페이지");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel ticketPanel = new JPanel();
        ticketPanel.setLayout(new GridBagLayout());
        ticketPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        ticketPanel.setBackground(new Color(210, 210, 210));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel ticketLabel = new JLabel("Ticket");
        ticketLabel.setFont(new Font("Sans Serif", Font.BOLD, 24));
        ticketLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        ticketPanel.add(ticketLabel, gbc);
        gbc.gridwidth = 1;

        JTextArea detailsTextArea = new JTextArea();
        detailsTextArea.setEditable(false);
        detailsTextArea.setFont(new Font("Sans Serif", Font.PLAIN, 14));

        JTextArea movieInfoTextArea = new JTextArea();
        movieInfoTextArea.setEditable(false);
        movieInfoTextArea.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT tk.TicketNumber, tk.ScheduleNumber, tk.TheaterNumber, tk.SeatNumber, " +
                           "tk.ReservationNumber, tk.StandardPrice, tk.SellingPrice, " +
                           "m.MovieName, m.RunningTime, m.FilmRatings, m.Director, m.Actor, m.Genre, m.MovieIntroduction, m.ReleaseDate, " +
                           "s.ScreeningStartDate, s.ScreeningStartTime " +
                           "FROM ticket tk " +
                           "JOIN reservation r ON r.ReservationNumber = tk.ReservationNumber " +
                           "JOIN schedule s ON s.ScheduleNumber = tk.ScheduleNumber " +
                           "JOIN movie m ON m.MovieNumber = s.MovieNumber " +
                           "JOIN theater t ON t.TheaterNumber = tk.TheaterNumber " +
                           "WHERE r.ReservationNumber = " + reservationNumber;
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                addTicketInfo(ticketPanel, rs, gbc);

                String details = String.format("영화명: %s\n상영일시: %s %s\n상영관: %d\n좌석 번호: %d\n예매가격: %d원",
                        rs.getString("MovieName"), rs.getString("ScreeningStartDate"), rs.getString("ScreeningStartTime"),
                        rs.getInt("TheaterNumber"), rs.getInt("SeatNumber"), rs.getInt("SellingPrice"));
                detailsTextArea.setText(details);

                String movieInfo = String.format("영화명: %s\n상영시간: %d분\n등급: %s\n감독: %s\n배우: %s\n장르: %s\n소개: %s\n개봉일: %s\n",
                        rs.getString("MovieName"), rs.getInt("RunningTime"), rs.getString("FilmRatings"),
                        rs.getString("Director"), rs.getString("Actor"), rs.getString("Genre"),
                        rs.getString("MovieIntroduction"), rs.getString("ReleaseDate"));
                movieInfoTextArea.setText(movieInfo);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "예매 세부 정보를 가져오는 중 오류가 발생했습니다.");
        }

        panel.add(ticketPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("예매 상세 정보"));
        detailsPanel.setBackground(new Color(230, 230, 230));
        detailsPanel.add(detailsTextArea, BorderLayout.CENTER);

        JPanel movieInfoPanel = new JPanel();
        movieInfoPanel.setLayout(new BorderLayout());
        movieInfoPanel.setBorder(BorderFactory.createTitledBorder("영화 정보"));
        movieInfoPanel.setBackground(new Color(230, 230, 230));
        movieInfoPanel.add(movieInfoTextArea, BorderLayout.CENTER);

        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(movieInfoPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void addTicketInfo(JPanel panel, ResultSet rs, GridBagConstraints gbc) throws SQLException {
        Font labelFont = new Font("Sans Serif", Font.BOLD, 16);

        String ticketInfo = String.format("티켓 번호: %d, 예매 번호: %d, 예매가격: %d원, 상영관: %d, 좌석 번호: %d",
                rs.getInt("TicketNumber"), rs.getInt("ReservationNumber"),
                rs.getInt("SellingPrice"), rs.getInt("TheaterNumber"),
                rs.getInt("SeatNumber"));

        JLabel label = new JLabel(ticketInfo);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(label, gbc);
    }
}
