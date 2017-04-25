package ch.fhnw.bacnetit.ase.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class _ByteQueue implements Cloneable {

    private byte[] queue;

    private int head;

    private int tail;

    private int size;

    private int markHead;

    private int markTail;

    private int markSize;

    public _ByteQueue() {
        this(1024);
    }

    public _ByteQueue(final int initialLength) {
        head = -1;
        tail = 0;
        size = 0;
        queue = new byte[initialLength];
    }

    public _ByteQueue(final byte b[]) {
        this(b.length);
        push(b, 0, b.length);
    }

    public _ByteQueue(final byte b[], final int pos, final int length) {
        this(length);
        push(b, pos, length);
    }

    public void push(final byte b) {
        if (room() == 0) {
            expand();
        }
        queue[tail] = b;
        if (head == -1) {
            head = 0;
        }
        tail = (tail + 1) % queue.length;
        size++;
    }

    public void push(final int i) {
        push((byte) i);
    }

    public void push(final long l) {
        push((byte) (int) l);
    }

    public void pushU2B(final int i) {
        push((byte) (i >> 8));
        push((byte) i);
    }

    public void pushU3B(final int i) {
        push((byte) (i >> 16));
        push((byte) (i >> 8));
        push((byte) i);
    }

    public void pushS4B(final int i) {
        pushInt(i);
    }

    public void pushU4B(final long l) {
        push((byte) (int) (l >> 24));
        push((byte) (int) (l >> 16));
        push((byte) (int) (l >> 8));
        push((byte) (int) l);
    }

    public void pushChar(final char c) {
        push((byte) (c >> 8));
        push((byte) c);
    }

    public void pushDouble(final double d) {
        pushLong(Double.doubleToLongBits(d));
    }

    public void pushFloat(final float f) {
        pushInt(Float.floatToIntBits(f));
    }

    public void pushInt(final int i) {
        push((byte) (i >> 24));
        push((byte) (i >> 16));
        push((byte) (i >> 8));
        push((byte) i);
    }

    public void pushLong(final long l) {
        push((byte) (int) (l >> 56));
        push((byte) (int) (l >> 48));
        push((byte) (int) (l >> 40));
        push((byte) (int) (l >> 32));
        push((byte) (int) (l >> 24));
        push((byte) (int) (l >> 16));
        push((byte) (int) (l >> 8));
        push((byte) (int) l);
    }

    public void pushShort(final short s) {
        push((byte) (s >> 8));
        push((byte) s);
    }

    public void read(final InputStream in, final int length)
            throws IOException {
        if (length == 0) {
            return;
        }
        for (; room() < length; expand()) {
            ;
        }
        final int tailLength = queue.length - tail;
        if (tailLength > length) {
            readImpl(in, tail, length);
        } else {
            readImpl(in, tail, tailLength);
        }
        if (length > tailLength) {
            readImpl(in, 0, length - tailLength);
        }
        if (head == -1) {
            head = 0;
        }
        tail = (tail + length) % queue.length;
        size += length;
    }

    private void readImpl(final InputStream in, int offset, int length)
            throws IOException {
        int readcount;
        for (; length > 0; length -= readcount) {
            readcount = in.read(queue, offset, length);
            offset += readcount;
        }
    }

    public void push(final byte b[]) {
        push(b, 0, b.length);
    }

    public void push(final byte b[], final int pos, final int length) {
        if (length == 0) {
            return;
        }
        for (; room() < length; expand()) {
            ;
        }
        final int tailLength = queue.length - tail;
        if (tailLength > length) {
            System.arraycopy(b, pos, queue, tail, length);
        } else {
            System.arraycopy(b, pos, queue, tail, tailLength);
        }
        if (length > tailLength) {
            System.arraycopy(b, tailLength + pos, queue, 0,
                    length - tailLength);
        }
        if (head == -1) {
            head = 0;
        }
        tail = (tail + length) % queue.length;
        size += length;
    }

    public void push(_ByteQueue source) {
        if (source.size == 0) {
            return;
        }
        if (source == this) {
            source = (_ByteQueue) clone();
        }
        int firstCopyLen = source.queue.length - source.head;
        if (source.size < firstCopyLen) {
            firstCopyLen = source.size;
        }
        push(source.queue, source.head, firstCopyLen);
        if (firstCopyLen < source.size) {
            push(source.queue, 0, source.tail);
        }
    }

    public void mark() {
        markHead = head;
        markTail = tail;
        markSize = size;
    }

    public void reset() {
        head = markHead;
        tail = markTail;
        size = markSize;
    }

    public byte pop() {
        final byte retval = queue[head];
        if (size == 1) {
            head = -1;
            tail = 0;
        } else {
            head = (head + 1) % queue.length;
        }
        size--;
        return retval;
    }

    public int popU1B() {
        return pop() & 0xff;
    }

    public int popU2B() {
        return (pop() & 0xff) << 8 | pop() & 0xff;
    }

    public int popU3B() {
        return (pop() & 0xff) << 16 | (pop() & 0xff) << 8 | pop() & 0xff;
    }

    public int popS4B() {
        return (pop() & 0xff) << 24 | (pop() & 0xff) << 16 | (pop() & 0xff) << 8
                | pop() & 0xff;
    }

    public long popU4B() {
        return (long) (pop() & 0xff) << 24 | (long) (pop() & 0xff) << 16
                | (long) (pop() & 0xff) << 8 | pop() & 0xff;
    }

    public int pop(final byte buf[]) {
        return pop(buf, 0, buf.length);
    }

    public int pop(final byte buf[], final int pos, int length) {
        length = peek(buf, pos, length);
        size -= length;
        if (size == 0) {
            head = -1;
            tail = 0;
        } else {
            head = (head + length) % queue.length;
        }
        return length;
    }

    public int pop(int length) {
        if (length == 0) {
            return 0;
        }
        if (size == 0) {
            throw new ArrayIndexOutOfBoundsException(-1);
        }
        if (length > size) {
            length = size;
        }
        size -= length;
        if (size == 0) {
            head = -1;
            tail = 0;
        } else {
            head = (head + length) % queue.length;
        }
        return length;
    }

    public String popString(final int length, final Charset charset) {
        final byte b[] = new byte[length];
        pop(b);
        return new String(b, charset);
    }

    public byte[] popAll() {
        final byte data[] = new byte[size];
        pop(data, 0, data.length);
        return data;
    }

    public void write(final OutputStream out) throws IOException {
        write(out, size);
    }

    public void write(final OutputStream out, int length) throws IOException {
        if (length == 0) {
            return;
        }
        if (size == 0) {
            throw new ArrayIndexOutOfBoundsException(-1);
        }
        if (length > size) {
            length = size;
        }
        int firstCopyLen = queue.length - head;
        if (length < firstCopyLen) {
            firstCopyLen = length;
        }
        out.write(queue, head, firstCopyLen);
        if (firstCopyLen < length) {
            out.write(queue, 0, length - firstCopyLen);
        }
        size -= length;
        if (size == 0) {
            head = -1;
            tail = 0;
        } else {
            head = (head + length) % queue.length;
        }
    }

    public byte tailPop() {
        if (size == 0) {
            throw new ArrayIndexOutOfBoundsException(-1);
        }
        tail = ((tail + queue.length) - 1) % queue.length;
        final byte retval = queue[tail];
        if (size == 1) {
            head = -1;
            tail = 0;
        }
        size--;
        return retval;
    }

    public byte peek(int index) {
        if (index >= size) {
            throw new IllegalArgumentException((new StringBuilder())
                    .append("index ").append(index).append(" is >= queue size ")
                    .append(size).toString());
        } else {
            index = (index + head) % queue.length;
            return queue[index];
        }
    }

    public byte[] peek(final int index, final int length) {
        final byte result[] = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = peek(index + i);
        }

        return result;
    }

    public byte[] peekAll() {
        final byte data[] = new byte[size];
        peek(data, 0, data.length);
        return data;
    }

    public int peek(final byte buf[]) {
        return peek(buf, 0, buf.length);
    }

    public int peek(final byte buf[], final int pos, int length) {
        if (length == 0) {
            return 0;
        }
        if (size == 0) {
            throw new ArrayIndexOutOfBoundsException(-1);
        }
        if (length > size) {
            length = size;
        }
        int firstCopyLen = queue.length - head;
        if (length < firstCopyLen) {
            firstCopyLen = length;
        }
        System.arraycopy(queue, head, buf, pos, firstCopyLen);
        if (firstCopyLen < length) {
            System.arraycopy(queue, 0, buf, pos + firstCopyLen,
                    length - firstCopyLen);
        }
        return length;
    }

    public int indexOf(final byte b) {
        return indexOf(b, 0);
    }

    public int indexOf(final byte b, final int start) {
        if (start >= size) {
            return -1;
        }
        int index = (head + start) % queue.length;
        for (int i = start; i < size; i++) {
            if (queue[index] == b) {
                return i;
            }
            index = (index + 1) % queue.length;
        }
        return -1;
    }

    public int indexOf(final byte b[]) {
        return indexOf(b, 0);
    }

    public int indexOf(final byte b[], int start) {
        if (b == null || b.length == 0) {
            throw new IllegalArgumentException(
                    "cannot search for empty values");
        }
        for (; (start = indexOf(b[0], start)) != -1
                && start < (size - b.length) + 1; start++) {
            boolean found = true;
            int i = 1;
            do {
                if (i >= b.length) {
                    break;
                }
                if (peek(start + i) != b[i]) {
                    found = false;
                    break;
                }
                i++;
            } while (true);
            if (found) {
                return start;
            }
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
        head = -1;
        tail = 0;
    }

    private int room() {
        return queue.length - size;
    }

    private void expand() {
        final byte newb[] = new byte[queue.length * 2];
        if (head == -1) {
            queue = newb;
            return;
        }
        if (tail > head) {
            System.arraycopy(queue, head, newb, head, tail - head);
            queue = newb;
            return;
        } else {
            System.arraycopy(queue, head, newb, head + queue.length,
                    queue.length - head);
            System.arraycopy(queue, 0, newb, 0, tail);
            head += queue.length;
            queue = newb;
            return;
        }
    }

    @Override
    public Object clone() {
        try {
            final _ByteQueue clone = (_ByteQueue) super.clone();
            clone.queue = queue.clone();
            return clone;
        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(Integer.toHexString(peek(0) & 0xff));
        for (int i = 1; i < size; i++) {
            sb.append(',').append(Integer.toHexString(peek(i) & 0xff));
        }

        sb.append("]");
        return sb.toString();
    }

    public String dumpQueue() {
        final StringBuffer sb = new StringBuffer();
        if (queue.length == 0) {
            sb.append("[]");
        } else {
            sb.append('[');
            sb.append(queue[0]);
            for (int i = 1; i < queue.length; i++) {
                sb.append(", ");
                sb.append(queue[i]);
            }
            sb.append("]");
        }
        sb.append(", h=").append(head).append(", t=").append(tail)
                .append(", s=").append(size);
        return sb.toString();
    }
}
