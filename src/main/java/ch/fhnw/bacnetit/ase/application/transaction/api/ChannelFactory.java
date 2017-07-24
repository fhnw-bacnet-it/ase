package ch.fhnw.bacnetit.ase.application.transaction.api;

public class ChannelFactory {

    public static ch.fhnw.bacnetit.ase.application.transaction.api.ASEServices getInstance() {
        return new ch.fhnw.bacnetit.ase.application.service.ASEChannel();
    }

}
