/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.social.admin.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.Logger;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.SWBContext;
import org.semanticwb.model.User;
import org.semanticwb.model.WebSite;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.social.SocialUserExtAttributes;

/**
 *
 * @author jorge.jimenez
 */
public class SocialUserExtAttributesRes extends GenericResource{
    
    private static Logger log = SWBUtils.getLogger(SocialUserExtAttributesRes.class);
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException 
    {
        PrintWriter out=response.getWriter();
        String suri=request.getParameter("suri");
        
        out.println("<script type=\"javascript\">");
        if (request.getParameter("statusMsg") != null) {
            out.println("   showStatus('" + request.getParameter("statusMsg") + "');");
        }
        if (request.getParameter("reloadTap") != null) {
            out.println(" reloadTab('" + suri + "'); ");
        }
        out.println("</script>");

        
        final String myPath = SWBPlatform.getContextPath() + "/work/models/" + paramRequest.getWebPage().getWebSiteId() + "/jsp/user/socialUserExtAttributes.jsp";
        RequestDispatcher dis = request.getRequestDispatcher(myPath);
        if (dis != null) {
            try {
                request.setAttribute("paramRequest", paramRequest);
                dis.include(request, response);
            } catch (Exception ex) {
                log.error(ex);
                ex.printStackTrace(System.out);
            }
        }
    }
    
      @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
          
        setUserExtendedAttributes(request, response);

        response.setRenderParameter("statusMsg", response.getLocaleString("saveUserExtAttr"));
        response.setRenderParameter("suri", request.getParameter("suri"));
        response.setRenderParameter("reloadTap", request.getParameter("suri"));
        response.setMode(response.Mode_VIEW);
        //response.setCallMethod(response.Call_CONTENT);
    }
      
      
    private void setUserExtendedAttributes(HttpServletRequest request, SWBActionResponse response) {
        //User user=response.getUser();
        //Tomando en cuenta que todos los usuarios que se modificaran en sus propiedades extendidas, sean usuarios del repositorio de Admin.
        if(request.getParameter("suri")!=null)
        {
            WebSite wsite=SWBContext.getAdminWebSite();

            SemanticObject semObj=SemanticObject.getSemanticObject(request.getParameter("suri"));
            User user=(User)semObj.getGenericInstance();

            SocialUserExtAttributes socialextatt = SocialUserExtAttributes.ClassMgr.getSocialUserExtAttributes(user.getId(), wsite);
            System.out.println("user.getId():"+user.getId()+",socialextatt-1:"+socialextatt);
            if (socialextatt == null) {
                socialextatt = SocialUserExtAttributes.ClassMgr.createSocialUserExtAttributes(user.getId(), wsite);
                System.out.println("user.getId():"+user.getId()+",socialextatt-2:"+socialextatt);
            }
            
            /*
            Enumeration enParams=request.getParameterNames();
            while(enParams.hasMoreElements())
            {
                String paramName=(String)enParams.nextElement();
                System.out.println("param:"+paramName+",value:"+request.getParameter(paramName));
            }*/

            if (request.getParameter(SocialUserExtAttributes.social_userCanRemoveMsg.getName()) != null) {
                System.out.println(SocialUserExtAttributes.social_userCanRemoveMsg.getName()+"---True");
                socialextatt.setUserCanRemoveMsg(true);
            }else{
                System.out.println(SocialUserExtAttributes.social_userCanRemoveMsg.getName()+"---False");
                socialextatt.setUserCanRemoveMsg(false);
               // socialextatt.removeProperty(SocialUserExtAttributes.social_userCanRemoveMsg.getName());
            }

            if (request.getParameter(SocialUserExtAttributes.social_userCanRespondMsg.getName()) != null) {
                System.out.println(SocialUserExtAttributes.social_userCanRespondMsg.getName()+"---True");
                socialextatt.setUserCanRespondMsg(true);
            }else{
                System.out.println(SocialUserExtAttributes.social_userCanRespondMsg.getName()+"---False");
                socialextatt.setUserCanRespondMsg(false);
                //socialextatt.removeProperty(SocialUserExtAttributes.social_userCanRespondMsg.getName());
            }

            if (request.getParameter(SocialUserExtAttributes.social_userCanReValueMsg.getName()) != null) {
                System.out.println(SocialUserExtAttributes.social_userCanReValueMsg.getName()+"---True");
                socialextatt.setUserCanReValueMsg(true);
            }else{
                System.out.println(SocialUserExtAttributes.social_userCanReValueMsg.getName()+"---False");
                socialextatt.setUserCanReValueMsg(false);
                //socialextatt.removeProperty(SocialUserExtAttributes.social_userCanReValueMsg.getName());
            }

            if (request.getParameter(SocialUserExtAttributes.social_userCanReTopicMsg.getName()) != null) {
                System.out.println(SocialUserExtAttributes.social_userCanReTopicMsg.getName()+"---True");
                socialextatt.setUserCanReTopicMsg(true);
            }else{
                System.out.println(SocialUserExtAttributes.social_userCanReTopicMsg.getName()+"---False");
                socialextatt.setUserCanReTopicMsg(false);
                //socialextatt.removeProperty(SocialUserExtAttributes.social_userCanReTopicMsg.getName());
            }
        }
    }
    
}
