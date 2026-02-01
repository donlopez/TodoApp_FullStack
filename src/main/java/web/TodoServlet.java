package web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import todo.TodoItem;
import todo.TodoRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TodoServlet extends HttpServlet {

    private final TodoRepository repo = new TodoRepository();

    // HTML <input type="datetime-local"> pattern: 2025-11-16T23:08
    private static final DateTimeFormatter DTF_INPUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        Long userId = getCurrentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?login=required");
            return;
        }

        String action = req.getPathInfo();
        if (action == null || action.isBlank() || "/".equals(action)) {
            action = "/list";
        }

        try {
            switch (action) {
                case "/list": {
                    List<TodoItem> items = repo.findAllByUser(userId);
                    req.setAttribute("items", items);
                    forward(req, resp, "/list.jsp");
                    return;
                }
                case "/new": {
                    forward(req, resp, "/form.jsp");
                    return;
                }
                case "/edit": {
                    Long id = parseId(req.getParameter("id"));
                    if (id != null) {
                        TodoItem item = repo.findById(id);
                        if (item != null) {
                            req.setAttribute("item", item);
                        }
                    }
                    forward(req, resp, "/form.jsp");
                    return;
                }
                case "/delete": {
                    Long id = parseId(req.getParameter("id"));
                    if (id != null) {
                        repo.deleteById(id);
                    }
                    redirect(req, resp, "/todos/list");
                    return;
                }
                case "/toggle": {
                    Long id = parseId(req.getParameter("id"));
                    if (id != null) {
                        TodoItem item = repo.findById(id);
                        if (item != null) {
                            repo.setCompleted(id, !item.isCompleted());
                        }
                    }
                    redirect(req, resp, "/todos/list");
                    return;
                }
                default: {
                    redirect(req, resp, "/todos/list");
                    return;
                }
            }
        } catch (Exception e) {
            send500(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Long userId = getCurrentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?login=required");
            return;
        }

        String idStr        = param(req, "id");
        String description  = param(req, "description");
        boolean completed   = "true".equalsIgnoreCase(param(req, "completed"));
        String priorityStr  = param(req, "priority");
        String dueDateStr   = param(req, "dueDate");

        TodoItem.Priority priority = parsePriority(priorityStr);
        LocalDateTime dueDate      = parseDueDate(dueDateStr);

        try {
            if (idStr == null || idStr.isBlank()) {
                // create
                if (description != null && !description.isBlank()) {
                    repo.add(description, userId, completed, priority, dueDate);
                }
            } else {
                // update
                Long id = parseId(idStr);
                if (id != null && description != null) {
                    repo.updateTask(id, description, completed, priority, dueDate);
                }
            }
            redirect(req, resp, "/todos/list");
        } catch (Exception e) {
            send500(resp, e);
        }
    }

    // ----------------- helpers -----------------

    private Long getCurrentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object val = session.getAttribute("currentUserId");
        return (val instanceof Long) ? (Long) val : null;
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view) throws IOException {
        try {
            RequestDispatcher rd = req.getRequestDispatcher(view);
            rd.forward(req, resp);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    private Long parseId(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Long.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return (v == null) ? null : v.trim();
    }

    private TodoItem.Priority parsePriority(String value) {
        if (value == null || value.isBlank()) {
            return TodoItem.Priority.MEDIUM;
        }
        String v = value.trim().toUpperCase();
        switch (v) {
            case "HIGH":
                return TodoItem.Priority.HIGH;
            case "LOW":
                return TodoItem.Priority.LOW;
            default:
                return TodoItem.Priority.MEDIUM;
        }
    }

    private LocalDateTime parseDueDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DTF_INPUT);
        } catch (DateTimeParseException ex) {
            // if parsing fails, just ignore due date rather than breaking the request
            return null;
        }
    }

    private void send500(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Server error: " + e.getMessage());
    }
}
