package controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {@Override
protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession httpSession = request.getSession(false);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    JSONObject result = new JSONObject();

    if (httpSession == null || httpSession.getAttribute("user") == null) {
        result.put("success", false);
        result.put("message", "Unauthorized");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(result.toString());
        return;
    }

    User user = (User) httpSession.getAttribute("user");

    // Read JSON from request
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = request.getReader()) {
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
    }

    JSONObject json = new JSONObject(sb.toString());
    String type = json.getString("type");
    double amount = json.getDouble("amount");

    if (amount <= 0) {
        result.put("success", false);
        result.put("message", "Invalid amount");
        response.getWriter().write(result.toString());
        return;
    }

    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    if ("deposit".equalsIgnoreCase(type)) {
        user.setBalance(user.getBalance() + amount);
        result.put("message", "Deposit successful");
    } else if ("withdraw".equalsIgnoreCase(type)) {
        if (user.getBalance() < amount) {
            result.put("success", false);
            result.put("message", "Insufficient funds");
            response.getWriter().write(result.toString());
            session.close();
            return;
        }
        user.setBalance(user.getBalance() - amount);
        result.put("message", "Withdrawal successful");
    } else {
        result.put("success", false);
        result.put("message", "Invalid transaction type");
        response.getWriter().write(result.toString());
        session.close();
        return;
    }

    session.update(user);
    tx.commit();
    session.close();

    httpSession.setAttribute("user", user);  // Update session

    result.put("success", true);
    result.put("balance", user.getBalance());

    response.getWriter().write(result.toString());
}

	  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	        String action = request.getParameter("action");

	        HttpSession httpSession = request.getSession(false);
	        if (httpSession == null || httpSession.getAttribute("user") == null) {
	            response.sendRedirect("login.html");
	            return;
	        }

	        User user = (User) httpSession.getAttribute("user");

	        switch (action) {
	            case "deposit":
	                deposit(request, user, response);
	                break;
	            case "withdraw":
	                withdraw(request, user, response);
	                break;
	            default:
	                response.sendRedirect("account.html");
	        }
	    }

	    private void deposit(HttpServletRequest request, User user, HttpServletResponse response) throws IOException {
	        double amount;
	        try {
	            amount = Double.parseDouble(request.getParameter("amount"));
	            if (amount <= 0) {
	                response.sendRedirect("account.html?msg=Invalid+deposit+amount");
	                return;
	            }
	        } catch (NumberFormatException e) {
	            response.sendRedirect("account.html?msg=Invalid+input");
	            return;
	        }

	        Session session = HibernateUtil.getSessionFactory().openSession();
	        Transaction tx = session.beginTransaction();

	        user.setBalance(user.getBalance() + amount);
	        session.update(user);
	        tx.commit();
	        session.close();

	        request.getSession().setAttribute("user", user); // update session
	        response.sendRedirect("account.html?msg=Deposit+successful");
	    }

	    private void withdraw(HttpServletRequest request, User user, HttpServletResponse response) throws IOException {
	        double amount;
	        try {
	            amount = Double.parseDouble(request.getParameter("amount"));
	            if (amount <= 0) {
	                response.sendRedirect("account.html?msg=Invalid+withdrawal+amount");
	                return;
	            }
	        } catch (NumberFormatException e) {
	            response.sendRedirect("account.html?msg=Invalid+input");
	            return;
	        }

	        if (user.getBalance() < amount) {
	            response.sendRedirect("account.html?msg=Insufficient+funds");
	            return;
	        }

	        Session session = HibernateUtil.getSessionFactory().openSession();
	        Transaction tx = session.beginTransaction();

	        user.setBalance(user.getBalance() - amount);
	        session.update(user);
	        tx.commit();
	        session.close();

	        request.getSession().setAttribute("user", user); // update session
	        response.sendRedirect("account.html?msg=Withdrawal+successful");
	    }

	    @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	        HttpSession httpSession = request.getSession(false);
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");

	        JSONObject result = new JSONObject();

	        if (httpSession == null || httpSession.getAttribute("user") == null) {
	            result.put("success", false);
	            result.put("message", "Unauthorized");
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        } else {
	            User user = (User) httpSession.getAttribute("user");
	            result.put("success", true);
	            result.put("balance", user.getBalance());
	            result.put("accountNumber", user.getAccountNumber());
	        }

	        response.getWriter().write(result.toString());
	    }

	}


