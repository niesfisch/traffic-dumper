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

/**
 * @author msauer
 */
class PgMessage {

    private final int type;
    private final byte[] payload;
    private Sender sender;

    public PgMessage(int type, byte[] payload, Sender sender) {
        this.type = type;
        this.payload = payload;
        this.sender = sender;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (byte aPayload : this.payload) {
            char c = (char) aPayload;
            String label = encode(c);
            buf.append(label);
        }
        return String.format("[%s][%s][%s][%s bytes]", this.sender.getShortname(), encode(this.type), buf, this.payload.length - Constants.DEFAULT_LENGTH);
    }

    private String encode(int c) {
        if (c == -1) {
            return "<-1>";
        }

        if (c == 0) {
            return "<0>";
        }

        if (Character.isDefined((char) c)) {
            return String.valueOf((char) c);
        } else {
            return "<" + Integer.toHexString(c) + ">";
        }
    }

}
