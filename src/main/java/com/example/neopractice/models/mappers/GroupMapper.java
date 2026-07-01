package com.example.neopractice.models.mappers;

import com.example.neopractice.models.dtos.requests.GroupRequest;
import com.example.neopractice.models.dtos.responses.GroupResponse;
import com.example.neopractice.models.entities.Group;
import com.example.neopractice.models.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupMapper {
    private final UserMapper userMapper;

    public GroupResponse toGroupResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .owner(userMapper.toUserResponse(group.getOwner()))
                .createdAt(group.getCreatedAt())
                .build();
    }

    public List<GroupResponse> toGroupResponseList(List<Group> groups) {
        return groups.stream().map(this::toGroupResponse).toList();
    }

    public Group toGroupEntity(GroupRequest request, User owner) {
        return Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();
    }

    public void updateGroupEntity(Group group, GroupRequest request) {
        group.setName(request.getName());
        group.setDescription(request.getDescription());
    }
}
