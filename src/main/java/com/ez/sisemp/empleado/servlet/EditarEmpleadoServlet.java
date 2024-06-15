package com.ez.sisemp.empleado.servlet;

import com.ez.sisemp.empleado.business.EmpleadoBusiness;
import com.ez.sisemp.empleado.exception.EmailAlreadyInUseException;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.parametro.dao.ParametroDao;
import com.ez.sisemp.parametro.model.Departamento;
import com.ez.sisemp.shared.enums.Routes;
import com.ez.sisemp.shared.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/empleado/editar")
public class EditarEmpleadoServlet extends HttpServlet {

    private static final String EDITAR_EMPLEADO_JSP = "/empleado/editar.jsp";
    private static final String ERROR_SERVER = "Error interno en el servidor";
    private EmpleadoBusiness empleadoBusiness;
    private ParametroDao parametroDao;
    private String codigoEmpleado;
    private Long idEmpleado;

    @Override
    public void init() throws ServletException {
        super.init();
        empleadoBusiness = new EmpleadoBusiness();
        parametroDao = new ParametroDao();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (!SessionUtils.validarSesion(req, resp)) {
            return;
        }

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String id = req.getParameter("id");
        if(id == null || id.trim().isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El id del empleado es requerido");
            return;
        }

        try {
            idEmpleado = Long.parseLong(id);
            EmpleadoBusiness business = new EmpleadoBusiness();
            var empleadoObtenido = business.buscarEmpleadoPorIdJPA(idEmpleado);

            if (empleadoObtenido == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Empleado no encontrado");
                return;
            }

            codigoEmpleado = empleadoObtenido.codigoEmpleado();

            loadDepartamentos(req);
            req.setAttribute("empleado", empleadoObtenido);
            req.getRequestDispatcher(EDITAR_EMPLEADO_JSP).forward(req, resp);

        }catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El Id del empleado es incorrecto");
        } catch (Exception e) {
            throw new ServletException(ERROR_SERVER, e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (!SessionUtils.validarSesion(req, resp)) {
            return;
        }

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if(idEmpleado == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El id del empleado es requerido");
            return;
        }

        try {
            Empleado empleado = createEmpleadoFromRequest(req, Math.toIntExact(idEmpleado));
            empleadoBusiness.editarEmpleadoJPA(empleado);
            req.setAttribute("msj", "Empleado editado correctamente");
            resp.sendRedirect(Routes.EMPLEADO.getRoute());
        } catch (ParseException e) {
            handleParseException(req, resp, e);
        } catch (EmailAlreadyInUseException e){
            handleEmailAlreadyInUseException(req, resp, e);
        } catch (Exception e) {
            throw new ServletException(e);
        }


    }

    private Empleado createEmpleadoFromRequest(HttpServletRequest request, Integer id) throws ParseException {
        String strDate = request.getParameter("fechaNacimiento");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return new Empleado(
                id,
                codigoEmpleado,
                request.getParameter("nombres"),
                request.getParameter("apellidoPat"),
                request.getParameter("apellidoMat"),
                Integer.parseInt(request.getParameter("idDepartamento")),
                request.getParameter("correo"),
                Double.parseDouble(request.getParameter("salario")),
                sdf.parse(strDate));
    }

    private void handleParseException(HttpServletRequest request, HttpServletResponse response, ParseException e) throws ServletException, IOException {
        loadDepartamentos(request);
        request.setAttribute("error", "Fecha Nacimiento no válido, el formato debe ser yyyy-MM-dd");
        request.getRequestDispatcher("/empleado/editar.jsp").forward(request, response);
    }

    private void handleEmailAlreadyInUseException(HttpServletRequest request, HttpServletResponse response, EmailAlreadyInUseException e) throws ServletException, IOException {
        loadDepartamentos(request);
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/empleado/editar.jsp").forward(request, response);
    }

    private void loadDepartamentos(HttpServletRequest request)  {
        List<Departamento> departamentos = parametroDao.obtenerDepartamentos();
        request.setAttribute("departamentos", departamentos);
    }
}
