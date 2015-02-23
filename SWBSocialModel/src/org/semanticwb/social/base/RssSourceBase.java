package org.semanticwb.social.base;


   /**
   * Fuente de Rss 
   */
public abstract class RssSourceBase extends org.semanticwb.model.SWBClass implements org.semanticwb.model.Filterable,org.semanticwb.model.Descriptiveable,org.semanticwb.model.Activeable,org.semanticwb.model.Traceable
{
   /**
   * Url del rss
   */
    public static final org.semanticwb.platform.SemanticProperty social_rss_URL=org.semanticwb.SWBPlatform.getSemanticMgr().getVocabulary().getSemanticProperty("http://www.semanticwebbuilder.org/swb4/social#rss_URL");
    public static final org.semanticwb.platform.SemanticProperty social_rssPhoto=org.semanticwb.SWBPlatform.getSemanticMgr().getVocabulary().getSemanticProperty("http://www.semanticwebbuilder.org/swb4/social#rssPhoto");
   /**
   * Fuente de Rss
   */
    public static final org.semanticwb.platform.SemanticClass social_RssSource=org.semanticwb.SWBPlatform.getSemanticMgr().getVocabulary().getSemanticClass("http://www.semanticwebbuilder.org/swb4/social#RssSource");
   /**
   * The semantic class that represents the currentObject
   */
    public static final org.semanticwb.platform.SemanticClass sclass=org.semanticwb.SWBPlatform.getSemanticMgr().getVocabulary().getSemanticClass("http://www.semanticwebbuilder.org/swb4/social#RssSource");

    public static class ClassMgr
    {
       /**
       * Returns a list of RssSource for a model
       * @param model Model to find
       * @return Iterator of org.semanticwb.social.RssSource
       */

        public static java.util.Iterator<org.semanticwb.social.RssSource> listRssSources(org.semanticwb.model.SWBModel model)
        {
            java.util.Iterator it=model.getSemanticObject().getModel().listInstancesOfClass(sclass);
            return new org.semanticwb.model.GenericIterator<org.semanticwb.social.RssSource>(it, true);
        }
       /**
       * Returns a list of org.semanticwb.social.RssSource for all models
       * @return Iterator of org.semanticwb.social.RssSource
       */

        public static java.util.Iterator<org.semanticwb.social.RssSource> listRssSources()
        {
            java.util.Iterator it=sclass.listInstances();
            return new org.semanticwb.model.GenericIterator<org.semanticwb.social.RssSource>(it, true);
        }

        public static org.semanticwb.social.RssSource createRssSource(org.semanticwb.model.SWBModel model)
        {
            long id=model.getSemanticObject().getModel().getCounter(sclass);
            return org.semanticwb.social.RssSource.ClassMgr.createRssSource(String.valueOf(id), model);
        }
       /**
       * Gets a org.semanticwb.social.RssSource
       * @param id Identifier for org.semanticwb.social.RssSource
       * @param model Model of the org.semanticwb.social.RssSource
       * @return A org.semanticwb.social.RssSource
       */
        public static org.semanticwb.social.RssSource getRssSource(String id, org.semanticwb.model.SWBModel model)
        {
            return (org.semanticwb.social.RssSource)model.getSemanticObject().getModel().getGenericObject(model.getSemanticObject().getModel().getObjectUri(id,sclass),sclass);
        }
       /**
       * Create a org.semanticwb.social.RssSource
       * @param id Identifier for org.semanticwb.social.RssSource
       * @param model Model of the org.semanticwb.social.RssSource
       * @return A org.semanticwb.social.RssSource
       */
        public static org.semanticwb.social.RssSource createRssSource(String id, org.semanticwb.model.SWBModel model)
        {
            return (org.semanticwb.social.RssSource)model.getSemanticObject().getModel().createGenericObject(model.getSemanticObject().getModel().getObjectUri(id,sclass),sclass);
        }
       /**
       * Remove a org.semanticwb.social.RssSource
       * @param id Identifier for org.semanticwb.social.RssSource
       * @param model Model of the org.semanticwb.social.RssSource
       */
        public static void removeRssSource(String id, org.semanticwb.model.SWBModel model)
        {
            model.getSemanticObject().getModel().removeSemanticObject(model.getSemanticObject().getModel().getObjectUri(id,sclass));
        }
       /**
       * Returns true if exists a org.semanticwb.social.RssSource
       * @param id Identifier for org.semanticwb.social.RssSource
       * @param model Model of the org.semanticwb.social.RssSource
       * @return true if the org.semanticwb.social.RssSource exists, false otherwise
       */

