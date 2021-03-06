/**  
* SWB Social es una plataforma que descentraliza la publicación, seguimiento y monitoreo hacia las principales redes sociales. 
* SWB Social escucha y entiende opiniones acerca de una organización, sus productos, sus servicios e inclusive de su competencia, 
* detectando en la información sentimientos, influencia, geolocalización e idioma, entre mucha más información relevante que puede ser 
* útil para la toma de decisiones. 
* 
* SWB Social, es una herramienta basada en la plataforma SemanticWebBuilder. SWB Social, como SemanticWebBuilder, es una creación original 
* del Fondo de Información y Documentación para la Industria INFOTEC, cuyo registro se encuentra actualmente en trámite. 
* 
* INFOTEC pone a su disposición la herramienta SWB Social a través de su licenciamiento abierto al público (‘open source’), 
* en virtud del cual, usted podrá usarla en las mismas condiciones con que INFOTEC la ha diseñado y puesto a su disposición; 
* aprender de élla; distribuirla a terceros; acceder a su código fuente y modificarla, y combinarla o enlazarla con otro software, 
* todo ello de conformidad con los términos y condiciones de la LICENCIA ABIERTA AL PÚBLICO que otorga INFOTEC para la utilización 
* del SemanticWebBuilder 4.0. y SWB Social 1.0
* 
* INFOTEC no otorga garantía sobre SWB Social, de ninguna especie y naturaleza, ni implícita ni explícita, 
* siendo usted completamente responsable de la utilización que le dé y asumiendo la totalidad de los riesgos que puedan derivar 
* de la misma. 
* 
* Si usted tiene cualquier duda o comentario sobre SemanticWebBuilder o SWB Social, INFOTEC pone a su disposición la siguiente 
* dirección electrónica: 
*  http://www.semanticwebbuilder.org
**/ 
 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.social.admin.resources;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticwb.Logger;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.SWBContext;
import org.semanticwb.model.SWBModel;
import org.semanticwb.model.UserGroup;
import org.semanticwb.model.WebSite;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticProperty;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.social.Message;
import org.semanticwb.social.Photo;
import org.semanticwb.social.Post;
import org.semanticwb.social.PostIn;
import org.semanticwb.social.SocialNetwork;
import org.semanticwb.social.SocialNetworkUser;
import org.semanticwb.social.SocialPFlow;
import org.semanticwb.social.SocialTopic;

import org.semanticwb.social.Video;
import org.semanticwb.social.VideoIn;
import org.semanticwb.social.Youtube;
import org.semanticwb.social.util.SWBSocialUtil;
import org.semanticwb.social.util.SocialLoader;

/**
 *
 * @author francisco.jimenez
 */
public class YoutubeWall extends GenericResource{

    private static Logger log = SWBUtils.getLogger(YoutubeWall.class);
    
    /*variables used to define the id of '<div>' for the fields of information, favorite and retweet.
     Each link is in a different '<div>' and it's updated individually*/

    /** Contiene el id del div con la liga de esta opcion {@value /inf}    */
    public static String INFORMATION = "/inf";
    
    /** Contiene el id del div con la liga de esta opcion {@value /like}    */
    public static String LIKE = "/like";
    
    /** Contiene el id del div con la liga de esta opcion {@value /unlike}    */
    public static String DISLIKE = "/unlike";
    
    /** Contiene el id del div con la liga de esta opcion {@value /topic}    */
    public static String TOPIC ="/topic";
    
    /*Additionally every div has a suffix to identify if the status is inside the tab*/ 
    public static String HOME_TAB = "/myvideos";
    public static String DISCOVER_TAB ="/discover";
    public static String CONEXION ="/conexion";
    
    /**
     * Define el numero de comentarios asociados a videos por defecto = 5
     */
    public static int DEFAULT_VIDEO_COMMENTS = 5;
    
    /**
     * Define la url base para la reproduccion de los videos [{@literal http://www.youtube.com/v/}]
     */
    public static String BASE_VIDEO_URL = "http://www.youtube.com/v/";
    
    /**
     * Define el formato a utilizar para el despliegue de fechas [{@value yyyy-MM-dd'T'HH:mm:ss.SSS}]
     */
    public static DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        PrintWriter out = response.getWriter();
        String objUri = (String) request.getParameter("suri");
        String contentTabId = (String) request.getParameter("contentTabId");
        Youtube youtube = (Youtube) SemanticObject.createSemanticObject(objUri).createGenericInstance();
        if (!youtube.isSn_authenticated() || youtube.getAccessToken() == null) {
            out.println("<div id=\"configuracion_redes\">");
            out.println("<div id=\"autenticacion\">");
            out.println("<p>      La cuenta no ha sido autenticada correctamente</p>");
            out.println("</div>");
            out.println("</div>");
            return;
        }
        
        if (contentTabId == null) {//The resource is loaded for the first time and it needs to display the tabs
            String jspResponse = SWBPlatform.getContextPath() + "/work/models/" +
                                 paramRequest.getWebPage().getWebSiteId() + "/jsp/socialNetworks/youtubeTabs1.jsp";
            RequestDispatcher dis = request.getRequestDispatcher(jspResponse);
            try {
                request.setAttribute("paramRequest", paramRequest);
                dis.include(request, response);
            } catch (Exception e) {
                YoutubeWall.log.error("Error loading Youtube tabs", e);
            }
            return;
        }
        
        String jspResponse = "";
        //Each one of the tabs is loaded once
        if (contentTabId.equals(HOME_TAB)) {
            jspResponse = SWBPlatform.getContextPath() + "/work/models/" +
                          paramRequest.getWebPage().getWebSiteId() +"/jsp/socialNetworks/youtubeVideos.jsp";
        } else if (contentTabId.equals(DISCOVER_TAB)) {
            jspResponse = SWBPlatform.getContextPath() + "/work/models/" +
                          paramRequest.getWebPage().getWebSiteId() +"/jsp/socialNetworks/youtubeDiscover.jsp";
        } else if (contentTabId.equals(CONEXION)) {
            jspResponse = SWBPlatform.getContextPath() + "/work/models/" +
                          paramRequest.getWebPage().getWebSiteId() +"/jsp/socialNetworks/youtubeConexion.jsp";
        }
        
        RequestDispatcher dis = request.getRequestDispatcher(jspResponse);
        
