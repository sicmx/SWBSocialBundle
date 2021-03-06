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
 
package org.semanticwb.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Represents any message, image or video posted in any of the social networks
 * at which this application searches for information
 * @author jose.jimenez
 */
public class ExternalPost {
    
    
    /**
     * The identification string or number for the message
     */
    private String postId;
    
    /**
     * The message's creator's name
     */
    private String creatorName;
    
    /**
     * The message's creator's identification number or string
     */
    private String creatorId;
    
    /**
     * User photo url in SocialNetwork
     */
    
    private String creatorPhotoUrl;
    
    /**
     * The message posted
     */
    private String message;
    
    /**
     * The link or URL for a picture related to the message
     */
    private ArrayList pictures=new ArrayList();
    
    /**
     * The link or URL for a video related to the message
     */
    private String video;
    
    /**
     * The category of the message, its very used with categorias for youtube
     */
    private String category;
    
    /**
     * A link related to the message
     */
    private String link;
    
    /**
     * The name with which this message was created
     */
    private String postName;
    
    
    /**
     * The text that titles this message
     */
    private String tags;
    
    /**
     * The text that describes the link related to this message
     */
    private String description;
    
    /**
     * A link to an icon representing the type of this post (in Facebook)
     */
    private String icon;
    
    /**
     * The time the post was initially published
     */
    private Date creationTime;
    
    /**
     * The time of the last comment or update to this post
     */
    private Date updateTime;
    
    /**
     * Creator's friends number
     */
    private int friendsNumber;
    
    /**
     * Creator's followers number
     */
    private int followers;
    
    /**
     * Number that post has been retweeted
     */
    private int postShared;
    
    /**
     * Device used by the post creator
     */
    //private String device;
    
    /**
     * Date when the user was created
     */
    private Date usercreation;
    
    /**
     * Place where the post was originated
     */
    private String place;
    
    /**
     * location where the post was originated
     */
    //private String location;
    
    /**
     * Indicates the type of post retrieved from the Social Net
     */
    private String postType;
    
    /*
     * Indicates the type of socialNet by the postIn comes;
     */
    private SocialNetwork socialNetwork;
    
    /*
     * Indicates the latitude where the message is comming from
     */
    private double latitude;
    
    /*
     * Indicates the longitude where the message is comming from
     */
    private double longitude;
    
    
    /*
     * Indicates the country code where the message is comming from. Ej from México would be MX, from USA would be US and so on.
     */
    private String countryCode;
    
    /*
     * indicates de user geolocation which is registered in the specific social netwwork.
     */
    private String userGeoLocation;
    
    /**
     * Indicates the userUrl in the social network that the user belongs
     */
    private String userUrl;
    
    /*
     * Indicates the post url that refers to the social Network
     */
    private String postUrl;
    
    /**
     * Device type
     */
    private DeviceType deviceType;
    
     /**
     * Device platform
     */
    private DevicePlatform devicePlatform;
    

    /**
     * Creates a new instance of ExternalPost
     */
    public ExternalPost() {
    }
    
    /**
     * @return the postId
     */
    public String getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * @return the creatorName
     */
    public String getCreatorName() {
        return creatorName;
    }
    
    /**
     * @return the User Url
     */
    public String getUserUrl(){
        return userUrl;
    }
    
    
     /**
     * @return the User Url
     */
    public void setUserUrl(String userUrl){
        this.userUrl=userUrl;
    }
    
    
    /**
     * @return the Post Url
     */
    public String getPostUrl(){
        return postUrl;
    }
    
    
     /**
     * @return the User Url
     */
    public void setPostUrl(String postUrl){
        this.postUrl=postUrl;
    }
    
    /**
     * @param creatorName the creatorName to set
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    /**
     * 
     * @return the creator Photo Url
     */
    public String getCreatorPhotoUrl()
    {
        return creatorPhotoUrl;
    }
    
    /**
     * @param creatorPhotoUrl the creatorPhotoUrl to set
     */
    public void setCreatorPhotoUrl(String creatorPhotoUrl)
    {
        this.creatorPhotoUrl = creatorPhotoUrl;
    }
    

    /**
     * @return the creatorId
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * @param creatorId the creatorId to set
     */
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the picture
     */
    public Iterator lisPictures() {
        return pictures.iterator();
    }
    
    
    
