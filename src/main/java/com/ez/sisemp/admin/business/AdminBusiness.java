package com.ez.sisemp.admin.business;

import com.ez.sisemp.admin.dao.AdminDao;
import com.ez.sisemp.admin.model.Usuario;
import com.ez.sisemp.login.entity.UsuarioEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminBusiness {

    private final AdminDao adminDao;

    public AdminBusiness() {
        this.adminDao = new AdminDao();
    }

    //JPA

    public List<Usuario> obtenerUsuariosJPA() throws SQLException, ClassNotFoundException {

        var usuarios = adminDao.obtenerUsuariosJPA();
        if (usuarios.isEmpty()){
            throw new SQLException("No se encontro usuarios en la base de datos");
        }

        var usuariosToReturn = new ArrayList<Usuario>();
        usuarios.forEach(usuario -> {
            var usuarioRecord = mapToRecordUsuarios(usuario);
            usuariosToReturn.add(usuarioRecord);
        });

        return usuariosToReturn;

    }

    public Boolean usuarioActivo(Integer activeValue) {
        if (activeValue.intValue() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public String rolUsuario(Integer idRol) {
        if(idRol == 1){
            return "Administrador";
        } else {
            return "Usuario";
        }
    }

    public Usuario mapToRecordUsuarios(UsuarioEntity usuarioEntity) {
        Usuario usuario = new Usuario(
                usuarioEntity.getId(),
                usuarioEntity.getNombreUsuario(),
                usuarioEntity.getContrasena(),
                usuarioEntity.getContrasenaAnterior(),
                usuarioEntity.getUltimaConexion(),
                usuarioActivo(usuarioEntity.getActive()),
                usuarioEntity.getPrimerNombre(),
                usuarioEntity.getApellidoPat(),
                usuarioEntity.getFotoPerfil(),
                rolUsuario(usuarioEntity.getIdRol())
        );
        return usuario;
    }
}
