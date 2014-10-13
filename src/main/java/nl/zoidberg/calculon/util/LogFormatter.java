/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2009 Barry Smith
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
package nl.zoidberg.calculon.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
	@Override
	public synchronized String format(LogRecord record) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(dateFormat.format(new Date(record.getMillis())));
		buf.append(" ").append(record.getLevel());
		String className = record.getSourceClassName();
		className = className.substring(className.lastIndexOf('.')+1);
		buf.append(" ").append(record.getSourceClassName()).append(":").append(record.getSourceMethodName());
		buf.append(" - ").append(record.getMessage());
		buf.append('\n');
		
		if(record.getThrown() != null) {
			buf.append(record.getThrown().getMessage()).append('\n');
			for(StackTraceElement e: record.getThrown().getStackTrace()) {
				buf.append("    ").append(e.toString()).append('\n');
			}
		}
		
		return buf.toString();
	}

}
