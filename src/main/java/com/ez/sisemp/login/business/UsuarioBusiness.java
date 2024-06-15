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
            UsuarioEntity usuarioEntity = usuarioDao.loginJPA(username, password);

            if (usuarioEntity == null) {
                throw new UserOrPassIncorrectException("Usuario o contraseña incorrectos");
            }

            Usuario usuario = new Usuario(
                    usuarioEntity.getId(),
                    usuarioEntity.getNombreUsuario(),
                    usuarioEntity.getContrasena(),
                    usuarioEntity.getPrimerNombre(),
                    usuarioEntity.getApellidoPat(),
                    usuarioEntity.getFotoPerfil(),
                    usuarioEntity.getIdRol()
            );

            return usuario;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

}
