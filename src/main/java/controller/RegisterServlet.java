package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.User;
import util.HibernateUtil;
import jakarta.servlet.annotation.WebServlet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String body = sb.toString();
            System.out.println("Request body: " + body); // 👈 debug log

            JSONObject json = new JSONObject(body);
            String email = json.getString("email");
            String user_password = json.getString("user_password");

            // Hibernate logic
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();

            int accountNumber = (int) (Math.random() * 100 + 900);
            User user = new User();
            user.setEmail(email);
            user.setPassword(user_password);
            user.setAccountNumber(accountNumber);
            user.setBalance(0.0);

            session.persist(user);
            tx.commit();
            session.close();

            JSONObject result = new JSONObject();
            result.put("message", "Registration successful");
            result.put("accountNumber", accountNumber);

            response.getWriter().write(result.toString());

        } catch (Exception e) {
            e.printStackTrace();  // 👈 logs full error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Something went wrong: " + e.getMessage() + "\"}");
        }
    }
}
