import history.HistoryBean;
import history.HistoryNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AreaCheckServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        String x = req.getParameter("X");
        String y = req.getParameter("Y");
        String[] rs = req.getParameterValues("R");

        if (x == null || y == null || rs == null) {
            req.getRequestDispatcher("/main.jsp").forward(req, resp);
            return;
        }

        Object history = req.getSession().getAttribute("history");
        HistoryBean bean;
        if (!(history instanceof HistoryBean)) {
            bean = new HistoryBean();
            req.getSession().setAttribute("history", bean);
        } else bean = (HistoryBean) history;

        PrintWriter writer = resp.getWriter();
        String contextPath = req.getContextPath();

        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Результаты</title>");
        writer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + contextPath + "/light.css\">");
        writer.println("</head>");
        writer.println("<body>");

        for (String r: rs) {
            writer.println("<table class=\"label centered\"><caption>Результат для R = " + r + "</caption>");
            try {
                HistoryNode node = new HistoryNode(x, y, r);
                bean.getNodeList().add(node);
                writer.println("<tr><td>X</td><td>" + x + "</td></tr>");
                writer.println("<tr><td>Y</td><td>" + y + "</td></tr>");
                writer.println("<tr><td colspan=\"2\">" + ( node.isHit() ? "Попал в цель!" : "Промазал!" ) + "</td></tr>");
            }
            catch (Exception e) {
                writer.println("<tr><td colspan=\"2\">" + e.getMessage() + "</td></tr>");
            }
            writer.println("</table>");
        }

        writer.println("<div class=\"centered\">");
        writer.println("<input type=\"button\" class=\"button\" value=\"Вернуться на главную страницу\"" +
                " onclick=\"location.href='" + contextPath + "/';\">");
        writer.println("</div>");

        writer.println("</body>");
        writer.println("</html>");
    }
}
