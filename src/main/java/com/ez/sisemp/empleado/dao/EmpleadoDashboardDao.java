package com.ez.sisemp.empleado.dao;

import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.empleado.model.EmpleadoDashboard;
import com.ez.sisemp.parametro.dao.ParametroDao;
import com.ez.sisemp.shared.config.MySQLConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

public class EmpleadoDashboardDao {

    private static final String SQL_GET_TOTAL_EMPLEADOS = "SELECT COUNT(*) FROM empleado";
    private static final String SQL_GET_PROMEDIO_EDAD = "SELECT FLOOR(AVG(DATEDIFF(NOW(), fecha_nacimiento) / 365.25)) AS avg_age FROM empleado";
    private static final String SQL_GET_MAYOR_SALARIO = "SELECT MAX(salario) FROM empleado";
    private static final String SQL_GET_TOTAL_DEPARTAMENTOS = "SELECT COUNT(DISTINCT id_departamento) FROM empleado"; //TODO

    private static final String JPQL_GET_TOTAL_EMPLEADOS = "SELECT COUNT(e) FROM EmpleadoEntity e WHERE e.activo = 1";
    private static final String JPQL_GET_PROMEDIO_EDAD = "SELECT FLOOR(AVG(YEAR(CURRENT_DATE()) - YEAR(e.fechaNacimiento))) AS avg_age FROM EmpleadoEntity e WHERE e.activo = 1";
    private static final String JPQL_GET_MAYOR_SALARIO = "SELECT MAX(e.salario) FROM EmpleadoEntity e";
    private static final String JPQL_GET_TOTAL_DEPARTAMENTOS = "SELECT COUNT(DISTINCT e.departamento.id) FROM EmpleadoEntity e";

    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");


    private static Logger logger = Logger.getLogger(EmpleadoDashboardDao.class.getName());

    public EmpleadoDashboard get() throws SQLException, ClassNotFoundException {
        return new EmpleadoDashboard(
            getTotalEmpleadosJPA(),
            getMayorSalarioJPA(),
            getPromedioEdadJPA(),
            getTotalDepartamentosJPA()
        );
    }

    //JDBC

    /*
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
    */


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

    public int getPromedioEdadJPA() throws SQLException, ClassNotFoundException {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try{
            entityManager.getTransaction().begin();
            EmpleadoDao empleadoDao = new EmpleadoDao();
            var empleados = empleadoDao.obtenerEmpleadosJPA();

            int totalEdad = 0;
            int numeroEmpleados = empleados.size();

            for(EmpleadoEntity empleado : empleados){
                LocalDate fechaNacimiento = convertToLocalDateViaInstant(empleado.getFechaNacimiento());
                int edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
                var edadEmpleado = Integer.parseInt(String.valueOf(edad));
                totalEdad += edadEmpleado;
            }

            int promedioEdad = (numeroEmpleados > 0) ? totalEdad / numeroEmpleados : 0;

            entityManager.getTransaction().commit();

            return promedioEdad;

        } finally {
            entityManager.close();
        }

    }

    public double getMayorSalarioJPA() throws SQLException, ClassNotFoundException {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try{
            entityManager.getTransaction().begin();
            Query query = entityManager.createQuery(JPQL_GET_MAYOR_SALARIO);
            Double result = (Double) query.getSingleResult();
            //logger.info("Mayor salario: " + result);
            entityManager.getTransaction().commit();
            if (result != null) {
                return result;
            } else {
                return 0;
            }
        } catch (Exception e){
            logger.info("Error en Mayor salario: " + e.getMessage());
            return 0;
        }
        finally {
            entityManager.close();
        }
    }

    public int getTotalDepartamentosJPA() throws SQLException, ClassNotFoundException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {

            entityManager.getTransaction().begin();
            ParametroDao parametroDao = new ParametroDao();
            var departametos = parametroDao.obtenerDepartamentos();
            int totalDepartamentos = departametos.size();
            entityManager.getTransaction().commit();
            return totalDepartamentos;

        } catch (Exception e) {
            logger.info("Error en TotalDepartamentos: " + e.getMessage());
            return 0;
        } finally {
            entityManager.close();
        }
    }

    //Otras funcionalidades

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
