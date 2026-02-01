package web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import todo.UserAccount;
import todo.UserRepository;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String login = trim(req.getParameter("login"));

        if (login == null || login.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=empty");
            return;
        }

        UserAccount user = userRepo.findByUsernameOrEmail(login);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=notfound");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("currentUserId", user.getId());
        session.setAttribute("currentUserName", user.getDisplayName());

        resp.sendRedirect(req.getContextPath() + "/todos/list");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
