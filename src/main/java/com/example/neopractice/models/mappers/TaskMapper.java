package com.example.neopractice.models.mappers;

import com.example.neopractice.models.dtos.requests.TaskRequest;
import com.example.neopractice.models.dtos.responses.TaskResponse;
import com.example.neopractice.models.entities.Group;
import com.example.neopractice.models.entities.Task;
import com.example.neopractice.models.entities.User;
import com.example.neopractice.models.entities.enums.PriorityEnum;
import com.example.neopractice.models.entities.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .dueDate(task.getDueDate())
                .assignee(userMapper.toUserResponse(task.getAssignee()))
                .group(groupMapper.toGroupResponse(task.getGroup()))
                .author(userMapper.toUserResponse(task.getAuthor()))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public List<TaskResponse> toTaskResponseList(List<Task> tasks) {
        return tasks.stream().map(this::toTaskResponse).toList();
    }

    public Task toTaskEntity(TaskRequest request, User author, Group group, User assignee, StatusEnum status, PriorityEnum priority) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(status)
                .priority(priority)
                .dueDate(request.getDueDate())
                .author(author)
                .group(group)
                .assignee(assignee)
                .build();
    }

    public void updateTaskEntity(TaskRequest request, Task task, User assignee, StatusEnum status, PriorityEnum priority) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(request.getDueDate());
        task.setAssignee(assignee);
    }

    public void updateTaskStatus(Task task, StatusEnum status) {
        task.setStatus(status);
    }
}
