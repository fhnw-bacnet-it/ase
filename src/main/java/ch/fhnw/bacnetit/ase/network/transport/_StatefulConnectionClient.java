package ch.fhnw.bacnetit.ase.network.transport;

/**
 * Marker interface only to signal a stateful connection. A stateful connection
 * can be handled differently to stateless connection, elgl using cahcing.
 *
 * @author juerg.luthiger@fhnw.ch
 */
public interface _StatefulConnectionClient extends ConnectionClient {

}