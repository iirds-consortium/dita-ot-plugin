/*******************************************************************************
 * Copyright 2024 Gesellschaft für Technische Kommunikation – tekom Deutschland e.V., https://iirds.org 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.iirds.dita.ot.plugin.test;

import org.dita.dost.log.DITAOTLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Logger for unit tests. It wraps a normal logger into a {@link DITAOTLogger}
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 * @see TestLogger#get(Logger)
 */

public class TestLogger implements DITAOTLogger {
	private Logger inner;

	public static DITAOTLogger get(Logger logger) {
		return new TestLogger(logger);
	}

	private TestLogger(Logger logger) {
		inner = logger;
	}

	@Override
	public String getName() {
		return inner.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return inner.isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		inner.trace(msg);
		;
	}

	@Override
	public void trace(String format, Object arg) {
		inner.trace(format, arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		inner.trace(format, arg1, arg2);
	}

	@Override
	public void trace(String format, Object... arguments) {
		inner.trace(format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		inner.trace(msg, t);

	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return inner.isTraceEnabled();
	}

	@Override
	public void trace(Marker marker, String msg) {
		inner.trace(marker, msg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		inner.trace(marker, format, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		inner.trace(marker, format, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		inner.trace(marker, format, argArray);

	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		inner.trace(marker, msg, t);

	}

	@Override
	public boolean isDebugEnabled() {
		return inner.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		inner.debug(msg);

	}

	@Override
	public void debug(String format, Object arg) {
		inner.debug(format, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		inner.debug(format, arg1, arg2);
	}

	@Override
	public void debug(String format, Object... arguments) {
		inner.debug(format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		inner.debug(msg, t);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return inner.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String msg) {
		inner.debug(marker, msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		inner.debug(marker, format, arg);

	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		inner.debug(marker, format, arg1, arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		inner.debug(marker, format, arguments);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		inner.debug(marker, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return inner.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		inner.info(msg);
	}

	@Override
	public void info(String format, Object arg) {
		inner.info(format, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		inner.info(format, arg1, arg2);
	}

	@Override
	public void info(String format, Object... arguments) {
		inner.info(format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		inner.info(msg, t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return inner.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String msg) {
		inner.info(marker, msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		inner.info(marker, format, arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		inner.info(marker, format, arg1, arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		inner.info(marker, format, arguments);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		inner.info(marker, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return inner.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		inner.warn(msg);
	}

	@Override
	public void warn(String format, Object arg) {
		inner.warn(format, arg);

	}

	@Override
	public void warn(String format, Object... arguments) {
		inner.warn(format, arguments);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		inner.warn(format, arg1, arg2);
	}

	@Override
	public void warn(String msg, Throwable t) {
		inner.warn(msg, t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return inner.isWarnEnabled();
	}

	@Override
	public void warn(Marker marker, String msg) {
		inner.warn(marker, msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		inner.warn(marker, format, arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		inner.warn(marker, format, arg1, arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		inner.warn(marker, format, arguments);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		inner.warn(marker, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return inner.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		inner.error(msg);
	}

	@Override
	public void error(String format, Object arg) {
		inner.error(format, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		inner.error(format, arg1, arg2);
	}

	@Override
	public void error(String format, Object... arguments) {
		inner.error(format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		inner.error(msg, t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return inner.isErrorEnabled();
	}

	@Override
	public void error(Marker marker, String msg) {
		inner.error(marker, msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		inner.error(marker, format, arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		inner.error(marker, format, arg1, arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		inner.error(format, arguments);
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		inner.error(marker, msg, t);
	}

}
