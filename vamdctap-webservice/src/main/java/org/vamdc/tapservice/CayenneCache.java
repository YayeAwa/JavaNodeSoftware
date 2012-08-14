package org.vamdc.tapservice;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;

@Path("/clear_cache")
public class CayenneCache {

	@GET
	@Produces("text/plain")
	public String clearCache(){
		String result = "Removed "+clearCache(BaseContext.getThreadObjectContext())+" objects from common cache.";
		return result;
	}
	
	private int clearCache(ObjectContext context) {
		int size = 0;
		synchronized(this){
			DataContext dc = (DataContext) context;
			Collection<Object> objects = dc.getObjectStore().registeredNodes();
			size = objects.size();
			dc.unregisterObjects(objects);
			if (dc.getObjectStore()!=null && dc.getObjectStore().getDataRowCache()!=null){
				size+=dc.getObjectStore().getDataRowCache().size();
				dc.getObjectStore().getDataRowCache().clear();
				
			}
		}
		return size;
	}
}
