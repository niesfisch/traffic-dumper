package de.marcelsauer.pgProtocolDebugger;

import de.marcelsauer.pgProtocolDebugger.PgMessageParserV3.Sender;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class PgMessageParserV3Test {

    @Test
    public void thatNextMessageCanBeParsed() throws IOException {
        // given
        InputStream in = getClass().getResourceAsStream("/backend-frontend.bin");
        PgMessageParserV3 parser = new PgMessageParserV3(in, Sender.BACKEND);

        // when
        PgMessage nextMessage = null;
        while ((nextMessage = parser.nextMessage()) != null) {
            System.out.println(nextMessage);
        }

        // then
    }
}
