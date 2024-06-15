package com.ez.sisemp.empleado.servlet;

import com.ez.sisemp.empleado.business.EmpleadoBusiness;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.parametro.model.Departamento;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/empleado/editar")
public class EditarEmpleadoServlet extends HttpServlet {

    private static final String EDITAR_EMPLEADO_JSP = "/empleado/editar.jsp";
    private static final String ERROR_SERVER = "Error interno en el servidor";
    private static final Logger LOGGER = Logger.getLogger(EditarEmpleadoServlet.class.getName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        System.out.println(id);

        if(id == null || id.trim().isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El id del empleado es requerido");
            return;
        }

        try {
            long empleadoId = Long.parseLong(id);
            EmpleadoBusiness business = new EmpleadoBusiness();
            var empleadoObtenido = business.buscarEmpleadoPorIdJPA(empleadoId);

            if (empleadoObtenido == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Empleado no encontrado");
                return;
            }

            var fechaNacimiento = empleadoObtenido.fechaNacimiento().toString();

            req.setAttribute("empleado", empleadoObtenido);
            req.setAttribute("fechaNacimiento", fechaNacimiento);
            req.getRequestDispatcher(EDITAR_EMPLEADO_JSP).forward(req, resp);

        }catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El Id del empleado es incorrecto");
        } catch (Exception e) {
            throw new ServletException(ERROR_SERVER, e);
        }
    }
}
