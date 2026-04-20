package com.turnon.turnonapi.controller;

import com.turnon.turnonapi.dao.EstadoTurnoDAO;
import com.turnon.turnonapi.model.EstadoTurno;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/estado-turno")
public class EstadoTurnoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EstadoTurnoDAO dao = new EstadoTurnoDAO();
        List<EstadoTurno> lista = dao.listarEstadosTurno();

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < lista.size(); i++) {
            EstadoTurno e = lista.get(i);

            json.append("{")
                .append("\"id_estado_turno\":").append(e.getIdEstadoTurno()).append(",")
                .append("\"descripcion\":\"").append(escapeJson(e.getDescripcion())).append("\"")
                .append("}");

            if (i < lista.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        out.print(json.toString());
        out.flush();
    }

    private String escapeJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}