package ch.fhnw.bacnetit.ase.encoding;

public class ControlMessageReceivedEvent {
    private final ControlMessage controlMessage;

    public ControlMessageReceivedEvent(final ControlMessage cm) {
        this.controlMessage = cm;
    }

    public ControlMessage getControlMessage() {
        return this.controlMessage;
    }

}
