package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaMapper implements RowMapper<MPA> {

    @Override
    public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("mpa"))
                .build();
    }

}