    /**
     * @param picture the picture to set
     */
    public void setPictures(ArrayList pictures) {
        this.pictures = pictures;
    }
    
    
     /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }
    
     /**
     * @param tags the Tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    
    /**
     * @return the video
     */
    public String getVideo() {
        return video;
    }
    
    /**
     * @param video the video to set
     */
    public void setVideo(String video) {
        this.video = video;
    }
    
    /**
     * @return the category, most used when is a Video
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }
    

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the postName
     */
    public String getPostName() {
        return postName;
    }

    /**
     * @param postName the postName to set
     */
    public void setPostName(String postName) {
        this.postName = postName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the linkDescription to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the creationTime
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime the creationTime to set
     */
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @return the updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the friendsNumber
     */
    public int getFriendsNumber() {
        return friendsNumber;
    }

    /**
     * @param friendsNumber the friendsNumber to set
     */
    public void setFriendsNumber(int friendsNumber) {
        this.friendsNumber = friendsNumber;
    }

    /**
     * @return the followers
     */
    public int getFollowers() {
        return followers;
    }

    /**
     * @param followers the followers to set
     */
    public void setFollowers(int followers) {
        this.followers = followers;
    }
    
    /**
     * 
     * @return the retweets
     */
    public int getPostShared()
    {
       return postShared; 
    }
    
    
    /**
     * 
     * @param the retweets to set
     */
    public void setPostShared(int postShared)
    {
       this.postShared=postShared;
    }
    
    
    /**
     * 
     * @return usercreation
     */
    public Date getUsercreation()
    {
        return usercreation;
    }
    
    /**
     * 
     * @param usercreation the creation date to set
     */
    public void setUsercreation(Date usercreation)
    {
        this.usercreation=usercreation;
    }
    
    /**
     * 
     * @return the place
     */
    public String getPlace()
    {
        return place;
    }
    
    
    /**
     * 
     * @param place the place to set
     */
    public void setPlace(String place)
    {
        this.place=place;
    }
    
    
    /**
     * 
     * @return location the location where the post was originated
     */
    /*
    public String getLocation()
    {
       return location; 
    }
    */
    /**
     * 
     * @param location the location where the post was originated
     * 
     */
    /*
    public void setLocation(String location)
    {
        this.location=location;
    }
    * */
    
    /*
     * Sets the latitude the message is comming from
     */
    public void setLatitude(double latitude)
    {
        this.latitude=latitude;
    }
    
    /*
     * Gets the latitude the message is comming from
     */
    public double getLatitude()
    {
        return latitude;
    }
    
    
    /*
     * Sets the longitude the message is comming from
     */
    public void setLongitude(double longitude)
    {
        this.longitude=longitude;
    }
    
    /*
     * Gets the longitude the message is comming from
     */
    public double getLongitude()
    {
        return longitude;
    }

    
    /*
     * Sets the country code the message is comming from
     */
    public void setCountryCode(String countryCode)
    {
        this.countryCode=countryCode;
    }
    
    /*
     * Gets the country code the message is comming from
     */
    public String getCountryCode()
    {
        return countryCode;
    }
    
    
    /**
     * Returns the type of post retrieved from the social net. Facebook uses one
     * of the following types: {@literal link}, {@literal page}, {@literal video},
     * {@literal status}, {@literal user}, {@literal photo}.
     * @return the postType
     */
    public String getPostType() {
        return postType;
    }

    /**
     * @param postType the postType to set
     */
    public void setPostType(String postType) {
        this.postType = postType;
    }
    
     /**
     * 
     * @param socialNetwork the socialNetwork where the post was originated
     * 
     */
    public void setSocialNetwork(SocialNetwork socialNetwork)
    {
        this.socialNetwork=socialNetwork;
    }
    
     /**
     * Returns the type of socialNetwork the post is comming from
     */
    public SocialNetwork getSocialNetwork()
    {
        return socialNetwork;
    }
    
    /**
     * 
     * @param userGeolocation is the user Geolocatión registered in the social network for the user
     */
    public void setUserGeoLocation(String userGeolocation)
    {
        this.userGeoLocation=userGeolocation;
    }
    
    /**
     * 
     * @return userGeoLocation of the user
     */
    public String getUserGeoLocation()
    {
        return userGeoLocation;
    }
    
    /*
     *  @param devicetype is the type of device which is used for the user that post the message that enters to the system
     */
    public void setDeviceType(DeviceType devicetype)
    {
        this.deviceType=devicetype;
    }
    
    
    /**
     * 
     * @return the Type of device user by the user
     */
    public DeviceType getDeviceType()
    {
        return deviceType;
    }
    
     /*
     *  @param deviceplatform is the opetating system of device which is used for the user that post the message that enters to the system
     */
    public void setDevicePlatform(DevicePlatform devicePlatform)
    {
        this.devicePlatform=devicePlatform;
    }
    
    
    /**
     * 
     * @return the Type of device user by the user
     */
    public DevicePlatform getDevicePlatform()
    {
        return this.devicePlatform;
    }
}
