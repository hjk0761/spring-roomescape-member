package roomescape.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Reservation;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(ReservationDao.class)
@Sql(scripts = {"/test_schema.sql", "/test_data.sql"})
public class ReservationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReservationDao reservationDao;


    @Test
    void findAllTest() {
        List<Reservation> reservations = reservationDao.findAll();

        assertThat(reservations.size()).isEqualTo(1);
    }

    @Test
    void findByIdTest() {
        Reservation reservation = reservationDao.findById(1L).get();

        assertThat(reservation.getId()).isEqualTo(1L);
    }

    @Test
    void findByWrongIdTest() {
        Optional<Reservation> reservation = reservationDao.findById(9L);

        assertThat(reservation).isEqualTo(Optional.empty());
    }

    @Test
    void insertTest() {
        Long index = jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Long.class);
        Long id = reservationDao.insert("토미", "2024-01-02", 1L, 1L);

        assertThat(id).isEqualTo(index + 1);
    }

    @Test
    void wrongInsertTest() {
        assertThatThrownBy(() -> reservationDao.insert("토미".repeat(130), "2024-01-01", 1L, 1L))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> reservationDao.insert("토미", "2024-01-01", -1L, 1L))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> reservationDao.insert("토미", null, 1L, 1L))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> reservationDao.insert("토미", "2024-01-01", 1L, -1L))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deleteByIdTest() {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO reservation(name, date, time_Id, theme_id) VALUES (?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, "네오");
            ps.setString(2, "2024-01-03");
            ps.setLong(3, 1L);
            ps.setLong(4, 1L);
            return ps;
        }, keyHolder);

        Long key = keyHolder.getKey().longValue();
        reservationDao.deleteById(key);

        assertThat(reservationDao.findById(key)).isEqualTo(Optional.empty());
    }

    @Test
    void countByTimeIdTest() {
        int count = reservationDao.countByTimeId(1L);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByNullTimeIdTest() {
        int count = reservationDao.countByTimeId(null);

        assertThat(count).isEqualTo(0);
    }

    @Test
    void countByThemeIdTest() {
        int count = reservationDao.countByThemeId(1L);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByNullThemeIdTest() {
        int count = reservationDao.countByThemeId(null);

        assertThat(count).isEqualTo(0);
    }
}
