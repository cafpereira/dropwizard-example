package com.acme.db;

import com.acme.core.mapper.UserMapper;
import com.acme.core.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(UserMapper.class)
public interface UserDAO {
    @SqlQuery("select * from USERS")
    List<User> getAll();

    @SqlQuery("select * from USERS where ID = :id")
    User findById(@Bind("id") int id);

    @SqlUpdate("delete from USERS where ID = :id")
    int deleteById(@Bind("id") int id);

    @SqlUpdate("update USERS set NAME = :name where ID = :id")
    int update(@BindBean User user);

    @SqlUpdate("insert into USERS (ID, NAME) values (:id, :name)")
    int insert(@BindBean User user);
}
