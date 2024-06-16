package com.ez.sisemp.admin.servlet;

import com.ez.sisemp.admin.business.AdminBusiness;
import com.ez.sisemp.admin.dao.AdminDao;
import com.ez.sisemp.shared.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private static final String ADMIN_JSP = "/admin/admin.jsp";
    private static final String LOGIN_JSP = "/";


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(!SessionUtils.validarSesion(request, response)){
            return;
        }
        //AdminDao dao = new AdminDao();
        AdminBusiness business = new AdminBusiness();
        try {
            request.setAttribute("usuarios", business.obtenerUsuariosJPA());
            request.getRequestDispatcher(ADMIN_JSP).forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!SessionUtils.validarSesion(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if("logout".equals(action)) {
            doLogout(request, response);
        }
    }

    public void doLogout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, NullPointerException {
        try{
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + LOGIN_JSP);
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

}
