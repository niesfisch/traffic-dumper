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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author msauer
 */
public class PgMessage {

    private final int type;
    private final byte[] payload;

    public PgMessage(int type, byte[] payload) {
        this.type = type;
        this.payload = payload;
    }

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.payload.length; ++i) {
            char c = (char) this.payload[i];
            String label = encode(c);
            buf.append(label);
        }

        return String.format("[%s][%s]", encode(this.type), buf);
    }

    public void writeTo(OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        if (this.type != -1) {
            dataOut.writeByte(this.type);
            dataOut.writeInt(this.payload.length + 4);
            dataOut.write(this.payload);
        }
    }

    private String encode(int c) {
        if (c == -1)
            return "<-1>";
        if (Character.isLetterOrDigit((char) c))
            return String.valueOf((char) c);
        else
            return "<" + Integer.toHexString(c) + ">";
    }

}
