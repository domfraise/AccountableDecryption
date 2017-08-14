package AccountableDec.JavaServer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Hello world!
 *
 */
public class JavaServer {

    public static void main( String[] args ) throws Exception{
    	Server server = new Server(8080);
    	ServletContextHandler handler = new ServletContextHandler(server, "/");
    	handler.addServlet(Servlet.class, "/");
    	server.start();
    	server.join();
    }
}
