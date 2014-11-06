/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.util;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.util.logging.Logger;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * <p>This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans.</p>
 *
 * <p>Example injection on a managed bean field:<p/>
 * 
 * <code>
 * &#064;Inject
 * private EntityManager em;
 * </code>
 *
 * @author Joshua Wilson
 *
 */
public class Resources {

    @Produces
    @PersistenceContext(unitName = "travel_pu")
    private EntityManager em;

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    @Produces
    @Named("logger")
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Produces
    @Named("httpClient")
    public CloseableHttpClient produceHttpClient() {
        return HTTP_CLIENT;
    }

}
