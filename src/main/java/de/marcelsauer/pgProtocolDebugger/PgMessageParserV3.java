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

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * version 3.0 see http://www.postgresql.org/docs/devel/static/protocol.html
 * <p/>
 * holds the internal parsing state, not thread safe
 *
 * @author msauer
 */
class PgMessageParserV3 {

    private final Sender sender;

    // state
    private final byte[] int4buf = new byte[4];
    private final ByteArrayInputStream byteStream;

    // per message
    private boolean isFirstMessage = true;
    private int type = -1;
    private int length = -1;
    private byte[] payload;

    PgMessageParserV3(byte[] bytes, Sender sender) {
        this.sender = sender;
        this.byteStream = new ByteArrayInputStream(bytes);
    }

    PgMessage nextMessage() throws IOException {
        resetState();

        if (isClientInit()) {
            this.isFirstMessage = false;
            this.type = -1;
            readLength();
            readPayload();
        } else {
            if (readType() == -1) {
                return null;
            }
            readLength();
            readPayload();
        }
        return new PgMessage(this.type, this.payload, this.sender);
    }

    private boolean isClientInit() {
        return this.sender == Sender.FRONTEND && this.isFirstMessage;
    }

    private void resetState() {
        this.type = -1;
        this.length = -1;
        this.payload = new byte[0];
    }

    private int readType() throws IOException {
        int next = this.byteStream.read();
        this.type = next;
        return next;
    }

    private void readPayload() throws IOException {
        this.payload = new byte[this.length];
        this.byteStream.read(this.payload);
    }

    private void readLength() throws IOException {
        this.length = readInt4() - Constants.DEFAULT_LENGTH;
        if (this.length < 0) {
            this.length = 0;
        }
    }


    public int readInt4() throws IOException {
        this.byteStream.read(this.int4buf);
        return (this.int4buf[0] & 0xFF) << 24 | (this.int4buf[1] & 0xFF) << 16 | (this.int4buf[2] & 0xFF) << 8 | this.int4buf[3] & 0xFF;
    }
}
