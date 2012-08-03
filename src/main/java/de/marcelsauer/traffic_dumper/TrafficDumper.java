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
package de.marcelsauer.traffic_dumper;

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
public class TrafficDumper {

    private static final String SERVER = "targetserver";
    private static final String PORT = "targetport";
    private static final String LOCALPORT = "localport";

    private final String targetserver;
    private final int targetport;
    private final int localPort;

    public TrafficDumper(String targetserver, int targetport, int localPort) {
        this.targetserver = targetserver;
        this.targetport = targetport;
        this.localPort = localPort;
    }

    public static void main(String[] args) {
        TrafficDumper trafficDumper = createTrafficDumperBasedOn(args);
        trafficDumper.start();
    }

    private static TrafficDumper createTrafficDumperBasedOn(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<String> server = parser.accepts(SERVER).withRequiredArg().required().ofType(String.class);
        OptionSpec<Integer> targetport = parser.accepts(PORT).withRequiredArg().required().ofType(Integer.class);
        OptionSpec<Integer> localPort = parser.accepts(LOCALPORT).withRequiredArg().required().ofType(Integer.class);

        OptionSet options = parser.parse(args);

        return new TrafficDumper(server.value(options), targetport.value(options), localPort.value(options));
    }

    public void start() {
        ServerSocket server = createLocalServerSocket();

        System.out.println(String.format("waiting for incoming connections on port '%s' which will be forward to '%s:%s' ... ", this.localPort, this.targetserver, this.targetport));

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
        Socket outgoing = new Socket(this.targetserver, this.targetport);
        outgoing.setTcpNoDelay(true);

        ProxyThread frontendToBackend = new ProxyThread("client->server", incoming, outgoing, Sender.CLIENT);
        frontendToBackend.start();

        ProxyThread backendToFrontend = new ProxyThread("server->client", outgoing, incoming, Sender.SERVER);
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
}
