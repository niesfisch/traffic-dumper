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
