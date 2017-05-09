package ch.fhnw.bacnetit.ase.application.transaction.api;

public class ChannelFactory {
   
    public static ch.fhnw.bacnetit.ase.application.transaction.api.Channel getInstance(){
       return new ch.fhnw.bacnetit.ase.application.transaction.ASEChannel();
   }
   
}
