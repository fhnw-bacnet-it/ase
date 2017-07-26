/**
 * 
 */
package ch.fhnw.bacnetit.ase.application.service;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;
import ch.fhnw.bacnetit.ase.application.service.api.ASEServices;
import ch.fhnw.bacnetit.ase.application.service.api.ChannelConfiguration;
import ch.fhnw.bacnetit.ase.application.service.api.ChannelFactory;
import ch.fhnw.bacnetit.ase.application.service.api.TransportBindingService;
import ch.fhnw.bacnetit.ase.application.transaction.api.ChannelListener;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;

/**
 * @author IMVS, FHNW
 *
 */
public class TestASEChannel {
    
    @Test
    public void createInstanceWithoutFactory(){
        boolean worked;
        try{
            ASEChannel aseChannel = new ASEChannel(this);
            worked=true;
        }catch(Exception e){
            System.err.println(e);
            worked=false;
        }
        Assert.assertEquals(false,worked);         
    }
    @Test
    public void createInstanceWithoutFactory2(){
        boolean worked;
        try{
            ASEChannel aseChannel = new ASEChannel(new Object());
            worked=true;
        }catch(Exception e){
            System.err.println(e);
            worked=false;
        }
        Assert.assertEquals(false,worked);         
    }
    
    @Test
    public void createInstanceWithFactory(){
        boolean worked;
        try{
            ASEServices aseServices = ChannelFactory.getInstance();
            worked=true;
        }catch(Exception e){
            System.err.println(e);
            worked=false;
        }
        Assert.assertEquals(true,worked);
                
    }
    
    // Testing ChannelConfiguration
    @Test
    public void ChannelConfiguration1(){
        ChannelConfiguration cc = ChannelFactory.getInstance();
        Assert.assertEquals(0,((TransportBindingService)cc).getChannelListeners().size());
        
        class CL extends ChannelListener{

            public CL(BACnetEID listenerEid) {
                super(listenerEid);
                // TODO Auto-generated constructor stub
            }
            @Override
            public void onIndication(T_UnitDataIndication tUnitDataIndication,
                    Object context) {
                // TODO Auto-generated method stub
                
            }
            @Override
            public void onError(String cause) {
                // TODO Auto-generated method stub
                
            }
            
        }
        cc.registerChannelListener(new CL(new BACnetEID(1000)));
        Assert.assertEquals(1,((TransportBindingService)cc).getChannelListeners().size());
        cc.registerChannelListener(new CL(new BACnetEID(1001)));
        Assert.assertEquals(2,((TransportBindingService)cc).getChannelListeners().size());
        
        Assert.assertEquals(((TransportBindingService)cc).getChannelListeners().get(0).intValue(),1000);
        Assert.assertEquals(((TransportBindingService)cc).getChannelListeners().get(1).intValue(),1001);
        
        
        
    }
    
    

   

}
