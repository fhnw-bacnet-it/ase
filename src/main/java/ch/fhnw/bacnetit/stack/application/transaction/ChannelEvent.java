package ch.fhnw.bacnetit.stack.application.transaction;

public enum ChannelEvent {
    CLOSE_CHANNEL_EVENT, CLOSE_CHANNEL_EVENT_ONLY_ON_UNCONFIRMED_REQUEST, REMOVE_CONNECTION_EVENT;

    private Object msg = null;

    public void setMsg(final Object msg) {
        if (msg != null) {
            this.msg = msg;
        }
    }

    public Object getMsg() {
        return msg;
    }

}
