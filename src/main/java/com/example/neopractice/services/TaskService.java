package com.example.neopractice.services;

import com.example.neopractice.exceptions.types.*;
import com.example.neopractice.models.dtos.requests.TaskRequest;
import com.example.neopractice.models.dtos.requests.TaskStatusRequest;
import com.example.neopractice.models.dtos.responses.PageResponse;
import com.example.neopractice.models.dtos.responses.TaskResponse;
import com.example.neopractice.models.entities.Group;
import com.example.neopractice.models.entities.Task;
import com.example.neopractice.models.entities.User;
import com.example.neopractice.models.entities.enums.PriorityEnum;
import com.example.neopractice.models.entities.enums.StatusEnum;
import com.example.neopractice.models.mappers.PageMapper;
import com.example.neopractice.models.mappers.TaskMapper;
import com.example.neopractice.repositories.GroupRepository;
import com.example.neopractice.repositories.TaskRepository;
import com.example.neopractice.repositories.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TaskMapper taskMapper;
    private final PageMapper pageMapper;

    @Transactional
    public TaskResponse createTask(TaskRequest request, String username) {
        User author = checkUser(username);
        Group group = checkGroup(request.getGroup());
        isMemberOfGroup(request.getGroup(), author.getId(), "Вы не являетесь членом данной группы");
        User assignee = null;
        if (request.getAssignee() != null) {
            assignee = userRepository.findById(request.getAssignee())
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
            isMemberOfGroup(request.getGroup(), assignee.getId(), "Исполнитель не является членом данной группы");
        }
        PriorityEnum priorityEnum;
        try {
            priorityEnum = PriorityEnum.valueOf(request.getPriority().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("Некорректное значение приоритета");
        }
        Task task = taskMapper.toTaskEntity(request, author, group, assignee, StatusEnum.NEW, priorityEnum);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(TaskRequest request, String username, UUID taskId) {
        User author = checkUser(username);
        Task task = checkTask(taskId);
        isMemberOfGroup(task.getGroup().getId(), author.getId(), "Вы не являетесь членом данной группы");
        isTaskAuthorOrOwner(task, author.getId(), "Только владелец группы или автор может обновить данную задачу");
        User assignee = null;
        if (request.getAssignee() != null) {
            assignee = userRepository.findById(request.getAssignee())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getAssignee()));
            isMemberOfGroup(task.getGroup().getId(), assignee.getId(), "Исполнитель не является членом данной группы");
        }
        PriorityEnum priorityEnum;
        StatusEnum statusEnum;
        try {
            priorityEnum = PriorityEnum.valueOf(request.getPriority().toUpperCase());
            statusEnum = StatusEnum.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("Некорректное значение статуса и/или приоритета");
        }
        taskMapper.updateTaskEntity(request, task, assignee, statusEnum, priorityEnum);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(UUID taskId, String username) {
        User author = checkUser(username);
        Task task = checkTask(taskId);
        isMemberOfGroup(task.getGroup().getId(), author.getId(), "Вы не являетесь членом данной группы");
        isTaskAuthorOrOwner(task, author.getId(), "Только владелец группы или автор может удалить данную задачу");
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse getTask(UUID taskId, String username) {
        User user = checkUser(username);
        Task task = checkTask(taskId);
        isMemberOfGroup(task.getGroup().getId(), user.getId(), "Вы не являетесь членом данной группы");
        return taskMapper.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID taskId, String username, TaskStatusRequest request) {
        User user = checkUser(username);
        Task task = checkTask(taskId);
        isMemberOfGroup(task.getGroup().getId(), user.getId(), "Вы не являетесь членом данной группы");
        StatusEnum statusEnum;
        try {
            statusEnum = StatusEnum.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("Некорректное значение статуса");
        }
        taskMapper.updateTaskStatus(task, statusEnum);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    @Transactional
    public PageResponse<TaskResponse> getTasks(String username, int page, int size, String status,
                                               UUID assigneeId, LocalDate dueDate, String priority) {
        User user = checkUser(username);
        List<Group> groups = groupRepository.findAllGroupsByUser(user);
        if (groups.isEmpty()) {
            return pageMapper.emptyPageResponse(page, size);
        }
        List<UUID> groupIds = groups.stream().map(Group::getId).toList();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Task> groupSpec = (root, cQuery, cBuilder) -> cBuilder.in(root.get("group").get("id")).value(groupIds);
        Specification<Task> specification = Specification.where(groupSpec).and(getTaskSpecification(status, assigneeId, dueDate, priority));
        Page<Task> taskPage = taskRepository.findAll(specification, pageable);
        List<TaskResponse> content = taskMapper.toTaskResponseList(taskPage.getContent());
        return pageMapper.toPageResponse(taskPage, content, page, size);
    }

    private User checkUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    private Group checkGroup(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
    }

    private Task checkTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));
    }

    private void isMemberOfGroup(UUID groupId, UUID userId, String message) {
        if (!groupRepository.isUserMemberOfGroup(groupId, userId)) {
            throw new PermissionDeniedException(message);
        }
    }

    private void isTaskAuthorOrOwner(Task task, UUID userId, String message) {
        boolean isOwner = task.getGroup().getOwner().getId().equals(userId);
        boolean isAuthor = task.getAssignee().getId().equals(userId);
        if (!isOwner && !isAuthor) {
            throw new PermissionDeniedException(message);
        }
    }

    public Specification<Task> getTaskSpecification(String status, UUID assignee, LocalDate dueDate, String priority) {
        Specification<Task> taskSpec = (root, cQuery, cBuilder) -> {
            var criteria = Stream.of(
                    Optional.ofNullable(status).filter(Strings::isNotBlank).map(v -> {
                        try {
                            StatusEnum statusEnum = StatusEnum.valueOf(v.toUpperCase());
                            return cBuilder.equal(root.get("status"), statusEnum);
                        } catch (IllegalArgumentException e) {
                            throw new InvalidEnumValueException("Некорректное значение статуса");
                        }
                    }),
                    Optional.ofNullable(assignee).map(v -> cBuilder.equal(root.get("assignee").get("id"), v)),
                    Optional.ofNullable(dueDate).map(v -> cBuilder.equal(root.get("dueDate"), v)),
                    Optional.ofNullable(priority).filter(Strings::isNotBlank).map(v -> {
                        try {
                            PriorityEnum priorityEnum = PriorityEnum.valueOf(v.toUpperCase());
                            return cBuilder.equal(root.get("priority"), priorityEnum);
                        } catch (IllegalArgumentException e) {
                            throw new InvalidEnumValueException("Некорректное значение приоритета");
                        }
                    })).filter(Optional::isPresent)
                    .map(Optional::get)
                    .toArray(Predicate[]::new);
            return cBuilder.and(criteria);
        };
        return taskSpec;
    }
}
