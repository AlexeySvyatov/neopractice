package com.example.neopractice.services;

import com.example.neopractice.exceptions.types.*;
import com.example.neopractice.models.dtos.requests.AddMemberRequest;
import com.example.neopractice.models.dtos.requests.GroupRequest;
import com.example.neopractice.models.dtos.responses.GroupMembersResponse;
import com.example.neopractice.models.dtos.responses.GroupResponse;
import com.example.neopractice.models.entities.Group;
import com.example.neopractice.models.entities.GroupMembers;
import com.example.neopractice.models.entities.GroupMembersId;
import com.example.neopractice.models.entities.User;
import com.example.neopractice.models.entities.enums.RoleEnum;
import com.example.neopractice.models.mappers.GroupMapper;
import com.example.neopractice.models.mappers.GroupMembersMapper;
import com.example.neopractice.repositories.GroupMembersRepository;
import com.example.neopractice.repositories.GroupRepository;
import com.example.neopractice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final GroupMapper groupMapper;
    private final GroupMembersMapper groupMembersMapper;

    @Transactional
    public GroupResponse createGroup(GroupRequest groupRequest, String username) {
        User owner = checkUser(username);
        Group group = groupMapper.toGroupEntity(groupRequest, owner);
        Group savedGroup = groupRepository.save(group);
        GroupMembers member = groupMembersMapper.toEntity(savedGroup, owner, RoleEnum.OWNER);
        groupMembersRepository.save(member);
        return groupMapper.toGroupResponse(savedGroup);
    }

    @Transactional
    public GroupResponse updateGroup(UUID id, GroupRequest request, String username) {
        User user = checkUser(username);
        Group group = checkGroup(id);
        checkPermissions(id, user);
        groupMapper.updateGroupEntity(group, request);
        Group updatedGroup = groupRepository.save(group);
        return groupMapper.toGroupResponse(updatedGroup);
    }

    @Transactional
    public void deleteGroup(UUID id, String username) {
        User user = checkUser(username);
        Group group = checkGroup(id);
        checkPermissions(id, user);
        List<GroupMembers> members = groupRepository.findAllMembersByGroup(id);
        groupMembersRepository.deleteAll(members);
        groupRepository.delete(group);
    }

    @Transactional
    public GroupResponse getGroup(UUID id, String username) {
        User user = checkUser(username);
        Group group = checkGroup(id);
        checkPermissions(id, user);
        return groupMapper.toGroupResponse(group);
    }

    @Transactional
    public List<GroupResponse> getUserGroups(String username) {
        User user = checkUser(username);
        List<Group> groups = groupRepository.findAllGroupsByUser(user);
        return groupMapper.toGroupResponseList(groups);
    }

    @Transactional
    public List<GroupMembersResponse> getGroupMembers(UUID id, String username) {
        User user = checkUser(username);
        Group group = checkGroup(id);
        checkPermissions(id, user);
        List<GroupMembers> members = groupRepository.findAllMembersByGroup(id);
        return groupMembersMapper.toResponseList(members);
    }

    @Transactional
    public GroupMembersResponse addGroupMember(UUID id, String username, AddMemberRequest request) {
        User user = checkUser(username);
        Group group = checkGroup(id);
        checkPermissions(id, user);
        User newMember = userRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (groupRepository.isUserMemberOfGroup(id, newMember.getId())) {
            throw new DuplicateResourceException("Пользователь уже состоит в данной группе");
        }
        GroupMembers member = groupMembersMapper.toEntity(group, newMember, RoleEnum.MEMBER);
        GroupMembers savedMember = groupMembersRepository.save(member);
        return groupMembersMapper.toResponse(savedMember);
    }

    @Transactional
    public void removeGroupMember(UUID id, String username, UUID userId) {
        User user = checkUser(username);
        Group group = checkGroup(id);
        checkPermissions(id, user);
        User removedMember = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (group.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Нельзя удалить владельца группы");
        }
        GroupMembersId memberId = new GroupMembersId(id, userId);
        GroupMembers member = groupMembersRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        groupMembersRepository.delete(member);
    }

    private User checkUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    private Group checkGroup(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
    }

    private void checkPermissions(UUID id, User user) {
        Group group = checkGroup(id);
        if (!group.getOwner().getId().equals(user.getId())) {
            throw new PermissionDeniedException("Вы не можете взаимодействовать с данной группой");
        }
    }
}
