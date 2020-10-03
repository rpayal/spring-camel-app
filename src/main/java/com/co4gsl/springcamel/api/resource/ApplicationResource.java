package com.co4gsl.springcamel.api.resource;

import com.co4gsl.springcamel.api.exception.OrderProcessingException;
import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ApplicationResource extends RouteBuilder {

    @Value("${server.port}")
    private String port;

    @Value("${api.path}")
    String contextPath;

    /*
    Added this as was having issue with Camel-context creation during app startup
     */
    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(new CamelHttpTransportServlet(), contextPath + "/*");
        servlet.setName("CamelServlet");
        return servlet;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .port(port)
                .host("localhost")
                .contextPath(contextPath)
                .bindingMode(RestBindingMode.json);

        // error channel handlers
        onException(ValidationException.class)
                .log("${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .handled(true)
                .setBody(simple("{\"message\": \"Not a valid order\"}"));


        rest().get("/hello").produces(MediaType.APPLICATION_JSON_VALUE).route()
                .setBody(constant("Welcome to Spring-Camel Integration")).endRest();

        rest().get("/orders")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .route()
                .to("direct:getOrders")
                .log("received response from getOrders")
                .to("direct:routeResponse")
                .endRest();

        rest()
                .post("/order")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .route()
                .log(">>> Validating request create order >>> ${body}")
                .to("direct:validateOrderCreate")
                .log(">>> validation ok")
                .log(">>> Transforming to backend order dto schema")
                .to("direct:transformToOrderDto")
                .log(">>> Sending to orderService processing")
                .to("direct:orderService")
                .log(">>> Received response from orderService ${body} <<<")
                .to("direct:routeResponse")
                .endRest();

        from("direct:validateOrderCreate")
                .marshal().json(JsonLibrary.Jackson, true)
                .to("json-validator:request-order-create-schema.json");

        from("direct:transformToOrderDto")
                .log("transforming request >>> ${body}")
                .to("jolt:request-to-order-dto-jolt.json?inputType=JsonString&outputType=JsonString")
                .log("transformed to <<< ${body}");

        from("direct:unmarshalString")
                .unmarshal()
                .json(JsonLibrary.Jackson, true);

        from("direct:routeResponse")
                .log(">>> Routing based on http response code: ${headers}")
                .choice()
                .when(header("SERVER_ERROR").isEqualTo(500))
                .throwException(new OrderProcessingException("Failure from order service."))
                .otherwise()
                .to("direct:transformResponse");

        from("direct:transformResponse")
                .log(">>> Transforming >>> ${body}")
                .to("jolt:order-service-to-order-dto-jolt.json?inputType=JsonString&outputType=JsonString")
                .log(">>> Transformed response <<< ${body}")
                .to("direct:unmarshalString")
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE));
    }
}
