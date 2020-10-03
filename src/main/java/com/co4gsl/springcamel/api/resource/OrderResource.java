package com.co4gsl.springcamel.api.resource;

import com.co4gsl.springcamel.api.dto.Order;
import com.co4gsl.springcamel.api.service.OrderService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;

@Component
public class OrderResource extends RouteBuilder {

    @Autowired
    private OrderService orderService;

    @Override
    public void configure() throws Exception {
        // error channel handlers
        onException(ValidationException.class)
                .log("${exception.message}")
                .setHeader("SERVER_ERROR", constant(500))
                .setHeader("ERROR_TYPE", constant("Error processing this order"))
                .handled(true)
                .setBody(simple("{\"message\": \"Order processing failed\"}"));

        from("direct:getOrders")
                .to("direct:getAllOrders")
                .endRest();

        from("direct:orderService")
                .routeId("direct-route")
                .tracing()
                .log(">>> Validating orderService input >>> ${body}")
                .to("direct:validateOrder")
                .log(">>> Validation ok at orderService")
                .to("direct:unmarshalString")
                .log(">>> Calling orderService for creation")
                .to("direct:createOrder")
                .endRest();

        from("direct:validateOrder")
                .to("json-validator:order-service-schema.json");

        from("direct:createOrder")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        HashMap<String, Object> body = (HashMap<String, Object>) exchange.getIn().getBody();
                        Order order = new Order(0, (String)body.get("name"), (Integer)body.get("qty"));
                        exchange.getIn().setBody(Arrays.asList(orderService.addOrder(order)));
                    }
                })
                .marshal().json(JsonLibrary.Jackson);

        from("direct:getAllOrders")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(orderService.getOrders());
                    }
                })
                .marshal().json(JsonLibrary.Jackson);
    }
}
