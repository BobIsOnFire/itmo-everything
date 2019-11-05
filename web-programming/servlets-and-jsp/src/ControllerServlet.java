import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher;
        if (req.getParameter("X") == null || req.getParameter("Y") == null || req.getParameter("R") == null)
            dispatcher = req.getRequestDispatcher("/main.jsp");
        else
            dispatcher = req.getRequestDispatcher("/area-check");

        dispatcher.forward(req, resp);
    }
}