        public static boolean hasRssSource(String id, org.semanticwb.model.SWBModel model)
        {
            return (getRssSource(id, model)!=null);
        }
       /**
       * Gets all org.semanticwb.social.RssSource with a determined ModifiedBy
       * @param value ModifiedBy of the type org.semanticwb.model.User
       * @param model Model of the org.semanticwb.social.RssSource
       * @return Iterator with all the org.semanticwb.social.RssSource
       */

        public static java.util.Iterator<org.semanticwb.social.RssSource> listRssSourceByModifiedBy(org.semanticwb.model.User value,org.semanticwb.model.SWBModel model)
        {
            org.semanticwb.model.GenericIterator<org.semanticwb.social.RssSource> it=new org.semanticwb.model.GenericIterator(model.getSemanticObject().getModel().listSubjectsByClass(swb_modifiedBy, value.getSemanticObject(),sclass));
            return it;
        }
       /**
       * Gets all org.semanticwb.social.RssSource with a determined ModifiedBy
       * @param value ModifiedBy of the type org.semanticwb.model.User
       * @return Iterator with all the org.semanticwb.social.RssSource
       */

        public static java.util.Iterator<org.semanticwb.social.RssSource> listRssSourceByModifiedBy(org.semanticwb.model.User value)
        {
            org.semanticwb.model.GenericIterator<org.semanticwb.social.RssSource> it=new org.semanticwb.model.GenericIterator(value.getSemanticObject().getModel().listSubjectsByClass(swb_modifiedBy,value.getSemanticObject(),sclass));
            return it;
        }
       /**
       * Gets all org.semanticwb.social.RssSource with a determined Creator
       * @param value Creator of the type org.semanticwb.model.User
       * @param model Model of the org.semanticwb.social.RssSource
       * @return Iterator with all the org.semanticwb.social.RssSource
       */

        public static java.util.Iterator<org.semanticwb.social.RssSource> listRssSourceByCreator(org.semanticwb.model.User value,org.semanticwb.model.SWBModel model)
        {
            org.semanticwb.model.GenericIterator<org.semanticwb.social.RssSource> it=new org.semanticwb.model.GenericIterator(model.getSemanticObject().getModel().listSubjectsByClass(swb_creator, value.getSemanticObject(),sclass));
            return it;
        }
       /**
       * Gets all org.semanticwb.social.RssSource with a determined Creator
       * @param value Creator of the type org.semanticwb.model.User
       * @return Iterator with all the org.semanticwb.social.RssSource
       */

        public static java.util.Iterator<org.semanticwb.social.RssSource> listRssSourceByCreator(org.semanticwb.model.User value)
        {
            org.semanticwb.model.GenericIterator<org.semanticwb.social.RssSource> it=new org.semanticwb.model.GenericIterator(value.getSemanticObject().getModel().listSubjectsByClass(swb_creator,value.getSemanticObject(),sclass));
            return it;
        }
    }

    public static RssSourceBase.ClassMgr getRssSourceClassMgr()
    {
        return new RssSourceBase.ClassMgr();
    }

   /**
   * Constructs a RssSourceBase with a SemanticObject
   * @param base The SemanticObject with the properties for the RssSource
   */
    public RssSourceBase(org.semanticwb.platform.SemanticObject base)
    {
        super(base);
    }
   /**
   * Sets the value for the property ModifiedBy
   * @param value ModifiedBy to set
   */

    public void setModifiedBy(org.semanticwb.model.User value)
    {
        if(value!=null)
        {
            getSemanticObject().setObjectProperty(swb_modifiedBy, value.getSemanticObject());
        }else
        {
            removeModifiedBy();
        }
    }
   /**
   * Remove the value for ModifiedBy property
   */

    public void removeModifiedBy()
    {
        getSemanticObject().removeProperty(swb_modifiedBy);
    }

