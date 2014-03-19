package it.geosolutions.geofence;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;

import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

/**
 * Sets the context parameters for the {@link com.codahale.metrics.servlets.AdminServlet}
 *
 * Created by Jesse on 3/19/14.
 */
public class MetricsRegistryContextListener extends MetricsServlet.ContextListener {
    private MetricRegistry metricsRegistry;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        final WebApplicationContext webApplicationContext = getWebApplicationContext(event.getServletContext());
        this.metricsRegistry = webApplicationContext.getBean(MetricRegistry.class);
        super.contextInitialized(event);
    }

    @Override
    protected MetricRegistry getMetricRegistry() {
        return this.metricsRegistry;
    }
}
