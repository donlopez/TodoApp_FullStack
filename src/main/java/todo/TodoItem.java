package todo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "todo_items")
public class TodoItem {

    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private Priority priority = Priority.MEDIUM;

    public TodoItem() {
    }

    public TodoItem(String description, UserAccount user) {
        this.description = description;
        this.user = user;
        // priority stays at default MEDIUM from field declaration
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // -------- getters / setters --------

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDueDate() { return dueDate; }

    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public UserAccount getUser() { return user; }

    public void setUser(UserAccount user) { this.user = user; }

    public Priority getPriority() { return priority; }

    public void setPriority(Priority priority) {
        this.priority = (priority == null ? Priority.MEDIUM : priority);
    }

    /** Created column: full date + time (one line) */
    public String getCreatedAtFormatted() {
        return createdAt == null ? "" :
                createdAt.format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"));
    }

    /** Due column: date on first line */
    public String getDueDateFormatted() {
        return dueDate == null ? "" :
                dueDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }

    /** Due column: time on second line */
    public String getDueTimeFormatted() {
        return dueDate == null ? "" :
                dueDate.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    /** For datetime-local input */
    public String getDueDateInputValue() {
        return dueDate == null ? "" :
                dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    @Override
    public String toString() {
        return "[" + id + "] " + description +
                (completed ? " (done)" : "") +
                " - Priority: " + priority +
                " - Created at: " + createdAt +
                " - Due: " + dueDate;
    }
}
