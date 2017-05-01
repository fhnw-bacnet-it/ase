package ch.fhnw.bacnetit.ase.application.transaction;

import ch.fhnw.bacnetit.ase.encoding.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.T_UnitDataRequest;

public interface ApplicationService {

    public void doRequest(T_UnitDataRequest t_unitDataRequest);

    public void doCancel(BACnetEID destination, BACnetEID source);
}
