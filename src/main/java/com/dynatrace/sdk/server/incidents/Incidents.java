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

package com.dynatrace.sdk.server.incidents;


import com.dynatrace.sdk.server.DynatraceClient;
import com.dynatrace.sdk.server.Service;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.incidents.models.CreateUpdateIncidentRequest;
import com.dynatrace.sdk.server.incidents.models.FetchIncidentsRequest;
import com.dynatrace.sdk.server.incidents.models.FetchedIncidents;
import com.dynatrace.sdk.server.incidents.models.Incident;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Wraps Dynatrace server Incidents REST API
 * https://community.dynatrace.com/community/pages/viewpage.action?pageId=221381767
 */
public class Incidents extends Service {
    public static final String INCIDENTS_EP = "/rest/management/profiles/%s/incidentrules/%s/incidents/";
    public static final String INCIDENT_EP = "/rest/management/profiles/%s/incidentrules/%s/incidents/%s";

    private static final String LOCATION_HEADER = "Location";
    private static final String PARAM_TO = "to";
    private static final String PARAM_FROM = "from";
    private static final String PARAM_STATE = "state";

    public Incidents(DynatraceClient client) {
        super(client);
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    /**
     * Retrieves incidents from server
     * @param request {@link FetchIncidentsRequest} - filter parameters
     * @return a {@link FetchedIncidents} object, which contains incident ids and hrefs.
     * @throws ServerConnectionException
     * @throws ServerResponseException
     */
    public FetchedIncidents fetchIncidents(FetchIncidentsRequest request) throws ServerConnectionException, ServerResponseException {
        String systemProfile = request.getSystemProfile();
        String rule = request.getIncidentRule();

        ArrayList<NameValuePair> nvps = new ArrayList<>();
        if (request.getTo() != null) {
            String formattedDate = dateFormat.format(request.getTo());
            nvps.add(new BasicNameValuePair(PARAM_TO, formattedDate));
        }
        if (request.getFrom() != null ) {
            String formattedDate = dateFormat.format(request.getFrom());
            nvps.add( new BasicNameValuePair(PARAM_FROM, formattedDate));
        }
        if (request.getState() != null ) {
            nvps.add( new BasicNameValuePair(PARAM_STATE, request.getState().getInternal()));
        }

        try {
            URI uri = this.buildURI(String.format(INCIDENTS_EP, systemProfile, rule), nvps.toArray(new NameValuePair[nvps.size()]));
            return this.doGetRequest(uri, FetchedIncidents.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Could not build incidents endpoint for: %s", INCIDENTS_EP));
        }
    }

    /**
     * Retrieves single {@link Incident} from Dynatrace server
     * @param systemProfile - sysmtem profile id
     * @param rule - incident rule, e.g. Deployment
     * @param id - incident id
     * @return {@link Incident} - object with incident data
     * @throws ServerConnectionException
     * @throws ServerResponseException
     */
    public Incident getIncident(String systemProfile, String rule, String id) throws ServerConnectionException, ServerResponseException {
        try {
            URI uri = this.buildURI(String.format(INCIDENT_EP, systemProfile, rule, id));
            return this.doGetRequest(uri, Incident.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Could not build incidents endpoint for: %s", INCIDENT_EP));
        }
    }

    /**
     * Creates an {@link Incident} on a Dynatrace server
     * @param request {@link CreateUpdateIncidentRequest} - incident data
     * @return id - incident id
     * @throws ServerConnectionException
     * @throws ServerResponseException
     */
    public String createIncident(CreateUpdateIncidentRequest request) throws ServerConnectionException, ServerResponseException {
        String systemProfile = request.getSystemProfile();
        String rule = request.getIncidentRule();
        try {
            URI uri = this.buildURI(String.format(INCIDENTS_EP, systemProfile, rule));
            CloseableHttpResponse response = this.doPostRequest(uri, Service.xmlObjectToEntity(request));
            Header location = response.getFirstHeader(LOCATION_HEADER);
            String href = location.getValue();
            String[] bits = href.split("/");
            return bits[bits.length-1];
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Could not build incidents endpoint for: %s", INCIDENTS_EP));
        }
    }

    /**
     * Updates incident on server
     * @param id - incident id
     * @param request {@link CreateUpdateIncidentRequest} - incident data
     * @throws ServerConnectionException
     * @throws ServerResponseException
     */
    public void updateIncident(String id, CreateUpdateIncidentRequest request) throws ServerConnectionException, ServerResponseException {
        String systemProfile = request.getSystemProfile();
        String rule = request.getIncidentRule();
        try {
            URI uri = this.buildURI(String.format(INCIDENT_EP, systemProfile, rule, id));
            this.doPutRequest(uri, Service.xmlObjectToEntity(request));
        } catch(URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Could not build incidents endpoint for: %s", INCIDENT_EP));
        }
    }


    @Deprecated
    private static void dumpEntity(org.apache.http.entity.StringEntity entity) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
            String input;
            while( (input = br.readLine()) != null ) {
                System.out.println(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
