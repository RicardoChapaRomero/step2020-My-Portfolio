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
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> messages = new ArrayList<>();
  private String json;

  /** Converts Messages ArrayList to Json */
  public void toJson() throws IOException {
    json = new Gson().toJson(messages);
  }

  /** Redirection to the main page */
  private void doRedirect(HttpServletResponse response) throws IOException {
    response.sendRedirect(
      "https://8080-1a70d96e-39c0-4773-92eb-3774626a1f7d.us-central1.cloudshell.dev/"
    );
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String value = request.getParameter("user-comment");

    /** Redirection if value is empty or accidental click */
    if(value == null || value.length() == 0) {
      doRedirect(response);
    } else {
      messages.add(value); // Add every new submition to recorded messages 
      toJson();
    }

    response.setContentType("text/html;");
    response.getWriter().println(json);
    doRedirect(response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(json);
  }
}
