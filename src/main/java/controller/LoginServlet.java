package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.User;
import util.HibernateUtil;
import jakarta.servlet.annotation.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read raw JSON from body
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // Parse JSON
        JSONObject json = new JSONObject(sb.toString());
        String email = json.getString("email");
        String user_password = json.getString("user_password");

        // Query user
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<User> query = session.createQuery("FROM User WHERE email = :email AND user_password = :user_password", User.class);
        query.setParameter("email", email);
        query.setParameter("user_password", user_password);

        User user = query.uniqueResult();
        session.close();

        // Send JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject result = new JSONObject();
        if (user != null) {
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);
            result.put("success", true);
            result.put("accountNumber", user.getAccountNumber());
        } else {
            result.put("success", false);
            result.put("message", "Invalid credentials");
        }

        response.getWriter().write(result.toString());
    }
}
