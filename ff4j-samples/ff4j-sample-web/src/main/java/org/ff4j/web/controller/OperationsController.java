package org.ff4j.web.controller;

import static org.ff4j.web.WebConstants.CONTENT_TYPE_JSON;

/*
 * #%L
 * ff4j-sample-web
 * %%
 * Copyright (C) 2013 - 2016 FF4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import static org.ff4j.web.WebConstants.OP_EXPORT;
import static org.ff4j.web.WebConstants.OP_FEATURES;
import static org.ff4j.web.WebConstants.OP_PROPERTIES;
import static org.ff4j.web.embedded.ConsoleOperations.exportFile;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.property.Property;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Mini API to get informations through AJAX in JSON.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class OperationsController extends AbstractController {

    /** {@inheritDoc} */
    public OperationsController(FF4j ff4j, TemplateEngine te) {
        super(ff4j, null, te);
    }

    /** {@inheritDoc} */
    public void post(HttpServletRequest req, HttpServletResponse res, WebContext ctx)
    throws IOException {
    }
    
    /** {@inheritDoc} */
    public void get(HttpServletRequest req, HttpServletResponse res, WebContext ctx)
    throws IOException {
        String[] pathParts = req.getPathInfo().split("/");
        String operation   = pathParts[2];

        if (OP_EXPORT.equalsIgnoreCase(operation)) {
            exportFile(ff4j, res);
            return;

        } else if (OP_FEATURES.equalsIgnoreCase(operation)) {
            featuresAsJson(req, res);
            return;
            
        } else if (OP_PROPERTIES.equalsIgnoreCase(operation)) {
            propertiesAsJson(req, res);
            return;
        }   
    }
   
    /**
     * Generation of JSON to render Features.
     *
     * @param req
     *      current request
     * @param res
     *      current response
     * @throws IOException 
     */
    private void featuresAsJson(HttpServletRequest req, HttpServletResponse res)
    throws IOException {
        String[] pathParts = req.getPathInfo().split("/");
        res.setContentType(CONTENT_TYPE_JSON);
        if (pathParts.length > 3) {
            String featureId   = pathParts[3];
            if (getFf4j().getFeatureStore().exist(featureId)) {
                Feature f = getFf4j().getFeatureStore().read(featureId);
                res.getWriter().println(f.toJson());
            } else {
                res.setStatus(Status.NOT_FOUND.getStatusCode());
                res.getWriter().println("Feature " + featureId + " does not exist in feature store." );
            }
        } else {
            Map< String, Feature > mapOfFeatures = getFf4j().getFeatureStore().readAll();
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Feature feature : mapOfFeatures.values()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(feature.toJson());
               first = false;
            }
            sb.append("]");
            res.getWriter().println(sb.toString());
        }
    }
    
    /**
     * Generation of JSON to render Properties.
     *
     * @param req
     *      current request
     * @param res
     *      current response
     */
    private void propertiesAsJson(HttpServletRequest req, HttpServletResponse res)
    throws IOException {
        String[] pathParts = req.getPathInfo().split("/");
        res.setContentType(CONTENT_TYPE_JSON);
        if (pathParts.length > 3) {
            String propertyName   = pathParts[3];
            if (getFf4j().getPropertiesStore().existProperty(propertyName)) {
                Property<?> p = getFf4j().getPropertiesStore().readProperty(propertyName);
                res.getWriter().println(p.toJson());
            } else {
                res.setStatus(Status.NOT_FOUND.getStatusCode());
                res.getWriter().println("Property " + propertyName + " does not exist in property store." );
            }
        } else {
            Map< String, Property<?> > mapOfFeatures = getFf4j().getPropertiesStore().readAllProperties();
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Property<?> myProperty : mapOfFeatures.values()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(myProperty.toJson());
               first = false;
            }
            sb.append("]");
            res.getWriter().println(sb.toString());
        }
        return;
    }

}
