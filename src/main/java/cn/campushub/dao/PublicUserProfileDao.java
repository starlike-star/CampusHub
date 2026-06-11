package cn.campushub.dao;

import cn.campushub.model.PublicUserProfile;

import java.sql.SQLException;
import java.util.Optional;

public interface PublicUserProfileDao {
    Optional<PublicUserProfile> findActiveById(long userId) throws SQLException;
}
