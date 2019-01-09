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
 
package org.semanticwb.social.admin.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.semanticwb.Logger;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBPortal;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.User;
import org.semanticwb.model.WebSite;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.social.Facebook;
import org.semanticwb.social.Google;
import org.semanticwb.social.Instagram;
import org.semanticwb.social.SocialNetwork;
import org.semanticwb.social.SocialSite;
import org.semanticwb.social.Tumblr;
import org.semanticwb.social.Twitter;
import org.semanticwb.social.Youtube;

/**
 *
 * @author carlos.ramos
 * @modified by Francisco.Jimenez
 */
public class SocialWebResource extends GenericAdmResource
{
    public static Logger log = SWBUtils.getLogger(SocialWebResource.class);
    
    public static final String ATTR_THIS = "this";
    public static final String ATTR_PARAMREQUEST = "paramRequest";
    public static final String ATTR_AXN = "action";
    public static final String ATTR_OBJURI = "objUri";
    public static final String ATTR_BRAND = "wsite";
    public static final String ATTR_TREEITEM = "treeItem";
    
    public static final String OAUTH_MODE = "oauth";
    public static final String RELOAD_TAB = "relTab";
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        if(SocialWebResource.OAUTH_MODE.equals(paramRequest.getMode())) {
            doAuthenticate(request, response, paramRequest);
        }else if(SocialWebResource.RELOAD_TAB.equals(paramRequest.getMode())){
            doReloadTab(request, response, paramRequest);
        }else {
            super.processRequest(request, response, paramRequest);
        }
    }

    public void doReloadTab(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramsRequest) throws SWBResourceException, IOException {
        PrintWriter out = response.getWriter();
        
        SocialNetwork socialNetwork;
        String objUri = URLDecoder.decode(request.getParameter("suri")); //uri of socialNetwork        
        socialNetwork = (SocialNetwork)SemanticObject.createSemanticObject(objUri).getGenericInstance();
        if(socialNetwork.isSn_authenticated()){
            out.println("true");
        }else{
            out.println("false");
        }
    }
    
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        PrintWriter out = response.getWriter();
        User user = paramRequest.getUser();
        SocialNetwork socialNetwork;
        String objUri = request.getParameter("suri"); //uri of socialNetwork
        boolean isFacebookNet = false;
        boolean requestPublishPerm = false;
        boolean isGoogleNet = false;
        
        try {
            socialNetwork = (SocialNetwork) SemanticObject.createSemanticObject(objUri).getGenericInstance();
            boolean validConfiguration = false;
            if (socialNetwork instanceof Twitter) {//for twitter nets
                validConfiguration = isValidConfiguration(socialNetwork,
                        SWBPortal.getEnv("swbsocial/twitterAppKey"),
                        SWBPortal.getEnv("swbsocial/twitterSecretKey"));
            } else if (socialNetwork instanceof Facebook) {//for facebook nets
                validConfiguration = isValidConfiguration(socialNetwork,
                        SWBPortal.getEnv("swbsocial/facebookAppKey"),
                        SWBPortal.getEnv("swbsocial/facebookSecretKey"));
                isFacebookNet = true;
            } else if (socialNetwork instanceof Youtube) {//for youtube nets
                Youtube youtube = (Youtube)socialNetwork;
                validConfiguration = isValidConfiguration(socialNetwork,
                        SWBPortal.getEnv("swbsocial/youtubeAppKey"),
                        SWBPortal.getEnv("swbsocial/youtubeSecretKey"));
                //Valid appkey and appsecret - validate
                if (youtube.getDeveloperKey() == null || youtube.getDeveloperKey().isEmpty()) {
                    if (SWBPortal.getEnv("swbsocial/youtubeDeveloperKey") == null) {
                        validConfiguration = false;
                    } else {
                        youtube.setDeveloperKey(SWBPortal.getEnv("swbsocial/youtubeDeveloperKey"));
                    }
                }
            } if (socialNetwork instanceof Instagram) {//for instagram nets
                socialNetwork.setAppKey(null);
                socialNetwork.setSecretKey(null);
                validConfiguration = isValidConfiguration(socialNetwork,
                        SWBPortal.getEnv("swbsocial/instagramAppKey"),
                        SWBPortal.getEnv("swbsocial/instagramSecretKey"));
            } else if (socialNetwork instanceof Google) {//for Google+ nets
                validConfiguration = isValidConfiguration(socialNetwork,
                        SWBPortal.getEnv("swbsocial/googleAppKey"),
                        SWBPortal.getEnv("swbsocial/googleSecretKey"));
                isGoogleNet = true;
            }else if(socialNetwork instanceof Tumblr ){//for Tumblr nets
                validConfiguration = isValidConfiguration(socialNetwork, SWBPortal.getEnv("swbsocial/tumblrAppKey"), SWBPortal.getEnv("swbsocial/tumblrSecretKey"));
            }

            if (validConfiguration == false) {
                out.println("<div id=\"configuracion_redes\">");
                out.println("<div id=\"autenticacion\">");
                out.println("<p>No han sido configuradas la llave de la aplicación o la llave secreta ");
                out.println((socialNetwork instanceof Youtube) ? "o la llave de desarrollador." : "");
                out.println(".</p></div>");
                out.println("</div>");
                return;
            }
            
            /*if(socialNetwork.getAppKey() == null || socialNetwork.getSecretKey() == null){
                if(socialNetwork instanceof Twitter){
                    TwitterGC tw = wsite.getAdm_twittergc();
                    //System.out.println("tw:" + tw.getAppKey());
                    //System.out.println("tw:" + tw.getSecretKey());
                }else if(socialNetwork instanceof Facebook){
                    FacebookGC fb = wsite.getAdm_facebookgc();
                    //System.out.println("fb:" + fb.getAppKey());
                    //System.out.println("fb:" + fb.getSecretKey());
                }else if(socialNetwork instanceof Youtube){
                    YoutubeGC yt = wsite.getAdm_youtubegc();
                    //System.out.println("yt:" + yt.getAppKey());
                    //System.out.println("yt:" + yt.getSecretKey());
                    //System.out.println("yt:" + yt.getDeveloperKey());
                }
                out.println("No han sido configuradas la llave de la aplicación o la llave secreta");
                //System.out.println("NULL SOCIAL NET PARAMS");
                return;
            }else if(socialNetwork.getAppKey().isEmpty() || socialNetwork.getSecretKey().isEmpty()){
                out.println("No han sido configuradas la llave de la aplicación o la llave secreta");
                //System.out.println("EMPTY SOCIAL NET PARAMS");
                return;
            }*/
        } catch (Exception ex) {
            socialNetwork = null;
            //System.out.println("No valid value for current social Network");
            SocialWebResource.log.error("", ex);
            return;
        }
        
        if (user.isSigned()) {
            if (socialNetwork.isSn_authenticated()) {
                out.println("<form id=\"authNet/" + socialNetwork.getEncodedURI() +
                        "\" action=\"" + paramRequest.getRenderUrl().setParameter("suri", objUri) +
                        "\" method=\"post\" onsubmit=\"try{document.getElementById('csLoading" +
                        socialNetwork.getEncodedURI() +
                        "').style.display='inline';}catch(noe){}; setTimeout(function(){submitForm('authNet/" +
                        socialNetwork.getEncodedURI() + "')},1000); return false;\">" );
                out.println("<div id=\"configuracion_redes\">");
                out.println("<div id=\"autenticacion\">");
                out.println("<p>" + paramRequest.getLocaleString("authenticated") + "</p>");
                String permission2Add = "";
                boolean retryAskPerm = false;

                if (isFacebookNet) {
                    String checkPubPerm = null;
                    if (!((Facebook) socialNetwork).isIsFanPage()) {
                        permission2Add = "manage_pages,publish_pages";
                        checkPubPerm = ((Facebook) socialNetwork).hasPermissions(permission2Add);
                    } else {
                        permission2Add = "manage_pages,publish_pages";
                        checkPubPerm = ((Facebook) socialNetwork).hasPermissions(permission2Add);
                    }
                    if (!checkPubPerm.equalsIgnoreCase("true")) {
                        requestPublishPerm = true;
                        out.println("<p style=\"background-color: red;\">Aunque sin permisos de publicaci&oacute;n;");
                        out.println("por favor, pulsa el bot&oacute;n de abajo y acepta que esta aplicaci&oacute;n");
                        out.println(" publique en tu nombre a fin de que tengas una mejor experiencia con Facebook.</p>");
                        //System.out.println("  ++  ++  ++  ++ checkPubPerm: " + permission2Add + " - " + checkPubPerm);
                        if (checkPubPerm.equals("declined") || checkPubPerm.equals("false")) {
                            retryAskPerm = true;
                        }
                    }
                } else if (isGoogleNet) {
                    //TODO: refrescar token para cuentas de Google+
                }
                    
                out.println("</div>");
                if (isFacebookNet && ((Facebook) socialNetwork).isIsFanPage()) {//for facebook nets
                    //If the social network is a fan page DO NOT authenticate manually
                } else {
                    SWBResourceURL oAuthUrl = paramRequest.getRenderUrl();
                    oAuthUrl.setMode(SocialWebResource.OAUTH_MODE).
                            setParameter("suri", objUri).
                            setParameter("wsid", socialNetwork.getSemanticObject().getModel().getName()).
                            setParameter("fromDoView", "true");
                    if (requestPublishPerm) {
                        oAuthUrl.setParameter("permission", permission2Add);
                    }
                    if (retryAskPerm) {
                        oAuthUrl.setParameter("retry", "true");
                    }
                    out.println("<div id=\"refrescar_cred\">");
                    /*out.println("   <form type=\"dijit.form.Form\" id=\"authenticate/" + objUri + "\" action=\"" +  paramRequest.getRenderUrl().setMode(OAUTH_MODE) + "\" method=\"post\" onsubmit=\"submitForm('authenticate/" + objUri +  "'); return false;\">");
                    out.println("       <input type=\"hidden\"  name=\"suri\" value=\"" + objUri +"\">");
                    out.println("       <input type=\"hidden\"  name=\"wsid\" value=\"" + socialNetwork.getSemanticObject().getModel().getName()+"\">");
                    out.println("       <input type=\"hidden\"  name=\"fromDoView\" value=\"true\">");
                    out.println("       <a href=\"#\" onclick=\"submitForm('authenticate/" + objUri +  "'); return false;\" title=\"" + paramRequest.getLocaleString("refreshCredentials") +"\"><span>" + paramRequest.getLocaleString("refreshCredentials") + "</span></a>");
                    out.println("   </form>");*/
                    out.println("<a href=\"#\" onclick=\"myFunction('" + oAuthUrl.toString() +
                            "'); return false;\"><span>" + paramRequest.getLocaleString("refreshCredentials") +
                            "</span></a>");
                    out.println("</div>");
                    out.println("</div>");
                }
                out.println("</form>");
                out.println("<div align=\"center\"><span id=\"csLoading" + socialNetwork.getEncodedURI() + "\" style=\"width: 100px; display: none\" align=\"center\"><img src=\"" +SWBPlatform.getContextPath() + "/swbadmin/images/loading.gif\"/></span></div>");
                /*
                ////System.out.println("Ya esta autenticada, puede refrescar tokens");
                out.println("<div class=\"swbform\">");
                out.println("<table width=\"100%\" border=\"0px\">");            
                out.println("   <tr>");
                out.println("       <td style=\"text-align: center;\"><h3>" + paramRequest.getLocaleString("authenticated") + "</h3></td>");
                out.println("   </tr>");
                out.println("</table>");
                out.println("</div>");
                
                out.println("<div class=\"swbform\">");
                out.println("<table width=\"100%\" border=\"0px\">");            
                out.println("   <tr>");
                out.println("       <td style=\"text-align: center;\"><h3>" + paramRequest.getLocaleString("refreshCredentialsMsg") +"</h3></td>");
                out.println("   </tr>");
                out.println("   <tr>");
                out.println("       <td style=\"text-align: center;\">");
                out.println("   <form type=\"dijit.form.Form\" id=\"authenticate/" + objUri + "\" action=\"" +  paramRequest.getRenderUrl().setMode(OAUTH_MODE) + "\" method=\"post\" onsubmit=\"submitForm('authenticate/" + objUri +  "'); return false;\">");
                out.println("       <input type=\"hidden\"  name=\"suri\" value=\"" + objUri +"\">");
                out.println("       <input type=\"hidden\"  name=\"wsid\" value=\"" + socialNetwork.getSemanticObject().getModel().getName()+"\">");
                out.println("       <input type=\"hidden\"  name=\"fromDoView\" value=\"true\">");
                out.println("       <button dojoType=\"dijit.form.Button\" type=\"submit\">" + paramRequest.getLocaleString("refreshCredentials") + "</button>");
                out.println("   </form>");
                out.println("       </td>");
                out.println("   </tr>");
                out.println("</table>");
                out.println("</div>");
                */
            } else if (!socialNetwork.isSn_authenticated()) {
                ////System.out.println("No esta autenticada");
                out.println("<form id=\"authNet/" + socialNetwork.getEncodedURI() + "\" action=\"" + paramRequest.getRenderUrl().setParameter("suri", objUri)+ "\" method=\"post\" onsubmit=\"try{document.getElementById('csLoading" + socialNetwork.getEncodedURI() + "').style.display='inline';}catch(noe){}; setTimeout(function(){submitForm('authNet/" + socialNetwork.getEncodedURI() + "')},1000); return false;\">" );
                out.println("<div id=\"configuracion_redes\">");
                out.println("<div id=\"autenticacion\">");
                out.println("<p>      La cuenta aún no está autenticada</p>");
                out.println("</div>");
                out.println("<div id=\"autenticar_cuenta\">");
                /*out.println("   <form type=\"dijit.form.Form\" id=\"reauthenticate/" + objUri + "\" action=\"" +  paramRequest.getRenderUrl().setMode(OAUTH_MODE) + "\" method=\"post\" onsubmit=\"submitForm('reauthenticate/" + objUri + "'); return false;\">");
                out.println("       <input type=\"hidden\"  name=\"suri\" value=\"" + objUri +"\">");
                out.println("       <input type=\"hidden\"  name=\"wsid\" value=\"" + socialNetwork.getSemanticObject().getModel().getName()+"\">");
                out.println("       <input type=\"hidden\"  name=\"fromDoView\" value=\"true\">");
                //out.println("       <button dojoType=\"dijit.form.Button\" type=\"submit\">" + paramRequest.getLocaleString("lblAuthentic") + "</button>");
                out.println("   </form>");
                out.println("<a href=\"#\" onclick=\"submitForm('reauthenticate/" + objUri +  "'); return false;\" title=\"" + paramRequest.getLocaleString("lblAuthentic") +"\"><span>" + paramRequest.getLocaleString("lblAuthentic") + "</span></a>");
                */
                //out.println("<a href=\"" + paramRequest.getRenderUrl().setMode(OAUTH_MODE).setParameter("suri", objUri).setParameter("wsid", socialNetwork.getSemanticObject().getModel().getName()).setParameter("fromDoView", "true")+ "\" target=\"_new\" onclick=\"setInterval(postSocialHtml('" + paramRequest.getRenderUrl().setMode(RELOAD_TAB).setCallMethod(SWBResourceURL.Call_DIRECT).setParameter("suri", socialNetwork.getEncodedURI()) + "','resp/" + objUri + "'),1000)\"><span>Autenticar</span></a>");
                //out.println("<a href=\"" + paramRequest.getRenderUrl().setMode(OAUTH_MODE).setParameter("suri", objUri).setParameter("wsid", socialNetwork.getSemanticObject().getModel().getName()).setParameter("fromDoView", "true")+ "\" target=\"_new\" onclick=\"var intervalValue = setInterval(function () {postSocialHtml('" + paramRequest.getRenderUrl().setMode(RELOAD_TAB).setCallMethod(SWBResourceURL.Call_DIRECT).setParameter("suri", socialNetwork.getEncodedURI()) + "','resp/" + objUri + "')},3000); console.log('Interval:' + intervalValue);\"><span>Autenticar</span></a>");
                out.println("<a href=\"#\" onclick=\"myFunction('" + paramRequest.getRenderUrl().setMode(SocialWebResource.OAUTH_MODE).setParameter("suri", objUri).setParameter("wsid", socialNetwork.getSemanticObject().getModel().getName()).setParameter("fromDoView", "true") + "'); return false;\"><span>" + paramRequest.getLocaleString("lblAuthentic") + "</span></a>");
                out.println("</div>");
                out.println("</div>");
                out.println("</form>");
                out.println("<div align=\"center\"><span id=\"csLoading" + socialNetwork.getEncodedURI() + "\" style=\"width: 100px; display: none\" align=\"center\"><img src=\"" +SWBPlatform.getContextPath() + "/swbadmin/images/loading.gif\"/></span></div>");
                /*out.println("<div class=\"swbform\">");
                out.println("<table width=\"100%\" border=\"0px\">");            
                out.println("   <tr>");
                out.println("       <td style=\"text-align: center;\"><h3>La cuenta aún no está autenticada!</h3></td>");
                out.println("   </tr>");
                out.println("   <tr>");
                out.println("       <td style=\"text-align: center;\">");
                out.println("   <form type=\"dijit.form.Form\" id=\"reauthenticate/" + objUri + "\" action=\"" +  paramRequest.getRenderUrl().setMode(OAUTH_MODE) + "\" method=\"post\" onsubmit=\"submitForm('reauthenticate/" + objUri + "'); return false;\">");
                out.println("       <input type=\"hidden\"  name=\"suri\" value=\"" + objUri +"\">");
                out.println("       <input type=\"hidden\"  name=\"wsid\" value=\"" + socialNetwork.getSemanticObject().getModel().getName()+"\">");
                out.println("       <input type=\"hidden\"  name=\"fromDoView\" value=\"true\">");
                out.println("       <button dojoType=\"dijit.form.Button\" type=\"submit\">" + paramRequest.getLocaleString("lblAuthentic") + "</button>");
                out.println("   </form>");
                out.println("       </td>");
                out.println("   </tr>");
                out.println("</table>");
                out.println("</div>");*/
            }
        } else {
            out.println("<h3>Usuario no autorizado. Consulte a su administrador</h3>");
        }
    }
    
    public void doAuthenticate(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        ////System.out.println("**************** ENTRANDO A DO AUTHENTICATE*******************");
        ////System.out.println("\t\tsuri:" + request.getParameter("suri"));
        ////System.out.println("\t\twsid:" + request.getParameter("wsid"));
        ////System.out.println("\t\tParamX:" + request.getParameter("paramX"));
        
        String fromDoView = (String) request.getParameter("fromDoView");
        HttpSession session = request.getSession(true);
        
        if (fromDoView != null && fromDoView.equals("true")) {//If the call is from the doView method clear session var affected
            session.removeAttribute("sw");
        }
        WebSite wsite = null;
        String suri = request.getParameter("suri");
        
        if (WebSite.ClassMgr.getWebSite(request.getParameter("wsid")) instanceof WebSite) {
            wsite = WebSite.ClassMgr.getWebSite(request.getParameter("wsid"));
        } else if (WebSite.ClassMgr.getWebSite(request.getParameter("wsid")) instanceof SocialSite) {
            wsite = (SocialSite) WebSite.ClassMgr.getWebSite(request.getParameter("wsid"));
        }
        
        ////System.out.println("\t\tsw:" + session.getAttribute("sw") );
        if (session.getAttribute("sw") == null) {
            ////System.out.println("\n\nLa primera vez que entra SW es null");
            ////System.out.println("SW:" + session.getAttribute("sw") );
            
            SocialNetwork socialNetwork = (SocialNetwork) wsite.getSemanticObject().getModel().getGenericObject(suri);

            session.setAttribute("sw", socialNetwork);
            ////System.out.println("Ahora ya tiene un valor:" +  socialNetwork);
            socialNetwork.authenticate(request, response, paramRequest);
            //System.out.println("Y va a autenticar");
        } else {
            ////System.out.println("\n\nLa segunda vez que entra SW ya no es null");
            SocialNetwork socialNetwork = (SocialNetwork)session.getAttribute("sw");
            session.removeAttribute("sw");
            //objUri = socialNetwork.getURI();
            ////System.out.println("Y contiene el siguiente valor:" +  socialNetwork);
            //if(!socialNetwork.isSn_authenticated()) {//removed to allow reauthentication
                socialNetwork.authenticate(request, response, paramRequest);
                ////System.out.println("Y fue a autenticar, DE NUEVO y entró al else.");
            //}                        
        }        
    }
    
    @Override
    public void doEdit(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException
    {
//System.out.println("********************   doEdit.");
        
        final String basePath = "/work/models/" + paramRequest.getWebPage().getWebSite().getId() + "/admin/jsp/components/" + this.getClass().getSimpleName() + "/";
//System.out.println(" recuperando socialNetwork....");
        String objUri = (String)request.getAttribute("objUri");
        SocialNetwork socialNetwork;
        try {
            socialNetwork = (SocialNetwork)SemanticObject.getSemanticObject(objUri).getGenericInstance();
            //System.out.println(" 1.socialNetwork="+socialNetwork.getId());
            String title = request.getParameter("title");
            String desc = request.getParameter("desc");
            String appId = request.getParameter("appId");
            String sk = request.getParameter("sk");
            if(title!=null && !title.isEmpty()) {
                socialNetwork.setTitle(title);
            }
            if(desc!=null && !desc.isEmpty()) {
                socialNetwork.setDescription(desc);
            }
            if(!socialNetwork.isSn_authenticated() && appId!=null && sk!=null && !appId.isEmpty() && !sk.isEmpty())
            {
                socialNetwork.setAppKey(appId);
                socialNetwork.setSecretKey(sk);
                HttpSession session = request.getSession(true);
                session.setAttribute("objUri", objUri);
                socialNetwork.authenticate(request, response, paramRequest);
            }
        }catch(Exception e) {
            HttpSession session = request.getSession(true);
            objUri = (String)session.getAttribute("objUri");
            try {
                socialNetwork = (SocialNetwork)SemanticObject.getSemanticObject(objUri).getGenericInstance();
//System.out.println(" 2.socialNetwork="+socialNetwork.getId());
                if(!socialNetwork.isSn_authenticated()) {
                    socialNetwork.authenticate(request, response, paramRequest);
                }
            }catch(Exception ex) {
                socialNetwork = null;
            }
        }
        
        RequestDispatcher dis = null;
        dis = request.getRequestDispatcher(basePath+"/edit.jsp");
        try
        {
            request.setAttribute(ATTR_THIS, this);
            request.setAttribute(ATTR_PARAMREQUEST, paramRequest);
            request.setAttribute("objUri", objUri);
            dis.include(request, response);
        }catch (Exception e) {
            log.error(e);
            e.printStackTrace(System.out);
        }
    }
    
    /*
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
//System.out.println("processAction....");
        final String wsiteId = request.getParameter(ATTR_BRAND);
        final SocialSite model = SocialSite.ClassMgr.getSocialSite(wsiteId);
        final String action = response.getAction();
        if(SWBResourceURL.Action_ADD.equals(action))
        {
            String sclassURI = request.getParameter("socialweb");
            String title = request.getParameter("title");
            String desc = request.getParameter("desc");
            String appId = request.getParameter("appId");
            String sk = request.getParameter("sk");
            
            SemanticClass sclass = SWBPlatform.getSemanticMgr().getVocabulary().getSemanticClass(sclassURI);
            long id = model.getSemanticObject().getModel().getCounter(sclass);
            SocialNetwork socialNetwork = (SocialNetwork)model.getSemanticObject().getModel().createGenericObject(model.getSemanticObject().getModel().getObjectUri(Long.toString(id), sclass), sclass);
            socialNetwork.setTitle(title);
            socialNetwork.setDescription(desc);
            socialNetwork.setAppKey(appId);            
            socialNetwork.setSecretKey(sk);
            final String url = ((Oauthable)socialNetwork).doRequestPermissions();
            response.setRenderParameter(ATTR_OBJURI, socialNetwork.getURI());
            response.setRenderParameter(ATTR_BRAND, wsiteId);
            response.setRenderParameter(ATTR_TREEITEM, request.getParameter(ATTR_TREEITEM));
        }
    }
    */
    
    /**
     * Revisa si la {@code socialNetwork} tiene asignados los valores de {@code appKey} y {@code appSecret}
     * de lo contrario asigna a {@code socialNetwork} los valores de los par&aacute;metros recibidos y devuelve {@literal true}.
     * Si {@code socialNetwork} no tiene valores asignados y los par&aacute;metros tampoco, devolver&aacute; {@literal false}.
     * @param socialNetwork la instancia de la red social de la que se desea hacer la verificaci&oacute;n
     * @param appKey representa el valor de la llave de la aplicaci&oacute;n creada en la red social
     * @param appSecret representa el valor secreto de la aplicaci&oacute;n creada en la red social
     * @return {@literal true} si la {@code socialNetwork} tiene los valores de la llave y el secreto de la aplicaci&oacute;n
     *         o si fueron asignados los valores de los par&aacute;metros, en caso contrario devuelve {@literal false}.
     */
    private boolean isValidConfiguration(SocialNetwork socialNetwork, String appKey, String appSecret) {
        
        boolean validConfiguration = true;
        ////System.out.println("appKey:" + appKey + "---" + appSecret);
        if (socialNetwork.getAppKey() == null || socialNetwork.getAppKey().isEmpty()) {
            if (appKey != null && !appKey.isEmpty()) {
                socialNetwork.setAppKey(appKey);
                ////System.out.println("valid key!");
            } else {
                validConfiguration = false;
            }
        }
        if (socialNetwork.getSecretKey() == null || socialNetwork.getSecretKey().isEmpty()) {
            if (appSecret != null && !appSecret.isEmpty()) {
                socialNetwork.setSecretKey(appSecret);
                ////System.out.println("valid secret!");
            } else {
                validConfiguration = false;
            }
        }
        return validConfiguration;
    }
}
