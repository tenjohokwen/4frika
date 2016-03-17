package org.fourfrika.health;

import com.codahale.metrics.JmxAttributeGauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.base.CaseFormat;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.Map;

@Component
public class TomcatGaugeSet implements MetricSet {

    private static final Map<String, String[]> attributes = ImmutableMap.<String, String[]>builder()
            .put("Tomcat:type=GlobalRequestProcessor,name=\"http-nio-8080\"", new String[] {
                    "bytesReceived"
                    , "bytesSent"
                    , "errorCount"
                    , "maxTime"
                    , "processingTime"
                    , "requestCount"})
            .put("Tomcat:type=ThreadPool,name=\"http-nio-8080\"", new String[] {
                    "connectionCount"
                    , "currentThreadCount"
                    , "currentThreadsBusy"
                    , "maxThreads"}).build();

    @Override
    public Map<String, Metric> getMetrics() {
        MBeanServer server = JmxUtils.locateMBeanServer();

        ImmutableMap.Builder<String, Metric> builder = ImmutableMap.<String, Metric>builder();

        attributes.forEach((key, attributeNames) -> {
            try {
                ObjectName objectName = ObjectNameManager.getInstance(key);
                String gaugeName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, objectName.getKeyProperty("type"));
                for ( String attributeName : attributeNames ) {
                    builder.put("tomcat." + gaugeName + "." + attributeName, new JmxAttributeGauge(server, objectName, attributeName));
                }
            } catch (Exception ex) {
                Throwables.propagate(ex);
            }
        });
        return builder.build();
    }

}
