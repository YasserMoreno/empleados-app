package com.ez.sisemp.login.business;

import com.ez.sisemp.login.dao.UsuarioDao;
import com.ez.sisemp.login.entity.UsuarioEntity;
import com.ez.sisemp.login.exception.UserOrPassIncorrectException;
import com.ez.sisemp.login.model.Usuario;
import jakarta.persistence.PersistenceException;

import java.sql.SQLException;

public class UsuarioBusiness {

    private final UsuarioDao usuarioDao;

    public UsuarioBusiness() {
        this.usuarioDao = new UsuarioDao();
    }

    //JDBC

    /*
    public Usuario login(String username, String password) throws SQLException, ClassNotFoundException {
        return usuarioDao.login(username, password);
    }
    */
    //JPA

    public Usuario loginJPA(String username, String password) throws SQLException, ClassNotFoundException {
        try {
            // Llamada al DAO para obtener la entidad UsuarioEntity
            UsuarioEntity usuarioEntity = usuarioDao.loginJPA(username, password);

            if (usuarioEntity == null) {
                // Manejo cuando no se encuentra el usuario
                throw new UserOrPassIncorrectException("Usuario o contraseña incorrectos");
            }

            // Creación del objeto Usuario a partir de UsuarioEntity
            Usuario usuario = new Usuario(
                    usuarioEntity.getId(),
                    usuarioEntity.getNombreUsuario(),
                    usuarioEntity.getContrasena(), // Aquí se debe considerar si se devuelve la contraseña en texto plano o no
                    usuarioEntity.getPrimerNombre(),
                    usuarioEntity.getApellidoPat(),
                    usuarioEntity.getFotoPerfil(),
                    usuarioEntity.getIdRol()
            );

            // Realizar validaciones adicionales si es necesario

            return usuario;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

}
