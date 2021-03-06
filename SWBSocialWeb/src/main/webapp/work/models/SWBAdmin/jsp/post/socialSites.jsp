<%-- 
    Document   : createMenuPost
    Created on : 25/03/2013, 11:31:50 am
    Author     : Jorge.Jimenez
--%>
<%@page import="org.semanticwb.model.UserGroup"%>
<%@page import="org.semanticwb.model.SWBContext"%>
<%@page import="org.semanticwb.model.AdminFilter"%>
<jsp:useBean id="paramRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest"/>
<%@page import="org.semanticwb.social.SocialSite,java.util.*,org.semanticwb.social.SocialTopic,org.semanticwb.model.WebSite,org.semanticwb.model.User,org.semanticwb.platform.SemanticObject,org.semanticwb.model.SWBComparator"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>

<%    
        ArrayList<SocialSite> aListSites=new ArrayList(); 
        User user=paramRequest.getUser(); 
        SWBResourceURL url = paramRequest.getRenderUrl();  
        
        UserGroup userSuperAdminGrp = SWBContext.getAdminWebSite().getUserRepository().getUserGroup("su");
        if(user.hasUserGroup(userSuperAdminGrp)){//is super user-> can see everything
            Iterator<SocialSite> itSocialSites=sortByDisplayNameSet(SocialSite.ClassMgr.listSocialSites(), user.getLanguage());
            while(itSocialSites.hasNext())
            {
                SocialSite socialSite=itSocialSites.next();
                if(socialSite.isValid())
                {
                    aListSites.add(socialSite);
                }
            }
        }else{
            Iterator<SocialSite> itSocialSites=sortByDisplayNameSet(SocialSite.ClassMgr.listSocialSites(), user.getLanguage());  
            while(itSocialSites.hasNext())
            {
                SocialSite socialSite=itSocialSites.next();
                if(socialSite.isValid())
                {
                    Iterator<AdminFilter> userAdmFilters=user.listAdminFilters();
                    while(userAdmFilters.hasNext())
                    {
                        AdminFilter userAdmFilter=userAdmFilters.next();
                        if(userAdmFilter.haveTreeAccessToSemanticObject(socialSite.getSemanticObject()))
                        {
                            aListSites.add(socialSite);
                        }
                    }
                }
            }
            
            if(aListSites.isEmpty() && (!user.listAdminFilters().hasNext())){//User has not admin filters and the data is Empty
                itSocialSites=sortByDisplayNameSet(SocialSite.ClassMgr.listSocialSites(), user.getLanguage());
                while(itSocialSites.hasNext())
                {
                    SocialSite socialSite=itSocialSites.next();
                    if(socialSite.isValid() && user.haveAccess(socialSite))
                    {
                        aListSites.add(socialSite);
                    }
                }
            }
        }
        

%>


<div class="swbform">
    <div id="pub-detalle">
    <form name="formSites" id="formSites">        
    <table width="50%" border="0px">            
       <tr>
           <td colspan="3" style="text-align: center;" class="titulo">Seleccione una marca:</td>        
       </tr>
       <tr>
           <td colspan="3" style="text-align: center;">&nbsp;</td>        
       </tr>
       <tr>
            <td style="text-align: center;">
                <select name="socialSite" id="socialSite" onchange="javascript:postHtml('<%=url.setMode("afterChooseSite")%>?socialSite='+escape(document.formSites.socialSite[document.formSites.socialSite.selectedIndex].value), 'socialTopics');"> 
                     <option value="" selected="selected">Seleccione una marca</option>
                    <%
                        Iterator<SocialSite> itSocialSites2=aListSites.iterator();
                        while(itSocialSites2.hasNext())
                        {
                            SocialSite socialSite=itSocialSites2.next();  
                            %>
                                <option value="<%=socialSite.getURI()%>"><%=socialSite.getDisplayTitle(user.getLanguage())%></option>
                            <%
                        }
                    %>
                </select>
            </td>
            
       </tr>
       
    </table>
    </form>
    </div>
</div>
<div id="socialTopics" dojoType="dijit.layout.ContentPane" style="width:100%; height:100%;">
</div>

<%!

    /**
     * Sort by display name set.
     *
     * @param it the it
     * @param lang the lang
     * @return the sets the
     */
    public static Iterator sortByDisplayNameSet(Iterator it, String lang) {
        TreeSet set = new TreeSet(new SWBComparator(lang)); 

        while (it.hasNext()) {
            set.add(it.next());
        }        

        return set.descendingSet().iterator();
    }

%>
