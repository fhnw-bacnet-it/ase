package ch.fhnw.bacnetit.ase.encoding;

import java.util.LinkedList;
import java.util.List;

public class ControlMessage extends ITBBase {

    private static final long serialVersionUID = -4832973466431041450L;
    public static final byte ADDREMOTE = (byte) 1;
    public static final byte REMOVEREMOTE = (byte) 2;

    private byte messageType;
    private List<UnsignedInteger31> bacnetEidList = null;
    private List<UnsignedInteger31> deviceGroupIdList = null;

    /*
     * ControlMessageType: Should by be (byte)1 oder (byte)2, for ADDREMOTE or
     * REMOVEREMOTE
     *
     * bacnetEids a list with BACnetEIDs or null
     *
     * deviceGroupIds a list with DeviceGroupIDs or null
     */

    public ControlMessage(final byte controlMessageType,
            final List<UnsignedInteger31> bacnetEids,
            final List<UnsignedInteger31> deviceGroupIds) {
        this.messageType = controlMessageType;
        if (bacnetEids != null) {
            this.bacnetEidList = bacnetEids;
        }
        if (deviceGroupIds != null) {
            this.deviceGroupIdList = deviceGroupIds;
        }
    }

    public ControlMessage() {
    };

    public ControlMessage(final _ByteQueue queue) throws Exception {
        queue.pop();
        this.messageType = queue.pop();
        this.bacnetEidList = new LinkedList<UnsignedInteger31>();
        this.deviceGroupIdList = new LinkedList<UnsignedInteger31>();

        while (queue.size() > 0 && queue.peek(0) >> 4 == (byte) 1) {
            this.bacnetEidList.add(read(queue, UnsignedInteger31.class, 1));
        }

        while (queue.size() > 0 && queue.peek(0) >> 4 == (byte) 2) {
            this.deviceGroupIdList.add(read(queue, UnsignedInteger31.class, 2));
        }
        if (this.bacnetEidList.size() == 0) {
            this.bacnetEidList = null;
        }
        if (this.deviceGroupIdList.size() == 0) {
            this.deviceGroupIdList = null;
        }
    }

    // Getter
    public List<UnsignedInteger31> getBacnetEidList() {
        return this.bacnetEidList;
    }

    public List<UnsignedInteger31> getDeviceGroupIdList() {
        return this.deviceGroupIdList;
    }

    @Override
    public void write(final _ByteQueue queue) {
        // write(queue,this.messageType,0);
        queue.push(this.messageType);
        if (this.bacnetEidList != null) {
            for (final UnsignedInteger31 bacneteid : this.bacnetEidList) {
                write(queue, bacneteid, 1);
            }
        }
        if (this.deviceGroupIdList != null) {
            for (final UnsignedInteger31 deviceGroupId : this.deviceGroupIdList) {
                write(queue, deviceGroupId, 2);
            }
        }

    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("MessageType: " + ((this.messageType == (byte) 1)
                ? "ADDREMOTE" : "REMOVEREMOTE"));
        buf.append(System.getProperty("line.separator"));
        buf.append("List of BacnetEIDs: ");
        if (this.bacnetEidList != null) {
            for (final UnsignedInteger31 bacnet : this.bacnetEidList) {
                buf.append(bacnet + " | ");
            }
        }
        buf.append(System.getProperty("line.separator"));
        buf.append("List of GroupIDs: ");
        if (this.deviceGroupIdList != null) {
            for (final UnsignedInteger31 groupid : this.deviceGroupIdList) {
                buf.append(groupid + " | ");
            }
        }
        return buf.toString();

    }

    // testing purposes
    public static void main_(final String[] args) throws Exception {

        final List<UnsignedInteger31> baclist = new LinkedList<UnsignedInteger31>();
        baclist.add(new UnsignedInteger31(4711));
        baclist.add(new UnsignedInteger31(471123));
        baclist.add(new UnsignedInteger31(12));
        baclist.add(new UnsignedInteger31(13));
        baclist.add(new UnsignedInteger31(14));

        final List<UnsignedInteger31> grouplist = new LinkedList<UnsignedInteger31>();
        grouplist.add(new UnsignedInteger31(31195));
        grouplist.add(new UnsignedInteger31(41195));
        grouplist.add(new UnsignedInteger31(51195));
        grouplist.add(new UnsignedInteger31(61195));

        final _ByteQueue bq = new _ByteQueue();
        final ControlMessage cm = new ControlMessage(ControlMessage.ADDREMOTE,
                baclist, grouplist);
        cm.write(bq);

        final ControlMessage cm2 = new ControlMessage(bq);

    }

}
