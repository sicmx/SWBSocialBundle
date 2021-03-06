<%-- 
    Document   : wall
    Created on : 8/04/2013, 10:10:48 AM
    Author     : francisco.jimenez
--%>
<%@page import="org.semanticwb.social.admin.resources.FacebookWall"%>
<%@page import="org.semanticwb.social.Facebook"%>
<%@page import="org.semanticwb.model.WebSite"%>
<%@page import="org.semanticwb.model.SWBModel"%>
<%@page import="org.semanticwb.platform.SemanticObject"%>
<%@page import="java.io.Reader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="java.io.Writer"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TimeZone"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="java.util.HashMap"%>
<jsp:useBean id="paramRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest"/>
<jsp:useBean id="facebookBean" scope="request" type="org.semanticwb.social.Facebook"/>
<%@page import="static org.semanticwb.social.admin.resources.FacebookWall.*"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONException"%>
<%@page import="org.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="x-iso-8859-11"%>
<style type="text/css">
    div.bar{
      background-color: #F5F5F5;
      border-top: 1px solid #DDDDDD;
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.05) inset;
      cursor: pointer;
      display: block;
      font-size: 13px;
      font-weight: normal;
      padding: 10px 1px;
      position: relative;
      text-align: center;          
    }
</style>
<%
    try {
        String objUri = (String) request.getParameter("suri");
        Facebook facebook = (Facebook) SemanticObject.createSemanticObject(objUri).createGenericInstance();
        String username = request.getParameter("title");
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("access_token", facebookBean.getAccessToken());
//        String user = postRequest(params, Facebook.FACEBOOKGRAPH + "me",
//                            Facebook.USER_AGENT, "GET");
//        JSONObject userObj = new JSONObject(user);
//        if (!userObj.isNull("name")) {
//            username = userObj.getString("name");
//        } else {
//            username = facebookBean.getTitle();
//        }
%>
<div class="timelineTab" style="padding:10px 5px 10px 5px; overflow-y: scroll; height: 400px;">
    <div class="timelineTab-title">
        <p><strong><%=username%></strong><%=paramRequest.getLocaleString("myWall")%></p>
    </div>
    <div class="bar" id="<%=objUri%>newPostsWallAvailable" dojoType="dojox.layout.ContentPane"></div>
    <div id="<%=objUri%>facebookWallStream" dojoType="dojox.layout.ContentPane"></div>
<%
        SWBModel model = WebSite.ClassMgr.getWebSite(facebookBean.getSemanticObject().getModel().getName());
        params.put("limit", "30");
        params.put("fields", "id,from,to,message,message_tags,story,story_tags,picture,caption,link,object_id,application,source,name,description,properties,actions,privacy,type,status_type,created_time,likes.summary(true),comments.limit(5).summary(true),place");
        String since = (String) session.getAttribute(objUri + WALL_TAB + "since");

        //GETS ONLY MY POSTS
        String fbResponse = postRequest(params, Facebook.FACEBOOKGRAPH + "me/feed",
                            Facebook.USER_AGENT, "GET");
        //out.println("--fbResponse:<br>" + fbResponse + "--");
        String untilPost = parseResponse(fbResponse, out, true, request, paramRequest, WALL_TAB, model);//Gets the newest post and saves the ID of the last one    
        SWBResourceURL renderURL = paramRequest.getRenderUrl().setParameter("suri", objUri).setParameter("currentTab", WALL_TAB);
        //quite setParameter("before", since) de renderURL
        if (untilPost != null && !untilPost.isEmpty()) {
            since = untilPost;//para que traiga los anteriores
%>
    <div id="<%=objUri%>getMorePostsWall" dojoType="dijit.layout.ContentPane">
        <div align="center" style="margin-bottom: 10px;">
            <label id="<%=objUri%>morePostsWallLabel">
                <a href="#" onclick="appendHtmlAt('<%=renderURL.setMode("getMorePosts").setParameter("scope", "wall")%>','<%=objUri%>getMorePostsWall', 'bottom');try{this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode);}catch(noe){}; return false;"><%=paramRequest.getLocaleString("getMorePosts")%></a>
            </label>
        </div>
    </div>
<%
        }
%>
</div>
<%
    } catch (Exception e) {
        out.print("Problem displaying Wall: " + e.getMessage());
    }
%>