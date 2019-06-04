package it.jsql.connector.controller;

import it.jsql.connector.dto.JSQLConfig;
import it.jsql.connector.dto.JSQLResponse;
import it.jsql.connector.exceptions.JSQLException;
import it.jsql.connector.service.JSQLConnector;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;

/**
 * Created by Dawid on 2016-09-13.
 * Modified by Michael on 2018-09-10.
 */
@Path("/api/jsql")
public abstract class JSQLController {

    public abstract JSQLConfig getConfig();

    private static final String API_URL = "https://provider.jsql.it/api/jsql";

    public String getProviderUrl() {
        return API_URL;
    }

    public static final String TRANSACTION_ID = "txid";

    @Path("/select")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List select(Map<String, Object> data,
                       @Context HttpServletResponse response,
                       @DefaultValue("") @HeaderParam(TRANSACTION_ID) String transactionId) throws JSQLException {

        JSQLResponse jsqlResponse = JSQLConnector.callSelect(transactionId, this.getProviderUrl(), data, this.getConfig());

        if (jsqlResponse.transactionId != null) {
            response.setHeader(TRANSACTION_ID, jsqlResponse.transactionId);
        }

        return jsqlResponse.response;

    }

    @Path("/delete")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List delete(Map<String, Object> data,
                       @Context HttpServletResponse response,
                       @DefaultValue("") @HeaderParam(TRANSACTION_ID) String transactionId) throws JSQLException {

        JSQLResponse jsqlResponse = JSQLConnector.callDelete(transactionId, this.getProviderUrl(), data, this.getConfig());

        if (jsqlResponse.transactionId != null) {
            response.setHeader(TRANSACTION_ID, jsqlResponse.transactionId);
        }

        return jsqlResponse.response;

    }

    @Path("/update")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List update(Map<String, Object> data,
                       @Context HttpServletResponse response,
                       @DefaultValue("") @HeaderParam(TRANSACTION_ID) String transactionId) throws JSQLException {

        JSQLResponse jsqlResponse = JSQLConnector.callUpdate(transactionId, this.getProviderUrl(), data, this.getConfig());

        if (jsqlResponse.transactionId != null) {
            response.setHeader(TRANSACTION_ID, jsqlResponse.transactionId);
        }

        return jsqlResponse.response;

    }

    @Path("/insert")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List insert(Map<String, Object> data,
                       @Context HttpServletResponse response,
                       @DefaultValue("") @HeaderParam(TRANSACTION_ID) String transactionId) throws JSQLException {

        JSQLResponse jsqlResponse = JSQLConnector.callInsert(transactionId, this.getProviderUrl(), data, this.getConfig());

        if (jsqlResponse.transactionId != null) {
            response.setHeader(TRANSACTION_ID, jsqlResponse.transactionId);
        }

        return jsqlResponse.response;

    }

    @Path("/rollback")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List rollback(@Context HttpServletResponse response,
                         @DefaultValue("") @HeaderParam(TRANSACTION_ID) String transactionId) throws JSQLException {


        JSQLResponse jsqlResponse = JSQLConnector.callRollback(this.getProviderUrl(), transactionId, this.getConfig());
        return jsqlResponse.response;

    }

    @Path("/commit")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List commit(@Context HttpServletResponse response,
                         @DefaultValue("") @HeaderParam(TRANSACTION_ID) String transactionId) throws JSQLException {


        JSQLResponse jsqlResponse = JSQLConnector.callCommit(this.getProviderUrl(), transactionId, this.getConfig());
        return jsqlResponse.response;

    }

}
