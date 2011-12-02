package de.marcelsauer.pgProtocolDebugger;

import java.io.IOException;
import java.io.InputStream;

/**
 * version 3.0 see http://www.postgresql.org/docs/devel/static/protocol.html
 *
 * @author msauer
 */
public class PgMessageParserV3 {

    public enum Sender {
        FRONTEND,
        BACKEND;
    }

    private final InputStream in;
    private final Sender sender;

    private final byte[] int4buf = new byte[4];
    private boolean isFirstMessage = true;
    int type = -1;
    int length = -1;
    byte[] payload;

    public PgMessageParserV3(InputStream in, Sender sender) {
        this.in = in;
        this.sender = sender;
    }

    public PgMessage nextMessage() throws IOException {
        reset();

        if (this.sender == Sender.FRONTEND && this.isFirstMessage) {
            this.isFirstMessage = false;
            this.type = -1;
            length();
            payload();
        } else {
            if (type() == -1)
                return null;
            length();
            payload();
        }
        return new PgMessage(this.type, this.payload);

    }

    private void reset() {
        this.type = 0;
        this.length = -1;
        this.payload = new byte[0];
    }

    private int type() throws IOException {
        int next = this.in.read();
        this.type = (char) next;
        return next;
    }

    private void payload() throws IOException {
        this.payload = new byte[this.length];
        int count = 0;
        while ((count = this.in.read(this.payload, count, this.length - count)) < this.length) ;
    }

    private void length() throws IOException {
        this.length = readInt4() - 4;
    }

    public int readInt4() throws IOException {
        this.in.read(this.int4buf);
        return (this.int4buf[0] & 0xFF) << 24 | (this.int4buf[1] & 0xFF) << 16 | (this.int4buf[2] & 0xFF) << 8 | this.int4buf[3] & 0xFF;
    }
}
