<%-- 
    Document   : twitterFavorites
    Created on : 10/05/2013, 01:55:57 PM
    Author     : francisco.jimenez
--%>

<%@page import="org.semanticwb.model.SWBContext"%>
<%@page import="org.semanticwb.social.SocialUserExtAttributes"%>
<%@page import="org.semanticwb.social.SocialUserExtAttributes"%>
<%@page import="static org.semanticwb.social.admin.resources.Timeline.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.semanticwb.social.admin.resources.SocialUserStreamListener"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="org.semanticwb.social.admin.resources.Timeline"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="org.semanticwb.social.Twitter"%>
<%@page import="org.semanticwb.social.base.TwitterBase"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.semanticwb.social.SocialTopic"%>
<%@page import="org.semanticwb.model.WebSite"%>
<%@page import="org.semanticwb.platform.SemanticObject"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="twitter4j.*"%>
<%@page import="twitter4j.conf.ConfigurationBuilder"%>

<jsp:useBean id="paramRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest"/>
<jsp:useBean id="twitterBean" scope="request" type="twitter4j.Twitter"/>

<%    
    String objUri = (String) request.getParameter("suri");
    SWBResourceURL renderURL = paramRequest.getRenderUrl().setParameter("suri", objUri);
    long maxTweetID = 0L;
    //'resetTabTitle' resets the Tab title to the specified value
%>

<div dojoType="dojox.layout.ContentPane">
    <script type="dojo/method">
        resetTabTitle('<%=objUri%>','<%=FAVORITES_TAB%>', 'Favorites' );
   </script>
</div>

<div class="swbform">
<%
    try {
            //gets Twitter4j instance with account credentials
            out.println("<div class=\"swbform\">");
            System.out.println( paramRequest.getLocaleString("showing") + " @" + twitterBean.getScreenName() +  "'s Favorites.");
            out.println("<div align=\"center\"><h2>" + paramRequest.getLocaleString("showing") + " @" + twitterBean.getScreenName() + " " + paramRequest.getLocaleString("favorites") +  "</h2><br/></div>");
            out.println("<div class=\"bar\" id=\"" + objUri + "/newFavoritesAvailable\" dojoType=\"dojox.layout.ContentPane\"></div>");
            out.println("<div id=\"" + objUri + "/favoritesStream\" dojoType=\"dojox.layout.ContentPane\"></div>");

            Paging paging = new Paging(); //used to set maxId and count
            paging.count(5);//Gets a number of tweets of timeline. Max value is 200           
            int i = 0;
            org.semanticwb.model.User user = paramRequest.getUser();
            SocialUserExtAttributes socialUserExtAttr = null;
            if(user.isSigned()){
                socialUserExtAttr = SocialUserExtAttributes.ClassMgr.getSocialUserExtAttributes(user.getId(), SWBContext.getAdminWebSite());
            }
            for (Status status : twitterBean.getFavorites(paging)){
                //maxTweetID = status.getId();
                doPrintTweet(request, response, paramRequest, status, twitterBean, out, null, FAVORITES_TAB, null, socialUserExtAttr);
                i++;
            }
            System.out.println("Total Favorites" + i);
        } catch (Exception te) {
            System.out.println("Se presento un error en Twitter Favorites!!");
            te.printStackTrace();
        }
            out.println("</div>");
%>
<div id="<%=objUri%>/getMoreFavorites" dojoType="dojox.layout.ContentPane">
    <div align="center">
        <label id="<%=objUri%>/moreFavoritesLabel"><a href="#" onclick="appendHtmlAt('<%=renderURL.setMode("getMoreFavorites").setParameter("maxTweetID", maxTweetID+"")%>','<%=objUri%>/getMoreFavorites', 'bottom');try{this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode);}catch(noe){}; return false;">More Favorites</a></label>
    </div>
</div>
</div>