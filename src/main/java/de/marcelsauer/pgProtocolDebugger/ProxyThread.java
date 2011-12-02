package de.marcelsauer.pgProtocolDebugger;

import de.marcelsauer.pgProtocolDebugger.PgMessageParserV3.Sender;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author msauer
 */
class ProxyThread extends Thread {

    private final Socket incoming, outgoing;
    private final Sender sender;

    ProxyThread(String name, Socket in, Socket out, Sender sender) {
        this.incoming = in;
        this.outgoing = out;
        this.sender = sender;
        this.setName(name);
    }

    @Override
    public void run() {
        OutputStream out = null;
        InputStream in = null;
        try {
            out = this.outgoing.getOutputStream();
            in = this.incoming.getInputStream();

            PgMessageParserV3 parser = new PgMessageParserV3(in, this.sender);
            PgMessage nextMessage;
            while ((nextMessage = parser.nextMessage()) != null) {
                System.out.println(nextMessage);
                nextMessage.writeTo(out);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        } finally {
            closeSafely(out);
            closeSafely(in);
        }
    }

    private void closeSafely(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
