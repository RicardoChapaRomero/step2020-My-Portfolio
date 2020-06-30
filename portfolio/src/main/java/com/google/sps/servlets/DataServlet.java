// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  private List<String> messages = new ArrayList<>();

  /** Converts Messages ArrayList to Json */
  public String toJson() throws IOException {
   return new Gson().toJson(messages);
  }

  /** Redirection to the main page */
  private void doRedirect(HttpServletResponse response) throws IOException {
    response.sendRedirect("/index.html");
  }

  /** Write a new message to DataStore */
  private void toDatastore(String comment) throws IOException {
    // Create a new entity to save in datastore 
    Entity newComment = new Entity("Comment");

    // Set the entity's values { key: value } 
    newComment.setProperty("comment", comment); 

    // Call to get datastore service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(newComment); // Write the entity into datastore 
  }

  private void dataServletResponse(HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(toJson());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String value = request.getParameter("user-comment");
   
    /** Redirection if value is empty or accidental click */
    if(value == null || value.length() == 0) {
      doRedirect(response);
      return;
    } else {
      messages.add(value); // Add every new submition to recorded messages 
      toJson();
      toDatastore(value);
    }

    dataServletResponse(response);
    doRedirect(response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    dataServletResponse(response);
  }
}
