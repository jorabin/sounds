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

package org.linguafranca.sound.cadence;

import org.linguafranca.sound.tone.Tone;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Parse the Sipura/Linksys/Cisco ToneScript syntax which looks something like the below.
 *
 * The parsing is carried out "more or less" as follows. Does not enforce e.g. fewer than 7 repetitions
 * nor maximum string length of 127, etc.
 *     
    <pre><code>
    // An optional list of freqScript (up to 6 in total), followed by a cadenceScript
    &lt;toneScript>   ::= [{&lt;freqScript>','}*5&lt;freqScript>';']&lt;cadenceScript>

    // a comma separated list of tones  (up to 6)
    &lt;freqScript>     ::= &lt;tone>{','&lt;tone>}*5

    // a tone is the combination of a frequency and a volume
    &lt;tone>           ::= &lt;frequency>'@'&lt;dBm>

    // a frequency in Hertz  (must be an integer)
    &lt;frequency>      ::= &lt;integer>

    // a loudness value -35 is very soft, 0 is the loudest distinguishable, one decimal place is allowed
    &lt;dBm>            ::= &lt;float>


    // a script is one or two sections
    &lt;cadenceScript>  ::= &lt;cadenceSection>[';'&lt;cadenceSection>]

    // a section is a duration followed by a comma separated list up to 6 segments
    &lt;cadenceSection> ::= &lt;duration>'('&lt;cadenceSegment>'{,&lt;cadenceSegment>}*5)'

    // the duration to make the sound for - with a resolution of 1 ms, specified in secs
    // the segments are repeated, in order, till the duration of the section has elapsed 
    &lt;duration>       ::= &lt;float>


    // if &lt;freqScript> is present - a &lt;toneCadence> must be used.
    // if &lt;freqScript> is not present then the third element of a &lt;toneCadence> if present is ignored.
    // not clear if it should be ignored or if that's an error
    &lt;cadenceSegment>        ::= &lt;toneCadence> | &lt;nonToneCadence> 

    // meaning sound-on/sound-off in secs ('*' for the on-value means for the &lt;duration> of the cadence)
    &lt;nonToneCadence> ::= (&lt;duration> | '*') '/' &lt;float> 

    // meaning sound-on/sound-off/tones-to-play
    &lt;toneCadence>    ::= &lt;nonToneCadence>'/'&lt;toneList>

    // a plus separated list of indexes into the list of &lt;tones>
    // nothing is done about duplicates - not clear if that is an error
    &lt;toneList>       ::= &lt;toneIndex>{'+'&lt;toneIndex>]}*5

    // referencing the earlier &lt;tones> 1 is the first
    &lt;toneIndex>      ::= &lt;integer>
 </code></pre


 */
public class ToneScriptParser {

    /** the tones to use for cadences that don't have any */
    public static Tone.List RINGER_TONES = Cadences.REGULAR_PHONE_RING;

    // hide constructor, all methods static
    private ToneScriptParser() {throw new IllegalStateException("This class only has static methods");}

    public static CadenceScript parseToneScript(String scriptText) {
        return parseToneScript(scriptText, scriptText);
    }

    public static CadenceScript parseToneScript(String description, String scriptText) {

        try {
            Tone.List tones = new Tone.List();

            int partIndex = 0;
            String[] parts = scriptText.trim().split(";");

            // check if there is a FreqScript and parse it if so
            if (parts.length > 1 && parts[0].contains("@")) {
                tones = parseFreqScript(parts[0]);
                partIndex += 1;
            }

            /* strictly there can only be a max of 3 parts */
            checkArgument((parts.length - partIndex > 0) && (parts.length - partIndex <= 2),
                    "Tone Script must have at most 1 FreqScript and 2 CadenceScripts");

            CadenceScript script = new CadenceScript(description);
            for (int i = partIndex; i < parts.length; i++) {
                script.addCadence(parseCadenceSection(parts[i], tones));
            }

            script.setRinger(checkToneStatus(tones, script));
            if (script.isRinger()) {
                script.setTones(RINGER_TONES);
            } else {
                script.setTones(tones);
            }
            return script;
        } catch (Exception e) {
            throw new IllegalArgumentException(String .format("ToneScript \"%s\" was not valid because \"%s\"", scriptText, e.getMessage()), e);
        }
    }

