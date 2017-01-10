package com.securityx.health.agent.test;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MockFeatureServiceServer {
    HttpServer httpServer;
    List<HttpContext> routes = new ArrayList<>();
    public MockFeatureServiceServer(String ip, int port) throws Exception{
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(ip,port), 0);
        routes.add(httpServer.createContext("/prometheusMetrics", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                String response ="# HELP jvm_buffers_direct_capacity Generated from dropwizard metric import (metric=jvm_buffers_direct_capacity, type=com.codahale.metrics.JmxAttributeGauge)\n" +
                        "# TYPE jvm_buffers_direct_capacity gauge\n" +
                        "jvm_buffers_direct_capacity 85641.0\n" +
                        "# HELP jvm_buffers_direct_count Generated from dropwizard metric import (metric=jvm_buffers_direct_count, type=com.codahale.metrics.JmxAttributeGauge)\n" +
                        "# TYPE jvm_buffers_direct_count gauge\n" +
                        "jvm_buffers_direct_count 50.0\n" +
                        "# HELP jvm_buffers_direct_used Generated from dropwizard metric import (metric=jvm_buffers_direct_used, type=com.codahale.metrics.JmxAttributeGauge)\n" +
                        "# TYPE jvm_buffers_direct_used gauge\n" +
                        "jvm_buffers_direct_used 85641.0\n" +
                        "# HELP jvm_buffers_mapped_capacity Generated from dropwizard metric import (metric=jvm_buffers_mapped_capacity, type=com.codahale.metrics.JmxAttributeGauge)\n" +
                        "# TYPE jvm_buffers_mapped_capacity gauge\n" +
                        "jvm_buffers_mapped_capacity 0.0\n" +
                        "# HELP jvm_buffers_mapped_count Generated from dropwizard metric import (metric=jvm_buffers_mapped_count, type=com.codahale.metrics.JmxAttributeGauge)\n" +
                        "# TYPE jvm_buffers_mapped_count gauge\n" +
                        "jvm_buffers_mapped_count 0.0\n" +
                        "# HELP jvm_buffers_mapped_used Generated from dropwizard metric import (metric=jvm_buffers_mapped_used, type=com.codahale.metrics.JmxAttributeGauge)\n" +
                        "# TYPE jvm_buffers_mapped_used gauge\n" +
                        "jvm_buffers_mapped_used 0.0\n" +
                        "# HELP jvm_gc_PS_MarkSweep_count Generated from dropwizard metric import (metric=jvm_gc_PS_MarkSweep_count, type=com.codahale.metrics.jvm.GarbageCollectorMetricSet$1)\n" +
                        "# TYPE jvm_gc_PS_MarkSweep_count gauge\n" +
                        "jvm_gc_PS_MarkSweep_count 3.0\n" +
                        "# HELP jvm_gc_PS_MarkSweep_time Generated from dropwizard metric import (metric=jvm_gc_PS_MarkSweep_time, type=com.codahale.metrics.jvm.GarbageCollectorMetricSet$2)\n" +
                        "# TYPE jvm_gc_PS_MarkSweep_time gauge\n" +
                        "jvm_gc_PS_MarkSweep_time 237.0\n" +
                        "# HELP jvm_gc_PS_Scavenge_count Generated from dropwizard metric import (metric=jvm_gc_PS_Scavenge_count, type=com.codahale.metrics.jvm.GarbageCollectorMetricSet$1)\n" +
                        "# TYPE jvm_gc_PS_Scavenge_count gauge\n" +
                        "jvm_gc_PS_Scavenge_count 78.0\n" +
                        "# HELP jvm_gc_PS_Scavenge_time Generated from dropwizard metric import (metric=jvm_gc_PS_Scavenge_time, type=com.codahale.metrics.jvm.GarbageCollectorMetricSet$2)\n" +
                        "# TYPE jvm_gc_PS_Scavenge_time gauge\n" +
                        "jvm_gc_PS_Scavenge_time 946.0\n" +
                        "# HELP jvm_memory_heap_committed Generated from dropwizard metric import (metric=jvm_memory_heap_committed, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$8)\n" +
                        "# TYPE jvm_memory_heap_committed gauge\n" +
                        "jvm_memory_heap_committed 3.026714624E9\n" +
                        "# HELP jvm_memory_heap_init Generated from dropwizard metric import (metric=jvm_memory_heap_init, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$5)\n" +
                        "# TYPE jvm_memory_heap_init gauge\n" +
                        "jvm_memory_heap_init 2.147483648E9\n" +
                        "# HELP jvm_memory_heap_max Generated from dropwizard metric import (metric=jvm_memory_heap_max, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$7)\n" +
                        "# TYPE jvm_memory_heap_max gauge\n" +
                        "jvm_memory_heap_max 2.863136768E10\n" +
                        "# HELP jvm_memory_heap_usage Generated from dropwizard metric import (metric=jvm_memory_heap_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$9)\n" +
                        "# TYPE jvm_memory_heap_usage gauge\n" +
                        "jvm_memory_heap_usage 0.008494098176451486\n" +
                        "# HELP jvm_memory_heap_used Generated from dropwizard metric import (metric=jvm_memory_heap_used, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$6)\n" +
                        "# TYPE jvm_memory_heap_used gauge\n" +
                        "jvm_memory_heap_used 2.43197648E8\n" +
                        "# HELP jvm_memory_non_heap_committed Generated from dropwizard metric import (metric=jvm_memory_non_heap_committed, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$13)\n" +
                        "# TYPE jvm_memory_non_heap_committed gauge\n" +
                        "jvm_memory_non_heap_committed 9.7124352E7\n" +
                        "# HELP jvm_memory_non_heap_init Generated from dropwizard metric import (metric=jvm_memory_non_heap_init, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$10)\n" +
                        "# TYPE jvm_memory_non_heap_init gauge\n" +
                        "jvm_memory_non_heap_init 2555904.0\n" +
                        "# HELP jvm_memory_non_heap_max Generated from dropwizard metric import (metric=jvm_memory_non_heap_max, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$12)\n" +
                        "# TYPE jvm_memory_non_heap_max gauge\n" +
                        "jvm_memory_non_heap_max -1.0\n" +
                        "# HELP jvm_memory_non_heap_usage Generated from dropwizard metric import (metric=jvm_memory_non_heap_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$14)\n" +
                        "# TYPE jvm_memory_non_heap_usage gauge\n" +
                        "jvm_memory_non_heap_usage -9.5437672E7\n" +
                        "# HELP jvm_memory_non_heap_used Generated from dropwizard metric import (metric=jvm_memory_non_heap_used, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$11)\n" +
                        "# TYPE jvm_memory_non_heap_used gauge\n" +
                        "jvm_memory_non_heap_used 9.5437736E7\n" +
                        "# HELP jvm_memory_pools_Code_Cache_usage Generated from dropwizard metric import (metric=jvm_memory_pools_Code_Cache_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$15)\n" +
                        "# TYPE jvm_memory_pools_Code_Cache_usage gauge\n" +
                        "jvm_memory_pools_Code_Cache_usage 0.10872522989908855\n" +
                        "# HELP jvm_memory_pools_Compressed_Class_Space_usage Generated from dropwizard metric import (metric=jvm_memory_pools_Compressed_Class_Space_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$15)\n" +
                        "# TYPE jvm_memory_pools_Compressed_Class_Space_usage gauge\n" +
                        "jvm_memory_pools_Compressed_Class_Space_usage 0.0071277618408203125\n" +
                        "# HELP jvm_memory_pools_Metaspace_usage Generated from dropwizard metric import (metric=jvm_memory_pools_Metaspace_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$15)\n" +
                        "# TYPE jvm_memory_pools_Metaspace_usage gauge\n" +
                        "jvm_memory_pools_Metaspace_usage 0.9825278958388837\n" +
                        "# HELP jvm_memory_pools_PS_Eden_Space_usage Generated from dropwizard metric import (metric=jvm_memory_pools_PS_Eden_Space_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$15)\n" +
                        "# TYPE jvm_memory_pools_PS_Eden_Space_usage gauge\n" +
                        "jvm_memory_pools_PS_Eden_Space_usage 0.015811356144031563\n" +
                        "# HELP jvm_memory_pools_PS_Old_Gen_usage Generated from dropwizard metric import (metric=jvm_memory_pools_PS_Old_Gen_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$15)\n" +
                        "# TYPE jvm_memory_pools_PS_Old_Gen_usage gauge\n" +
                        "jvm_memory_pools_PS_Old_Gen_usage 0.003378887056367193\n" +
                        "# HELP jvm_memory_pools_PS_Survivor_Space_usage Generated from dropwizard metric import (metric=jvm_memory_pools_PS_Survivor_Space_usage, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$15)\n" +
                        "# TYPE jvm_memory_pools_PS_Survivor_Space_usage gauge\n" +
                        "jvm_memory_pools_PS_Survivor_Space_usage 0.3125050862630208\n" +
                        "# HELP jvm_memory_total_committed Generated from dropwizard metric import (metric=jvm_memory_total_committed, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$4)\n" +
                        "# TYPE jvm_memory_total_committed gauge\n" +
                        "jvm_memory_total_committed 3.123838976E9\n" +
                        "# HELP jvm_memory_total_init Generated from dropwizard metric import (metric=jvm_memory_total_init, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$1)\n" +
                        "# TYPE jvm_memory_total_init gauge\n" +
                        "jvm_memory_total_init 2.150039552E9\n" +
                        "# HELP jvm_memory_total_max Generated from dropwizard metric import (metric=jvm_memory_total_max, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$3)\n" +
                        "# TYPE jvm_memory_total_max gauge\n" +
                        "jvm_memory_total_max 2.8631367679E10\n" +
                        "# HELP jvm_memory_total_used Generated from dropwizard metric import (metric=jvm_memory_total_used, type=com.codahale.metrics.jvm.MemoryUsageGaugeSet$2)\n" +
                        "# TYPE jvm_memory_total_used gauge\n" +
                        "jvm_memory_total_used 3.38635736E8\n" +
                        "# HELP jvm_threads_blocked_count Generated from dropwizard metric import (metric=jvm_threads_blocked_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$1)\n" +
                        "# TYPE jvm_threads_blocked_count gauge\n" +
                        "jvm_threads_blocked_count 30.0\n" +
                        "# HELP jvm_threads_count Generated from dropwizard metric import (metric=jvm_threads_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$2)\n" +
                        "# TYPE jvm_threads_count gauge\n" +
                        "jvm_threads_count 128.0\n" +
                        "# HELP jvm_threads_daemon_count Generated from dropwizard metric import (metric=jvm_threads_daemon_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$3)\n" +
                        "# TYPE jvm_threads_daemon_count gauge\n" +
                        "jvm_threads_daemon_count 14.0\n" +
                        "# HELP jvm_threads_new_count Generated from dropwizard metric import (metric=jvm_threads_new_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$1)\n" +
                        "# TYPE jvm_threads_new_count gauge\n" +
                        "jvm_threads_new_count 0.0\n" +
                        "# HELP jvm_threads_runnable_count Generated from dropwizard metric import (metric=jvm_threads_runnable_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$1)\n" +
                        "# TYPE jvm_threads_runnable_count gauge\n" +
                        "jvm_threads_runnable_count 71.0\n" +
                        "# HELP jvm_threads_terminated_count Generated from dropwizard metric import (metric=jvm_threads_terminated_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$1)\n" +
                        "# TYPE jvm_threads_terminated_count gauge\n" +
                        "jvm_threads_terminated_count 0.0\n" +
                        "# HELP jvm_threads_timed_waiting_count Generated from dropwizard metric import (metric=jvm_threads_timed_waiting_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$1)\n" +
                        "# TYPE jvm_threads_timed_waiting_count gauge\n" +
                        "jvm_threads_timed_waiting_count 14.0\n" +
                        "# HELP jvm_threads_waiting_count Generated from dropwizard metric import (metric=jvm_threads_waiting_count, type=com.codahale.metrics.jvm.ThreadStatesGaugeSet$1)\n" +
                        "# TYPE jvm_threads_waiting_count gauge\n" +
                        "jvm_threads_waiting_count 13.0\n" +
                        "# HELP org_eclipse_jetty_util_thread_QueuedThreadPool_dw_jobs Generated from dropwizard metric import (metric=org_eclipse_jetty_util_thread_QueuedThreadPool_dw_jobs, type=com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool$3)\n" +
                        "# TYPE org_eclipse_jetty_util_thread_QueuedThreadPool_dw_jobs gauge\n" +
                        "org_eclipse_jetty_util_thread_QueuedThreadPool_dw_jobs 0.0\n" +
                        "# HELP org_eclipse_jetty_util_thread_QueuedThreadPool_dw_size Generated from dropwizard metric import (metric=org_eclipse_jetty_util_thread_QueuedThreadPool_dw_size, type=com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool$2)\n" +
                        "# TYPE org_eclipse_jetty_util_thread_QueuedThreadPool_dw_size gauge\n" +
                        "org_eclipse_jetty_util_thread_QueuedThreadPool_dw_size 49.0\n" +
                        "# HELP org_eclipse_jetty_util_thread_QueuedThreadPool_dw_utilization Generated from dropwizard metric import (metric=org_eclipse_jetty_util_thread_QueuedThreadPool_dw_utilization, type=com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool$1)\n" +
                        "# TYPE org_eclipse_jetty_util_thread_QueuedThreadPool_dw_utilization gauge\n" +
                        "org_eclipse_jetty_util_thread_QueuedThreadPool_dw_utilization 0.9795918367346939\n" +
                        "# HELP io_dropwizard_jetty_MutableServletContextHandler_active_dispatches Generated from dropwizard metric import (metric=io_dropwizard_jetty_MutableServletContextHandler_active_dispatches, type=com.codahale.metrics.Counter)\n" +
                        "# TYPE io_dropwizard_jetty_MutableServletContextHandler_active_dispatches gauge\n" +
                        "io_dropwizard_jetty_MutableServletContextHandler_active_dispatches 0.0\n" +
                        "# HELP io_dropwizard_jetty_MutableServletContextHandler_active_requests Generated from dropwizard metric import (metric=io_dropwizard_jetty_MutableServletContextHandler_active_requests, type=com.codahale.metrics.Counter)\n" +
                        "# TYPE io_dropwizard_jetty_MutableServletContextHandler_active_requests gauge\n" +
                        "io_dropwizard_jetty_MutableServletContextHandler_active_requests 0.0\n" +
                        "# HELP io_dropwizard_jetty_MutableServletContextHandler_active_suspended Generated from dropwizard metric import (metric=io_dropwizard_jetty_MutableServletContextHandler_active_suspended, type=com.codahale.metrics.Counter)\n" +
                        "# TYPE io_dropwizard_jetty_MutableServletContextHandler_active_suspended gauge\n" +
                        "io_dropwizard_jetty_MutableServletContextHandler_active_suspended 0.0\n" +
                        "# HELP com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies Generated from dropwizard metric import (metric=com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies, type=com.codahale.metrics.Timer)\n" +
                        "# TYPE com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies summary\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies{quantile=\"0.5\",} 0.0\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies{quantile=\"0.75\",} 0.0\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies{quantile=\"0.95\",} 0.0\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies{quantile=\"0.98\",} 0.0\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies{quantile=\"0.99\",} 0.0\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies{quantile=\"0.999\",} 0.0\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies_count 0.0\n" +
                        "com_securityx_modelfeature_resources_AbnormalBehaviorFeature_getTimeSeriesBehaviorAnomalies_sum 0.0\n";
                httpExchange.sendResponseHeaders(Response.Status.OK.getStatusCode(), response.getBytes(StandardCharsets.UTF_8).length);
                //Write the response string
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            }}));

    }
    public void start(){
        httpServer.start();
    }
    public void stop(){
        httpServer.stop(0);
    }
}
