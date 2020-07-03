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
import com.google.sps.usercomment.UserComments;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/load-comments")
public class LoadCommentServlet extends HttpServlet {

  private List<UserComments> commentArray = new ArrayList<>();
  private int numberOfComments;

  public void loadComments() throws IOException {
    Query commentsQuery = new Query("Comment"); // Get previous stored comments
    commentArray = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    // Prepare to instance the past stored comments
    Iterable<Entity> comments = datastore.prepare(commentsQuery).asIterable(FetchOptions.Builder.withLimit(numberOfComments));

    for (Entity commentEntity : comments) {
      // Get the value of every stored comment
      long id = commentEntity.getKey().getId();
      String comment = (String) commentEntity.getProperty("comment");
      String user = (String ) commentEntity.getProperty("user");

      UserComments userCommentEntity = new UserComments(user,comment,id); 
      commentArray.add(userCommentEntity); // Add the value to the comments array
    }
  }

  public String toJson() throws IOException {
    //System.out.println(commentArray.size());
    return new Gson().toJson(commentArray);
  }

  public int getNumberOfComments(HttpServletRequest request) throws IOException {
    String value = request.getParameter("number-of-comments"); 
    int selectedNumberOfComments;

    selectedNumberOfComments = (value == null || value.length() == 0) ? 5 : Integer.parseInt(value);

    return selectedNumberOfComments;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    numberOfComments = getNumberOfComments(request);
    loadComments();

    response.setContentType("application/json;");
    response.getWriter().println(toJson());
  }
}