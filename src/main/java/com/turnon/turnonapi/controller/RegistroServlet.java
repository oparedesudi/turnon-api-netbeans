package com.turnon.turnonapi.controller;

import com.turnon.turnonapi.dao.UsuarioDAO;
import com.turnon.turnonapi.model.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String tipoUsuario = request.getParameter("tipo_usuario");

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (nombre == null || nombre.trim().isEmpty()
                || apellido == null || apellido.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()
                || contrasena == null || contrasena.trim().isEmpty()
                || tipoUsuario == null || tipoUsuario.trim().isEmpty()) {
            out.println("Todos los campos son obligatorios");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setTipoUsuario(tipoUsuario);

        UsuarioDAO dao = new UsuarioDAO();
        boolean resultado = dao.registrarUsuario(usuario);

        if (resultado) {
            out.println("Usuario registrado correctamente");
        } else {
            out.println("Error al registrar usuario");
        }
    }
}