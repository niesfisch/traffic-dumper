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

import java.io.IOException;
import java.io.InputStream;

/**
 * version 3.0 see http://www.postgresql.org/docs/devel/static/protocol.html
 *
 * holds the internal parsing state, not thread safe
 *
 * @author msauer
 */
class PgMessageParserV3 {

    public enum Sender {
        FRONTEND("F"),
        BACKEND("B");

        private final String shortname;

        private Sender(String shortname) {
            this.shortname = shortname;
        }

        public String getShortname() {
            return shortname;
        }
    }

    private final InputStream in;
    private final Sender sender;

    private final byte[] int4buf = new byte[4];
    private boolean isFirstMessage = true;
    int type = -1;
    int length = -1;
    byte[] payload;

    PgMessageParserV3(InputStream in, Sender sender) {
        this.in = in;
        this.sender = sender;
    }

    PgMessage nextMessage() throws IOException {
        resetState();

        if (isClientInit()) {
            this.isFirstMessage = false;
            this.type = -1;
            readLength();
            readPayload();
        } else {
            if (type() == -1) {
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
        this.type = 0;
        this.length = -1;
        this.payload = new byte[0];
    }

    private int type() throws IOException {
        int next = this.in.read();
        this.type = (char) next;
        return next;
    }

    private void readPayload() throws IOException {
        this.payload = new byte[this.length];
        int count = 0;
        while ((count = this.in.read(this.payload, count, this.length - count)) < this.length) ;
    }

    private void readLength() throws IOException {
        this.length = readInt4() - 4;
    }

    public int readInt4() throws IOException {
        this.in.read(this.int4buf);
        return (this.int4buf[0] & 0xFF) << 24 | (this.int4buf[1] & 0xFF) << 16 | (this.int4buf[2] & 0xFF) << 8 | this.int4buf[3] & 0xFF;
    }
}
