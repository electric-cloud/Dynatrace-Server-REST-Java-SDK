/*
 * Dynatrace Server SDK
 * Copyright (c) 2008-2016, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  Neither the name of the dynaTrace software nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package com.dynatrace.sdk.server.memorydumps.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "memorydumpjob")
@XmlAccessorType(XmlAccessType.FIELD)
public class MemoryDumpJob {
    @XmlAttribute
    private String id;

    private JobState state;
    @XmlElement(name = "ispostprocessed")
    private Boolean isPostProcessed;
    @XmlElement(name = "issessionlocked")
    private Boolean isSessionLocked;
    @XmlElement(name = "iscaptureprimitives")
    private Boolean isCapturePrimitives;
    @XmlElement(name = "iscapturestrings")
    private Boolean isCaptureStrings;
    @XmlElement(name = "isdogc")
    private Boolean isDogc;

    private Value progress;
    private Value duration;

    @XmlElement(name = "agentpattern")
    private AgentPattern agentPattern;
    @XmlElement(name = "sessionreference")
    private SessionReference sessionReference;

    public String getId() {
        return this.id;
    }

    public JobState getState() {
        return this.state;
    }

    public Boolean isPostProcessed() {
        return this.isPostProcessed;
    }

    public Boolean isSessionLocked() {
        return this.isSessionLocked;
    }

    public Boolean isCapturePrimitives() {
        return this.isCapturePrimitives;
    }

    public Boolean isCaptureStrings() {
        return this.isCaptureStrings;
    }

    public Boolean isDogc() {
        return this.isDogc;
    }

    public Value getProgress() {
        return this.progress;
    }

    public Value getDuration() {
        return this.duration;
    }

    public AgentPattern getAgentPattern() {
        return this.agentPattern;
    }

    public SessionReference getSessionReference() {
        return this.sessionReference;
    }

    @Override
    public String toString() {
        return "MemoryDumpJob{" +
                "id='" + this.id + '\'' +
                ", state=" + this.state +
                ", isPostProcessed=" + this.isPostProcessed +
                ", isSessionLocked=" + this.isSessionLocked +
                ", isCapturePrimitives=" + this.isCapturePrimitives +
                ", isCaptureStrings=" + this.isCaptureStrings +
                ", isDogc=" + this.isDogc +
                ", progress=" + this.progress +
                ", duration=" + this.duration +
                ", agentPattern=" + this.agentPattern +
                ", sessionReference=" + this.sessionReference +
                '}';
    }
}
