package com.example.neopractice.controllers;

import com.example.neopractice.models.dtos.requests.AddMemberRequest;
import com.example.neopractice.models.dtos.requests.GroupRequest;
import com.example.neopractice.models.dtos.responses.GroupMembersResponse;
import com.example.neopractice.models.dtos.responses.GroupResponse;
import com.example.neopractice.services.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(HttpServletRequest request, @RequestBody GroupRequest groupRequest) {
        String username = (String) request.getAttribute("username");
        GroupResponse createdGroup = groupService.createGroup(groupRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> updateGroup(HttpServletRequest request, @PathVariable UUID id, @RequestBody GroupRequest groupRequest) {
        String username = (String) request.getAttribute("username");
        GroupResponse updatedGroup = groupService.updateGroup(id, groupRequest, username);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(HttpServletRequest request, @PathVariable UUID id) {
        String username = (String) request.getAttribute("username");
        groupService.deleteGroup(id, username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(HttpServletRequest request, @PathVariable UUID id) {
        String username = (String) request.getAttribute("username");
        GroupResponse group = groupService.getGroup(id, username);
        return ResponseEntity.status(HttpStatus.OK).body(group);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getUserGroups(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        List<GroupResponse> groupsList = groupService.getUserGroups(username);
        return ResponseEntity.status(HttpStatus.OK).body(groupsList);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMembersResponse>> getGroupMembers(HttpServletRequest request, @PathVariable UUID groupId) {
        String username = (String) request.getAttribute("username");
        List<GroupMembersResponse> groupMembers = groupService.getGroupMembers(groupId, username);
        return ResponseEntity.status(HttpStatus.OK).body(groupMembers);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupMembersResponse> addMemberToGroup(HttpServletRequest request, @PathVariable UUID groupId, @RequestBody AddMemberRequest addMemberRequest) {
        String username = (String) request.getAttribute("username");
        GroupMembersResponse addedMember = groupService.addGroupMember(groupId, username, addMemberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedMember);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromGroup(HttpServletRequest request, @PathVariable UUID groupId, @PathVariable UUID userId) {
        String username = (String) request.getAttribute("username");
        groupService.removeGroupMember(groupId, username, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
