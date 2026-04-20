package com.turnon.turnonapi.controller;

import com.turnon.turnonapi.dao.ServicioDAO;
import com.turnon.turnonapi.model.Servicio;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/servicios")
public class ServicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServicioDAO dao = new ServicioDAO();
        List<Servicio> lista = dao.listarServicios();

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < lista.size(); i++) {
            Servicio s = lista.get(i);

            json.append("{")
                .append("\"id_servicio\":").append(s.getIdServicio()).append(",")
                .append("\"nombre\":\"").append(escapeJson(s.getNombre())).append("\",")
                .append("\"id_tipo_servicio\":").append(s.getIdTipoServicio())
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