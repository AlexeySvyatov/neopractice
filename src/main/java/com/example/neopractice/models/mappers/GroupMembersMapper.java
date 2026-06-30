package com.example.neopractice.models.mappers;

import com.example.neopractice.models.dtos.responses.GroupMembersResponse;
import com.example.neopractice.models.entities.Group;
import com.example.neopractice.models.entities.GroupMembers;
import com.example.neopractice.models.entities.User;
import com.example.neopractice.models.entities.enums.RoleEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMembersMapper {
    public GroupMembersResponse toResponse(GroupMembers groupMembers) {
        return GroupMembersResponse.builder()
                .id(groupMembers.getUser().getId())
                .username(groupMembers.getUser().getUsername())
                .email(groupMembers.getUser().getEmail())
                .role(groupMembers.getRole().name())
                .build();
    }

    public List<GroupMembersResponse> toResponseList(List<GroupMembers> groupMembers) {
        return groupMembers.stream().map(this::toResponse).toList();
    }

    public GroupMembers toEntity(Group group, User user, RoleEnum role) {
        return GroupMembers.builder()
                .group(group)
                .user(user)
                .role(role)
                .build();
    }
}
