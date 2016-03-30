package com.gecisyon.timeseries.ingest.rest;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.DefaultJaxrsConfig;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

import com.gecisyon.timeseries.ingest.util.TSConfig;

public class RestServer implements Runnable{

	@Override
	public void run() {
		// Swagger Configuration 
		BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0");
        beanConfig.setBasePath( "/webapi" );
        beanConfig.setDescription( "Timeseries Ingest RESTful services" );
        beanConfig.setTitle( "Timeseries Ingest RESTful API" );
        beanConfig.setScan( true );
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
 
        Server jettyServer = new Server(TSConfig.getInstance().getWEB_CONTAINER_PORT());
        jettyServer.setHandler(context);    
              
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/webapi/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES,"com.gecisyon.timeseries.ingest.rest.resource;io.swagger.jaxrs.json;io.swagger.jaxrs.listing;");
        
// 		Setup API resources
//      ServletHolder apiServlet = context.addServlet(ServletContainer.class, "/api/*");
//      apiServlet.setInitOrder(1);
//      apiServlet.setInitParameter("jersey.config.server.provider.packages", "com.gecisyon.timeseries.ingest.rest;io.swagger.jaxrs.json;io.swagger.jaxrs.listing");

        // Setup Swagger servlet
        ServletHolder swaggerServlet = context.addServlet(DefaultJaxrsConfig.class, "/swagger-core");
        swaggerServlet.setInitOrder(2);
        swaggerServlet.setInitParameter("api.version", "1.0.0");

        // Setup Swagger-UI static resources
        String resourceBasePath = RestServer.class.getResource("/webapp").toExternalForm();
        context.setWelcomeFiles(new String[] {"index.html"});
        context.setResourceBase(resourceBasePath);
        context.addServlet(new ServletHolder(new DefaultServlet()), "/*");
        
        
        
        try {
            jettyServer.start();
            jettyServer.join();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        finally {
            jettyServer.destroy();
        }
		
	}

}
