package com.ez.sisemp.empleado.business;

import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.empleado.model.Empleado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.*;

public class EmpleadoBusinessTest {

    private EmpleadoBusiness empleadoBusiness;

    @BeforeEach
    void setUp(){
        empleadoBusiness = new EmpleadoBusiness();
    }

    @Test
    void testRegistrarEmpleado() throws SQLException, ClassNotFoundException {

        var empleado = crearEmpleadoTest();
        empleadoBusiness.registrarEmpleadoJPA(empleado);
        var empleadoTest = empleadoBusiness.buscarEmpleadoPorCorreoJPA(empleado.correo());
        assertNotNull(empleadoTest);

        //Borrar Data
        empleadoBusiness.eliminarEmpleadoFisicaJPA(Long.valueOf(empleadoTest.id()));
    }

    @Test
    void testEliminacionLogicaEmpleado() throws SQLException, ClassNotFoundException {

        var empleado = crearEmpleadoTest();
        empleadoBusiness.registrarEmpleadoJPA(empleado);
        var empleadoTest = empleadoBusiness.buscarEmpleadoPorCorreoJPA(empleado.correo());
        assertNotNull(empleadoTest);
        empleadoBusiness.eliminarEmpleadoJPA(Long.valueOf(empleadoTest.id()));
        var empleadoEdit = empleadoBusiness.buscarEmpleadoPorCorreoJPA(empleado.correo());
        assertNull(empleadoEdit);

        //Borrar Data
        var empleadoToDelete = empleadoBusiness.buscarEmpleadoPorCorreoAllJPA(empleado.correo());
        empleadoBusiness.eliminarEmpleadoFisicaJPA(Long.valueOf(empleadoToDelete.id()));
    }

    @Test
    void testListarEmpleados() {
        var empleados = empleadoBusiness.obtenerEmpleadosJpa();
        assertNotNull(empleados);
    }

    @Test
    void testEditarEmpleado() throws SQLException, ClassNotFoundException {

        var empleado = crearEmpleadoTest();
        empleadoBusiness.registrarEmpleadoJPA(empleado);
        var empleadoTest = empleadoBusiness.buscarEmpleadoPorCorreoJPA(empleado.correo());
        assertNotNull(empleadoTest);

        var empleadoEdit = new Empleado(
            empleadoTest.id(),
            empleado.codigoEmpleado(),
            empleado.nombres(),
            "Perez",
            empleado.apellidoMat(),
            empleado.idDepartamento(),
            empleado.correo(),
            empleado.salario(),
            empleado.fechaNacimiento()
        );

        empleadoBusiness.editarEmpleadoJPA(empleadoEdit);
        var empleadoEditado = empleadoBusiness.buscarEmpleadoPorCorreoJPA(empleado.correo());
        assertNotNull(empleadoEditado);
        assertEquals(empleadoEdit.correo(), empleadoEditado.correo());

        //Borrar Data
        var empleadoToDelete = empleadoBusiness.buscarEmpleadoPorCorreoAllJPA(empleado.correo());
        empleadoBusiness.eliminarEmpleadoFisicaJPA(Long.valueOf(empleadoToDelete.id()));
    }

    //Funciones Adicionales

    private Empleado crearEmpleadoTest(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(1996, Calendar.JUNE, 24, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date fechaNacimiento = calendar.getTime();

        var empleado = new Empleado(
                generarCodigoEmpleado(),
                "José",
                "Rodriguez",
                "Pastor",
                2,
                "prueba2@isil.pe",
                5000,
                fechaNacimiento
        );

        return empleado;
    }

    private String generarCodigoEmpleado(){
        return "EMP" + (int) (Math.random() * 1000000);
    }
  
}