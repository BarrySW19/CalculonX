/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2013 Barry Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package barrysw19.calculon.site.icc;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class ResponseBlockLv2 {
    protected String data;
    protected DgCommand command;

    public static ResponseBlockLv2 createResponseBlock(String s) {
        return new ResponseBlockLv2(s);
    }

    private ResponseBlockLv2(String s) {
        this.data = s;
        StringTokenizer st = new StringTokenizer(s);
        this.command = DgCommand.valueOf(Integer.parseInt(st.nextToken()));
    }

    public String[] tokenize() {
        boolean inBrackets = false;
        List<String> output = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if(c == ('Y'&0x1f) && data.length() >= i && data.charAt(i+1) == '{') {
                inBrackets = true;
                i++;
                continue;
            }
            if(c == '{') {
                inBrackets = true;
                continue;
            }
            if(c == ('Y'&0x1f) && data.length() >= i && data.charAt(i+1) == '}') {
                inBrackets = false;
                i++;
                continue;
            }
            if(c == '}') {
                inBrackets = false;
                continue;
            }
            if(c == ' ' && ! inBrackets) {
                output.add(builder.toString());
                builder.setLength(0);
                continue;
            }
            builder.append(c);
        }
        if(builder.length() > 0) {
            output.add(builder.toString());
        }
        return output.toArray(new String[output.size()]);
    }

    public DgCommand getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder("");
        buf.append("ResponseBlock(command=").append(command).append(" = ").append(data);
        return buf.toString();
    }
}
