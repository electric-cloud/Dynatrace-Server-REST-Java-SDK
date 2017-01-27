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

package incidents;


import com.dynatrace.sdk.server.BasicServerConfiguration;
import com.dynatrace.sdk.server.DynatraceClient;
import com.dynatrace.sdk.server.incidents.Incidents;
import com.dynatrace.sdk.server.incidents.models.*;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IncidentsTest {
    @Rule
    public WireMockRule wireMock = new WireMockRule();
    private Incidents incidents = new Incidents(new DynatraceClient(new BasicServerConfiguration("admin", "admin", false, "localhost", 8080, false, 2000)));

    @Test
    public void createIncident() throws Exception {
        stubFor(post(urlPathEqualTo(String.format(Incidents.INCIDENTS_EP, "Test", "Deployment")))
                .withRequestBody(equalToXml("<incident><message>Test message</message><description>Test description</description><systemProfile>Test</systemProfile><incidentRule>Deployment</incidentRule></incident>"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody("")
                        .withHeader("Location", "http://localhost:8021/rest/management/incidents/Deployment/test_guid")));

        CreateUpdateIncidentRequest request = new CreateUpdateIncidentRequest("Test", "Deployment");
        request.setMessage("Test message");
        request.setDescription("Test description");

        String id = incidents.createIncident(request);
        assertThat(id, is("test_guid"));
    }

    @Test
    public void getIncident() throws Exception {
        stubFor(get(urlPathEqualTo(String.format(Incidents.INCIDENT_EP, "Test", "Deployment", "test_id")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<incident id=\"7825229f-039d-4761-95e1-01cb79d98a81\">\n" +
                                "    <message>Test Message</message>\n" +
                                "    <description>Test Description</description>\n" +
                                "    <severity>warning</severity>\n" +
                                "    <state>Created</state>\n" +
                                "    <start>2013-12-18T04:31:12.772+01:00</start>\n" +
                                "    <end>2013-12-19T04:30:00.000+01:00</end>\n" +
                                "</incident>")));
        Incident incident = incidents.getIncident("Test", "Deployment", "test_id");
        assertThat(incident.getDescription(), is("Test Description"));
        assertThat(incident.getMessage(), is("Test Message"));
        assertThat(incident.getState().getInternal(), is("Created"));
        assertThat(incident.getSeverity().getInternal(), is("warning"));
        assertThat(incident.getId(), is("7825229f-039d-4761-95e1-01cb79d98a81"));
    }


    @Test
    public void fetchIncidents() throws Exception {
        stubFor(get(urlPathEqualTo(String.format(Incidents.INCIDENTS_EP, "Test", "Deployment")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
                                "<incidents>\n"+
                                "    <incidentreference id=\"9cb9d5b2-59bb-4a08-912c-637ce5f6aee2\" href=\"http://localhost:8020/rest/management/profiles/easyTravel/incidentrules/Custom/incidents/9cb9d5b2-59bb-4a08-912c-637ce5f6aee2\" />\n"+
                                "    <incidentreference id=\"e95ee7d4-0999-4887-90e2-35a3179feaf8\" href=\"http://localhost:8020/rest/management/profiles/easyTravel/incidentrules/Custom/incidents/e95ee7d4-0999-4887-90e2-35a3179feaf8\" />\n"+
                                "    <incidentreference id=\"7825229f-039d-4761-95e1-01cb79d98a8d\" href=\"http://localhost:8020/rest/management/profiles/easyTravel/incidentrules/Custom/incidents/7825229f-039d-4761-95e1-01cb79d98a8d\" />\n"+
                                "    <incidentreference id=\"b295aa73-0fc2-44a1-b75a-b279602f5973\" href=\"http://localhost:8020/rest/management/profiles/easyTravel/incidentrules/Custom/incidents/b295aa73-0fc2-44a1-b75a-b279602f5973\" />\n"+
                                "</incidents>")));
        FetchIncidentsRequest request  = new FetchIncidentsRequest("Test", "Deployment");
        FetchedIncidents fetchedIncidents = incidents.fetchIncidents(request);

        List<FetchedIncident> list = fetchedIncidents.getIncidents();
        assertThat(list.size(), is(4));
        FetchedIncident firstIncident = list.get(0);
        assertThat(firstIncident.getId(), is("9cb9d5b2-59bb-4a08-912c-637ce5f6aee2"));
    }


}
