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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author msauer
 */
public class Server {

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(5432);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Socket incoming = server.accept();
                incoming.setTcpNoDelay(true);

                Socket outgoing = new Socket("omega", 5432);
                outgoing.setTcpNoDelay(true);

                ProxyThread frontendToBackend = new ProxyThread("frontend-backend", incoming, outgoing, Sender.FRONTEND);
                frontendToBackend.start();

                ProxyThread backendToFrontend = new ProxyThread("backend-frontend", outgoing, incoming, Sender.BACKEND);
                backendToFrontend.start();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
