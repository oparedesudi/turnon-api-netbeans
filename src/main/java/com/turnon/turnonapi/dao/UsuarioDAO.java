package com.turnon.turnonapi.dao;

import com.turnon.turnonapi.config.ConexionBD;
import com.turnon.turnonapi.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public Usuario login(String correo, String contrasena) {
        Usuario usuario = null;

        String sql = "SELECT id_usuario, nombre, apellido, correo, contrasena, tipo_usuario "
                   + "FROM usuario WHERE correo = ? AND contrasena = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasena(rs.getString("contrasena"));
                    usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuario;
    }

    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre, apellido, correo, contrasena, tipo_usuario) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getTipoUsuario());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}