        try {
            request.setAttribute("paramRequest", paramRequest);
            dis.include(request, response);
        } catch (Exception e) {
            YoutubeWall.log.error("Error in doView() for requestDispatcher" , e);
        }
    }
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        String mode = paramRequest.getMode();
        String objUri = request.getParameter("suri");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm a", new Locale("es", "MX"));
        if (mode.equals("videoUpdated")) {
            //Porque en este modo se remplaza contenido HTML directamente en el div del video
            response.setContentType("text/html;charset=UTF-8");
        }
        PrintWriter out = response.getWriter();
        
        if (mode.equals("commentVideoSent")) {//Feedback of commented video
            out.println("<script type=\"text/javascript\">");
            out.println("   hideDialog();");
            out.println("   showStatus('Comment sent successfully');");
            out.println("</script>");
        } else if (mode.equals("likeSent")) {//Feedback of liked video
            SWBResourceURL actionURL = paramRequest.getActionUrl();
            actionURL.setParameter("suri", request.getParameter("suri"));
            String videoId = request.getParameter("videoId");
            String action = request.getParameter("action");
            String htmlImage = "";
            String actionTitle = "";
            SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
            Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
        
            try {
                HashMap<String, String> paramsVideo = new HashMap<String, String>(3);
                paramsVideo.put("part", "snippet,statistics");
                paramsVideo.put("id", videoId);
                String videoInfo = semanticYoutube.getRequest(paramsVideo, Youtube.API_URL + "/videos",
                                                              Youtube.USER_AGENT, "GET");
                String favCount = "0";
                if (videoInfo != null) {
                    JSONObject resp = new JSONObject(videoInfo);
                    if (!resp.isNull("items") && resp.getJSONArray("items").length() > 0) {
                        JSONObject video = resp.getJSONArray("items").getJSONObject(0);
                        JSONObject snippet = resp.getJSONArray("items").
                                getJSONObject(0).getJSONObject("snippet");
                        if (snippet.has("publishedAt")) {
                            Date date = YoutubeWall.FORMATTER.parse(snippet.getString("publishedAt"));
                            out.write(df.format(date) + "&nbsp; ");
                        }
                        JSONObject statistics = video.getJSONObject("statistics");
                        if (statistics.has("viewCount")) {
                            out.println( paramRequest.getLocaleString("views") + ":" +
                                    statistics.getString("viewCount") + " ");
                        }
                        if (statistics.has("favoriteCount")) {
                            favCount = statistics.getString("favoriteCount");
                        }
                        out.write(" <strong><span> " + paramRequest.getLocaleString("likes") + ": </span>");
                        out.write(statistics.getLong("likeCount") + " ");
                        out.write(" " + paramRequest.getLocaleString("dislikes") + ": ");
                        out.println(statistics.getLong("dislikeCount") + " ");
                        out.println(" " + paramRequest.getLocaleString("favorites") + ": " +  favCount);
                        out.write("</strong>");
                    }
                }

                if (action.equals("doLike")) {
                    actionURL.setAction("doDislike");
                    action = "dislike";
                    actionTitle = paramRequest.getLocaleString("dislike");
                    htmlImage = "class=\"nolike\"";
                } else if (action.equals("doDislike")) {
                    actionURL.setAction("doLike");
                    action = "like";
                    actionTitle = paramRequest.getLocaleString("like");
                    htmlImage= "class=\"like\"";
                }
                
                /* updates only the DOM of the 'Like/Dislike' message and change URL also*/
                out.println("<span class=\"inline\" dojoType=\"dojox.layout.ContentPane\">");
                out.println("<script type=\"dojo/method\">");
                out.println("   var spanId = dijit.byId('" + semanticYoutube.getId() +
                            videoId + YoutubeWall.LIKE + "');");
                out.println("   spanId.attr('content', '" + "<a href=\"\" " + htmlImage +
                        " onclick=\"try{dojo.byId(this.parentNode).innerHTML = \\'<img src=" +
                        SWBPlatform.getContextPath() +
                        "/swbadmin/icons/loading.gif>\\';}catch(noe){} postSocialHtml(\\'" +
                        actionURL.setParameter("videoId", videoId).setParameter("action", action) +
                        "\\',\\'" + semanticYoutube.getId() + videoId + YoutubeWall.INFORMATION +
                        "\\');return false;" +"\"><span>" + actionTitle + "</span></a>" +"')");
                out.println("   showStatus('" + paramRequest.getLocaleString("actionExecuted") + "');");
                out.println("</script>");
                out.println("</span>");
            } catch (Exception ex) {
                YoutubeWall.log.error("Error when trying to like/dislike ", ex);
            }
        } else if (mode.equals("commentVideo")) {//Displays dialog to create a comment for a video
            SocialNetwork socialNetwork = null;
            String videoId = request.getParameter("videoId");

            try {
                socialNetwork = (SocialNetwork) SemanticObject.getSemanticObject(objUri).getGenericInstance();
            } catch (Exception e) {
                YoutubeWall.log.error("Error getting the social Network", e);
                return;
            }
            
            try {
                SWBModel model = WebSite.ClassMgr.getWebSite(socialNetwork.getSemanticObject().getModel().getName());
                PostIn postIn = PostIn.getPostInbySocialMsgId(model, videoId);
                Youtube ytInstance = (Youtube) socialNetwork;
                
                if (postIn == null) {//Responding for the first time, save the post
                    HashMap<String, String> paramsVideo = new HashMap<String, String>(2);
                    paramsVideo.put("part", "snippet,fileDetails");
                    paramsVideo.put("id", videoId);
                    
                    String ytVideo = ytInstance.getRequest(paramsVideo, Youtube.API_URL + "/videos",
                                                           Youtube.USER_AGENT, "GET");
                    JSONObject resp = new JSONObject(ytVideo);
                    JSONObject jsonVideo = null;
                    String title = "";
                    String description = "";
                    String creatorName = "";
                    String creatorId = "";
                    String created = "";
                    SocialNetworkUser socialNetUser = null;
                    if (!resp.isNull("items") && resp.getJSONArray("items").length() > 0) {
                        jsonVideo = resp.getJSONArray("items").getJSONObject(0);
                    }
                    if (jsonVideo != null && jsonVideo.has("snippet")) {
                        JSONObject snippet = jsonVideo.getJSONObject("snippet");
                        if (snippet.has("title")){//Title
                            title = snippet.getString("title");
                        }
                        if (snippet.has("description")){//Desc
                            description = snippet.getString("description");
                        }
                        if (snippet.has("publishedAt")) {
                            created = snippet.getString("publishedAt");
                        }
                    }

                    if (!creatorId.equals("")) {
                        socialNetUser = SocialNetworkUser.getSocialNetworkUserbyIDAndSocialNet(creatorId, socialNetwork, model);
                    }
                    postIn = VideoIn.ClassMgr.createVideoIn(model);
                    postIn.setSocialNetMsgId(videoId);
                    postIn.setMsg_Text(title + (description.isEmpty() ? "" : " / " + description));
                    postIn.setPostInSocialNetwork(socialNetwork);
                    postIn.setPostInStream(null);
                    Date postTime = !created.isEmpty() ? YoutubeWall.FORMATTER.parse(created) : new Date();
                    if (postTime.after(new Date())) {
                        postIn.setPi_createdInSocialNet(new Date());
                    } else {
                        postIn.setPi_createdInSocialNet(postTime);
                    }
                    postIn.setMsg_url("https://www.youtube.com/watch?v=" + videoId + "&feature=youtube_gdata");
                    Calendar calendario = Calendar.getInstance();
                    postIn.setPi_created(calendario.getTime());
                    postIn.setPi_type(SWBSocialUtil.POST_TYPE_VIDEO);

                    VideoIn videoIn = (VideoIn) postIn;
                    videoIn.setVideo(YoutubeWall.BASE_VIDEO_URL + videoId);

                    if (socialNetUser == null) {//User does not exist                    
                        socialNetUser = SocialNetworkUser.ClassMgr.createSocialNetworkUser(model);//Create a socialNetworkUser
                        socialNetUser.setSnu_id(creatorId);
                        socialNetUser.setSnu_name((creatorName.isEmpty()) ? creatorId : creatorName);
                        socialNetUser.setSnu_SocialNetworkObj(socialNetwork.getSemanticObject());                    
                        socialNetUser.setCreated(new Date());
                        socialNetUser.setUserUrl("https://www.youtube.com/" + creatorId);
                        socialNetUser.setFollowers(0);
                        socialNetUser.setFriends(0);
                    }

                    postIn.setPostInSocialNetworkUser(socialNetUser);

                    SocialTopic defaultSocialTopic = SocialTopic.ClassMgr.getSocialTopic("DefaultTopic", model);
                    if (defaultSocialTopic != null) {
                        postIn.setSocialTopic(defaultSocialTopic);//Asigns socialTipic
                    } else {
                        postIn.setSocialTopic(null);
                    }
                }
                
                response.setContentType("text/html; charset=ISO-8859-1");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                //The post in has been created
                final String path = SWBPlatform.getContextPath() + "/work/models/" +
                                    paramRequest.getWebPage().getWebSiteId() + "/jsp/socialTopic/postInResponse.jsp";
                RequestDispatcher dis = request.getRequestDispatcher(path);
                if (dis != null) {
                    try {
                        request.setAttribute("postUri", SemanticObject.createSemanticObject(postIn.getURI()));
                        request.setAttribute("paramRequest", paramRequest);
                        dis.include(request, response);
                    } catch (Exception e) {
                        YoutubeWall.log.error(e);
                    }
                }
            } catch (Exception e) {
                YoutubeWall.log.error("ERROR:", e);
            }            
        } else if (mode.equals("commentComment")) {//Displays dialog to create a comment for another comment
            SWBResourceURL actionURL = paramRequest.getActionUrl();
            actionURL.setParameter("videoId", request.getParameter("videoId"));
            actionURL.setParameter("suri", request.getParameter("suri"));
            actionURL.setParameter("commentId", request.getParameter("commentId"));

            out.println("<form type=\"dijit.form.Form\" id=\"commentCommentForm\" action=\"" +
                    actionURL.setAction("createCommentComment") +
                    "\" method=\"post\" onsubmit=\"submitForm('commentCommentForm'); try{document.getElementById('csLoading').style.display='inline';}catch(noe){}; return false;\">");
            out.println("<fieldset>");
            out.println("<table>");
            out.println("<tr>");
            out.println("   <td>");
            out.println("       <textarea type=\"dijit.form.Textarea\" name=\"commentComment\" id=\"commentComment\" rows=\"4\" cols=\"50\"></textarea>");
            out.println("   </td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("       <td style=\"text-align: center;\"><button dojoType=\"dijit.form.Button\" type=\"submit\">Comentar</button></td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</fieldset>");
            out.println("</form>");
            out.println("<span id=\"csLoading\" style=\"width: 100px; display: none\" align=\"center\">&nbsp;&nbsp;&nbsp;<img src=\"" +
                    SWBPlatform.getContextPath() +
                    "/swbadmin/images/loading.gif\"/></span>");
        } else if (mode.equals("getMoreComments")) {
            doGetMoreComments(request, response, paramRequest);
        } else if (mode.equals("doShowTopic")) {
            final String path = SWBPlatform.getContextPath() + "/work/models/" +
                    paramRequest.getWebPage().getWebSiteId() + "/jsp/socialTopic/assignTopic.jsp";
            RequestDispatcher dis = request.getRequestDispatcher(path);
            if (dis != null) {
                try {
                    request.setAttribute("suri", objUri);
                    request.setAttribute("paramRequest", paramRequest);
                    dis.include(request, response);
                } catch (Exception e) {
                    YoutubeWall.log.error("Error on doShowTopic: " + e);
                }
            }
        } else if (mode.equals("doReclassifyTopic")) {
            final String path = SWBPlatform.getContextPath() + "/work/models/" +
                    paramRequest.getWebPage().getWebSiteId() + "/jsp/socialTopic/classifybyTopic.jsp";
            RequestDispatcher dis = request.getRequestDispatcher(path);
            if (dis != null) {
                try {
                    SemanticObject semObject = SemanticObject.createSemanticObject(request.getParameter("postUri"));
                    request.setAttribute("postUri", semObject);
                    request.setAttribute("paramRequest", paramRequest);
                    dis.include(request, response);
                } catch (Exception e) {
                    YoutubeWall.log.error("Error on doReclassifyTopic: " + e);
                }
            }
        } else if (mode.equals("assignedPost")) {
            String id = request.getParameter("id");
            String currentTab = request.getParameter("currentTab");
            String postUri = request.getParameter("postUri");
            SWBResourceURL renderURL = paramRequest.getRenderUrl();
            SWBResourceURL clasifybyTopic = renderURL.setMode("doReclassifyTopic").
                    setCallMethod(SWBResourceURL.Call_DIRECT).
                    setParameter("id", id).
                    setParameter("postUri", postUri).
                    setParameter("currentTab", currentTab);
            
            SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
            Youtube semYoutube = (Youtube) semanticObject.createGenericInstance();
            
            String url= "<a href=\"#\" class=\"clasifica\" title=\"" +
                    paramRequest.getLocaleString("reclassify") +
                    "\" onclick=\"showDialog('" + clasifybyTopic + "','" +
                    paramRequest.getLocaleString("reclassify") +"'); return false;\"></a>";
            out.println("<span class=\"inline\" dojoType=\"dojox.layout.ContentPane\">");
            out.println("<script type=\"dojo/method\">");
            out.println("   hideDialog(); ");            
            out.println("   try{");
            out.println("   var spanId = dijit.byId('" + semYoutube.getId() +  id + YoutubeWall.TOPIC + "');");
            out.println("   spanId.attr('content', '" + url.replace("'", "\\'") +"');");
            out.println("   }catch(noe){alert('Error:' + noe);}");
            out.println("   showStatus('" +paramRequest.getLocaleString("assignedTopicOk") + "');");
            out.println("</script>");
            out.println("</span>");
        } else if (mode.equals("reAssignedPost")) {
            out.println("<script type=\"javascript\">");
            out.println("   hideDialog(); ");
            out.println("   showStatus('" + paramRequest.getLocaleString("changedTopicOk") + "');");
            out.println("</script>");
        } else if (mode.equals("getMoreVideos")) {
            doGetMoreVideos(request, response, paramRequest);
        } else if (mode.equals("displayVideo")) {
            String jspResponse = SWBPlatform.getContextPath() + "/work/models/" +
                    paramRequest.getWebPage().getWebSiteId() + "/jsp/socialNetworks/playVideo.jsp";
            RequestDispatcher dis = request.getRequestDispatcher(jspResponse);
            try {
                dis.include(request, response);
            } catch (Exception e) {
                YoutubeWall.log.error("Error in displayVideo() for requestDispatcher" , e);
            }
        } else if (mode.equals("showUserProfile")) {
            RequestDispatcher dis = request.getRequestDispatcher(SWBPlatform.getContextPath() +
                    "/work/models/" + paramRequest.getWebPage().getWebSiteId() +
                    "/jsp/socialNetworks/youtubeUserProfile.jsp");
            try {
                request.setAttribute("paramRequest", paramRequest);
                request.setAttribute("suri", objUri);
                dis.include(request, response);
            } catch (Exception e) {
                YoutubeWall.log.error("Error in mode showUserProfile, processRequest() for requestDispatcher" , e);
            }
        } else if (mode.equals("editVideo")) {
            response.setContentType("text/html; charset=ISO-8859-1");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            String jspResponse = SWBPlatform.getContextPath() + "/work/models/" +
                                 paramRequest.getWebPage().getWebSiteId() + "/jsp/socialNetworks/youtubeEditVideo.jsp";
            RequestDispatcher dis = request.getRequestDispatcher(jspResponse);
            try {
                request.setAttribute("paramRequest", paramRequest);
                dis.include(request, response);
            } catch (Exception e) {
                YoutubeWall.log.error("Error in editVideo() for requestDispatcher" , e);
            }
        } else if (mode.equals("videoUpdated")) {
            try {
                String videoId = (String) request.getParameter("videoId");
                if (videoId == null || objUri == null) {
                    return;
                }

                SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
                Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();

                String video = this.getFullVideoFromId(videoId, semanticYoutube);
                JSONObject videoResp = new JSONObject(video);
                JSONObject videoUpdated = null;
                if (videoResp.has("items") && videoResp.getJSONArray("items").length() > 0) {
                    videoUpdated = videoResp.getJSONArray("items").getJSONObject(0);
                }

                String title = "";
                String description = "";
                String privacy = "";
                String thumbnail = "";
                
                if (videoUpdated != null) {
                    if (videoUpdated.has("snippet")) {
                        JSONObject snippet = videoUpdated.getJSONObject("snippet");
                        if (snippet.has("title")){//Title
                            title = snippet.getString("title");
                        }
                        if (snippet.has("description")){//Desc
                            description = snippet.getString("description");
                        }
                        if (snippet.has("thumbnails")) {
                            JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                            if (thumbnails.has("default")) {
                                thumbnail = thumbnails.getJSONObject("default").getString("url");
                            } else if (thumbnails.has("medium")) {
                                thumbnail = thumbnails.getJSONObject("medium").getString("url");
                            } else if (thumbnails.has("high")) {
                                thumbnail = thumbnails.getJSONObject("high").getString("url");
                            }
                        }
                    }
                    if (videoUpdated.has("status")) {
                        JSONObject status = videoUpdated.getJSONObject("status");
                        //Los valores devueltos pueden ser: private, public o unlisted
                        if (status.has("privacyStatus")) {
                            privacy = status.getString("privacyStatus").toUpperCase();
                        }
                        if (privacy.equalsIgnoreCase("unlisted")) {
                            privacy = "NOT_LISTED";
                        }
                    }
                }
                StringBuilder output = new StringBuilder(256);
                String identifier = semanticYoutube.getId() + videoId;
                output.append("<p>");
                output.append(title);
                output.append("</p>");
                output.append("<div class=\"timelineimg\">");
                output.append(" <span>");                                
                output.append("      <span id=\"img");
                output.append(identifier);
                output.append("\" style=\"width: 250px; height: 250px; border: thick #666666; overflow: hidden; position: relative;\">");
                output.append("      <a href=\"#\" onclick=\"showDialog(\\'");
                output.append(paramRequest.getRenderUrl().setMode("displayVideo").
                        setParameter("videoUrl", URLEncoder.encode("http://www.youtube.com/v/" + videoId, "UTF-8")));
                output.append("\\',\\'");
                output.append(title);
                output.append("\\'); return false;\"><img src=\"");
                output.append(thumbnail);
                output.append("\" style=\"position: relative;\" onerror=\"this.src =\\'");
                output.append(thumbnail);
                output.append("\\'\" onload=\"imageLoad(" + "this, \\'img");
                output.append(identifier);
                output.append("\\');\"/></a>");
                output.append("      </span>");
                output.append(" </span>");
                output.append("<p class=\"imgtitle\">");
                output.append(  title);
                output.append("</p>");
                output.append("<p class =\"imgdesc\">");
                output.append( description.isEmpty() ?  "&nbsp;" : description.replace("\n", "</br>"));
                output.append("</p>");
                output.append("</div>");//End First section
                out.println("<script type=\"javascript\">");
                out.println("   var edited = document.getElementById('" +
                        semanticYoutube.getId() + "/" + videoId + "/detail" +"');");
                out.println("   edited.innerHTML='" + output.toString() + "';");
                out.println("   showStatus('" + paramRequest.getLocaleString("videoEdited") + "');");
                out.println("   hideDialog(); ");
                out.println("</script>");
            } catch (Exception e) {
                YoutubeWall.log.error("Error al traer datos del video ", e);
            }
        } else if (mode.equals("videoDeleted")) {
            String videoId = request.getParameter("videoId");

            if ((videoId == null || videoId.trim().isEmpty()) ||
                    (objUri == null || objUri.trim().isEmpty())) {
                YoutubeWall.log.error("Problem updating video information");
                return;
            }
            SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
            Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
            out.println("<script type=\"javascript\">");
            out.println("   showStatus('" + paramRequest.getLocaleString("videoDeleted") + "');");
            out.println("   var deleted = document.getElementById('" + semanticYoutube.getId() + "/" + videoId +"');");
            out.println("   deleted.style.display='none';");
            out.println("   deleted.innerHTML=''");
            out.println("</script>");
        } else if (paramRequest.getMode().equals("post")) {
            doCreatePost(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }

    public void doGetMoreComments(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        PrintWriter out = response.getWriter();
        String videoId = request.getParameter("videoId");
        String startIndex = request.getParameter("startIndex");
        String totalComments = request.getParameter("totalComments");
        String objUri = request.getParameter("suri");
        SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
        Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm a", new Locale("es", "MX"));
        
        try {
            HashMap<String, String> paramsComments = new HashMap<String, String>(2);
            paramsComments.put("part", "snippet,replies");
            paramsComments.put("videoId", videoId);
            paramsComments.put("maxResults", "10"); //Youtube default = 20
            if (startIndex != null) {
                paramsComments.put("pageToken", startIndex); //valor de la propiedad nextPageToken
            }
            String ytComments = semanticYoutube.getRequest(paramsComments, Youtube.API_URL + "/commentThreads",
                            Youtube.USER_AGENT, semanticYoutube.getAccessToken());
            JSONObject jsonCommentThreads = new JSONObject(ytComments);
            JSONArray arrayCommentThreads = null;
            if (!jsonCommentThreads.isNull("items")) {
                arrayCommentThreads = jsonCommentThreads.getJSONArray("items");
            }
            
            if (arrayCommentThreads != null && arrayCommentThreads.length() > 0) {
                //Only print <li></li> because the HTML will be returned inside <ul></ul
                int commentCounter = 0;
                for (int c = 0; c < arrayCommentThreads.length(); c++) {
                    JSONObject commentThread = arrayCommentThreads.getJSONObject(c);
                    JSONObject threadSnippet = null;
                    String channelId = null;
                    if (!commentThread.isNull("snippet")) {
                        threadSnippet = commentThread.getJSONObject("snippet");
                        channelId = threadSnippet.getString("channelId");
                    }
                    if (!commentThread.isNull("replies") && !commentThread.getJSONObject("replies").isNull("comments")) {
                        JSONArray arrayComments = commentThread.getJSONObject("replies").getJSONArray("comments");
                        for (int j = 0; j < arrayComments.length(); j++) {
                            commentCounter++;
                            JSONObject comment = arrayComments.getJSONObject(j);
                            JSONObject snippet = comment.getJSONObject("snippet");
                            
                            if (!snippet.isNull("authorChannelId") && !snippet.isNull("authorProfileImageUrl")) {
                                out.write("<li>");
                                out.write("<a href=\"#\" title=\"" + paramRequest.getLocaleString("viewProfile") +
                                        "\" onclick=\"showDialog('" + 
                                        paramRequest.getRenderUrl().setMode("showUserProfile").
                                                setParameter("id", snippet.getString("authorChannelId")) +
                                        "','" + paramRequest.getLocaleString("viewProfile") +
                                        "'); return false;\"><img src=\"" + snippet.getString("authorProfileImageUrl") +
                                        "\" width=\"50\" height=\"50\"/></a>");
                                out.write("<p>");
                                out.write("<a href=\"#\" title=\"" + paramRequest.getLocaleString("viewProfile") +
                                        "\" onclick=\"showDialog('" +
                                        paramRequest.getRenderUrl().setMode("showUserProfile").
                                                setParameter("id", snippet.getString("authorChannelId")) +
                                        "','" + paramRequest.getLocaleString("viewProfile") +
                                        "'); return false;\">" + snippet.getString("authorDisplayName") + "</a>:");
                                out.write(snippet.getString("textDisplay").replace("\n", "</br>"));
                                out.write("</p>");
                                out.write("<p class=\"timelinedate\">");
                                out.write("<span class=\"inline\">");
                                Date date = YoutubeWall.FORMATTER.parse(snippet.getString("publishedAt"));
                                out.write(df.format(date) + "&nbsp; ");
                                out.write("</span>");
                                String comentarioId = snippet.getString("id");
                                out.write("   <span class=\"inline\">");
                                out.write(" <a href=\"\" onclick=\"showDialog('" +
                                        paramRequest.getRenderUrl().setMode("commentComment").setParameter("suri", objUri).
                                                setParameter("videoId", videoId).
                                                setParameter("commentId", comentarioId.substring(
                                                             comentarioId.indexOf("comment") + 8)) +
                                        "','Comment to " + snippet.getString("textDisplay").replace("\n", "</br>") +
                                        "');return false;\">Comentar</a>");
                                out.write("   </span>");
                                out.write("</p>");
                                out.write("</li>");
                            }
                        }
                    }
                }
                if (!jsonCommentThreads.isNull("nextPageToken") &&
                        !jsonCommentThreads.getString("nextPageToken").isEmpty()) {//Link to get more comments
                    out.write("<li class=\"timelinemore\">");
                    out.write("<label><a href=\"#\" onclick=\"appendHtmlAt('" +
                            paramRequest.getRenderUrl().setMode("getMoreComments").
                                    setParameter("videoId", videoId).
                                    setParameter("startIndex", jsonCommentThreads.getString("nextPageToken")).
                                    setParameter("totalComments", totalComments + "").
                                    setParameter("suri", objUri) +
                            "','" + semanticYoutube.getId() + videoId +
                            "/comments', 'bottom');try{this.parentNode.parentNode.removeChild(" +
                            " this.parentNode );}catch(noe){}; return false;\"><span>+</span>" +
                            paramRequest.getLocaleString("moreComments") +
                            "</a></label>");
                    out.write("</li>");
                }
            }
        } catch (Exception e) {
            YoutubeWall.log.error("Problem getting more comments", e);
        }
    }
    
    
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException, IOException {
        
        String action = response.getAction();
        
        if (action != null && (action.equals("doLike") || action.equals("doDislike"))) {//Do a Like
            response.setRenderParameter("videoId", request.getParameter("videoId"));
            response.setRenderParameter("suri", request.getParameter("suri"));
            response.setRenderParameter("action", action);
            doLikeDislike(request);
            response.setMode("likeSent");
        } else if(action != null && action.equals("createCommentVideo")) {
            doCommentVideo(request);
            response.setMode("commentVideoSent");
        } else if (action != null && action.equals("createCommentComment")) {
            doCommentComment(request);
            response.setMode("commentVideoSent");
        } else if(action.equals("setSocialTopic")) {
            Youtube socialNetwork = null;
            String videoId = request.getParameter("id");
            String objUri = request.getParameter("suri");

            try {
                socialNetwork = (Youtube) SemanticObject.getSemanticObject(objUri).getGenericInstance();
            } catch (Exception e) {
                log.error("Error getting the social Network", e);
                return;
            }
            
            try {
                HashMap<String, String> paramsVideo = new HashMap<String, String>(3);
                paramsVideo.put("part", "snippet");
                paramsVideo.put("id", videoId);
                String ytVideo = socialNetwork.getRequest(paramsVideo, Youtube.API_URL + "/videos",
                                Youtube.USER_AGENT, socialNetwork.getAccessToken());
                JSONObject jsonVideo = new JSONObject(ytVideo);

                String title = "";
                String description = "";
                String creatorName = "";
                String creatorId =  "";
                String created = "";
                if (!jsonVideo.isNull("items")) {
                    JSONObject video = jsonVideo.getJSONArray("items").getJSONObject(0);
                    
                    if (video.getJSONObject("snippet").has("title")){//Title
                        title = video.getJSONObject("snippet").getString("title");
                    }
                    
                    if (video.getJSONObject("snippet").has("description")){//Desc
                        description = video.getJSONObject("snippet").getString("description");
                    }
/*
                    if(jsonVideo.getJSONObject("entry").has("author")){//User
                        if(jsonVideo.getJSONObject("entry").getJSONArray("author").getJSONObject(0).has("name")){
                            creatorName = jsonVideo.getJSONObject("entry").getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t");
                        }
                        
                    }
                    */
                    if (video.getJSONObject("snippet").has("channelId")) {
                        creatorId = video.getJSONObject("snippet").getString("channelId");
                    }
                    if (video.getJSONObject("snippet").has("publishedAt")) {//Title
                        created = video.getJSONObject("snippet").getString("publishedAt");
                    }
                }

                SWBModel model=WebSite.ClassMgr.getWebSite(socialNetwork.getSemanticObject().getModel().getName());
                SocialNetworkUser socialNetUser = SocialNetworkUser.getSocialNetworkUserbyIDAndSocialNet(
                                                  creatorId, socialNetwork, model);
                                
                PostIn postIn = PostIn.getPostInbySocialMsgId(model, videoId);
                if (postIn == null) {
                    postIn=VideoIn.ClassMgr.createVideoIn(model);
                    postIn.setSocialNetMsgId(videoId);
                    postIn.setMsg_Text(title + (description.isEmpty()? "" : " / " + description));
                    postIn.setPostInSocialNetwork(socialNetwork);
                    postIn.setPostInStream(null);
                    Date postTime = YoutubeWall.FORMATTER.parse(created);
                    if(postTime.after(new Date())){
                        postIn.setPi_createdInSocialNet(new Date());
                    }else{
                        postIn.setPi_createdInSocialNet(postTime);
                    }
                    postIn.setMsg_url("https://www.youtube.com/watch?v=" + videoId + "&feature=youtube_gdata");
                    Calendar calendario = Calendar.getInstance();
                    postIn.setPi_created(calendario.getTime());
                    postIn.setPi_type(SWBSocialUtil.POST_TYPE_VIDEO);

                    VideoIn videoIn=(VideoIn)postIn;
                    videoIn.setVideo(BASE_VIDEO_URL + videoId);

                     if(socialNetUser == null){//User does not exist                    
                        socialNetUser=SocialNetworkUser.ClassMgr.createSocialNetworkUser(model);//Create a socialNetworkUser
                        socialNetUser.setSnu_id(creatorId);
                        socialNetUser.setSnu_name((creatorName.isEmpty()) ? creatorId : creatorName);
                        socialNetUser.setSnu_SocialNetworkObj(socialNetwork.getSemanticObject());
                        socialNetUser.setUserUrl("https://www.youtube.com/" + creatorId);
                        socialNetUser.setCreated(new Date());
                        socialNetUser.setFollowers(0);
                        socialNetUser.setFriends(0);
                    }else{
                        //System.out.println("YA EXISTE EN EL SISTEMA:" + socialNetUser);
                    }

                    postIn.setPostInSocialNetworkUser(socialNetUser);
                }else{
                    log.error("The post with id :" + postIn.getSocialNetMsgId() + " already exists, making another response");
                }
                
                if(request.getParameter("newSocialTopic").equals("none")){
                    postIn.setSocialTopic(null);
                }else {
                    SemanticObject semObjSocialTopic=SemanticObject.getSemanticObject(request.getParameter("newSocialTopic"));
                    if(semObjSocialTopic!=null)
                    {
                        SocialTopic socialTopic=(SocialTopic)semObjSocialTopic.createGenericInstance();
                        postIn.setSocialTopic(socialTopic);//Asigns socialTipic
                    }
                }
                
                response.setRenderParameter("postUri", postIn.getURI());

            }catch(Exception e){
                log.error("ERROR:", e);
            }
            response.setRenderParameter("suri", socialNetwork.getURI());
            response.setRenderParameter("currentTab", request.getParameter("currentTab"));
            response.setRenderParameter("id", videoId);            
            response.setMode("assignedPost");
        }else if(action.equals("changeSocialTopic"))
        {
            if(request.getParameter("postUri")!=null && request.getParameter("newSocialTopic")!=null){
                SemanticObject semObj=SemanticObject.getSemanticObject(request.getParameter("postUri"));
                Post post=(Post)semObj.createGenericInstance();
                if(request.getParameter("newSocialTopic").equals("none")){
                    post.setSocialTopic(null);
                }else{
                    SemanticObject semObjSocialTopic=SemanticObject.getSemanticObject(request.getParameter("newSocialTopic"));
                    if(semObjSocialTopic!=null){
                        SocialTopic socialTopic=(SocialTopic)semObjSocialTopic.createGenericInstance();
                        post.setSocialTopic(socialTopic);
                    }
                }
            }
            response.setMode("reAssignedPost");
        }else if(action.equals("doUpdateVideo")){
            doUpdateVideo(request);            
            response.setRenderParameter("videoId", request.getParameter("videoId"));
            response.setRenderParameter("suri", request.getParameter("suri"));
            response.setMode("videoUpdated");
        }else if (action.equals("doDeleteVideo")){
            doDeleteVideo(request);
            response.setRenderParameter("videoId", request.getParameter("videoId"));
            response.setRenderParameter("suri", request.getParameter("suri"));
            response.setMode("videoDeleted");
        }else if (action.equals("postMessage") || action.equals("uploadPhoto") || action.equals("uploadVideo")) {
            if (request.getParameter("objUri") != null) {
                PostIn postIn = (PostIn) SemanticObject.getSemanticObject(request.getParameter("objUri")).createGenericInstance();
                SocialTopic stOld = postIn.getSocialTopic();
                ///
                WebSite wsite = WebSite.ClassMgr.getWebSite(request.getParameter("wsite"));
                String socialUri = "";
                int j = 0;
                Enumeration<String> enumParams = request.getParameterNames();
                while (enumParams.hasMoreElements()) {
                    String paramName = enumParams.nextElement();
                    if (paramName.startsWith("http://")) {//get param name starting with http:// -> URIs
                        if (socialUri.trim().length() > 0) {
                            socialUri += "|";
                        }
                        socialUri += paramName;
                        j++;
                    }
                }
                
                ArrayList aSocialNets = new ArrayList();//Social nets where the post will be published
                String[] socialUris = socialUri.split("\\|");  //Dividir valores
                if( j > 0 && wsite != null){
                    for (int i = 0; i < socialUris.length; i++) {
                        String tmp_socialUri = socialUris[i];
                        SemanticObject semObject = SemanticObject.createSemanticObject(tmp_socialUri, wsite.getSemanticModel());
                        SocialNetwork socialNet = (SocialNetwork) semObject.createGenericInstance();
                        //Se agrega la red social de salida al post
                        aSocialNets.add(socialNet);
                    }
                }
                                
                String toPost = request.getParameter("toPost");
                String socialFlow = request.getParameter("socialFlow");
                SocialPFlow socialPFlow = null;
                if (socialFlow != null && socialFlow.trim().length() > 0) {
                    socialPFlow = (SocialPFlow) SemanticObject.createSemanticObject(socialFlow).createGenericInstance();
                    //Revisa si el flujo de publicación soporte el tipo de postOut, de lo contrario, asinga null a spflow, para que no 
                    //asigne flujo al mensaje de salida., Esto también esta validado desde el jsp typeOfContent
                    if ((toPost.equals("msg") && !SocialLoader.getPFlowManager().isManagedByPflow(socialPFlow, Message.sclass))
                            || (toPost.equals("photo") && !SocialLoader.getPFlowManager().isManagedByPflow(socialPFlow, Photo.sclass))
                            || (toPost.equals("video") && !SocialLoader.getPFlowManager().isManagedByPflow(socialPFlow, Video.sclass))) {
                        socialPFlow = null;
                    }
                }

                SWBSocialUtil.PostOutUtil.sendNewPost(postIn, postIn.getSocialTopic(), socialPFlow, aSocialNets, wsite, toPost, request, response);
                response.setMode("commentVideoSent");
            }
        }
    }
    
    private void doDeleteVideo(HttpServletRequest request){
        String videoId = request.getParameter("videoId");
        String objUri = request.getParameter("suri");
        
        if ((videoId == null || videoId.trim().isEmpty()) ||
                (objUri == null || objUri.trim().isEmpty())) {
            YoutubeWall.log.error("Problem updating video information");
            return;
        }
        SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
        Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
        if (!semanticYoutube.validateToken()) {
            YoutubeWall.log.error("Unable to update the access token inside delete Video!");
            return;
        }
        
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("id", videoId);
        String response = null;
        try {
            response = semanticYoutube.getRequest(params, Youtube.API_URL + "/videos", Youtube.USER_AGENT, "DELETE");
        } catch (IOException ioe) {
            YoutubeWall.log.error("Deleting video", ioe);
            response = "Error al ejecutar eliminacion";
        }
        
    }
    
    /**
     * Actualiza los metadatos de un video en Youtube
     * @param request la peticion que genera el cliente cuyo contenido indica los datos a modificar
     */
    private void doUpdateVideo(HttpServletRequest request) {
        
        String videoId = request.getParameter("videoId");
        String objUri = request.getParameter("suri");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String keywords = request.getParameter("keywords");
        String privacy = request.getParameter("privacy");
        JSONObject video = null;
        
        if ((videoId == null || videoId.trim().isEmpty()) || (title == null || title.trim().isEmpty()) ||
                (objUri == null || objUri.trim().isEmpty()) || (description == null || description.trim().isEmpty())
                || (category == null || category.trim().isEmpty())) {
            YoutubeWall.log.error("Problem updating video information, missing fields.");
            return;
        }
        if (keywords == null || keywords.trim().isEmpty()) {
            keywords = "";
        }
        SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
        Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
        if (!semanticYoutube.validateToken()) {
            YoutubeWall.log.error("Unable to update the access token inside update Video!");
            return;
        }
        try {        
            video = new JSONObject();
            video.put("kind", "youtube#video");
            video.put("id", videoId);
            JSONObject snippet = new JSONObject();
            snippet.put("title", title);
            snippet.put("description", description);
            String[] tags = keywords.indexOf(",") > 0 ? keywords.split(",") : keywords.split(" ");
            for (String element : tags) {
                snippet.append("tags", element);
            }
            snippet.put("categoryId", category);
            JSONObject status = new JSONObject();
            status.put("privacyStatus", privacy.equalsIgnoreCase("NOT_LISTED")
                                        ? "unlisted" : privacy.toLowerCase());
            video.put("snippet", snippet);
            video.put("status", status);
        } catch (JSONException jsone) {
            YoutubeWall.log.error("Building JSON Metadata for video update", jsone);
        }
        
        if (!semanticYoutube.updateVideoMetadata(video)) {
            YoutubeWall.log.event("Video update fail - videoId: " + videoId);
        }
    }
    
    /**
     * Agrega una calificacion "like", "dislike" o "none" a un video
     * @param request la peticion que genera el cliente cuyo contenido indica los datos a modificar
     */
    private void doLikeDislike(HttpServletRequest request) {
        
        String action = request.getParameter("action");
        String videoId = request.getParameter("videoId");
        String objUri = request.getParameter("suri");
        
        if ((action == null || action.isEmpty()) || (videoId == null || videoId.isEmpty()) ||
                (objUri == null || objUri.isEmpty())) {
            YoutubeWall.log.error("Problema ejecutando accion Like/Dislike");
            return;
        }

        SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
        Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
        if (!semanticYoutube.validateToken()) {
            YoutubeWall.log.error("Unable to update the access token!");
            return;
        }
        
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("id", videoId);
        params.put("rating", action.toLowerCase());
        try {
            String response = semanticYoutube.postRequest(params,
                Youtube.API_URL + "/videos/rate", Youtube.USER_AGENT, "POST");
        } catch (IOException ioe) {
            YoutubeWall.log.error("Rating a video error", ioe);
        }
    }

    /**
     * Publica un comentario asociado a un video
     * @param request la peticion que genera el cliente cuyo contenido indica los datos a modificar
     */
    private void doCommentVideo(HttpServletRequest request) {
        String videoId = request.getParameter("videoId");
        String channelId = request.getParameter("channelId");
        String objUri = request.getParameter("suri");
        String comment = request.getParameter("comment");
        
        if ((videoId == null || videoId.isEmpty()) || (comment == null || comment.isEmpty()) ||
                (objUri == null || objUri.isEmpty())) {
            YoutubeWall.log.error("Problema ejecutando el posteo del comentario");
            return;
        }
        
        SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
        Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
        if (!semanticYoutube.validateToken()) {
            YoutubeWall.log.error("Unable to update the access token inside post Comment!");
            return;
        }
        
        String urlComment = Youtube.API_URL + "/commentThreads?part=id";
        URL url;
        HttpURLConnection conn = null;
        
        try {
            url = new URL(urlComment);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Host", Youtube.HOST);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + semanticYoutube.getAccessToken());
            
            JSONObject jComment = new JSONObject();
            JSONObject snippet = new JSONObject();
            snippet.put("channelId", channelId);
            JSONObject commSnippet = new JSONObject();
            commSnippet.put("textOriginal", comment);
            JSONObject topLevelComment = new JSONObject();
            topLevelComment.put("snippet", commSnippet);
            snippet.put("topLevelComment", topLevelComment);
            jComment.put("snippet", snippet);
            
            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
            writer.write(jComment.toString().getBytes("UTF-8"));
            writer.flush();
            writer.close();
            BufferedReader readerl = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String commentIdCreated = readerl.readLine();
        } catch (Exception ex) {
            YoutubeWall.log.error(ex);
        }
    }
    
    /**
     * Genera un comentario asociado a un comentario raiz
     * @param request la peticion hecha por el cliente
     */
    private void doCommentComment(HttpServletRequest request) {
        String videoId = request.getParameter("videoId");
        String commentId = request.getParameter("commentId");
        String objUri = request.getParameter("suri");
        String comment = request.getParameter("commentComment");

        if ((videoId == null || videoId.isEmpty()) || (comment == null || comment.isEmpty())
                || (objUri == null || objUri.isEmpty()) || ( commentId == null || commentId.isEmpty())) {
            YoutubeWall.log.error("Problema ejecutando el posteo del comentario hacia un comentario");
            return;
        }

        SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
        Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
        if (!semanticYoutube.validateToken()) {
            YoutubeWall.log.error("Unable to update the access token inside post Comment!");
            return;
        }

        boolean commentCreated = semanticYoutube.commentAComment(commentId, comment);
        if (!commentCreated) {
            System.out.println("No se creo el comentario del comentario!");
        }
    }
    
    /**
     * Despliega la informacion relacionada a un video, los comentarios que tenga asociados y 
     * las operaciones que el usuario en sesion puedE realizar con esta informacion
     * @param request
     * @param response
     * @param paramRequest
     * @param out
     * @param postURI
     * @param video
     * @param userCanDoEverything
     * @param userCanRetopicMsg
     * @param userCanRespondMsg
     * @param userCanRemoveMsg
     * @throws SWBResourceException
     * @throws IOException 
     */
    public static void doPrintVideo(HttpServletRequest request, HttpServletResponse response, 
             SWBParamRequest paramRequest, java.io.Writer out, String postURI, JSONObject video,
             boolean userCanDoEverything, boolean userCanRetopicMsg, boolean userCanRespondMsg,
             boolean userCanRemoveMsg) throws SWBResourceException, IOException {
        
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm a", new Locale("es", "MX"));
        String startIndex = request.getParameter("startIndex"); //para peticiones de comentarios del video
        String objUri = request.getParameter("suri");
        SocialNetwork socialNetwork = (SocialNetwork) SemanticObject.getSemanticObject(objUri).getGenericInstance();
        SWBModel model = WebSite.ClassMgr.getWebSite(socialNetwork.getSemanticObject().getModel().getName());
        SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
        Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();        
        try {
            String videoTitle = !video.isNull("snippet") && !video.getJSONObject("snippet").isNull("title")
                                ? video.getJSONObject("snippet").getString("title") : "";
            String videoId = !video.isNull("id") ? video.getString("id") : "";
            
            out.write("<div id=\"" + semanticYoutube.getId() +"/" + videoId +
                    "\" class=\"timeline timelinefacebook\" dojoType=\"dojox.layout.ContentPane\">");
            //Username and story
            out.write("<div id=\"" + semanticYoutube.getId() + "/" + videoId + "/detail" + "\">");
            out.write("<p>");
            out.write(videoTitle);
            out.write("</p>");
            out.write("<div class=\"timelineimg\">");
            out.write(" <span>");
            String imgPath = "";
            JSONObject snippet = !video.isNull("snippet") ? video.getJSONObject("snippet") : null;
            if (snippet.has("thumbnails")) {
                JSONObject thumbs = snippet.getJSONObject("thumbnails");
                if (thumbs.has("default") && !thumbs.getJSONObject("default").isNull("url")) {
                    imgPath = thumbs.getJSONObject("default").getString("url");
                } else if (thumbs.has("medium") && !thumbs.getJSONObject("medium").isNull("url")) {
                    imgPath = thumbs.getJSONObject("medium").getString("url");
                }
            }

            out.write("      <span id=\"img" + semanticYoutube.getId() + videoId);
            out.write("\" style=\"width: 250px; height: 250px; border: thick #666666; overflow: hidden; position: relative;\">");
            out.write("      <a href=\"#\" onclick=\"showDialog('");
            out.write(paramRequest.getRenderUrl().setMode("displayVideo").
                    setParameter("videoUrl", URLEncoder.encode(Youtube.BASE_VIDEO_URL + videoId, "UTF-8")).toString());
            out.write("','" + videoTitle + "'); return false;\"><img src=\"" + imgPath);
            out.write("\" style=\"position: relative;\" onerror=\"this.src ='" + imgPath);
            out.write("'\" onload=\"imageLoad(this, 'img" + semanticYoutube.getId() + videoId + "');\"/></a>");
            out.write("      </span>");
            out.write(" </span>");
            out.write("<p class=\"imgtitle\">");
            out.write(videoTitle);
            out.write("</p>");
            out.write("<p class =\"imgdesc\">");
            out.write(snippet.isNull("description") ?  "&nbsp;" : snippet.getString("description").replace("\n", "</br>"));
            out.write("</p>");
            out.write("</div>");//End First section
            out.write("</div>");
            out.write("<div class=\"clear\"></div>");//Clear
                
            //With changes of November 2013
            //I cannot longer request the comments of a private video
            boolean isPrivate = false;
            if (!video.isNull("status") && !video.getJSONObject("status").isNull("privacyStatus") &&
                    video.getJSONObject("status").getString("privacyStatus").equalsIgnoreCase("private")) {
                isPrivate = true;
            }
            
            //Comments,start
            String ytComments = "";
            JSONObject videoStats = !video.isNull("statistics") ? video.getJSONObject("statistics") : null;
            long commentCount = videoStats != null && !videoStats.isNull("commentCount")
                                ? videoStats.getLong("commentCount") : 0L; 
            if (!isPrivate && commentCount > 0) {
                HashMap<String, String> paramsComments = new HashMap<String, String>(8);
                paramsComments.put("part", "snippet,replies");
                paramsComments.put("videoId", videoId);
                paramsComments.put("maxResults", "5");
                paramsComments.put("moderationStatus", "published");
                paramsComments.put("textFormat", "html");
                if (startIndex != null && !startIndex.isEmpty()) {
                    paramsComments.put("pageToken", startIndex);
                }
                ytComments = semanticYoutube.getRequest(paramsComments, Youtube.API_URL + "/commentThreads",
                                                        Youtube.USER_AGENT, "GET");
                JSONObject jsonResponse = new JSONObject(ytComments);
                JSONArray arrayComments = null;
                if (!jsonResponse.isNull("items")) {
                    arrayComments = jsonResponse.getJSONArray("items");
                }
                if (arrayComments != null && arrayComments.length() > 0) {
                    out.write("<ul id=\"" + semanticYoutube.getId() + videoId + "/comments\">");
                    for (int c = 0; c < arrayComments.length(); c++) {
                        JSONObject comment = arrayComments.getJSONObject(c);
                        String parentCommentId = comment.getString("id");
                        //Como arrayComments contiene commentThreads, se obtiene de cada uno su topLevelComment (youtube#comment)
                        if (!comment.isNull("snippet") && !comment.getJSONObject("snippet").isNull("topLevelComment")) {
                            JSONObject topLevelComment = comment.getJSONObject("snippet").getJSONObject("topLevelComment");
                            out.write(YoutubeWall.assembleCommentHtml(topLevelComment, paramRequest, objUri, videoId, null));
                        }
                        //Si el comentario tiene comentarios asociados
                        if (!comment.isNull("replies") && !comment.getJSONObject("replies").isNull("comments")) {
                            JSONArray replies = comment.getJSONObject("replies").getJSONArray("comments");
                            for (int i = 0; i < replies.length(); i++) {
                                out.write(YoutubeWall.assembleCommentHtml(replies.getJSONObject(i),
                                        paramRequest, objUri, videoId, parentCommentId));
                            }
                        }
                    }
//                    if (!video.isNull("commentCount") && video.getInt("commentCount") > DEFAULT_VIDEO_COMMENTS &&
//                            totalComments == DEFAULT_VIDEO_COMMENTS) {//Link to get more comments
                    if (!jsonResponse.isNull("nextPageToken") && !jsonResponse.getString("nextPageToken").isEmpty()) {
                        out.write("<li class=\"timelinemore\">");
                        out.write("<label><a href=\"#\" onclick=\"appendHtmlAt('" +
                                paramRequest.getRenderUrl().setMode("getMoreComments").
                                        setParameter("videoId", videoId).
                                        setParameter("startIndex", jsonResponse.getString("nextPageToken")).
                                        setParameter("totalComments", commentCount + "").
                                        setParameter("suri", objUri) +
                                "','" + semanticYoutube.getId() + videoId +
                                "/comments', 'bottom');try{this.parentNode.parentNode.parentNode.removeChild( " +
                                "this.parentNode.parentNode );}catch(noe){}; return false;\"><span>+</span>" +
                                paramRequest.getLocaleString("moreComments") + "</a></label>");
                        out.write("</li>");
                    }
                    out.write("</ul>");
                    out.write("<div class=\"clear\"></div>");
                }
            }
            //Comments

            out.write("<div class=\"timelineresume\" dojoType=\"dijit.layout.ContentPane\">");//timelineresume
            out.write("<span id=\"" + semanticYoutube.getId() + videoId + YoutubeWall.INFORMATION +
                      "\" class=\"inline\" dojoType=\"dojox.layout.ContentPane\">");
            Date date = YoutubeWall.FORMATTER.parse(snippet.getString("publishedAt"));
            out.write(df.format(date) + "&nbsp; ");

            if (videoStats != null && videoStats.has("viewCount")) {
                out.write(paramRequest.getLocaleString("views") + ": " + videoStats.getLong("viewCount"));
            }
            out.write(" <strong><span> " + paramRequest.getLocaleString("likes") + ": </span>");
            if (videoStats != null && videoStats.has("likeCount")) {
                out.write(videoStats.getLong("likeCount") + " ");
            } else {
                out.write("0 ");
            }

            out.write(" " + paramRequest.getLocaleString("dislikes") + ": ");
            if (videoStats != null && videoStats.has("dislikeCount")) {
                out.write(videoStats.getLong("dislikeCount") + " ");
            } else {
                out.write("0 ");
            }

            if (videoStats != null && videoStats.has("favoriteCount")) {
                out.write(" " + paramRequest.getLocaleString("favorites") + ": " + videoStats.getInt("favoriteCount"));
            }

            out.write("</strong>");
            out.write("</span>");

            if (userCanRespondMsg || userCanDoEverything) {
                out.write("  <span class=\"inline\" dojoType=\"dojox.layout.ContentPane\">");
                out.write("    <a class=\"answ\" href=\"#\" title=\"Comentar\" onclick=\"showDialog('" +
                        paramRequest.getRenderUrl().setMode("commentVideo").setParameter("suri", objUri).
                                setParameter("videoId", videoId) + "','Comment to " +
                        videoTitle + "');return false;\"></a>  ");                    
                out.write("  </span>");
            }

            postURI = null;
            PostIn post = PostIn.getPostInbySocialMsgId(model, videoId);
            if (post != null) {
                postURI = post.getURI();
            }
            
            if (userCanRetopicMsg || userCanDoEverything) {
                out.write("   <span class=\"inline\" id=\"" +
                        semanticYoutube.getId() + videoId + YoutubeWall.TOPIC +
                        "\" dojoType=\"dojox.layout.ContentPane\">");
                if (userCanRetopicMsg || userCanDoEverything) {
                    if (postURI != null) {//If post already exists
                        SWBResourceURL clasifybyTopic = paramRequest.getRenderUrl().setMode("doReclassifyTopic").
                                setCallMethod(SWBResourceURL.Call_DIRECT).setParameter("videoId", videoId).
                                setParameter("postUri", postURI).setParameter("suri", objUri);
                        out.write("<a href=\"#\" class=\"clasifica\" title=\"" +
                                paramRequest.getLocaleString("reclassify") + "\" onclick=\"showDialog('" +
                                clasifybyTopic + "','" + paramRequest.getLocaleString("reclassify") +
                                "'); return false;\"></a>");
                    } else {//If posts does not exists 
                        SWBResourceURL clasifybyTopic = paramRequest.getRenderUrl().setMode("doShowTopic").
                                setCallMethod(SWBResourceURL.Call_DIRECT).setParameter("id", videoId).
                                setParameter("postUri", postURI).setParameter("suri", objUri);
                        out.write("<a href=\"#\" class=\"clasifica\" class=\"clasifica\" title=\"" +
                                paramRequest.getLocaleString("classify") + "\" onclick=\"showDialog('" +
                                clasifybyTopic + "','" + paramRequest.getLocaleString("classify") +
                                "'); return false;\"></a>");
                    }
                } else {
                    out.write("&nbsp;");
                }
                out.write("   </span>");
            }
            
            out.write("   <span id=\"" + semanticYoutube.getId() + videoId + YoutubeWall.LIKE +
                      "\" class=\"inline\" dojoType=\"dojox.layout.ContentPane\">");
            out.write("<a href=\"#\" class=\"like\" title=\"" + paramRequest.getLocaleString("like") +
                      "\" onclick=\"try{dojo.byId(this.parentNode).innerHTML = '<img src=" +
                      SWBPlatform.getContextPath() + "/swbadmin/icons/loading.gif>';}catch(noe){} postSocialHtml('" +
                      paramRequest.getActionUrl().setAction("doLike").setParameter("suri", objUri).
                              setParameter("action", "like").setParameter("videoId", videoId) +
                      "','" + semanticYoutube.getId() + videoId + YoutubeWall.INFORMATION + "'); return false;\"></a>");
            out.write("   </span>");

            if (userCanRetopicMsg || userCanRemoveMsg || userCanDoEverything) {
                out.write("   <span id=\"" + semanticYoutube.getId() + videoId +
                        "/edit\" class=\"inline\" dojoType=\"dojox.layout.ContentPane\">");
                SWBResourceURL editVideo = paramRequest.getRenderUrl().setMode("editVideo").
                        setCallMethod(SWBResourceURL.Call_DIRECT).setParameter("videoId", videoId).
                        setParameter("suri", objUri);
                out.write("<a href=\"#\" class=\"editarYoutube\" title=\"" + paramRequest.getLocaleString("edit") +
                        "\" onclick=\"showDialog('" + editVideo + "','" +
                        paramRequest.getLocaleString("edit") + "'); return false;\"></a>");
                out.write("   </span>");
            }
            if (userCanRemoveMsg || userCanDoEverything) {
                out.write("   <span id=\"" + semanticYoutube.getId() + videoId +
                          "/delete\" class=\"inline\" dojoType=\"dojox.layout.ContentPane\">");
                out.write("<a href=\"#\" class=\"eliminarYoutube\" title=\"" +
                        paramRequest.getLocaleString("deleteVideo") + "\"onclick=\"if(confirm('" +
                        paramRequest.getLocaleString("confirmDelete") + "')){ postSocialHtml('" +
                        paramRequest.getActionUrl().setAction("doDeleteVideo").
                                setParameter("suri", objUri).setParameter("videoId", videoId) +
                        "','" + semanticYoutube.getId() + "/" +  videoId + "');} return false;\"></a>");
                out.write("   </span>");
            }

            out.write("</div>");//timelineresume
            out.write("</div>");
        } catch (Exception e) {
            YoutubeWall.log.error("Problema imprimiendo video", e);
        }
    }
    
    /**
     * Crea el HTML a desplegar para un comentario, como parte de una lista de HTML
     * @param comment el objeto youtube#comment en formato JSON con la informacion a mostrar
     * @param paramRequest objeto del cual se obtienen los textos internacionalizados a mostrar
     * @param objUri parametro incluido en los vinculos usados en la interface
     * @param videoId identificador del video asociado con el comentario mostrado.
     *                Se utiliza como parametro en los vinculos utilizados en la interface
     * @param parentCommentId identificador del comentario de primer nivel asociado al comentario a mostrar
     * @return el String que contiene el HTML generado para el elemento de lista con la 
     *         informacion contenida en {@code comment}
     */
    private static String assembleCommentHtml(JSONObject comment, SWBParamRequest paramRequest,
            String objUri, String videoId, String parentCommentId) {

        StringBuilder output = new StringBuilder(256);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm a", new Locale("es", "MX"));
        
        output.append("<li>\n");
        try {
            JSONObject topComSnippet = comment != null && !comment.isNull("snippet")
                    ? comment.getJSONObject("snippet") : null;
            String authorChannelId = topComSnippet != null && !topComSnippet.isNull("authorChannelId")
                                     ? topComSnippet.getJSONObject("authorChannelId").getString("value") : "";
            String authorProfileImageUrl = topComSnippet != null && !topComSnippet.isNull("authorProfileImageUrl")
                                     ? topComSnippet.getString("authorProfileImageUrl") : "";
            String authorName = topComSnippet != null && !topComSnippet.isNull("authorDisplayName")
                                ? topComSnippet.getString("authorDisplayName") : "";
            String userProfileURL = paramRequest.getRenderUrl().setMode("showUserProfile").
                                    setParameter("id", authorChannelId).setParameter("suri", objUri).toString();

            output.append("<a href=\"#\" title=\"");
            output.append(paramRequest.getLocaleString("viewProfile"));
            output.append("\" onclick=\"showDialog('");
            output.append(userProfileURL);
            output.append("','");
            output.append(paramRequest.getLocaleString("viewProfile"));
            output.append("'); return false;\"><img src=\"");
            output.append(authorProfileImageUrl);
            output.append("\" width=\"50\" height=\"50\"/></a>\n");
            output.append("<p>\n");
            output.append("<a href=\"#\" title=\"");
            output.append(paramRequest.getLocaleString("viewProfile"));
            output.append("\" onclick=\"showDialog('");
            output.append(userProfileURL);
            output.append("','");
            output.append(paramRequest.getLocaleString("viewProfile"));
            output.append("'); return false;\">");
            output.append(authorName);
            output.append("</a>:\n");
            if (topComSnippet != null && !topComSnippet.isNull("textDisplay")) {
                output.append(topComSnippet.getString("textDisplay").replace("\n", "</br>"));
            }
            output.append("</p>\n");
            output.append("<p class=\"timelinedate\">\n");
            //out.write("<span dojoType=\"dojox.layout.ContentPane\">");
            output.append("<span class=\"inline\">\n");

            if (topComSnippet != null && !topComSnippet.isNull("publishedAt")) {
                Date date = YoutubeWall.FORMATTER.parse(topComSnippet.getString("publishedAt"));
                output.append(df.format(date));
            }
            output.append("&nbsp; </span>\n");
            String comentarioId = parentCommentId != null ? parentCommentId : comment.getString("id");
            output.append("   <span class=\"inline\">\n");
            output.append(" <a href=\"\" onclick=\"showDialog('");
            output.append(paramRequest.getRenderUrl().setMode("commentComment").
                            setParameter("suri", objUri).
                            setParameter("videoId", videoId).
                            setParameter("commentId", comentarioId));
            output.append("','Comment to ");
            if (topComSnippet != null && !topComSnippet.isNull("textDisplay")) {
                output.append(topComSnippet.getString("textDisplay").replace("\n", "</br>"));
            }
            output.append("');return false;\">Comentar</a>\n");
            output.append("   </span>\n");
            output.append("</p>\n");
        } catch (org.json.JSONException jsone) {
            YoutubeWall.log.error("Assembling text for comments", jsone);
        } catch (java.text.ParseException pe) {
            YoutubeWall.log.error("Assembling text for comments", pe);
        } catch (SWBResourceException swbre) {
            YoutubeWall.log.error("Assembling text for comments", swbre);
        }
        output.append("</li>\n");
        return output.toString();
    }
    
    public void doGetMoreVideos(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        try {
            PrintWriter out = response.getWriter();
            //maxVideoId contiene la pagina de los resultados que debe obtenerse valor de nextPageToken en una peticion a Youtube
            String maxVideoId = request.getParameter("maxVideoId");
            String uploadsList = request.getParameter("uploadsList");
            int videosInChannel = 0;
            JSONArray videosArray = null;
            HashMap<String, String> params = new HashMap<String, String>(4);
            params.put("part", "id,contentDetails");
            params.put("mine", "true");
            params.put("maxResults", "2");
            String objUri = (String) request.getParameter("suri");
            
            SemanticObject semanticObject = SemanticObject.createSemanticObject(objUri);
            Youtube semanticYoutube = (Youtube) semanticObject.createGenericInstance();
            
            if (!semanticYoutube.validateToken()) {//If was unable to refresh the token
                YoutubeWall.log.error("Unable to refresh the access token!");
                return;
            }
            if (uploadsList == null || (uploadsList != null &&
                    (uploadsList.isEmpty() || uploadsList.equals("null")) )) {
                //Se obtiene la lista de canales del usuario
                String ytResponse = semanticYoutube.getRequest(params, Youtube.API_URL + "/channels",
                                                               Youtube.USER_AGENT, "GET");
                JSONObject channelsResponse = new JSONObject(ytResponse);
                JSONArray channelLists = null;
                if (!channelsResponse.isNull("items")) {
                    channelLists = channelsResponse.getJSONArray("items");
                }
                if (channelLists != null && channelLists.length() > 0) {
                    JSONObject channel = channelLists.getJSONObject(0);
                    if (!channel.isNull("contentDetails") &&
                            !channel.getJSONObject("contentDetails").isNull("relatedPlaylists")) {
                        JSONObject relatedLists = channel.getJSONObject("contentDetails").getJSONObject("relatedPlaylists");
                        uploadsList = !relatedLists.isNull(("uploads"))
                                      ? relatedLists.getString("uploads") : null;
                    }
                }
            }
            //se obtiene la lista de videos cargados
            if (uploadsList != null) {
                HashMap<String, String> videoParams = new HashMap<String, String>(4);
                videoParams.put("part", "id,snippet");
                videoParams.put("playlistId", uploadsList);
                videoParams.put("maxResults", "10");
                if (maxVideoId != null && !maxVideoId.isEmpty()) {
                    params.put("pageToken", maxVideoId);
                }

                //realiza la peticion de la lista uploads
                String videosResponse = semanticYoutube.getRequest(videoParams,
                                        Youtube.API_URL + "/playlistItems", Youtube.USER_AGENT, "GET");
                JSONObject videosList = new JSONObject(videosResponse);
                if (!videosList.isNull("items")) {
                    videosArray = videosList.getJSONArray("items");
                }
                if (!videosList.isNull("pageInfo") &&
                        !videosList.getJSONObject("pageInfo").isNull("totalResults")) {
                    videosInChannel = videosList.getJSONObject("pageInfo").getInt("totalResults");
                }
                if (!videosList.isNull("nextPageToken")) {
                    maxVideoId = videosList.getString("nextPageToken");
                }
            }
            
            if (videosArray != null && videosArray.length() > 0) {
                String postURI = null;
                org.semanticwb.model.User user = paramRequest.getUser();
                HashMap<String, SemanticProperty> mapa = new HashMap<String, SemanticProperty>();
                Iterator<SemanticProperty> list = org.semanticwb.SWBPlatform.getSemanticMgr().
                        getVocabulary().getSemanticClass(
                                "http://www.semanticwebbuilder.org/swb4/social#SocialUserExtAttributes").listProperties();
                while (list.hasNext()) {
                    SemanticProperty sp = list.next();
                    mapa.put(sp.getName(),sp);
                }
                boolean userCanRetopicMsg = ((Boolean) user.getExtendedAttribute(
                        mapa.get("userCanReTopicMsg"))).booleanValue();
                boolean userCanRespondMsg = ((Boolean) user.getExtendedAttribute(
                        mapa.get("userCanRespondMsg"))).booleanValue();
                boolean userCanRemoveMsg = ((Boolean) user.getExtendedAttribute(
                        mapa.get("userCanRemoveMsg"))).booleanValue();
                UserGroup userSuperAdminGrp = SWBContext.getAdminWebSite().
                        getUserRepository().getUserGroup("su");
                //THE INFO OF THE USER SHOULD BE DISPLAYED AT TOP
                //int totalVideos = 0;

                for (int j = 0; j < videosArray.length(); j++ ) {
                    YoutubeWall.doPrintVideo(request, response, paramRequest, out,
                            postURI, videosArray.getJSONObject(j),
                            user.hasUserGroup(userSuperAdminGrp),
                            userCanRetopicMsg, userCanRespondMsg,
                            userCanRemoveMsg);
                    //totalVideos++;
                }

                //antes:totalVideos + Integer.parseInt(maxVideoId) < videosInChannel
                if (maxVideoId != null && !maxVideoId.isEmpty()) {
                    out.write("<div align=\"center\">");
                    out.write("<label id=\"" + objUri + "/moreVideosLabel\">");
                    out.write("<a href=\"#\" onclick=\"appendHtmlAt('");
                    out.write(paramRequest.getRenderUrl().setMode("getMoreVideos").
                            setParameter("maxVideoId", maxVideoId).
                            setParameter("playlistId", uploadsList).
                            setParameter("suri", objUri).toString());
                    out.write("','" + objUri + "/getMoreVideos', 'bottom');");
                    out.write("try{this.parentNode.parentNode.parentNode.removeChild( this.parentNode.parentNode );");
                    out.write("}catch(noe){}; return false;\">");
                    out.write("More Videos</a></label>");
                    out.write("</div>");
                }
            }
        } catch (Exception e) {
            YoutubeWall.log.error("Problem getting more videos", e);
        }
    }

    public static CharSequence delimit(Collection<Map.Entry<String, String>> entries,
            String delimiter, String equals, boolean doEncode)
            throws UnsupportedEncodingException {

        if (entries == null || entries.isEmpty()) {
            return null;
        }
        StringBuilder buffer
                = new StringBuilder(64);
	boolean notFirst = false;
        for (Map.Entry<String, String> entry : entries ) {
            if (notFirst) {
                buffer.append(delimiter);
            } else {
                notFirst = true;
            }
            CharSequence value = entry.getValue();
            buffer.append(entry.getKey());
            buffer.append(equals);
            buffer.append(doEncode ? encode(value) : value);
        }
        return buffer;
    }
    
    /**
     * Codifica el valor de {@code target} de acuerdo al c&oacute;digo de caracteres UTF-8
     * @param target representa el texto a codificar
     * @return un {@code String} que representa el valor de {@code target} de acuerdo al c&oacute;digo de caracteres UTF-8
     * @throws UnsupportedEncodingException en caso de ocurrir algun problema en la codificaci&oacute;n a UTF-8
     */
    private static String encode(CharSequence target) throws UnsupportedEncodingException {

        String result = "";
        if (target != null) {
            result = target.toString();
            result = URLEncoder.encode(result, "UTF8");
        }
        return result;
    }
    
    public static void close( Closeable c ) {
        if ( c != null ) {
            try {
                c.close();
            }
            catch ( IOException ex ) {             
            }
        }
    }
    
    public static String humanFriendlyDate(Date created, SWBParamRequest paramRequest){
        Date today = new Date();
        Long duration = today.getTime() - created.getTime();

        int second = 1000;
        int minute = second * 60;
        int hour = minute * 60;
        int day = hour * 24;
        String date = "";
        try{
            if (duration < second * 7) {//Less than 7 seconds
                date = paramRequest.getLocaleString("rightNow");
            }else if (duration < minute) {
                int n = (int) Math.floor(duration / second);
                date = n + " " + paramRequest.getLocaleString("secondsAgo");
            }else if (duration < minute * 2) {//Less than 2 minutes
                date = paramRequest.getLocaleString("about") + " 1 " + paramRequest.getLocaleString("minuteAgo");
            }else if (duration < hour) {
                int n = (int) Math.floor(duration / minute);
                date = n + " " + paramRequest.getLocaleString("minutesAgo");
            }else if (duration < hour * 2) {//Less than 1 hour
                date = paramRequest.getLocaleString("about") + " 1 " + paramRequest.getLocaleString("hourAgo");
            }else if (duration < day) {
                int n = (int) Math.floor(duration / hour);
                date = n + " " + paramRequest.getLocaleString("hoursAgo");
            }else  if (duration > day && duration < day * 2) {
                date = paramRequest.getLocaleString("yesterday");
            }else{
                int n = (int) Math.floor(duration / day);
                date = n + " " + paramRequest.getLocaleString("daysAgo");
            }
        }catch(Exception e){
            log.error("Problem found computing time of post. ", e);
        }        
        return date;
    }
    
    
    public String getFullVideoFromId(String id, Youtube socialNet) {
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("part", "snippet,status");
        params.put("id", id);
    
        String response = null;
        try {
            response = socialNet.getRequest(params, Youtube.API_URL + "/videos",
                    Youtube.USER_AGENT, "GET");
        }catch (Exception e) {
            YoutubeWall.log.error("Error getting video information", e);
        }
        return response;
    }

    public void doCreatePost(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        response.setContentType("text/html; charset=ISO-8859-1");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        RequestDispatcher rd = request.getRequestDispatcher(SWBPlatform.getContextPath() +
                "/work/models/" + paramRequest.getWebPage().getWebSiteId() + "/jsp/post/typeOfContent.jsp");
        request.setAttribute("contentType", request.getParameter("valor"));
        request.setAttribute("wsite", request.getParameter("wsite"));
        request.setAttribute("objUri", request.getParameter("objUri"));
        request.setAttribute("paramRequest", paramRequest);

        try {
            rd.include(request, response);
        } catch (ServletException ex) {
            YoutubeWall.log.error("Error al enviar los datos a typeOfContent.jsp " + ex.getMessage());
        }
    }
}
