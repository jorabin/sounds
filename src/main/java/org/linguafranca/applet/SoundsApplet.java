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

import netscape.javascript.JSObject;
import org.linguafranca.sound.DemoSounds;

import java.applet.Applet;
import java.awt.*;

public class SoundsApplet extends Applet {
    public JSObject window = null;
    public String loggername = null;
    DemoSounds demoSounds = new DemoSounds();

    public SoundsApplet () {

    }

    @Override
    public void paint(Graphics g){
        g.drawRect(0, 0,
                getWidth() - 1,
                getHeight() - 1);

        //Draw the current string inside the rectangle.
        g.drawString("Sound System is Running" , 5, 15);

    }
    @Override
    public void init() {
        demoSounds.logger.info("Applet is initialising");
        loggername = this.getParameter("logger");
        if (loggername != null) {
            // note that this causes a problem in IntelliJ as it loads the wrong netscape.javascript
            // need to set project up with an altered JDK 1.7 system libraries
            // with references to JFX removed
            // see POM for including the right library for Maven
            window = JSObject.getWindow(this);
            // window.call(loggername,new Object [] {"The logger is working"});
        }

        JsAppender.window = window;
        JsAppender.loggername = loggername;

        demoSounds.openSound();
        demoSounds.playSIT();
        demoSounds.logger.info(System.getProperty("java.class.path"));
    }
    public void playSIT(){
        demoSounds.playSIT();
    }
    public void play(String script){
        demoSounds.play(script);
    }
    @Override
    public void stop(){
        demoSounds.close();
    }
}
