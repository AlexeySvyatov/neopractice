package com.example.neopractice.repositories;

import com.example.neopractice.models.entities.GroupMembers;
import com.example.neopractice.models.entities.GroupMembersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers, GroupMembersId> {
}
