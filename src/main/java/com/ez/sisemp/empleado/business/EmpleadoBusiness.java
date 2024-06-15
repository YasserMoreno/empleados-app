package com.ez.sisemp.empleado.business;

import com.ez.sisemp.empleado.dao.EmpleadoDao;
import com.ez.sisemp.empleado.dao.EmpleadoDashboardDao;
import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.empleado.exception.EmailAlreadyInUseException;
import com.ez.sisemp.empleado.exception.EmpleadosNotFoundException;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.empleado.model.EmpleadoDashboard;
import com.ez.sisemp.parametro.dao.ParametroDao;
import com.ez.sisemp.shared.utils.EdadUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoBusiness {

    private final EmpleadoDao empleadoDao;
    private final EmpleadoDashboardDao empleadoDashboardDao;
    private final ParametroDao parametroDao;

    public EmpleadoBusiness(){
        this.empleadoDao = new EmpleadoDao();
        this.empleadoDashboardDao = new EmpleadoDashboardDao();
        this.parametroDao = new ParametroDao();
    }


    //JDBC

    /*
    public void registrarEmpleado(Empleado empleado) throws SQLException, ClassNotFoundException {
        empleado = new Empleado(generarCodigoEmpleado(), empleado.nombres(), empleado.apellidoPat(), empleado.apellidoMat(), empleado.idDepartamento(), empleado.correo(), empleado.salario(), empleado.fechaNacimiento());
        validarCampos(empleado);
        try {
            empleadoDao.agregarEmpleado(empleado);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new EmailAlreadyInUseException(String.format("El correo %s ya se encuentra registrado", empleado.correo()));
        }
    }


    public void editarEmpleado(Empleado empleado) throws SQLException, ClassNotFoundException {
        empleadoDao.editarEmpleado(empleado);
    }



    public void eliminarEmpleado(int id) throws SQLException, ClassNotFoundException {
        empleadoDao.eliminarEmpleado(id);
    }


    public List<Empleado> obtenerEmpleados() throws SQLException, ClassNotFoundException {
        var empleados = empleadoDao.obtenerEmpleados();
        if(empleados.isEmpty()){
            throw new EmpleadosNotFoundException("No se encontraron empleados");
        }
        return empleadoDao.obtenerEmpleados();
    }
    */

    //JPA

    public List<Empleado> obtenerEmpleadosJpa() {
        var empleados = empleadoDao.obtenerEmpleadosJPA();
        if(empleados.isEmpty()){
            throw new EmpleadosNotFoundException("No se encontraron empleados");
        }
        var empleadosToReturn = new ArrayList<Empleado>();
        empleados.forEach(
                e -> {
                    var empleadoRecord = mapToRecord(e);
                    empleadosToReturn.add(empleadoRecord);
                }
        );
        return empleadosToReturn;
    }

    public Empleado buscarEmpleadoPorIdJPA(Long id) {
        var empleadoEntity = empleadoDao.buscarEmpleadoJPA(id);
        if(empleadoEntity == null){
            return null;
        }
        Empleado empleado = mapToRecord(empleadoEntity);
        return empleado;
    }

    public void registrarEmpleadoJPA(Empleado empleado) throws SQLException, ClassNotFoundException {
        EmpleadoEntity empleadoEntity = new EmpleadoEntity();
        //empleadoEntity.setId(Long.parseLong(String.valueOf(empleado.id())));
        empleadoEntity.setCodigoEmpleado(generarCodigoEmpleado());
        empleadoEntity.setNombres(empleado.nombres());
        empleadoEntity.setApellidoPat(empleado.apellidoPat());
        empleadoEntity.setApellidoMat(empleado.apellidoMat());
        empleadoEntity.setIdDepartamento(empleado.idDepartamento());
        empleadoEntity.setCorreo(empleado.correo());
        empleadoEntity.setSalario(empleado.salario());
        empleadoEntity.setFechaNacimiento(empleado.fechaNacimiento());
        empleadoEntity.setActivo(1);
        empleadoDao.registrarEmpleadoJPA(empleadoEntity);

    }

    public void editarEmpleadoJPA(Empleado empleado) throws SQLException, ClassNotFoundException {

        EmpleadoEntity empleadoEntity = new EmpleadoEntity();
        empleadoEntity.setId(Long.parseLong(String.valueOf(empleado.id())));
        empleadoEntity.setCodigoEmpleado(empleado.codigoEmpleado());
        empleadoEntity.setNombres(empleado.nombres());
        empleadoEntity.setApellidoPat(empleado.apellidoPat());
        empleadoEntity.setApellidoMat(empleado.apellidoMat());
        empleadoEntity.setIdDepartamento(empleado.idDepartamento());
        empleadoEntity.setCorreo(empleado.correo());
        empleadoEntity.setSalario(empleado.salario());
        empleadoEntity.setFechaNacimiento(empleado.fechaNacimiento());
        empleadoDao.editarEmpleadoJPA(empleadoEntity);
    }

    public void eliminarEmpleadoJPA(Long id) throws SQLException, ClassNotFoundException {
        empleadoDao.eliminarEmpleadoJPA(id);
    }


    //Funciones Adicionales

    private Empleado mapToRecord(EmpleadoEntity e) {
        var departamento = parametroDao.getById(e.getIdDepartamento());
        return new Empleado(
                Math.toIntExact(e.getId()),
                e.getCodigoEmpleado(),
                e.getNombres(),
                e.getApellidoPat(),
                e.getApellidoMat(),
                e.getIdDepartamento(),
                departamento.getNombre(),
                e.getCorreo(),
                EdadUtils.calcularEdad(e.getFechaNacimiento()),
                e.getSalario(),
                e.getFechaNacimiento()
        );
    }

    public EmpleadoDashboard obtenerDatosDashboard() throws SQLException, ClassNotFoundException {
        return empleadoDashboardDao.get();
    }

    private String generarCodigoEmpleado(){
        return "EMP" + (int) (Math.random() * 1000000);
    }

    private void validarCampos (Empleado empleado){
        if(StringUtils.isBlank(empleado.codigoEmpleado())){
            throw new IllegalArgumentException("El codigo del empleado no puede ser nulo");
        }
        if(StringUtils.isBlank(empleado.nombres())){
            throw new IllegalArgumentException("El nombre del empleado no puede ser nulo");
        }
        if(StringUtils.isBlank(empleado.apellidoPat())){
            throw new IllegalArgumentException("El apellido paterno del empleado no puede ser nulo");
        }
        if(StringUtils.isBlank(empleado.correo())){
            throw new IllegalArgumentException("El correo del empleado no puede ser nulo");
        }
        if(StringUtils.isBlank(empleado.fechaNacimiento().toString())){
            throw new IllegalArgumentException("La fecha de nacimiento del empleado no puede ser nula");
        }
        if(empleado.salario() < 0){
            throw new IllegalArgumentException("El salario del empleado no puede ser negativo");
        }
    }
}