   /**
   * Gets the ModifiedBy
   * @return a org.semanticwb.model.User
   */
    public org.semanticwb.model.User getModifiedBy()
    {
         org.semanticwb.model.User ret=null;
         org.semanticwb.platform.SemanticObject obj=getSemanticObject().getObjectProperty(swb_modifiedBy);
         if(obj!=null)
         {
             ret=(org.semanticwb.model.User)obj.createGenericInstance();
         }
         return ret;
    }

/**
* Gets the Created property
* @return java.util.Date with the Created
*/
    public java.util.Date getCreated()
    {
        return getSemanticObject().getDateProperty(swb_created);
    }

/**
* Sets the Created property
* @param value long with the Created
*/
    public void setCreated(java.util.Date value)
    {
        getSemanticObject().setDateProperty(swb_created, value);
    }

/**
* Gets the Updated property
* @return java.util.Date with the Updated
*/
    public java.util.Date getUpdated()
    {
        return getSemanticObject().getDateProperty(swb_updated);
    }

/**
* Sets the Updated property
* @param value long with the Updated
*/
    public void setUpdated(java.util.Date value)
    {
        getSemanticObject().setDateProperty(swb_updated, value);
    }

/**
* Gets the Description property
* @return String with the Description
*/
    public String getDescription()
    {
        return getSemanticObject().getProperty(swb_description);
    }

/**
* Sets the Description property
* @param value long with the Description
*/
    public void setDescription(String value)
    {
        getSemanticObject().setProperty(swb_description, value);
    }

    public String getDescription(String lang)
    {
        return getSemanticObject().getProperty(swb_description, null, lang);
    }

    public String getDisplayDescription(String lang)
    {
        return getSemanticObject().getLocaleProperty(swb_description, lang);
    }

    public void setDescription(String description, String lang)
    {
        getSemanticObject().setProperty(swb_description, description, lang);
    }

/**
* Gets the Active property
* @return boolean with the Active
*/
    public boolean isActive()
    {
        return getSemanticObject().getBooleanProperty(swb_active);
    }

/**
* Sets the Active property
* @param value long with the Active
*/
    public void setActive(boolean value)
    {
        getSemanticObject().setBooleanProperty(swb_active, value);
    }
   /**
   * Sets the value for the property Creator
   * @param value Creator to set
   */

    public void setCreator(org.semanticwb.model.User value)
    {
        if(value!=null)
        {
            getSemanticObject().setObjectProperty(swb_creator, value.getSemanticObject());
        }else
        {
            removeCreator();
        }
    }
   /**
   * Remove the value for Creator property
   */

    public void removeCreator()
    {
        getSemanticObject().removeProperty(swb_creator);
    }

   /**
   * Gets the Creator
   * @return a org.semanticwb.model.User
   */
    public org.semanticwb.model.User getCreator()
    {
         org.semanticwb.model.User ret=null;
         org.semanticwb.platform.SemanticObject obj=getSemanticObject().getObjectProperty(swb_creator);
         if(obj!=null)
         {
             ret=(org.semanticwb.model.User)obj.createGenericInstance();
         }
         return ret;
    }

/**
* Gets the Title property
* @return String with the Title
*/
    public String getTitle()
    {
        return getSemanticObject().getProperty(swb_title);
    }

/**
* Sets the Title property
* @param value long with the Title
*/
    public void setTitle(String value)
    {
        getSemanticObject().setProperty(swb_title, value);
    }

    public String getTitle(String lang)
    {
        return getSemanticObject().getProperty(swb_title, null, lang);
    }

    public String getDisplayTitle(String lang)
    {
        return getSemanticObject().getLocaleProperty(swb_title, lang);
    }

    public void setTitle(String title, String lang)
    {
        getSemanticObject().setProperty(swb_title, title, lang);
    }

/**
* Gets the Rss_URL property
* @return String with the Rss_URL
*/
    public String getRss_URL()
    {
        return getSemanticObject().getProperty(social_rss_URL);
    }

/**
* Sets the Rss_URL property
* @param value long with the Rss_URL
*/
    public void setRss_URL(String value)
    {
        getSemanticObject().setProperty(social_rss_URL, value);
    }

/**
* Gets the RssPhoto property
* @return String with the RssPhoto
*/
    public String getRssPhoto()
    {
        return getSemanticObject().getProperty(social_rssPhoto);
    }

/**
* Sets the RssPhoto property
* @param value long with the RssPhoto
*/
    public void setRssPhoto(String value)
    {
        getSemanticObject().setProperty(social_rssPhoto, value);
    }
}
