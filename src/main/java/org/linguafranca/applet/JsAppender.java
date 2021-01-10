/*
 * Copyright (c) 2011 - 2021 Jo Rabin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.linguafranca.applet;


import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import ch.qos.logback.core.status.ErrorStatus;
import netscape.javascript.JSObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * By now (2021) this no longer works with up to date versions of logback.
 *
 * @param <E>
 */
public class JsAppender<E> extends AppenderBase<ILoggingEvent> {

    ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
    public static JSObject window = null;
    public static String loggername = null;
    
    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        if (encoder != null){
            encoder.stop();
        }
        this.encoder = encoder;
        try {
            encoder.init(baos);
        } catch (IOException e) {
            addStatus(new ErrorStatus("Initialising Encoder", this, e));
        }
        encoder.start();
    }

    public PatternLayoutEncoder encoder; 

    public JsAppender (){

    }
    @Override
    protected synchronized void append(ILoggingEvent event) {
        if (encoder == null){
            addStatus(new ErrorStatus("Encode is null", this));
            return;
        }
        try {
            encoder.doEncode(event);
        } catch (IOException e1) {
            addStatus(new ErrorStatus("Encoding failed ",this,e1));
            return;
        }
        if (window == null || loggername ==null) {
            addStatus(new ErrorStatus("JsAppender is not configured, null output destination",this));
            stop();
            return;
        }

        window.call(loggername, new Object[]{baos.toString()});
        baos.reset();
    }
}
