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
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author msauer
 */
public class PgDebugger {

    private static final String SERVER = "server";
    private static final String PORT = "serverport";
    private static final String LOCALPORT = "localport";

    private final String serverName;
    private final int serverPort;
    private final int localPort;

    private final PgMessageCallback callback;

    public PgDebugger(String serverName, int serverPort, int localPort, PgMessageCallback callback) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.localPort = localPort;
        this.callback = callback;
    }

    public static void main(String[] args) {
        PgDebugger pgDebugger = createPgDebuggerBasedOn(args);
        pgDebugger.start();
    }

    private static PgDebugger createPgDebuggerBasedOn(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<String> server = parser.accepts(SERVER).withRequiredArg().required().ofType(String.class);
        OptionSpec<Integer> serverPort = parser.accepts(PORT).withRequiredArg().required().ofType(Integer.class);
        OptionSpec<Integer> localPort = parser.accepts(LOCALPORT).withRequiredArg().required().ofType(Integer.class);

        OptionSet options = parser.parse(args);

        return new PgDebugger(server.value(options), serverPort.value(options), localPort.value(options), new DumpingMessageCallback());
    }

    public void start() {
        ServerSocket server = createLocalServerSocket();

        System.out.println(String.format("waiting for incoming connections on port '%s' which will be forward to '%s:%s' ... ", this.localPort, this.serverName, this.serverPort));

        try {
            Socket incoming = server.accept();
            incoming.setTcpNoDelay(true);

            System.out.println("established connection. starting to capture and forward messages ...");
            System.out.println();

            proxy(incoming);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void proxy(Socket incoming) throws IOException {
        Socket outgoing = new Socket(this.serverName, this.serverPort);
        outgoing.setTcpNoDelay(true);

        ProxyThread frontendToBackend = new ProxyThread("frontend->backend", incoming, outgoing, Sender.FRONTEND, callback);
        frontendToBackend.start();

        ProxyThread backendToFrontend = new ProxyThread("backend->frontend", outgoing, incoming, Sender.BACKEND, callback);
        backendToFrontend.start();
    }

    private ServerSocket createLocalServerSocket() {
        ServerSocket server;
        try {
            server = new ServerSocket(this.localPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return server;
    }

    private static class DumpingMessageCallback implements PgMessageCallback {

        @Override
        public void receivedMessage(PgMessage message) {
            System.out.println(message);
        }
    }
}
