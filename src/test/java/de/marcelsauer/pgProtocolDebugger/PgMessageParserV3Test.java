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
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author msauer
 */
public class PgMessageParserV3Test {

    @Test
    public void thatNextMessageCanBeParsed() throws IOException {
        // given
        InputStream in = getClass().getResourceAsStream("/backend-frontend.bin");
        PgMessageParserV3 parser = new PgMessageParserV3(in, Sender.BACKEND);

        // when
        int count = 0;
        PgMessage nextMessage;
        while ((nextMessage = parser.nextMessage()) != null) {

            count++;
            
            // then
            assertNotNull(nextMessage);
        }
        
        assertEquals(14, count);

    }
}
