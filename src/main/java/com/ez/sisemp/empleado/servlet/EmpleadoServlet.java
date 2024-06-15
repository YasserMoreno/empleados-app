package com.ez.sisemp.empleado.servlet;

import com.ez.sisemp.empleado.business.EmpleadoBusiness;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.empleado.model.EmpleadoDashboard;
import com.ez.sisemp.shared.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@WebServlet("/empleado")
public class EmpleadoServlet extends HttpServlet {

    private static final String EMPLEADO_JSP = "/empleado/empleado.jsp";
    private static final String LOGIN_JSP = "/";
    private static final String ERROR_SERVER = "Error interno en el servidor";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!SessionUtils.validarSesion(request, response)) {
            return;
        }
        EmpleadoBusiness business = new EmpleadoBusiness();
        try {
            EmpleadoDashboard dashboard = business.obtenerDatosDashboard();
            request.setAttribute("dashboard", dashboard);
//            List<Empleado> empleados = business.obtenerEmpleados();
            var empleados = business.obtenerEmpleadosJpa();
            request.setAttribute("empleados", empleados);
            request.getRequestDispatcher(EMPLEADO_JSP).forward(request, response);
        } catch (Exception e) {
            throw new ServletException(ERROR_SERVER, e);
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