    /**
     * Check for consistency of the script in respect of the presence or absence of
     * a FreqSpec and whether or not the Cadence Segments make refernce to it or not
     * @param tones the parsed FreqSpec
     * @param script the constructed script
     * @return true if all worked out and this is a ringer, false if it is a tone
     */
    private static boolean checkToneStatus(Tone.List tones, CadenceScript script) {
        // segments that have a tone and a non-zero onDuration or that have no tone but a 0 on duration
        // reflecting patterns like 0/4/0 meaning be silent for 4 secs
        int numToneSegments = 0;
        int numNonToneSegments = 0;

        for (CadenceSection section : script.getCadences()) {
            for (CadenceSegment segment : section) {
                if (segment.getTones().size() == 0 && segment.getOnDuration() > 0) {
                    numNonToneSegments += 1;
                    segment.setTones(RINGER_TONES);
                } else {
                    numToneSegments += 1;
                }
            }
        }

        if (numNonToneSegments != 0 && numToneSegments != 0) {
            throw new IllegalStateException("Have both Tone Segments and Non Tone Segments");
        }
        if (numToneSegments > 0 && tones.size() == 0) {
            throw new IllegalStateException(("Have Tone Segment but no FreqList"));
        }
        if (numNonToneSegments > 0 && tones.size() > 0) {
            throw new IllegalStateException(("Have FreqList but no segment uses it"));
        }
        return numNonToneSegments > 0;
    }

    /**
     * Parse a FreqScript - i.e. something of the form freq@dBels,freq@dBels etc.
     * @param input a string assumed to contain a FreqScript
     * @return a {@link Tone.List}
     */
    public static Tone.List parseFreqScript(String input) {
        final Tone.List toneList = new Tone.List();
        String [] parts = input.split(",", 7);
        for (String part : parts) {
            String[] components = part.split("@");
            checkArgument((components.length ==2),
                    "Tones are of the form 'freq@dB'");
            Tone tone = Tone.makeToneFD(Double.parseDouble(components[0].trim()), Double.parseDouble(components[1].trim()));
            toneList.add(tone);
        }
        return toneList;
    }


    /**
     * Parse a CadenceSection - i.e. something of the form dur(onTime/offTime[/freqs], onTime/offTime[/freqs[ ...)
     * @param input a string assumed to be a CadenceScript
     * @param tones a list of tones collected earlier in parsing that may be referred to in a cadence
     * @return a CadenceSection
     */
    public static CadenceSection parseCadenceSection(String input, Tone.List tones) {
        
        // this actually allows parentheses in the wrong order, do we care?
        String[] parts = input.trim().split("([()])",4);
        
        checkArgument(parts.length == 3, "Invalid Cadence Spec \"%s\"", input);
        checkArgument(!parts[1].trim().isEmpty(), "Invalid Cadence Spec no cadence found \"%s\"", input);
        checkArgument(parts[2].trim().isEmpty(), "Invalid Cadence Spec (extraneous chars) \"%s\"", input);
        double duration = Double.parseDouble(parts[0].trim());
        int millisecs = (int) Math.round(duration * 1000);

        final CadenceSection section = new CadenceSection(input, millisecs);

        String[] segments = parts[1].trim().split(",");
        checkArgument(segments.length >= 1, "Must have at least one segment \"%s\"", input);
        for (String segment : segments) {
            section.add(parseCadenceSegment(segment, tones));
        }
        return section;
    }
    
    /**
     * Parse a CadenceSegment - i.e. something of the form onTime/offTime[/freqs]
     * @param segmentString a string assumed to be a CadenceSegment
     * @param tones a list of tones collected earlier in parsing that may be referred to
     * @return a CadenceSegment
     */
    public static CadenceSegment parseCadenceSegment(String segmentString, Tone.List tones) {
 
        String[] component = segmentString.trim().split("/");
        checkArgument(component.length ==2 || component.length == 3,
                "Segment must have 2 or 3 components \"%s\"", segmentString);
        checkArgument(component.length ==2 && tones.size() == 0 || component.length == 3 && tones.size() > 0,
                "Segment specifies frequencies but none given \"%s\"", segmentString);

        int onDuration;
        int offDuration;
        try {
            onDuration = component[0].trim().equals("*") ? -1 :
                    (int) Math.round(Double.parseDouble(component[0]) * 1000);
            offDuration = (int) Math.round(Double.parseDouble(component[1]) * 1000);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid duration specified %s for segment %s", e.getMessage(), segmentString), e);
        }

        if (component.length < 3) {
            return new CadenceSegment(onDuration, offDuration);
        }

        String[] toneIndex = component[2].split("\\+");
        Tone.List componentTones = new Tone.List();
        for (String index : toneIndex) {
            int ti = Integer.parseInt(index.trim()) - 1;
            checkArgument(ti < tones.size(),"Frequency index is out of range %s", segmentString);
            checkArgument(!(ti == -1 && onDuration != 0),"Cannot specify 0 frequency index with non-zero on-duration %s", segmentString);
            if (ti >= 0) {
                componentTones.add(tones.get(ti));
            }
        }
        return new CadenceSegment(componentTones, onDuration, offDuration);
    }
}
