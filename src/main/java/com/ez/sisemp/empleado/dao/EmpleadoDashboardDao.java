package com.ez.sisemp.empleado.dao;

import com.ez.sisemp.empleado.model.EmpleadoDashboard;
import com.ez.sisemp.shared.config.MySQLConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.sql.SQLException;

public class EmpleadoDashboardDao {

    private static final String SQL_GET_TOTAL_EMPLEADOS = "SELECT COUNT(*) FROM empleado";
    private static final String SQL_GET_PROMEDIO_EDAD = "SELECT FLOOR(AVG(DATEDIFF(NOW(), fecha_nacimiento) / 365.25)) AS avg_age FROM empleado;";
    private static final String SQL_GET_MAYOR_SALARIO = "SELECT MAX(salario) FROM empleado";
    private static final String SQL_GET_TOTAL_DEPARTAMENTOS = "SELECT COUNT(DISTINCT id_departamento) FROM empleado"; //TODO

    private static final String JPQL_GET_TOTAL_EMPLEADOS = "SELECT COUNT(e) FROM EmpleadoEntity e WHERE e.activo = 1";
    private static final String JPQL_GET_PROMEDIO_EDAD = "SELECT FLOOR(AVG(FUNCTION('DATEDIFF', CURRENT_DATE(), e.fechaNacimiento) / 365.25)) FROM EmpleadoEntity e";
    private static final String JPQL_GET_MAYOR_SALARIO = "SELECT MAX(e.salario) FROM EmpleadoEntity e";
    private static final String JPQL_GET_TOTAL_DEPARTAMENTOS = "SELECT COUNT(DISTINCT e.departamento.id) FROM EmpleadoEntity e";

    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");

    public EmpleadoDashboard get() throws SQLException, ClassNotFoundException {
        return new EmpleadoDashboard(
            getTotalEmpleadosJPA(),
            getMayorSalario(),
            getPromedioEdad(),
            getTotalDepartamentos()
        );
    }

    public int getTotalEmpleados() throws SQLException, ClassNotFoundException {
        var result = MySQLConnection.executeQuery(SQL_GET_TOTAL_EMPLEADOS);
        result.next();
        return result.getInt(1);
    }
    public int getPromedioEdad() throws SQLException, ClassNotFoundException {
       var result = MySQLConnection.executeQuery(SQL_GET_PROMEDIO_EDAD);
       result.next();
       return result.getInt(1);
    }
    public double getMayorSalario() throws SQLException, ClassNotFoundException {
        var result = MySQLConnection.executeQuery(SQL_GET_MAYOR_SALARIO);
        result.next();
        return result.getDouble(1);
    }
    public int getTotalDepartamentos() {
        return 0;
    }

    //JPA
    public int getTotalEmpleadosJPA() throws SQLException, ClassNotFoundException {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try{
            entityManager.getTransaction().begin();
            Query query = entityManager.createQuery(JPQL_GET_TOTAL_EMPLEADOS);
            Long result = (Long) query.getSingleResult();
            entityManager.getTransaction().commit();
            if (result != null) {
                return result.intValue();
            } else {
                return 0;
            }
        } finally {
            entityManager.close();
        }

    }
}
