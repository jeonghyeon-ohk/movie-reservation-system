package reservation.customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;

public class ViewMoviesFrame extends JFrame {
    private Connection conn;
    private String customerId;
    private JComboBox<String> movieNameComboBox;
    private JComboBox<String> directorComboBox;
    private JComboBox<String> actorComboBox;
    private JComboBox<String> genreComboBox;
    private JTable movieTable;
    private DefaultTableModel movieTableModel;
    private Map<Integer, JComboBox<String>> scheduleComboBoxMap;
    private JButton bookButton;

    public ViewMoviesFrame(Connection conn, String customerId) {
        this.conn = conn;
        this.customerId = customerId;
        this.scheduleComboBoxMap = new HashMap<>();
        setTitle("영화 조회");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel movieNameLabel = new JLabel("영화명:");
        movieNameComboBox = new JComboBox<>();
        filterPanel.add(movieNameLabel);
        filterPanel.add(movieNameComboBox);

        JLabel directorLabel = new JLabel("감독:");
        directorComboBox = new JComboBox<>();
        filterPanel.add(directorLabel);
        filterPanel.add(directorComboBox);

        JLabel actorLabel = new JLabel("배우:");
        actorComboBox = new JComboBox<>();
        filterPanel.add(actorLabel);
        filterPanel.add(actorComboBox);

        JLabel genreLabel = new JLabel("장르:");
        genreComboBox = new JComboBox<>();
        filterPanel.add(genreLabel);
        filterPanel.add(genreComboBox);

        JButton filterButton = new JButton("조회");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchMovies();
            }
        });
        filterPanel.add(filterButton);

        contentPane.add(filterPanel, BorderLayout.NORTH);

        movieTableModel = new DefaultTableModel(
                new String[]{"영화명", "상영시간", "상영등급", "감독", "배우", "장르", "영화소개", "개봉일자", "평점", "상영시작시간"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movieTable = new JTable(movieTableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        movieTable.setFillsViewportHeight(true);
        movieTable.getTableHeader().setReorderingAllowed(false);
        movieTable.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(movieTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        bookButton = new JButton("예매");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveToBookMovieFrame();
            }
        });
        contentPane.add(bookButton, BorderLayout.SOUTH);

        loadFilters();
        fetchMovies();

        setVisible(true);
    }

    private void loadFilters() {
        loadComboBoxData("SELECT DISTINCT MovieName FROM movie", movieNameComboBox);
        loadComboBoxData("SELECT DISTINCT Director FROM movie", directorComboBox);
        loadComboBoxData("SELECT DISTINCT Actor FROM movie", actorComboBox);
        loadComboBoxData("SELECT DISTINCT Genre FROM movie", genreComboBox);
    }

    private void loadComboBoxData(String query, JComboBox<String> comboBox) {
        comboBox.addItem("");
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                comboBox.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchMovies() {
        movieTableModel.setRowCount(0);
        try {
            String query = "SELECT * FROM movie WHERE 1=1";
            if (movieNameComboBox.getSelectedItem() != null && !movieNameComboBox.getSelectedItem().toString().isEmpty()) {
                query += " AND MovieName = '" + movieNameComboBox.getSelectedItem().toString() + "'";
            }
            if (directorComboBox.getSelectedItem() != null && !directorComboBox.getSelectedItem().toString().isEmpty()) {
                query += " AND Director = '" + directorComboBox.getSelectedItem().toString() + "'";
            }
            if (actorComboBox.getSelectedItem() != null && !actorComboBox.getSelectedItem().toString().isEmpty()) {
                query += " AND Actor = '" + actorComboBox.getSelectedItem().toString() + "'";
            }
            if (genreComboBox.getSelectedItem() != null && !genreComboBox.getSelectedItem().toString().isEmpty())
            {
                query += " AND Genre = '" + genreComboBox.getSelectedItem().toString() + "'";
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    int movieNumber = rs.getInt("MovieNumber");
                    List<String> schedules = getSchedules(movieNumber);
                    for (String schedule : schedules) {
                        movieTableModel.addRow(new Object[]{
                                rs.getString("MovieName"),
                                rs.getInt("RunningTime"),
                                rs.getString("FilmRatings"),
                                rs.getString("Director"),
                                rs.getString("Actor"),
                                rs.getString("Genre"),
                                rs.getString("MovieIntroduction"),
                                rs.getDate("ReleaseDate"),
                                rs.getDouble("MovieRating"),
                                schedule
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> getSchedules(int movieNumber) {
        List<String> schedules = new ArrayList<>();
        try {
            String query = "SELECT TIME_FORMAT(ScreeningStartTime, '%H:%i:%s') AS ScreeningStartTime FROM schedule WHERE MovieNumber = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, movieNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                schedules.add(rs.getString("ScreeningStartTime"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    private void moveToBookMovieFrame() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow != -1) {
            String movieName = (String) movieTableModel.getValueAt(selectedRow, 0);
            int movieNumber = getMovieNumber(movieName); // 영화 이름으로부터 영화 번호 가져오기
            if (movieNumber != -1) {
                String scheduleInfo = (String) movieTableModel.getValueAt(selectedRow, 9);
                int scheduleNumber = getScheduleNumber(movieNumber, scheduleInfo); // 영화 번호와 상영시간으로부터 스케줄 번호 가져오기
                if (scheduleNumber != -1) {
                    new BookMovieFrame(conn, customerId, movieNumber, scheduleInfo, scheduleNumber); // 변경된 BookMovieFrame 생성자 호출
                } else {
                    JOptionPane.showMessageDialog(this, "스케줄 번호를 가져올 수 없습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "영화 번호를 가져올 수 없습니다.", "에러", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "예매할 영화를 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
        }
    }

private int getMovieNumber(String movieName) {
    int movieNumber = -1;
    try {
        String query = "SELECT MovieNumber FROM movie WHERE MovieName = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, movieName);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            movieNumber = rs.getInt("MovieNumber");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return movieNumber;
}

private int getScheduleNumber(int movieNumber, String scheduleInfo) {
    int scheduleNumber = -1;
    try {
        String query = "SELECT ScheduleNumber FROM schedule WHERE MovieNumber = ? AND ScreeningStartTime = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, movieNumber);
        pstmt.setString(2, scheduleInfo);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            scheduleNumber = rs.getInt("ScheduleNumber");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return scheduleNumber;
}
}
