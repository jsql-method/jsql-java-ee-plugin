package it.jsql.connector.controller;


import it.jsql.connector.dto.TransactionThread;
import it.jsql.connector.service.IJSQLService;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;

/**
 * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
 * ||                                                        ||
 * || entityManager.unwrap(SessionImpl.class).connection();  ||
 * ||                                                        ||
 * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
 * <p>
 * Created by Dawid on 2016-09-13.
 * Modified by Michael on 2018-09-10.
 */
@Path("/jsql")
public abstract class JSQLController {

    public JSQLController() {
    }

    public abstract IJSQLService getJsqlService();

    private IJSQLService ijsqlService = null;

    private IJSQLService getJsqlServiceSingleton() {

        if (this.ijsqlService == null) {
            this.ijsqlService = this.getJsqlService();
        }

        return this.ijsqlService;

    }

    @Path("/select")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List<Map<String, Object>> select(Map<String, Object> data,
                                            @Context HttpServletResponse response,
                                            @DefaultValue("false") @HeaderParam("TX") Boolean isTransactional,
                                            @HeaderParam("TXID") String txid) {

        TransactionThread transactionThread = new TransactionThread(data, isTransactional, txid);

        this.getJsqlServiceSingleton().select(transactionThread);

        if (isTransactional) {

            response.addHeader("TXID", transactionThread.getTransactionId());

        }

        return transactionThread.getResponse();
    }

    @Path("/delete")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List<Map<String, Object>> delete(Map<String, Object> data,
                                            @Context HttpServletResponse response,
                                            @HeaderParam("TX") Boolean isTransactional,
                                            @HeaderParam("TXID") String txid) {
        TransactionThread transactionThread = new TransactionThread(data, isTransactional, txid);

        this.getJsqlServiceSingleton().delete(transactionThread);

        if (isTransactional) {

            response.addHeader("TXID", transactionThread.getTransactionId());

        }

        return transactionThread.getResponse();
    }

    @Path("/update")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List<Map<String, Object>> update(Map<String, Object> data,
                                            @Context HttpServletResponse response,
                                            @HeaderParam("TX") Boolean isTransactional,
                                            @HeaderParam("TXID") String txid) {
        TransactionThread transactionThread = new TransactionThread(data, isTransactional, txid);

        this.getJsqlServiceSingleton().update(transactionThread);

        if (isTransactional) {

            response.addHeader("TXID", transactionThread.getTransactionId());

        }

        return transactionThread.getResponse();
    }

    @Path("/insert")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public List<Map<String, Object>> insert(Map<String, Object> data,
                                            @Context HttpServletResponse response,
                                            @HeaderParam("TX") Boolean isTransactional,
                                            @HeaderParam("TXID") String txid) {
        TransactionThread transactionThread = new TransactionThread(data, isTransactional, txid);

        this.getJsqlServiceSingleton().insert(transactionThread);

        if (isTransactional) {

            response.addHeader("TXID", transactionThread.getTransactionId());

        }

        return transactionThread.getResponse();
    }


}
