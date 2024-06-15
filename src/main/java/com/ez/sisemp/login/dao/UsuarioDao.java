package com.ez.sisemp.login.dao;

import com.ez.sisemp.login.entity.UsuarioEntity;
import com.ez.sisemp.shared.config.MySQLConnection;
import com.ez.sisemp.login.exception.UserOrPassIncorrectException;
import com.ez.sisemp.login.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuarioDao {
    private static final String SQL_GET_USER = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ?";
    private static final String JPQL_GET_USER = "SELECT u FROM UsuarioEntity u WHERE u.nombreUsuario = :username AND u.contrasena = :password";


    //JDBC

    /*
    public Usuario login(String username, String password) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = MySQLConnection.getConnection()
                                                .prepareStatement(SQL_GET_USER);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            return mapResultSetToUsuario(resultSet);
        }else {
            throw new UserOrPassIncorrectException("Usuario o contraseña incorrectos");
        }
    }
     */


    private Usuario mapResultSetToUsuario(ResultSet resultSet) throws SQLException {
        return new Usuario(resultSet.getInt("id"),
                resultSet.getString("nombre_usuario"),
                resultSet.getString("contrasena"),
                resultSet.getString("primer_nombre"),
                resultSet.getString("apellido_pat"),
                resultSet.getString("foto_perfil"),
                resultSet.getInt("id_rol")
        );
    }

    //JPA

    public UsuarioEntity loginJPA(String usuario, String contrasena) throws SQLException, ClassNotFoundException {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            // Crear consulta JPA
            Query query = entityManager.createQuery(JPQL_GET_USER, UsuarioEntity.class);
            query.setParameter("username", usuario);
            query.setParameter("password", contrasena);

            List<UsuarioEntity> resultList = query.getResultList();

            if (!resultList.isEmpty()) {
                UsuarioEntity usuarioEntity = resultList.get(0);
                entityManager.getTransaction().commit();
                return usuarioEntity;
            } else {
                throw new UserOrPassIncorrectException("Usuario o contraseña incorrectos");
            }
        } finally {
            entityManager.close();
        }
    }

}

