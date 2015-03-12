/*
Modified BSD License
====================

Copyright (c) 2015, CoNWeTLab, Universidad Politecnica Madrid
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
* Neither the name of the SAP AG nor the
names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL SAP AG BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.fiware.apps.repository.dao.impl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.fiware.apps.repository.exceptions.db.DatasourceException;
import org.fiware.apps.repository.exceptions.db.SameIdException;
import org.fiware.apps.repository.model.Resource;
import org.fiware.apps.repository.settings.RepositorySettings;


import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import java.util.Iterator;
import java.util.Objects;
import org.fiware.apps.repository.dao.VirtModelFactory;
import org.fiware.apps.repository.model.SelectQueryResponse;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import org.fiware.apps.repository.dao.VirtuosoQueryExecutionFactory;

public class VirtuosoResourceDAO {
    
    private VirtGraph set;
    private VirtModelFactory modelFactory;
    private VirtuosoQueryExecutionFactory queryExecutionFactory;
    
    public VirtuosoResourceDAO(VirtModelFactory factory, VirtGraph graph, VirtuosoQueryExecutionFactory queryExecutionFactory) {
        this.modelFactory = Objects.requireNonNull(factory);
        this.set = Objects.requireNonNull(graph);
        set.setReadFromAllGraphs(true);
        this.queryExecutionFactory = Objects.requireNonNull(queryExecutionFactory);
    }
    
    public Resource getResource(String graph, String type) throws DatasourceException {
        
        //Obtain the resource in the specified format.
        Resource res = new Resource();
        Model model = modelFactory.openDatabaseModel(graph, RepositorySettings.VIRTUOSO_HOST + RepositorySettings.VIRTUOSO_PORT,
                RepositorySettings.VIRTUOSO_USER, RepositorySettings.VIRTUOSO_PASSWORD);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            model.write(output, type, null);
        } catch (Exception e) {
            throw new DatasourceException(e.getMessage(), Resource.class);
        }
        model.close();
        
        res.setContent(output.toByteArray());
        return res;
    }
    
    public Boolean insertResource(String graph, String content, String type) throws DatasourceException {
        
        //Insert the nodes in a graph.
        Model model = modelFactory.openDatabaseModel(graph, RepositorySettings.VIRTUOSO_HOST + RepositorySettings.VIRTUOSO_PORT,
                RepositorySettings.VIRTUOSO_USER, RepositorySettings.VIRTUOSO_PASSWORD);
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        try
        {
            model.read(new InputStreamReader(input), null, type);
        } catch (Exception e) {
            throw new DatasourceException(e.getMessage(), Resource.class);
        }
        model.close();
        return true;
    }
    
    public Boolean isResource(String graph) {
        
        //Check if the graph exist and have any triple.
        Model model = modelFactory.openDatabaseModel(graph, RepositorySettings.VIRTUOSO_HOST + RepositorySettings.VIRTUOSO_PORT,
                RepositorySettings.VIRTUOSO_USER, RepositorySettings.VIRTUOSO_PASSWORD);
        return !model.isEmpty();
    }
    
    public Boolean updateResource(String graph, String content, String type)
            throws DatasourceException {
        //Remove the content of the resource in the triple store, and insert the new content.
        Model model = modelFactory.openDatabaseModel(graph, RepositorySettings.VIRTUOSO_HOST + RepositorySettings.VIRTUOSO_PORT,
                RepositorySettings.VIRTUOSO_USER, RepositorySettings.VIRTUOSO_PASSWORD);
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        model.removeAll();
        try {
            model.read(new InputStreamReader(input), null, type);
        } catch (Exception e) {
            throw new DatasourceException(e.getMessage(), Resource.class);
        }
        model.close();
        return true;
    }
    
    public Boolean replaceResource(String oldGraph, String newGraph, String type)
            throws DatasourceException {
        //
        Model oldModel = modelFactory.openDatabaseModel(oldGraph, RepositorySettings.VIRTUOSO_HOST + RepositorySettings.VIRTUOSO_PORT,
                RepositorySettings.VIRTUOSO_USER, RepositorySettings.VIRTUOSO_PASSWORD);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            oldModel.write(output, type, null);
            oldModel.removeAll();
        } catch (Exception e) {
            throw new DatasourceException(e.getMessage(), Resource.class);
        }
        oldModel.close();
        
        Model newModel = modelFactory.openDatabaseModel(newGraph, RepositorySettings.VIRTUOSO_HOST + RepositorySettings.VIRTUOSO_PORT,
                RepositorySettings.VIRTUOSO_USER, RepositorySettings.VIRTUOSO_PASSWORD);
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        try {
            newModel.removeAll();
            newModel.read(new InputStreamReader(input), null, type);
        } catch (Exception e) {
            throw new DatasourceException(e.getMessage(), Resource.class);
        }
        newModel.close();
        return true;
    }
    
    public Boolean deleteResource(String graph) {
        
        //It is not necessary to check if "id" is a graph.
        Model model = modelFactory.openDatabaseModel(graph, RepositorySettings.VIRTUOSO_HOST + RepositorySettings.VIRTUOSO_PORT,
                RepositorySettings.VIRTUOSO_USER, RepositorySettings.VIRTUOSO_PASSWORD);
        model.removeAll();
        model.close();
        return true;
    }
    
    public SelectQueryResponse executeQuerySelect(String query) throws QueryParseException {
        
        //Query sparql = QueryFactory.create(query);
        VirtuosoQueryExecution vqe = queryExecutionFactory.create(query, set);
        ResultSet resultSet = vqe.execSelect();
        SelectQueryResponse selectQueryResponse = new SelectQueryResponse();
        Boolean named = false;
        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();
            Iterator <String> varNames = querySolution.varNames();
            for (int i = 0; varNames.hasNext(); i++) {
                if (!named) {
                    String varName = varNames.next();
                    selectQueryResponse.addColumn(varName);
                    selectQueryResponse.addValue(i, querySolution.get(varName).toString());
                }
                else {
                    selectQueryResponse.addValue(i, querySolution.get(varNames.next()).toString());
                }
            }
            named = true;
        }
        vqe.close();
        return selectQueryResponse;
    }
    
    public String executeQueryConstruct(String query, String type) throws QueryParseException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        VirtuosoQueryExecution vqe = queryExecutionFactory.create(query, set);
        Model model = vqe.execConstruct();
        model.write(output, type, null);
        
        return output.toString();
    }
    
    public boolean executeQueryAsk(String query) throws QueryParseException {
        boolean response = false;
        
        VirtuosoQueryExecution vqe = queryExecutionFactory.create(query, set);
        response = vqe.execAsk();
        
        return response;
    }
    
    public String executeQueryDescribe (String query, String type) throws QueryParseException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        VirtuosoQueryExecution vqe = queryExecutionFactory.create(query, set);
        Model model = vqe.execDescribe();
        model.write(output, type, null);
        
        return output.toString();
    }
    
}