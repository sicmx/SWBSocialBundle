package org.semanticwb.social;

import java.util.Iterator;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticObserver;


public class Message extends org.semanticwb.social.base.MessageBase 
{
    public Message(org.semanticwb.platform.SemanticObject base)
    {
        super(base);
    }
    
    //CUANDO SE ELIMINA UN POSTOUT, NO SE REQUIERE BORRAR SUS POSTOUTNETS DESDE OBSERVADOR, YA QUE ESO SE HACE DESDE LA ONTOLOGÍA (REMOVEDEPENDENCY)
    /*
    static {
        //Observador del objeto Photo (PostOut)
        Message.social_Message.registerObserver(new SemanticObserver() {
            public void notify(SemanticObject obj, Object prop, String lang, String action) {
                System.out.println("Entra a Observer de MessagePOSTOUT-0");
                if(action!=null)
                {
                    System.out.println("Entra a Observer de MessagePOSTOUT");
                    //Cada que un PostOut se elimine, se ejecutara este código, revisa si el PostOut tiene PostOutNets asociados
                    //de ser así elimina cada uno de ellos.
                    if(action.equals("REMOVE")) //Si la acción es eliminar el PostOut
                    {
                        System.out.println("Entra a Observer de MessagePOSTOUT-REMOVE");
                        PostOut postOut = (PostOut) obj.createGenericInstance();
                        Iterator <PostOutNet> itPostOutNets=PostOutNet.ClassMgr.listPostOutNetBySocialPost(postOut);
                        while(itPostOutNets.hasNext())
                        {
                            PostOutNet postOutNet=itPostOutNets.next();
                            System.out.println("Entra a eliminar PostOutNet:"+postOutNet.getSocialPost());
                            postOutNet.remove();
                            System.out.println("Entra a eliminar PostOutNet-BORRADO...:");
                        }
                    }
                    
                }
            }
        });
     }
    **/
    
}
