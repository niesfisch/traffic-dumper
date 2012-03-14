/**
 * Copyright (C) 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private final PgMessageCallback pgMessageCallback;

    ProxyThread(String name, Socket in, Socket out, Sender sender, PgMessageCallback pgMessageCallback) {
        this.incoming = in;
        this.outgoing = out;
        this.sender = sender;
        this.pgMessageCallback = pgMessageCallback;
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
                this.pgMessageCallback.receivedMessage(nextMessage);
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
