package ch.fhnw.bacnetit.ase.application;

import ch.fhnw.bacnetit.ase.application.service.ASEChannel;
import ch.fhnw.bacnetit.ase.application.transaction.Transaction;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.exception.TransportErrorException;


public class ExceptionManager {

    public void manageException(final Throwable cause, final BACnetEID source,
            final Transaction transaction, final ASEChannel channel) {
        // Exception infos
        System.err.println("--------------------------------------");
        System.err.println("--------------------------------------");
        System.err.println("EXCEPTION HANDLING:");

        /*
         * if (transaction != null) {
         * System.err.println("Does remove transaction "+transaction.toString())
         * ; channel.getTransactionManager().removeTransaction(transaction); }
         */
        System.err.println("Message:" + cause.getMessage());
        System.err.println("Stacktrace:");
        cause.printStackTrace(System.err);

        if (cause instanceof TransportErrorException) {
            System.err.println(
                    "-> ->Exception contains a BACnet Transport Error");
            System.err.println("-> -> ->" + ((TransportErrorException) cause)
                    .getTransportError().toString());
        } else {
            System.err.println("Exception contains no BACnet Transport Error");
        }
        System.err.println("--------------------------------------");
        System.err.println("--------------------------------------");
        // Exception handling
        // if (cause instanceof java.io.IOException) {
        // channel.initializeAndStart();
        // }

    }

}
