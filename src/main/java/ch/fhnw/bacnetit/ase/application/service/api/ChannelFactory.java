package ch.fhnw.bacnetit.ase.application.service.api;

public class ChannelFactory {

    public static ch.fhnw.bacnetit.ase.application.service.api.ASEServices getInstance() {
        try{
            return new ch.fhnw.bacnetit.ase.application.service.ASEChannel(new ChannelFactory());
        }
        catch(Exception e){
            System.err.println(e);
            return null;
        }
    }

}
