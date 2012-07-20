package de.marcelsauer.pgProtocolDebugger;

import java.io.IOException;

/**
 * @author msauer
 */
class DumperThread implements Runnable {

    private final byte[] bytes;
    private final Sender sender;
    private final PgMessageCallback pgMessageCallback;

    public DumperThread(byte[] bytes, Sender sender, PgMessageCallback pgMessageCallback) {
        this.bytes = bytes;
        this.sender = sender;
        this.pgMessageCallback = pgMessageCallback;
    }

    @Override
    public void run() {
        dumpRaw();
        dumpMessage();
    }

    private void dumpMessage() {
        PgMessageParserV3 parser = new PgMessageParserV3(this.bytes, this.sender);
        PgMessage nextMessage;
        try {
            while ((nextMessage = parser.nextMessage()) != null) {
                this.pgMessageCallback.receivedMessage(nextMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void dumpRaw() {
        StringBuilder hexBuf = new StringBuilder();
        StringBuilder strBuf = new StringBuilder();

        for (byte aByte : this.bytes) {
            String s = Integer.toHexString(aByte);
            if (aByte == 0) {
                hexBuf.append("<0>");
            } else {
                hexBuf.append("<" + s + ">");

            }
            if (Character.isDefined((char) aByte)) {
                strBuf.append(String.valueOf((char) aByte));
            }
        }
        System.out.println(String.format("sender [%s], bytes [%s]", this.sender, hexBuf.toString()));
        System.out.println(String.format("sender [%s], data  [%s]", this.sender, strBuf.toString()));
    }

}
