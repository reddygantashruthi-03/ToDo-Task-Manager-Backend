package com.example.todo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Task> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Task getOne(@PathVariable Long id) {
        return findTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody Task task) {
        task.setId(null);
        task.setCompleted(false);
        task.setPriority(normalizePriority(task.getPriority()));
        return repository.save(task);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody Task incoming) {
        Task existing = findTask(id);
        existing.setTitle(incoming.getTitle());
        existing.setDescription(incoming.getDescription());
        existing.setDueDate(incoming.getDueDate());
        existing.setPriority(normalizePriority(incoming.getPriority()));
        return repository.save(existing);
    }

    @PatchMapping("/{id}/complete")
    public Task complete(@PathVariable Long id) {
        Task task = findTask(id);
        task.setCompleted(true);
        return repository.save(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(findTask(id));
    }

    private Task findTask(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private String normalizePriority(String priority) {
        if (priority == null) return "MEDIUM";
        String value = priority.trim().toUpperCase();
        return List.of("LOW", "MEDIUM", "HIGH").contains(value) ? value : "MEDIUM";
    }
}
