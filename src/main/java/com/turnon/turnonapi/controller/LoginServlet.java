package com.turnon.turnonapi.controller;

import com.turnon.turnonapi.dao.UsuarioDAO;
import com.turnon.turnonapi.model.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        try (PrintWriter out = response.getWriter()) {

            if (correo == null || correo.trim().isEmpty()
                    || contrasena == null || contrasena.trim().isEmpty()) {
                out.println("Debe ingresar correo y contraseña");
                return;
            }

            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.login(correo.trim(), contrasena.trim());

            if (usuario != null) {
                out.println("Login exitoso. Bienvenido: "
                        + usuario.getNombre() + " " + usuario.getApellido());
            } else {
                out.println("Correo o contraseña incorrectos");
            }

        } catch (Exception e) {
            response.getWriter().println("Error en el login: " + e.getMessage());
        }
    }
}