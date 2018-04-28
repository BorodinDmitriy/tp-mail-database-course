package course.db.dao;

import course.db.db_queries.QueryForUserProfile;
import course.db.models.UserProfileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserProfileDAO extends AbstractDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    public UserProfileDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(UserProfileModel userProfileModel) {
        jdbcTemplate.update(QueryForUserProfile.create(), userProfileModel.getAbout(), userProfileModel.getEmail(),
                            userProfileModel.getFullname(), userProfileModel.getNickname());
    }

    public UserProfileModel getUserByNick(String nickname) {
        return jdbcTemplate.queryForObject(QueryForUserProfile.getUserByNickOrEmail(),
                new Object[] {nickname, null}, _getUserModel);
    }

    public void change(UserProfileModel userProfileModel) {
        final StringBuilder builder = new StringBuilder("UPDATE userprofiles SET");
        final List<Object> list = new ArrayList<>();

        if (userProfileModel.getAbout() != null) {
            builder.append(" about = ?,");
            list.add(userProfileModel.getAbout());
        }
        if (userProfileModel.getEmail() != null) {
            builder.append(" email = ?::CITEXT,");
            list.add(userProfileModel.getEmail());
        }
        if (userProfileModel.getFullname() != null) {
            builder.append(" fullname = ?,");
            list.add(userProfileModel.getFullname());
        }
        if (!list.isEmpty()) {
            builder.delete(builder.length() - 1, builder.length());
            builder.append(" WHERE nickname = ?::CITEXT");
            list.add(userProfileModel.getNickname());
            jdbcTemplate.update(builder.toString(), list.toArray());
        }
    }

    public List<UserProfileModel> getUsersByNickOrEmail(String nickname, String email) {
        return jdbcTemplate.query(QueryForUserProfile.getUserByNickOrEmail(), new Object[] {nickname, email},
                _getUserModel);
    }

    @Override
    public Integer count() {
        return jdbcTemplate.queryForObject(QueryForUserProfile.count(), Integer.class);
    }

    @Override
    public void clear() {
        jdbcTemplate.execute(QueryForUserProfile.clear());
    }

}
