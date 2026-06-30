package com.example.neopractice.repositories;

import com.example.neopractice.models.entities.Group;
import com.example.neopractice.models.entities.GroupMembers;
import com.example.neopractice.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    @Query("SELECT gm.group FROM GroupMembers gm WHERE gm.user = :user")
    List<Group> findAllGroupsByUser(@Param("user") User user);

    @Query("SELECT gm FROM GroupMembers gm WHERE gm.group.id = :group")
    List<GroupMembers> findAllMembersByGroup(@Param("group") UUID group);

    @Query("SELECT COUNT(gm) > 0 FROM GroupMembers gm WHERE gm.group.id = :group AND gm.user.id = :user")
    boolean isUserMemberOfGroup(@Param("group") UUID groupId, @Param("user") UUID userId);
}
