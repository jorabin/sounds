**CadScript**—A mini-script that specifies the cadence parameters of a signal.

Up to 127 characters. 

Syntax: 

<pre>S<sub>1</sub>[;S<sub>2</sub>]</pre> 

where: 

<pre>S<sub>i</sub> = D<sub>i</sub>(on<sub>i,1</sub>/off<sub>i,1</sub>[,on<sub>i,2</sub>/off<sub>i,2</sub>[,on<sub>i,3</sub>/off<sub>i,3</sub>[,on<sub>i,4</sub>/off<sub>i,4</sub>[,on<sub>i,5</sub>/off<sub>i,5</sub>[,on<sub>i,6</sub>/off<sub>i,6</sub>]]]]])</pre>
and is known as a **section**, on<sub>i,j</sub> and off<sub>i,j</sub> are the on/off duration in seconds of a **segment** and i =
1 or 2, and j = 1 to 6. D<sub>i</sub> is the total duration of the section in seconds. 

All durations can have up to three decimal places to provide 1 ms resolution.
The wildcard character “*” stands for infinite duration. The segments within
a section are played in order and repeated until the total duration is played.

Example 1:

    60(2/4)
    
    Number of Cadence Sections = 1
    Cadence Section 1: Section Length = 60 s
    Number of Segments = 1
    Segment 1: On=2s, Off=4s
    Total Ring Length = 60s

Example 2—Distinctive ring (short,short,short,long):

    60(.2/.2,.2/.2,.2/.2,1/4)
    
    Number of Cadence Sections = 1
    Cadence Section 1: Section Length = 60s
    Number of Segments = 4
    Segment 1: On=0.2s, Off=0.2s
    Segment 2: On=0.2s, Off=0.2s
    Segment 3: On=0.2s, Off=0.2s
    Segment 4: On=1.0s, Off=4.0s
    Total Ring Length = 60s


**FreqScript**—A mini-script that specifics the frequency and level
parameters of a tone.

Up to 127 characters. 

Syntax: 

<pre>F<sub>1</sub>@L<sub>1</sub>[,F<sub>2</sub>@L<sub>2</sub>[,F<sub>3</sub>@L<sub>3</sub>[,F<sub>4</sub>@L<sub>4</sub>[,F<sub>5</sub>@L<sub>5</sub>[,F<sub>6</sub>@L<sub>6</sub>]]]]]</pre> 

where F<sub>1</sub>–F<sub>6</sub> are frequency in
Hz (unsigned integers only) and L<sub>1</sub>–L<sub>6</sub> are corresponding levels in dBm (with
up to 1 decimal places). White spaces before and after the comma are
allowed (but not recommended).

Example 1—Call Waiting Tone:

    440@-10
    
    Number of Frequencies = 1
    Frequency 2 = 440 Hz at –10 dBm
    Creating Provisioning Scripts for Configuration Profile


Example 2—Dial Tone:

    350@-19,440@-19
    
    Number of Frequencies = 2
    Frequency 1 = 350 Hz at –19 dBm
    Frequency 2 = 440 Hz at –19 dBm


**ToneScript**—A mini-script that specifies the frequency, level and cadence
parameters of a call progress tone. 

May contain up to 127 characters.

Syntax: 

<pre>FreqScript;Z<sub>1</sub>[;Z<sub>2</sub>]</pre> 

The section Z<sub>1</sub> is similar to the S<sub>1</sub> section in a
CadScript except that each on/off segment is followed by a frequency
components parameter: 
<pre>Z<sub>1</sub> = D<sub>1</sub>(on<sub>i,1</sub>/off<sub>i,1</sub>/f<sub>i,1</sub>[,on<sub>i,2</sub>/off<sub>i,2</sub>/f<sub>i,2</sub>[,on<sub>i,3</sub>/off<sub>i,3</sub>/f<sub>i,3</sub>[,on<sub>i,4</sub>/off<sub>i,4</sub>/f<sub>i,4</sub>[,on<sub>i,5</sub>/off<sub>i,5</sub>/f<sub>i,5</sub> [,on<sub>i,6</sub>/off<sub>i,6</sub>/f<sub>i,6</sub>]]]]])
</pre>

where 

<pre>f<sub>i,j</sub> = n<sub>1</sub>[+n<sub>2</sub>[+n<sub>3</sub>[+n<sub>4</sub>[+n<sub>5</sub>[+n<sub>6</sub>]]]]]</pre> 

and 1 < n<sub>k</sub> < 6 indicates which of the frequency
components given in the FreqScript are used in that segment; if more than
one frequency component is used in a segment, the components are
summed together.

Example 1—Dial tone:

    350@-19,440@-19;10(*/0/1+2)
    
    Number of Frequencies = 2
    Frequency 1 = 350 Hz at –19 dBm
    Frequency 2 = 440 Hz at –19 dBm
    
    Number of Cadence Sections = 1
    
    Cadence Section 1: Section Length = 10 s
    Number of Segments = 1
    
    Segment 1: On=forever, with Frequencies 1 and 2
    Total Tone Length = 10s

Example 2—Stutter tone:

    350@-19,440@-19;2(.1/.1/1+2);10(*/0/1+2)
    
    Number of Frequencies = 2
    Frequency 1 = 350 Hz at –19 dBm
    Frequency 2 = 440 Hz at –19 dBm
    
    Number of Cadence Sections = 2
    
    Cadence Section 1: Section Length = 2s
    
    Number of Segments = 1
    Segment 1: On=0.1s, Off=0.1s with Frequencies 1 and 2
    
    Cadence Section 2: Section Length = 10s
    
    Number of Segments = 1
    Segment 1: On=forever, with Frequencies 1 and 2
    Total Tone Length = 12s

Example 3—SIT tone:

    985@-16,1428@-16,1777@-16;20(.380/0/1,.380/0/2,.380/0/3,0/4/0)
    
    Number of Frequencies = 3
    Frequency 1 = 985 Hz at –16 dBm
    Frequency 2 = 1428 Hz at –16 dBm
    Frequency 3 = 1777 Hz at –16 dBm
    
    Number of Cadence Sections = 1
    
    Cadence Section 1: Section Length = 20s
    
    Number of Segments = 4
    Segment 1: On=0.38s, Off=0s, with Frequency 1
    Segment 2: On=0.38s, Off=0s, with Frequency 2
    Segment 3: On=0.38s, Off=0s, with Frequency 3
    Segment 4: On=0s, Off=4s, with no frequency components
    Total Tone Length = 20s

