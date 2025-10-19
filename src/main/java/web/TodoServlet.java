package web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import todo.TodoItem;
import todo.TodoRepository;

public class TodoServlet extends HttpServlet {

    private final TodoRepository repo = new TodoRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        String action = req.getPathInfo();
        if (action == null || action.isBlank() || "/".equals(action)) {
            action = "/list";
        }

        try {
            switch (action) {
                case "/list": {
                    List<TodoItem> items = repo.findAll();
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

        String idStr = param(req, "id");
        String description = param(req, "description");
        // checkbox: present => "true", absent => null => false
        boolean completed = "true".equalsIgnoreCase(param(req, "completed"));

        try {
            if (idStr == null || idStr.isBlank()) {
                // create
                if (description != null && !description.isBlank()) {
                    long newId = repo.add(description);
                    repo.setCompleted(newId, completed);
                }
            } else {
                // update (description + completed)
                Long id = parseId(idStr);
                if (id != null && description != null) {
                    repo.updateDescription(id, description);
                    repo.setCompleted(id, completed);
                }
            }
            redirect(req, resp, "/todos/list");
        } catch (Exception e) {
            send500(resp, e);
        }
    }

    /* ----------------- helpers ----------------- */

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

    private void send500(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Server error: " + e.getMessage());
    }
}
